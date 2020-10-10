package com.aiming.low.forum_post_service.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import java.util.Date;

/**
 * @ClassName Tag
 * @Description
 * @Author aiminglow
 */
@Value
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder(toBuilder = true)
public class Tag {
    private long tagId;
    private long createUserId;

    private String tagName;

    // 如果创建tag的用户在别人使用了之后修改这个字段，会造成其他帖子错误的分类
    private Date createTime;
    // 虽然这里有“删除时间”的域，但是我不应该给普通用户删除标签（tag）的权利，哪怕是他们创建的。
    // 删除的操作也会对已经使用了这个标签的帖子造成影响
    private Date deleteTime;


    private ThreeStatus tagStatus;
}
