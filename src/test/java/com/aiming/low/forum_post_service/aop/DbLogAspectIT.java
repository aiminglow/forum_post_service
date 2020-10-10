package com.aiming.low.forum_post_service.aop;

import com.aiming.low.forum_post_service.client.UserFeignClient;
import com.aiming.low.forum_post_service.config.KafkaProducerConfig;
import com.aiming.low.forum_post_service.config.KafkaTopicConfig;
import com.aiming.low.forum_post_service.controller.PostController;
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
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 测试@DbLogger注解以及与其相关的aop代码（DbLogAspect.java）是否有效
 * 这个类和PostControllerITMockMvc的测试环境类似，因为我们把注解放在了controller层的方法上面
 * 这个测试类除了需要controller层参与还需要aop和kafka的参与，所以这两方面的@Configuration和@Component也需要import
 */

@WebMvcTest(controllers = PostController.class)
@AutoConfigureMybatis
@ActiveProfiles("devutit")
@ComponentScan({"com.aiming.low.forum_post_service.aop", "com.aiming.low.forum_post_service.mq"})
// 因为要使用到aop，所以一定不要忘记了AopAutoConfiguration这个配置
@ImportAutoConfiguration({KafkaAutoConfiguration.class, AopAutoConfiguration.class})
@Import({KafkaProducerConfig.class, KafkaTopicConfig.class})
class DbLogAspectIT {
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
    void dbLogger_PostController_getPostList_pageNum1() throws Exception {
        PageResult pageResult = mockServiceAndFeignClient();
        HashMap<String, Object> sessionAttr = new HashMap<>(1);
        sessionAttr.put("userId", 321L);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.USER_AGENT, "ua");

        MvcResult mvcResult = mockMvc.perform(get("/post/list/{pageNum}", 1L)
                .sessionAttrs(sessionAttr)   // 如果没有设置session，DbLogAspect内用session获取attribute的部分会报错
                .headers(headers)
                .contentType("application/json"))
                .andExpect(status().isOk()).andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(pageResult));
    }
}