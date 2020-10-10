package com.aiming.low.forum_post_service.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import java.util.Date;

/**
 * @ClassName Comment
 * @Description 虽然这个类叫做Comment，但是它可以存储三种类型的数据，分别为帖子正文，帖子追加内容，评论内容
 * @Author aiminglow
 */
@Value
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder(toBuilder = true)
public class Comment {
    private long commentId;
    private long postId;
    private long commentUserId;

    private String commentContent;

    /**
     * floorNumber: 此条评论的楼层数
     * 如果楼层是0的话，那么这一条就是帖子的正文内容或者正文的追加内容；一个帖子只能有一个正文内容，可以有很多条追求加内容。
     * 具体是帖子的正文内容还是正文的追加内容，可以看createTime，创建的最早的肯定是正文内容，后面的都是追加内容。
     * 另一种区分的方法是看commentStatus的状态，他可以表示这一条是正文内容还是追加内容
     */
    private int floorNumber;

    // replyFloorNumber: 此条评论回复的楼层数
    // 如果回复的楼层是0的话，那么就是对这个帖子内容的回复；如果是N的话，那么就是对N层楼的回复
    private int replyFloorNumber;

    /**
     * createTime: 此条内容创建时间
     * 为了防止用户发布内容后通过修改内容的方式欺骗其他用户，所以正文、追加内容、评论没有“修改”操作，
     * 用户最多可以删除自己发布的这些内容，所以不需要“最后修改时间”这个字段
     */
    private Date createTime;
    private Date deleteTime;

    public enum CommentStatus {
        /**
         * POST_CONTENT，ADDING_CONTENT，COMMENT分别表示帖子正文，帖子正文的追加内容，评论
         * ACTIVE，NOT_ACTIVE，DELETE 分别表示可见状态，不可见状态，删除状态
         * 以上两组状态组合后则为此条COMMENT的状态
         */
        POST_CONTENT_ACTIVE(11),
        POST_CONTENT_NOT_ACTIVE(10),
        POST_CONTENT_DELETE(-11),
        ADDING_CONTENT_ACTIVE(21),
        ADDING_CONTENT_NOT_ACTIVE(20),
        ADDING_CONTENT_DELETE(-21),
        COMMENT_ACTIVE(31),
        COMMENT_NOT_ACTIVE(30),
        COMMENT_DELETE(-31);

        private int value;

        CommentStatus(int value) {
            this.value = value;
        }

        public static CommentStatus fromValue(int value) {
            for (CommentStatus cs: CommentStatus.values()) {
                if (cs.value == value) {
                    return cs;
                }
            }
            return null;
        }

        public int getValue() {
            return value;
        }

    }

    private CommentStatus commentStatus;
}
