package com.aiming.low.forum_post_service.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sun.istack.internal.Nullable;
import org.immutables.value.Value;

import java.util.Date;

/**
 * @ClassName Tag
 * @Description
 * @Author aiminglow
 */
@Value.Immutable
@JsonSerialize(as = ImmutableTag.class)
@JsonDeserialize(as = ImmutableTag.class)
public interface Tag {
    @Nullable Long tagId();
    @Nullable Long createUserId();

    @Nullable String tagName();

    // 如果创建tag的用户在别人使用了之后修改这个字段，会造成其他帖子错误的分类
    @Nullable Date createTime();
    // 虽然这里有“删除时间”的域，但是我不应该给普通用户删除标签（tag）的权利，哪怕是他们创建的。
    // 删除的操作也会对已经使用了这个标签的帖子造成影响
    @Nullable Date deleteTime();


    @Nullable ThreeStatus tagStatus();
}
