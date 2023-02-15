package com.amazonaws.blog.demo;

import com.google.common.base.Strings;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

public class AuthService {

    final static private String issuerUrl = System.getenv("COGNITO_ENDPOINT");
    public static boolean authorize(String bearerToken)  {
        // brief bearer token validation
        if (Strings.isNullOrEmpty(bearerToken) || !bearerToken.startsWith("Bearer")) {
            System.out.println(bearerToken);
            return false;
        }
        final List<String> splitToken = Arrays.asList(bearerToken.trim().split("\\s+"));

        if (splitToken.size() != 2) {
            splitToken.listIterator().forEachRemaining(System.out::println);
            return false;
        }

        final String authKey = splitToken.get(1);

        System.out.println(authKey);

        ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();

        JWKSource keySource = null;
        try {
            keySource = new RemoteJWKSet(new URL(issuerUrl));

        } catch ( MalformedURLException e) {
            e.printStackTrace();
        }

        JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256;

        JWSKeySelector keySelector = new JWSVerificationKeySelector(expectedJWSAlg, keySource);
        jwtProcessor.setJWSKeySelector(keySelector);

        SecurityContext ctx = null;
        JWTClaimsSet claimsSet = null;
        try {
            claimsSet = jwtProcessor.process(authKey, ctx);
            return true;
        } catch (ParseException | BadJOSEException | JOSEException e) {
            e.printStackTrace();
            return false;
        }
    }
}
