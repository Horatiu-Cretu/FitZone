package project.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.function.Function;

public interface IJWTService {

    String generateToken(Long userId, String email, String role);

    boolean validateToken(String jwtToken, UserDetails userDetails);

    Date extractExpiration(String jwtToken);

    String extractSubjectAsUserIdString(String jwtToken);

    Long extractUserId(String jwtToken);

    String extractEmail(String jwtToken);

    String extractRole(String jwtToken);

    <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver);

}