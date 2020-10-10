package com.aiming.low.forum_post_service.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.Date;

/**
 * @ClassName User
 * @Description
 * @Author aiminglow
 */
@Value
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder(toBuilder = true)
public class User {
    private long userId;
    private String userEmail;
    private String userName;
    private String userPassword;

    private Date createTime;
    private Date lastModTime;
    private Date deleteTime;

    private ThreeStatus userStatus;
}
