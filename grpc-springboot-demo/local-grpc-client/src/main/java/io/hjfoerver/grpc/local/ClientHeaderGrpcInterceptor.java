package io.hjfoerver.grpc.local;

import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 自定义请求头拦截器
 *
 * @author hjforever
 */
public class ClientHeaderGrpcInterceptor implements ClientInterceptor {

    Logger logger = LoggerFactory.getLogger(ClientHeaderGrpcInterceptor.class);

    private Map<Metadata.Key<String>, String> customHeaders = new HashMap<>();

    public ClientHeaderGrpcInterceptor(Metadata.Key<String> keys, String value) {
        super();
        this.customHeaders.put(keys, value);
    }

    public ClientHeaderGrpcInterceptor(Map<Metadata.Key<String>, String> headers) {
        super();
        this.customHeaders.putAll(headers);
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                /**
                 * 设置自定义的 header , 客户端会将 header 带到服务端
                 */
                if (!customHeaders.isEmpty()) {
                    customHeaders.forEach((k, v) -> {
                        if (k != null && v != null) {
                            headers.put(k, v);
                        }
                    });
                }
                super.start(new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
                    @Override
                    public void onHeaders(Metadata headers) {
                        logger.info("接收服务端请求头为 : {}", headers);
                        super.onHeaders(headers);
                    }
                }, headers);
            }
        };
    }
}
