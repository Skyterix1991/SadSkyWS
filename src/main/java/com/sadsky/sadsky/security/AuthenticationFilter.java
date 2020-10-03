package com.sadsky.sadsky.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sadsky.sadsky.user.domain.UserRepository;
import com.sadsky.sadsky.user.request.UserLoginRequest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final ApplicationContext applicationContext;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            UserLoginRequest userLoginRequest = new ObjectMapper()
                    .readValue(request.getInputStream(), UserLoginRequest.class);

            return this.authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userLoginRequest.getEmail(),
                            userLoginRequest.getPassword(),
                            new ArrayList<>()
                    )
            );
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        String userId = ((User) authResult.getPrincipal()).getUsername();

        com.sadsky.sadsky.user.domain.User user = applicationContext.getBean(UserRepository.class).findUserByUserId(UUID.fromString(userId)).orElseThrow();

        String secret = applicationContext.getBean(Environment.class).getProperty(SecurityConstants.TOKEN_SECRET_PROPERTY_NAME);

        String token = Jwts.builder()
                .setSubject(user.getUserId().toString())
                .setIssuer(SecurityConstants.NAME)
                .claim("lastTokenRevokeDate", user.getLastTokenRevokeDate().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.TOKEN_EXPIRATION_TIME_IN_MS))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();


        response.addHeader(SecurityConstants.TOKEN_HEADER_NAME, SecurityConstants.TOKEN_HEADER_PREFIX + " " + token);
        response.addHeader("Id", user.getUserId().toString());
    }

}