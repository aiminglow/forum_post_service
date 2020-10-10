package com.aiming.low.forum_post_service.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sun.istack.internal.Nullable;
import org.immutables.value.Value;

import java.util.Date;

/**
 * @ClassName PostTag
 * @Description
 * @Author aiminglow
 */
@Value.Immutable
@JsonSerialize(as = ImmutablePostTag.class)
@JsonDeserialize(as = ImmutablePostTag.class)
public interface PostTag {
    // 如果不需要主键约束的话，需要一个自增主键的字段，目前这个类还没有这个字段
    @Nullable Long postId();
    @Nullable Long tagId();

    // 帖子（post）和标签（tag）的对应关系目前不支持更改
    @Nullable Date createTime();
    @Nullable Date deleteTime();


    @Nullable ThreeStatus postTagStatus();
}
