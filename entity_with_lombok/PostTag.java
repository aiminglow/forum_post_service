package com.aiming.low.forum_post_service.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import java.util.Date;

/**
 * @ClassName PostTag
 * @Description
 * @Author aiminglow
 */
@Value
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder(toBuilder = true)
public class PostTag {
    // 如果不需要主键约束的话，需要一个自增主键的字段，目前这个类还没有这个字段
    private long postId;
    private long tagId;

    // 帖子（post）和标签（tag）的对应关系目前不支持更改
    private Date createTime;
    private Date deleteTime;


    private ThreeStatus postTagStatus;
}
