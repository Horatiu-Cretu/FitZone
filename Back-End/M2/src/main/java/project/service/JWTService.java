package project.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.function.Function;

public interface JWTService {

    String generateToken(Long userId, String email, String role); // Might not be used in M2 if it only consumes tokens

    boolean validateToken(String jwtToken, UserDetails userDetails); // UserDetails might be simplified or custom for M2

    boolean isTokenSignatureValid(String jwtToken);

    Date extractExpiration(String jwtToken);

    String extractSubjectAsUserIdString(String jwtToken);

    Long extractUserId(String jwtToken);

    String extractEmail(String jwtToken);

    String extractRole(String jwtToken);

    <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver);

    Claims extractAllClaims(String jwtToken); // Made public for UserDetailsServiceImpl
}