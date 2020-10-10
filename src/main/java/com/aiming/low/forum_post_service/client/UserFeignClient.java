package com.aiming.low.forum_post_service.client;

import com.aiming.low.forum_post_service.entity.ImmutableUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.List;

@FeignClient("forum-user-service")
public interface UserFeignClient {
    @GetMapping(value = "/user/list", consumes = "application/json")
    List<ImmutableUser> searchUserListByUserIdSet(@RequestParam("userId") HashSet<Long> userId);
}
