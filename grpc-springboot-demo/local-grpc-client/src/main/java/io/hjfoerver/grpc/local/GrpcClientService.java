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

    static final Metadata.Key<String> USERID_HEADER_KEY = Metadata.Key.of("user_id", Metadata.ASCII_STRING_MARSHALLER);

    @GrpcClient("local-grpc-server")
    private Channel serverChannel;

    public String queryUserNameById(Long userId) {

        Metadata metadata = new Metadata();
        metadata.put(USERID_HEADER_KEY, "" + userId);

        serverChannel = ClientInterceptors.intercept(serverChannel, new ClientHeaderGrpcInterceptor(metadata));

        UserGrpc.UserBlockingStub stub = UserGrpc.newBlockingStub(serverChannel);

        UserRequest userRequest = UserRequest.newBuilder().setUserId(userId).build();

        UserReply userReply = stub.queryUserById(userRequest);

        return userReply.getName();
    }
}
