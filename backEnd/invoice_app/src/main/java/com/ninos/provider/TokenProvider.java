package com.ninos.provider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.ninos.model.UserPrinciple;
import com.ninos.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
@RequiredArgsConstructor
@Component
public class TokenProvider {

    private static final String COMPANY_NAME = "ninoos.com";
    private static final String CUSTOMER_MANAGEMENT_SERVICE = "CUSTOMER_MANAGEMENT_SERVICE";
    public static final String AUTHORITIES = "authorities";
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 1_800_000;   // 30 minutes
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 432_000_000;  // 5 days;
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";

    private final UserService userService;


    @Value("${jwt.secret}")
    private String secret;


    public String createAccessToken(UserPrinciple userPrinciple){
        return JWT.create()
                .withIssuer(COMPANY_NAME)
                .withAudience(CUSTOMER_MANAGEMENT_SERVICE)
                .withIssuedAt(new Date())
                .withSubject(userPrinciple.getUsername())
                .withArrayClaim(AUTHORITIES, getClaimsFromUser(userPrinciple))
                .withExpiresAt(new Date(currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }


    public String createRefreshToken(UserPrinciple userPrinciple){
        return JWT.create()
                .withIssuer(COMPANY_NAME)
                .withAudience(CUSTOMER_MANAGEMENT_SERVICE)
                .withIssuedAt(new Date())
                .withSubject(userPrinciple.getUsername())
                .withExpiresAt(new Date(currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }



    public String getSubject(String token, HttpServletRequest request){

       try {
           return getJWTVerifier().verify(token).getSubject();
       }
       catch (TokenExpiredException exception){
          request.setAttribute("expireMessage", exception.getMessage());
          throw exception;
       }
       catch (InvalidClaimException exception){
          request.setAttribute("invalidClaim", exception.getMessage());
          throw exception;
       }
       catch (Exception exception){
           throw exception;
       }

    }


    public List<GrantedAuthority>getAuthorities(String token) {
        String[] claims = getClaimsFromToken(token);
        return stream(claims).map(SimpleGrantedAuthority::new).collect(toList());
    }

    public Authentication getAuthentication(String email, List<GrantedAuthority> authorities, HttpServletRequest request){
        UsernamePasswordAuthenticationToken userPasswordAuthToken = new UsernamePasswordAuthenticationToken(userService.getUserByEmail(email), null,authorities);
        userPasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return userPasswordAuthToken;
    }

    public boolean isTokenValid(String email, String token){
        JWTVerifier verifier = getJWTVerifier();
        return StringUtils.isNotEmpty(email) && !isTokenExpired(verifier, token);
    }

    private boolean isTokenExpired(JWTVerifier verifier, String token) {
        Date expiration = verifier.verify(token).getExpiresAt();
        return expiration.before(new Date());
    }


    private String[] getClaimsFromUser(UserPrinciple userPrinciple) {
        // return a specific authority and convert it to String
        return userPrinciple.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new);
    }


    private String[] getClaimsFromToken(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
    }

    private JWTVerifier getJWTVerifier() {
        JWTVerifier verifier;
        try {
           Algorithm algorithm = Algorithm.HMAC512(secret);
           verifier = JWT.require(algorithm).withIssuer(COMPANY_NAME).build();
        }
        catch (JWTVerificationException exception){
            throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
        }
        return verifier;
    }


}
