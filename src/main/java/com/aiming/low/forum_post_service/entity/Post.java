package com.aiming.low.forum_post_service.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sun.istack.internal.Nullable;
import org.immutables.value.Value;

import java.util.Date;

/**
 * @ClassName Post
 * @Description
 * @Author aiminglow
 */
@Value.Immutable
@JsonSerialize(as = ImmutablePost.class)
@JsonDeserialize(as = ImmutablePost.class)
public interface Post {
    @Nullable Long postId();

    @Nullable Long postUserId();

    @Nullable String postTitle();

    // 这个数据经常变化，可以分表出去进行优化；
    // 也可以用MQ，每个人点击之后不用即时更新数据，这个数据对及时性要求很低
    @Nullable Integer clickNumber();
    // 这个数据也算变化的比较频繁，但是比上一个会少很多。优化方法同上。
    @Nullable Integer replyNumber();

    @Nullable Date lastReplyTime();
    @Nullable Long lastReplyUserId();

    // 帖子暂时没有“修改”操作，所以不需要“最后修改时间”
    // 如果要添加修改操作，也必须能够让其他人看到修改的历史，不然可能会有讨论的内容前后不一致的情况。
    @Nullable Date createTime();
    @Nullable Date deleteTime();


    @Nullable ThreeStatus postStatus();
}
