package com.aiming.low.forum_post_service.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import java.util.Date;

/**
 * @ClassName SideBar
 * @Description
 * @Author aiminglow
 */
@Value
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder(toBuilder = true)
public class SideBar {
    /**
     * 目前sideBar只有一种功能——展示不同主题的帖子列表
     * 我不会涉及到使用帖子寻找它的“右侧sidebar的名称”，
     * 没有这种操作的话其实完全可以把其中的帖子id存储成用逗号隔开的id列表，不需要再建一个表
     */

    private long sideBarId;

    private String sideBarTitle;
    private String sideBarPostList;

    private Date createTime;
    private Date lastModTime;
    private Date deleteTime;


    private ThreeStatus sideBarStatus;
}
