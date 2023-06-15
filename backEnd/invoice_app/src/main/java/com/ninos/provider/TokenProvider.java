package com.ninos.provider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.ninos.model.UserPrinciple;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Date;

import static java.lang.System.currentTimeMillis;

@Component
public class TokenProvider {

    private static final String COMPANY_NAME = "ninoos.com";
    private static final String CUSTOMER_MANAGEMENT_SERVICE = "CUSTOMER_MANAGEMENT_SERVICE";
    public static final String AUTHORITIES = "authorities";
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 432_000_000;  // 5 days



    @Value("${jwt.secret}")
    private String secret;


    private String createAccessToken(UserPrinciple userPrinciple){
        String[] claims = getClaimsFromUser(userPrinciple);
        return JWT.create()
                .withIssuer(COMPANY_NAME)
                .withAudience(CUSTOMER_MANAGEMENT_SERVICE)
                .withIssuedAt(new Date())
                .withSubject(userPrinciple.getUsername())
                .withArrayClaim(AUTHORITIES, claims)
                .withExpiresAt(new Date(currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    private String[] getClaimsFromUser(UserPrinciple userPrinciple) {
        // return a specific authority and convert it to String
        return userPrinciple.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new);
    }


}
