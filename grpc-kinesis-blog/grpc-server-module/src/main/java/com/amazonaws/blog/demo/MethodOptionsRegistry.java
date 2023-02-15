package com.amazonaws.blog.demo;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;

import java.util.HashMap;
import java.util.Map;

class MethodOptionsRegistry {
    private final Map<String, DescriptorProtos.MethodOptions> options = new HashMap<>();

    void registerOptions(Descriptors.ServiceDescriptor sd) {
        for (Descriptors.MethodDescriptor md : sd.getMethods()) {
            String fqn = io.grpc.MethodDescriptor.generateFullMethodName(sd.getFullName(), md.getName());
            options.put(fqn, md.getOptions());
        }
    }

    public DescriptorProtos.MethodOptions get(String fullMethodName) {
        return options.get(fullMethodName);
    }
}
