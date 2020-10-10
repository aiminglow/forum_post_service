package com.aiming.low.forum_post_service.controller;

import com.aiming.low.forum_post_service.client.UserFeignClient;
import com.aiming.low.forum_post_service.entity.ImmutablePost;
import com.aiming.low.forum_post_service.entity.ImmutableUser;
import com.aiming.low.forum_post_service.entity.ThreeStatus;
import com.aiming.low.forum_post_service.page.PageResult;
import com.aiming.low.forum_post_service.service.impl.PostServiceImpl;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostControllerUT {
    private PostServiceImpl postService = Mockito.mock(PostServiceImpl.class);
    private UserFeignClient userFeignClient = Mockito.mock(UserFeignClient.class);
    private PostController postController = new PostController(postService, userFeignClient);

    @Test
    void addPost() {
    }

    @Test
    void deletePost() {
    }

    @Test
    void updatePost() {
    }

    /**
     * 目前PostController里面的获得user list信息的功能还没做出来，如果做出来了我是否能够对其进行mock呢？如果可以mock，
     * 那么这个单元测试就还可以保留。
     * 如果不能mock user service的服务的话，我就只能进行集成测试了，这个单元测试的代码也要删除了，因为无法测试通过。
     */
    @Test
    void getPostList() {
        List<ImmutablePost> posts = new ArrayList<>(20);
        ImmutablePost post = ImmutablePost.builder()
                .postId(1L).clickNumber(12).lastReplyTime(new Date((long) 1600771743 * 1000))
                .lastReplyUserId(Long.valueOf(123)).postTitle("post title").postUserId(321L)
                .replyNumber(3).postStatus(ThreeStatus.ACTIVE)
                .createTime(new Date((long) 1600771562 * 1000))
                .build();
        posts.add(post);
        for (int i = 2; i <= 20; i++) {
            posts.add(post.withPostId((long) i));
        }
        PageInfo<ImmutablePost> pageInfo = new PageInfo<>(posts);
        pageInfo.setPageNum(1);
        pageInfo.setPageSize(20);
        pageInfo.setSize(20);
        pageInfo.setPages(3);
        pageInfo.setTotal(45);

        HashSet<Long> userIds = new HashSet<>();
        userIds.add(321L);
        List<ImmutableUser> users = new ArrayList<>();
        users.add(ImmutableUser.builder().userId(321L).userName("aiming").build());

        // mock
        when(postService.selectPostByPage(1, 20))
                .thenReturn(pageInfo);
        when(userFeignClient.searchUserListByUserIdSet(userIds))
                .thenReturn(users);

        PageResult pageResult = postController.getPostList(1);

        verify(postService, atLeastOnce()).selectPostByPage(1, 20);
        assertEquals(1, pageResult.getPageNum());
        assertEquals(20, pageResult.getPageSize());
        assertEquals(20, pageResult.getCurrPageSize());
        assertEquals(3, pageResult.getTotalPages());
        assertEquals(45, pageResult.getTotalSize());
        verify(userFeignClient, times(1)).searchUserListByUserIdSet(userIds);

        HashMap<String, List> map = pageResult.getContent();
        List<ImmutablePost> actual = map.get("posts");
        assertEquals(20, actual.size());
        assertEquals(1L, actual.get(0).postId());
        List<ImmutableUser> actualUsers = map.get("users");
        assertEquals(1, actualUsers.size());
        assertEquals(321L, actualUsers.get(0).userId());
    }

    @Test
    void getPostDetail() {
    }
}