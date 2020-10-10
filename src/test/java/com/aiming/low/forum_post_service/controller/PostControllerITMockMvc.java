package com.aiming.low.forum_post_service.controller;

import com.aiming.low.forum_post_service.client.UserFeignClient;
import com.aiming.low.forum_post_service.entity.ImmutablePost;
import com.aiming.low.forum_post_service.entity.ImmutableUser;
import com.aiming.low.forum_post_service.entity.ThreeStatus;
import com.aiming.low.forum_post_service.page.PageResult;
import com.aiming.low.forum_post_service.service.impl.PostServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mybatis.spring.boot.test.autoconfigure.AutoConfigureMybatis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PostController.class)
// 这个配置其实和@MybatisTest的作用是一样的，最后都是注入了mybatis的sqlSessionFactory之类的Mapper接口需要的依赖，所以最后context才初始化成功
// 停掉数据库之后跑这个测试就失败了，说明这个配置需要现成的数据库，也确实给spring context添加了mybatis的bean
@AutoConfigureMybatis
class PostControllerITMockMvc {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostServiceImpl postService;
    @MockBean
    private UserFeignClient userFeignClient;

    PageResult mockServiceAndFeignClient() {
        // pageNum=1
        List<ImmutablePost> posts_1_20 = new ArrayList<>();
        ImmutablePost post1 = ImmutablePost.builder().postId(1L).postTitle("title")
                .postUserId(111L).postStatus(ThreeStatus.ACTIVE).build();
        posts_1_20.add(post1);
        posts_1_20.add(post1);
        posts_1_20.add(post1);
        posts_1_20.add(post1);
        PageInfo<ImmutablePost> pageInfo = new PageInfo<>(posts_1_20);
        pageInfo.setPageNum(1);
        pageInfo.setPageSize(20);
        pageInfo.setSize(4);
        pageInfo.setPages(1);
        pageInfo.setTotal(4);
        when(postService.selectPostByPage(1, 20)).thenReturn(pageInfo);

        HashSet<Long> userIds = new HashSet<>();
        userIds.add(111L);
        List<ImmutableUser> users = new ArrayList<>();
        users.add(ImmutableUser.builder().userId(321L).userName("aiming").build());
        when(userFeignClient.searchUserListByUserIdSet(userIds)).thenReturn(users);

        PageResult pageResult = PageResult.fromPageInfo(pageInfo);
        HashMap<String, List> content = new HashMap<>();
        content.put("posts", posts_1_20);
        content.put("users", users);
        pageResult.setContent(content);

        return pageResult;
    }

    @Test
    void getPostList_pageNum1_returnPage1() throws Exception {
        PageResult pageResult = mockServiceAndFeignClient();
        MvcResult mvcResult = mockMvc.perform(get("/post/list/{pageNum}", 1L)
                .contentType("application/json"))
                .andExpect(status().isOk()).andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(pageResult));

    }

    /**
     * 下面这个测试本以为要@Import({ValidatorConfig.class, ControllerFieldCheckHandler.class})才能起作用，最后发现根本不需要
     * 网上的 mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();也不需要
     *
     * 这里为什么会自动注入这两个组件呢？是因为@WebMvcTest扫描了其他包吗？
     */
    @Test
    // 只有对javax.validation.constraints包下的各种注解抛出的异常进行捕捉，才能最终让前端收到error message，不然前端只会受到500
    // 目前已添加这方面的配置(ControllerFieldCheckHandler)，其实就是给controller添加advice，也就是添加一个捕获上面的异常的AOP切点和对应的advice
    void getPostList_pageNumNegative1_Exception() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/post/list/{pageNum}", -1L)
                .contentType("application/json"))
                .andExpect(status().is(UNPROCESSABLE_ENTITY.value())).andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    // 目前没有捕获MethodArgumentTypeMismatchException的异常，所以这个测试没有通过。为了能够打包可以先注释掉这个方法
    // 未来会在ControllerFieldCheckHandler类中添加这部分功能
    @Test
    void getPostList_pageNumAbc_Exception() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/post/list/{pageNum}", "Abc")
                .contentType("application/json"))
                .andExpect(status().is(UNPROCESSABLE_ENTITY.value())).andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
    }
}