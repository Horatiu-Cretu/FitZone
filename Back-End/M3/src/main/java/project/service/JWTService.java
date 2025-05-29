package project.service;

import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.function.Function;

public interface JWTService {

    boolean isTokenSignatureValid(String jwtToken);
    Date extractExpiration(String jwtToken);
    String extractSubjectAsUserIdString(String jwtToken);
    Long extractUserId(String jwtToken);
    String extractEmail(String jwtToken);
    String extractRole(String jwtToken);
    <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver);
    Claims extractAllClaims(String jwtToken);
}