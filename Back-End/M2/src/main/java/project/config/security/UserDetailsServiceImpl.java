package project.config.security;

import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import project.service.JWTService;

import java.util.Collection;
import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    private final JWTService jwtService;


    @Autowired
    public UserDetailsServiceImpl(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        throw new UsernameNotFoundException("User lookup by email is not supported in M2. Use token for authentication.");
    }


    public UserDetails loadUserByTokenClaims(Claims claims) {
        String email = claims.get("email", String.class);
        String role = claims.get("role", String.class);
        String userIdString = claims.getSubject();

        if (email == null || role == null || userIdString == null) {
            logger.warn("JWT claims are missing required fields (email, role, or subject).");
            throw new UsernameNotFoundException("Invalid token: Missing claims.");
        }
        logger.debug("Loading UserDetails from token for email: {}, role: {}, userId: {}", email, role, userIdString);

        return new User(
                email,
                "",
                getAuthorities(role)
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        if (role == null || role.trim().isEmpty()) {
            logger.warn("Role is null or empty, returning no authorities.");
            return Collections.emptyList();
        }
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
    }
}