package com.springvuegradle.team200.jwt;

import com.springvuegradle.team200.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil implements Serializable {

    public static final long JWT_TOKEN_VALIDITY_MILLIS = (long)1000 * 60 * 60 * 3; // 3 hour

    // Automatically a key based on the mac address of the PC
    private Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private transient JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(key).build();

    private Claims getAllClaimsFromToken(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public Long getUserIdFromToken(String token) {
        return Long.parseLong(getClaimFromToken(token, Claims::getSubject));
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Gets a specific claim from a token
     *
     * @param token          token to be checked
     * @param claimsResolver Resolver: e.g. Claims:GetSubject
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Checks whether the token has passed its expiration time
     *
     * @param token token to be checked
     * @return returns whether the token has expired.
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Generates a token for the given user
     *
     * @param user user to have token generated for
     * @return generated token.
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        return doGenerateToken(claims, user.getId().toString());
    }

    /**
     * Generates a token for the given user
     *
     * @param user user to have token generated for
     * @return generated token.
     */
    public String generateToken(User user, Long expirationTime) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        return doGenerateToken(claims, user.getUsername(), expirationTime);
    }

    /**
     * Generates a JWT
     *
     * @param claims  Map of values to be stored in the token
     * @param subject Subject of the token (Which user it refers to)
     * @return Signed JWT valid for a set duration
     */
    private String doGenerateToken(Map<String, Object> claims, String subject, Long customExpirationTime) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + customExpirationTime))
                .signWith(key).compact();
    }

    /**
     * Generates a JWT
     *
     * @param claims  Map of values to be stored in the token
     * @param subject Subject of the token (Which user it refers to)
     * @return Signed JWT valid for a set duration
     */
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY_MILLIS))
                .signWith(key).compact();
    }

    /**
     * Determines whether the given token is valid
     *
     * @param token token to be verified
     * @param user  user for the token to be checked against
     * @return whether the token passes validation
     */
    public Boolean validateToken(String token, User user) {
        final Long tokeUserId = getUserIdFromToken(token);
        return (tokeUserId.equals(user.getId()) && !isTokenExpired(token));
    }

    /**
     * Returns the user that sent the current request to this endpoint from the token
     *
     * @return User that sent the request
     */
    public User getRequestingUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
