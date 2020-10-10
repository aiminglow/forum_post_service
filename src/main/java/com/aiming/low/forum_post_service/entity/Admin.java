package com.aiming.low.forum_post_service.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sun.istack.internal.Nullable;
import org.immutables.value.Value;

import java.util.Date;

/**
 * @ClassName Admin
 * @Description
 * @Author aiminglow
 */
@Value.Immutable
@JsonSerialize(as = ImmutableAdmin.class)
@JsonDeserialize(as = ImmutableAdmin.class)
public interface Admin {
    @Nullable Long adminId();
    @Nullable String adminEmail();
    @Nullable String adminName();
    @Nullable String adminPassword();

    @Nullable Date createTime();
    @Nullable Date lastModTime();
    @Nullable Date deleteTime();

    @Nullable ThreeStatus adminStatus();
}
