package com.aiming.low.forum_post_service.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sun.istack.internal.Nullable;
import org.immutables.value.Value;

import java.util.Date;

/**
 * @ClassName SideBar
 * @Description
 * @Author aiminglow
 */
@Value.Immutable
@JsonSerialize(as = ImmutableSideBar.class)
@JsonDeserialize(as = ImmutableSideBar.class)
public interface SideBar {
    /**
     * 目前sideBar只有一种功能——展示不同主题的帖子列表
     * 我不会涉及到使用帖子寻找它的“右侧sidebar的名称”，
     * 没有这种操作的话其实完全可以把其中的帖子id存储成用逗号隔开的id列表，不需要再建一个表
     */

    @Nullable Long sideBarId();

    @Nullable String sideBarTitle();
    @Nullable String sideBarPostList();

    @Nullable Date createTime();
    @Nullable Date lastModTime();
    @Nullable Date deleteTime();


    @Nullable ThreeStatus sideBarStatus();
}
