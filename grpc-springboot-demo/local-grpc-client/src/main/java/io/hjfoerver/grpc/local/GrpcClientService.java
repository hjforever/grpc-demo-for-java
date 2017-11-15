package io.hjfoerver.grpc.local;

import io.grpc.Channel;
import io.grpc.ClientInterceptors;
import io.grpc.Metadata;
import io.hjforever.grpc.user.UserGrpc;
import io.hjforever.grpc.user.UserReply;
import io.hjforever.grpc.user.UserRequest;
import net.devh.springboot.autoconfigure.grpc.client.GrpcClient;
import org.springframework.stereotype.Service;

/**
 * @author hjforever
 */
@Service
public class GrpcClientService {

    static final Metadata.Key<String> USERID_HEADER_KEY =
            Metadata.Key.of("user_id", Metadata.ASCII_STRING_MARSHALLER);

    @GrpcClient("local-grpc-server")
    private Channel serverChannel;

    public String queryUserNameById(Long userId) {

        /**
         *
         * 增加自定义拦截器
         *
         * 此处主要测试 自定义header 参数, 将 userId 放入 header 头里面
         */

        //注意 userid 为空的情况
        ClientHeaderGrpcInterceptor headerGrpcInterceptor = new ClientHeaderGrpcInterceptor(USERID_HEADER_KEY, String.valueOf(userId));

        //对channel增加拦截器
        Channel channel = ClientInterceptors.intercept(serverChannel, headerGrpcInterceptor);

        UserGrpc.UserBlockingStub stub = UserGrpc.newBlockingStub(channel);

        UserRequest userRequest = UserRequest.newBuilder().setUserId(userId).build();

        UserReply userReply = stub.queryUserById(userRequest);

        return userReply.getName();
    }
}
