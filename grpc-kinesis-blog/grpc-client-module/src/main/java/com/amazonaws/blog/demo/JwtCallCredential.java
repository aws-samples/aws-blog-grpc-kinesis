package com.amazonaws.blog.demo;

import io.grpc.CallCredentials;
import io.grpc.Metadata;

import java.util.concurrent.Executor;

public class JwtCallCredential extends CallCredentials {
    private final String jwt;

    public JwtCallCredential(String jwt) {
        super();
        this.jwt = jwt;
    }

    @Override
    public void applyRequestMetadata(RequestInfo requestInfo, Executor executor, MetadataApplier metadataApplier) {
        executor.execute(() -> {
            try {
                Metadata headers = new Metadata();
                Metadata.Key<String> jwtKey = Metadata.Key.of("Authorization",Metadata.ASCII_STRING_MARSHALLER);
                headers.put(jwtKey, "Bearer "+ jwt);
                metadataApplier.apply(headers);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
    }

    public void thisUsesUnstableApi() {
    }
}
