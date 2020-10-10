package com.aiming.low.forum_post_service.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sun.istack.internal.Nullable;
import org.immutables.value.Value;

import java.util.Date;

/**
 * @ClassName User
 * @Description
 * @Author aiminglow
 */
@Value.Immutable
@JsonSerialize(as = ImmutableUser.class)
@JsonDeserialize(as = ImmutableUser.class)
public interface User {
    @Nullable Long userId();
    @Nullable String userEmail();
    @Nullable String userName();
    @Nullable String userPassword();

    @Nullable Date createTime();
    @Nullable Date lastModTime();
    @Nullable Date deleteTime();

    @Nullable ThreeStatus userStatus();
}
