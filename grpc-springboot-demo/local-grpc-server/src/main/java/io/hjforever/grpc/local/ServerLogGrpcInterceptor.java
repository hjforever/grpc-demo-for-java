package io.hjforever.grpc.local;

import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 请求响应日志记录
 * <p>
 * 当有中文时则输出8进制转义码(数据有点不直观)
 *
 * @author hjforever
 */
public class ServerLogGrpcInterceptor implements ServerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ServerLogGrpcInterceptor.class);


    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {

        logger.info("服务端接收客户端请求头为 : {}", headers);

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
            @Override
            public void sendMessage(RespT message) {
                logger.info("服务端响应数据为: {}", message);
                super.sendMessage(message);
            }

            @Override
            public void sendHeaders(Metadata headers) {
                logger.info("服务端响应头为 : {}", headers);
                super.sendHeaders(headers);
            }
        }, headers)) {
            @Override
            public void onMessage(ReqT message) {
                logger.info("服务端接收客户端请求数据为: {}", message);
                super.onMessage(message);
            }
        };

    }
}

