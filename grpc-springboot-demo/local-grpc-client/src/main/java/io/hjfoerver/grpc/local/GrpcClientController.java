package io.hjfoerver.grpc.local;

import io.hjforever.grpc.user.UserReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * test controller
 */
@RestController
public class GrpcClientController {

    @Autowired
    private GrpcClientService grpcClientService;

    @RequestMapping("/api/user")
    public String queryUserName(@RequestParam(defaultValue = "1") Long userId) {
        return grpcClientService.queryUserNameById(userId);
    }

    @RequestMapping("/api/users")
    public List<User> users(@RequestBody List<Long> userIds){
        return grpcClientService.queryUserByIds(userIds);
    }
}
