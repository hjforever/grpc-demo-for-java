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

        UserReply userReply = UserReply.newBuilder().setUserId(userId).setName("mike" + userId).setAge(18).build();

        responseObserver.onNext(userReply);

        responseObserver.onCompleted();

    }


    @Override
    public StreamObserver<UserRequest> queryUserIds(final StreamObserver<UserReply> streamObserver) {

        return new StreamObserver<UserRequest>() {
            @Override
            public void onNext(UserRequest userRequest) {
                logger.info("user request is : {}", userRequest);
                /**
                 * 根据 userRequest 查询对应的 userReply
                 */
                UserReply userReply = UserReply.newBuilder()
                        .setName("mike"+userRequest.getUserId())
                        .setAge(18)
                        .setUserId(userRequest.getUserId())
                        .build();
                streamObserver.onNext(userReply);
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onCompleted() {
                streamObserver.onCompleted();
            }
        };
    }
}
