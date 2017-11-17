package io.hjfoerver.grpc.local;

import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端请求响应日志记录
 *
 * 当有中文时则输出8进制转义码(数据有点不直观)
 *
 * @author hjforever
 */
public class ClientLogGrpcInterceptor implements ClientInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ClientLogGrpcInterceptor.class);

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {

        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {

                logger.info("客户端发送请求头为 : {}",headers);

                delegate().start(new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {

                    @Override
                    public void onMessage(RespT message) {
                        logger.info("客户端接收服务端响应数据为 : {}",message);
                        super.onMessage(message);
                    }

                    @Override
                    public void onHeaders(Metadata headers) {
                        logger.info(" 客户端接收服务端响应头 : {}",headers);
                        super.onHeaders(headers);
                    }
                }, headers);
            }

            @Override
            public void sendMessage(ReqT message) {
                logger.info("客户端发送请求数据为 : {}", message);
                delegate().sendMessage(message);
            }

        };
    }
}
