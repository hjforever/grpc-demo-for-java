package io.hjfoerver.grpc.local;

import io.grpc.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 自定义客户端请求头拦截器
 * <p>
 * 功能和 MetadataUtils 中  newAttachHeadersInterceptor 功能类似 , 只是增加,不会覆盖之前的请求头.
 *
 * @author hjforever
 */
public class ClientHeaderGrpcInterceptor implements ClientInterceptor {

    private List<Metadata> metadataList = new ArrayList<>();

    public ClientHeaderGrpcInterceptor(Metadata metadata) {
        super();
        this.metadataList.add(metadata);
    }

    public ClientHeaderGrpcInterceptor(Metadata... headers) {
        super();
        this.metadataList.addAll(Arrays.asList(headers));
    }

    public ClientHeaderGrpcInterceptor(List<Metadata> headers) {
        super();
        this.metadataList.addAll(headers);
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                metadataList.forEach(metadata -> headers.merge(metadata));
                metadataList.clear();
                super.start(responseListener, headers);
            }
        };
    }
}
