package io.hjforever.grpc.local;


import io.grpc.stub.StreamObserver;
import io.hjforever.grpc.user.UserGrpc;
import io.hjforever.grpc.user.UserReply;
import io.hjforever.grpc.user.UserRequest;
import net.devh.springboot.autoconfigure.grpc.server.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * user service
 */
@GrpcService(UserGrpc.class)
public class GrpcServerService extends UserGrpc.UserImplBase {

    Logger logger = LoggerFactory.getLogger(GrpcServerService.class);

    @Override
    public void queryUserById(UserRequest userRequest, StreamObserver<UserReply> responseObserver) {

        logger.info("请求参数为: {}", userRequest);

        Long userId = userRequest.getUserId();

        UserReply userReply = UserReply.newBuilder().setUserId(userId).setName("小明" + userId).setAge(18).build();

        responseObserver.onNext(userReply);

        responseObserver.onCompleted();

    }
}
