package com.springvuegradle.team200.jwt;

import com.springvuegradle.team200.model.User;
import com.springvuegradle.team200.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

/**
 * Filters REST-ful requests by the content of their bearer token.
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final String COOKIE_NAME = "token";
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * Attempts to retrieve JWT from the Cookie received in the request
     * Cookie's name is defined in COOKIE_NAME variable
     *
     * @param request Request data received
     * @return Optional of JWT String
     */
    private Optional<String> getJwtFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) return Optional.empty();

        return Arrays.stream(cookies)
                .filter(c -> c.getName().equals(COOKIE_NAME))
                .map(Cookie::getValue)
                .findFirst();
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        Long userId = null;
        Optional<String> jwt = getJwtFromCookie(request);
        String token = null;

        if (jwt.isPresent()) {
            token = jwt.get();
            try {
                userId = jwtTokenUtil.getUserIdFromToken(token);
            } catch (IllegalArgumentException e) {
                logger.warn("Unable to get JWT Token");
            } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException e) {
                logger.warn(e);
            }
        }

        // Once we get the token validate it.
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userRepository.getUserFromId(userId);
            if (Boolean.TRUE.equals(jwtTokenUtil.validateToken(token, user))) {

                // Have to create a usernamePasswordAuthenticationToken to pass through the next steps of Spring's
                // Security filters.
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication level using username/pass auth token built into spring
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        chain.doFilter(request, response);
    }
}