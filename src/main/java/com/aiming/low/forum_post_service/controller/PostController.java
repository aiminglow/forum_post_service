package com.aiming.low.forum_post_service.controller;

import com.aiming.low.forum_post_service.annotation.DbLogger;
import com.aiming.low.forum_post_service.client.UserFeignClient;
import com.aiming.low.forum_post_service.entity.ImmutablePost;
import com.aiming.low.forum_post_service.entity.ImmutableUser;
import com.aiming.low.forum_post_service.page.PageResult;
import com.aiming.low.forum_post_service.service.impl.PostServiceImpl;
import com.github.pagehelper.PageInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * @ClassName PostController
 * @Description
 * @Author aiminglow
 */
@RestController
@Validated
@RequestMapping("/post")
public class PostController {

    private final PostServiceImpl postService;
    private final UserFeignClient userFeignClient;

    public PostController(PostServiceImpl postService, UserFeignClient userFeignClient) {
        this.postService = postService;
        this.userFeignClient = userFeignClient;
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addPost(@RequestParam Long userId,
                                         @RequestParam String postTitle) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updatePost(@RequestParam ImmutablePost post) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    /**
     * 查询最新创建的post列表
     *
     * 既然pageHelper的插件可以把totalPageNum等信息查询出来，那么我其实可以可以把这个信息也传到前端去，
     * 因为前端还是需要展示总页数的。所以传往前端的map除了post，user对象，还有一个paging的对象。
     */
    @GetMapping("/list/{pageNum}")
    // 这个@DbLogger只在测试DbLogAspectIT.dbLogger_PostController_getPostList_pageNum1()的时候使用，平时不用时注解掉
    // @DbLogger("查询post列表")
    public PageResult getPostList(@PathVariable
                                  @NotNull
                                  @Positive(message = "pageNum {javax.validation.constraints.Positive.message} but you give ${validatedValue}")
                                  int pageNum) {
        PageInfo<ImmutablePost> postPageInfo = postService.selectPostByPage(pageNum, 20);
        PageResult pageResult = PageResult.fromPageInfo(postPageInfo);

        List<ImmutablePost> posts = postPageInfo.getList();
        HashMap<String, List> content = new HashMap<>();
        content.put("posts", posts);
        pageResult.setContent(content);
        if (posts == null || posts.size() == 0) {
            return pageResult;
        }

        // use postUserId list to get user list from forum_user_service
        HashSet<Long> userIds = new HashSet<>();
        for (ImmutablePost post: posts) {
            userIds.add(post.postUserId());
        }
        if (userIds.size() == 0) {
            return pageResult;
        }
        List<ImmutableUser> users = userFeignClient.searchUserListByUserIdSet(userIds);
        content.put("users", users);
        return pageResult;
    }

    @GetMapping("/list")
    public PageResult getPostList() {
        return getPostList(1);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Void> getPostDetail(@PathVariable Long postId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
