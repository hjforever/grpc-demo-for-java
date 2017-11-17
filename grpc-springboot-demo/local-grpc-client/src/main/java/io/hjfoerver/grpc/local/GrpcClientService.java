package io.hjfoerver.grpc.local;

import io.grpc.Channel;
import io.grpc.ClientInterceptors;
import io.grpc.Metadata;
import io.grpc.stub.StreamObserver;
import io.hjforever.grpc.user.UserGrpc;
import io.hjforever.grpc.user.UserReply;
import io.hjforever.grpc.user.UserRequest;
import net.devh.springboot.autoconfigure.grpc.client.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

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

    public List<User> queryUserByIds(List<Long> ids) {

        UserGrpc.UserStub asyncStub = UserGrpc.newStub(serverChannel);

        List<User> userReplyList = new ArrayList<>();

        //只有当流结束或者发生异常时才终止,不然就一直等待，可以在调用时判断时间防止一直等待
        final CountDownLatch finishLatch = new CountDownLatch(1);

        StreamObserver<UserRequest> userRequestStreamObserver = asyncStub.queryUserIds(new StreamObserver<UserReply>() {

            @Override
            public void onNext(UserReply userReply) {
                User user = new User();
                user.setUserId(userReply.getUserId());
                user.setUserName(userReply.getName());
                user.setAge(userReply.getAge());
                userReplyList.add(user);
            }

            @Override
            public void onError(Throwable t) {
                finishLatch.countDown();
            }

            @Override
            public void onCompleted() {
                finishLatch.countDown();
            }
        });

        /**
         * 设置 UserRequest 请求流
         */
        for (Long id : ids) {
            UserRequest userRequest = UserRequest.newBuilder().setUserId(id).build();
            userRequestStreamObserver.onNext(userRequest);
        }
        userRequestStreamObserver.onCompleted();

        /**
         * 阻塞直到结束，建议加上超时时间 eg : finishLatch.await(1,TimeUnit.SECONDS)
         */
        try {
            finishLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return userReplyList;
    }


}
