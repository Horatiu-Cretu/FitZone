package project.config.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import project.service.JWTService;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    private final JWTService jwtService;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    public JwtFilter(JWTService jwtService, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.jwtService = jwtService;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String jwtToken = null;
        Claims claims = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
            try {
                if (jwtService.isTokenSignatureValid(jwtToken)) {
                    claims = jwtService.extractAllClaims(jwtToken);
                } else {
                    logger.warn("Invalid JWT signature: {}", jwtToken);
                }
            } catch (Exception e) {
                logger.warn("Error processing JWT: {}", e.getMessage());
            }
        } else {
            logger.trace("No JWT token found in Authorization header or header does not start with Bearer.");
        }


        if (claims != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsServiceImpl.loadUserByTokenClaims(claims);

            if (userDetails != null && !jwtService.extractExpiration(jwtToken).before(new java.util.Date())) {
                logger.debug("JWT token is valid. Setting authentication for user: {}", userDetails.getUsername());
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);


                Long userId = jwtService.extractUserId(jwtToken);
                String role = jwtService.extractRole(jwtToken);
                if (userId != null) {
                    request.setAttribute("userId", userId);
                    logger.trace("Set request attribute 'userId': {}", userId);
                }
                if (role != null) {
                    request.setAttribute("userRole", role);
                    logger.trace("Set request attribute 'userRole': {}", role);
                }

            } else {
                logger.warn("JWT token is expired or UserDetails could not be loaded from claims.");
            }
        }
        filterChain.doFilter(request, response);
    }
}