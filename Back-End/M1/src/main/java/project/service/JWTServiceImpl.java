package project.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTServiceImpl implements IJWTService {

    private static final Logger logger = LoggerFactory.getLogger(JWTServiceImpl.class);

    @Value("${jwt.secret}")
    private String base64SecretKey;

    @Value("${jwt.expiration}")
    private long expirationTimeInSeconds;

    private volatile SecretKey key = null;

    @Override
    public String generateToken(Long userId, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("role", role);

        SecretKey currentKey = getSigningKey();
        if (currentKey == null) {
            logger.error("Failed to generate JWT: SecretKey is null. Check jwt.secret property.");
            throw new RuntimeException("Failed to generate JWT due to missing or invalid secret key configuration.");
        }

        long nowMillis = System.currentTimeMillis();
        long expMillis = nowMillis + (this.expirationTimeInSeconds * 1000);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date(nowMillis))
                .setExpiration(new Date(expMillis))
                .signWith(currentKey)
                .compact();
    }

    private synchronized SecretKey getSigningKey() {
        if (key == null) {
            synchronized (this) {
                if (key == null) {
                    if (base64SecretKey == null || base64SecretKey.trim().isEmpty()) {
                        logger.error("jwt.secret property is null or empty. Cannot generate key.");
                        throw new IllegalStateException("jwt.secret property is not configured.");
                    }
                    try {
                        byte[] keyBytes = Decoders.BASE64.decode(base64SecretKey);
                        if (keyBytes.length < 32) {
                            logger.warn("Decoded JWT secret key length is less than 32 bytes ({}), which is potentially insecure for HS256. Ensure your jwt.secret is a strong, Base64 encoded key of appropriate length.", keyBytes.length);
                        }
                        this.key = Keys.hmacShaKeyFor(keyBytes);
                        logger.info("SecretKey for JWT signing generated successfully.");
                    } catch (IllegalArgumentException e) {
                        logger.error("Error decoding Base64 secret key for JWT: {}. Check if jwt.secret is a valid Base64 string.", e.getMessage(), e);
                        throw new IllegalStateException("Invalid jwt.secret configuration (not Base64).", e);
                    } catch (Exception e) {
                        logger.error("Unexpected error during SecretKey generation for JWT: {}", e.getMessage(), e);
                        throw new IllegalStateException("Failed to initialize JWT signing key.", e);
                    }
                }
            }
        }
        return key;
    }

    @Override
    public boolean validateToken(String jwtToken, UserDetails userDetails) {
        try {
            SecretKey currentKey = getSigningKey();
            if (currentKey == null) {
                logger.error("Token validation failed: Secret key is null.");
                return false;
            }
            Jwts.parser()
                    .verifyWith(currentKey)
                    .build()
                    .parseSignedClaims(jwtToken);

            final String usernameInToken = extractEmail(jwtToken);
            boolean isTokenValid = usernameInToken != null && usernameInToken.equals(userDetails.getUsername()) && !isTokenExpired(jwtToken);
            if (!isTokenValid) {
                logger.warn("Token validation failed: Username mismatch or token expired. Token username: '{}', UserDetails username: '{}'", usernameInToken, userDetails.getUsername());
            }
            return isTokenValid;
        } catch (Exception e) {
            logger.warn("Token validation failed: {} (Token: {})", e.getMessage(), jwtToken);
            return false;
        }
    }

    private boolean isTokenExpired(String jwtToken) {
        try {
            return extractExpiration(jwtToken).before(new Date());
        } catch (Exception e) {
            logger.warn("Could not extract expiration from token, treating as expired/invalid: {}", e.getMessage());
            return true;
        }
    }

    @Override
    public Date extractExpiration(String jwtToken) {
        return extractClaim(jwtToken, Claims::getExpiration);
    }

    @Override
    public String extractSubjectAsUserIdString(String jwtToken) {
        return extractClaim(jwtToken, Claims::getSubject);
    }

    @Override
    public Long extractUserId(String jwtToken) {
        try {
            String subject = extractSubjectAsUserIdString(jwtToken);
            if (subject == null) {
                logger.warn("Token subject (expected User ID) is null.");
                return null;
            }
            return Long.parseLong(subject);
        } catch (NumberFormatException e) {
            logger.warn("Invalid User ID format '{}' in token subject.", extractSubjectAsUserIdString(jwtToken), e);
            return null;
        } catch (Exception e) {
            logger.warn("Failed to extract User ID from token: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String extractEmail(String jwtToken) {
        try {
            Claims claims = extractAllClaimsInternal(jwtToken);
            if (claims == null) return null;
            String email = claims.get("email", String.class);
            if (email == null) {
                logger.warn("Email claim not found or not a String in token for subject {}", claims.getSubject());
            }
            return email;
        } catch (Exception e) {
            logger.warn("Failed to extract email claim from token: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String extractRole(String jwtToken) {
        try {
            Claims claims = extractAllClaimsInternal(jwtToken);
            if (claims == null) return null;
            String role = claims.get("role", String.class);
            if (role == null) {
                logger.warn("Role claim not found or not a String in token for subject {}", claims.getSubject());
            }
            return role;
        } catch (Exception e) {
            logger.warn("Failed to extract role claim from token: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaimsInternal(jwtToken);
        if (claims == null) {
            return null;
        }
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaimsInternal(String jwtToken) {
        SecretKey currentKey = getSigningKey();
        if (currentKey == null) {
            logger.error("Cannot extract claims: Secret key is null. JWT processing aborted.");
            throw new IllegalStateException("JWT Secret Key is not initialized. Cannot process token.");
        }
        try {
            return Jwts.parser()
                    .verifyWith(currentKey)
                    .build()
                    .parseSignedClaims(jwtToken)
                    .getPayload();
        } catch (Exception e) {
            logger.warn("Failed to parse claims from JWT: {} (Token: {})", e.getMessage(), jwtToken);
            return null;
        }
    }
}