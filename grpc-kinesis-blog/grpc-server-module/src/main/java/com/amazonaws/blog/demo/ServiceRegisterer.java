package com.amazonaws.blog.demo;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import io.grpc.protobuf.ProtoFileDescriptorSupplier;

class ServiceRegisterer {
    private final ServerBuilder sb;
    private final MethodOptionsRegistry reg;

    public ServiceRegisterer(ServerBuilder sb) {
        this.sb = sb;
        this.reg = new MethodOptionsRegistry();
    }

    public void registerService(ServerServiceDefinition ssd) {
        // register service with the ServerBuilder
        sb.addService(ssd);
        // and also record all options
        io.grpc.ServiceDescriptor sd = ssd.getServiceDescriptor();
        ProtoFileDescriptorSupplier fds = (ProtoFileDescriptorSupplier) (sd.getSchemaDescriptor());
        reg.registerOptions(fds.getFileDescriptor().findServiceByName(sd.getName()));
    }

    public void registerService(BindableService svc) {
        registerService(svc.bindService());
    }

    public MethodOptionsRegistry getOptionsRegistry() {
        return reg;
    }
}

