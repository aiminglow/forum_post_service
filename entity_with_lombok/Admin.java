package com.aiming.low.forum_post_service.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.Date;

/**
 * @ClassName Admin
 * @Description
 * @Author aiminglow
 */
@Value
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder(toBuilder = true)
public class Admin {
    private long adminId;
    private String adminEmail;
    private String adminName;
    private String adminPassword;

    private Date createTime;
    private Date lastModTime;
    private Date deleteTime;

    private ThreeStatus adminStatus;
}
