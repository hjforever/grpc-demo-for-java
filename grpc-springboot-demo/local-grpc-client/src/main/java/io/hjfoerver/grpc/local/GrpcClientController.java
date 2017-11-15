package io.hjfoerver.grpc.local;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * test controller
 */
@RestController
public class GrpcClientController {

    @Autowired
    private GrpcClientService grpcClientService;

    @RequestMapping("/api/user")
    public String printMessage(@RequestParam(defaultValue = "1") Long userId) {
        return grpcClientService.queryUserNameById(userId);
    }
}
