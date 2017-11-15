package io.hjforever.grpc.user;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * @author hjforever
 */
public class UserClient {

    private static final Logger logger = LoggerFactory.getLogger(UserClient.class);

    private final ManagedChannel channel;

    //阻塞
    private final UserGrpc.UserBlockingStub blockingStub;
    //异步
    private final UserGrpc.UserStub asyncStub;


    public UserClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                /**
                 *
                 * 此处将设为文本连接,只用于测试
                 *
                 */
                .usePlaintext(true)
                .build());
    }


    UserClient(ManagedChannel channel) {
        this.channel = channel;
        blockingStub = UserGrpc.newBlockingStub(channel);
        asyncStub = UserGrpc.newStub(channel);
    }


    public static void main(String[] args) throws Exception {

        UserClient client = new UserClient("localhost", 50051);
        try {
            client.queryUserByIds();
            client.queryUserByName();
        } finally {
            client.shutdown();
        }
    }


    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void queryUserByIds() {
        UserRequest userRequest = UserRequest.newBuilder().setUserId(1L).build();
        Iterator<UserReply> users = blockingStub.queryUserByIds(userRequest);
        for (int i = 0; users.hasNext(); i++) {
            UserReply userReply = users.next();
            logger.info("user id is : {} , user name is :{},user age is {}", userReply.getUserId(), userReply.getName(), userReply.getAge());
        }
    }

    public void queryUserByName() throws InterruptedException {

        //只有当流结束或者发生异常时才终止,不然就一直等待，可以在调用时判断时间防止一直等待
        final CountDownLatch finishLatch = new CountDownLatch(1);

        StreamObserver<UserRequest> userRequestStreamObserver = asyncStub.queryUserByName(new StreamObserver<UserReply>() {

            @Override
            public void onNext(UserReply value) {
                logger.info("get user id is :{} , name is :{} , age is :{}", value.getUserId(), value.getName(), value.getAge());
            }

            @Override
            public void onError(Throwable t) {
                logger.error("error:", t);
                finishLatch.countDown();
            }

            @Override
            public void onCompleted() {
                logger.info("query user by name finished!!");
                finishLatch.countDown();
            }
        });

        /**
         * 设置 UserRequest 请求流
         */
        for (int i = 0; i < 10; i++) {
            UserRequest userRequest = UserRequest.newBuilder().setUserId(i + 1).setName("user" + i).build();
            userRequestStreamObserver.onNext(userRequest);
        }
        userRequestStreamObserver.onCompleted();

        /**
         * 阻塞直到结束，建议加上超时时间 eg : finishLatch.await(1,TimeUnit.SECONDS)
         */
        finishLatch.await();

    }

}
