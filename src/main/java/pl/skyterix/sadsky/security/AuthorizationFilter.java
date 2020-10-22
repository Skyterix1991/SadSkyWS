package pl.skyterix.sadsky.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import pl.skyterix.sadsky.user.domain.User;
import pl.skyterix.sadsky.user.domain.UserRepository;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

class AuthorizationFilter extends BasicAuthenticationFilter {

    private final ApplicationContext applicationContext;

    public AuthorizationFilter(AuthenticationManager authenticationManager, ApplicationContext applicationContext) {
        super(authenticationManager);

        this.applicationContext = applicationContext;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(SecurityConstants.TOKEN_HEADER_NAME);

        if (header == null) {
            chain.doFilter(request, response);

            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(SecurityConstants.TOKEN_HEADER_NAME);
        if (token == null) {
            return null;
        }

        token = token.replace(SecurityConstants.TOKEN_HEADER_PREFIX, "").strip();

        String secret = applicationContext.getBean(Environment.class).getProperty(SecurityConstants.TOKEN_SECRET_PROPERTY_NAME);

        Claims claims;

        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (MalformedJwtException | SignatureException | ExpiredJwtException e) {
            return null;
        }

        if (claims.getSubject() == null) {
            return null;
        }

        Optional<User> user = applicationContext.getBean(UserRepository.class).findUserByUserId(UUID.fromString(claims.getSubject()));
        if (user.isEmpty()) {
            return null;
        }

        LocalDateTime lastTokenRevokeDate = LocalDateTime.parse(claims.get("lastTokenRevokeDate").toString());
        if (!lastTokenRevokeDate.isEqual(user.get().getLastTokenRevokeDate())) {
            return null;
        }

        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_USER");

        return new UsernamePasswordAuthenticationToken(claims.getSubject(), null, Collections.singletonList(grantedAuthority));
    }
}