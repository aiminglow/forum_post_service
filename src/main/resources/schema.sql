-- create schema forum_microservice if not exists default character set utf8mb4;
use forum_microservice;

/*
下面的varchar写的比较大并不意味着controller层的字段校验也是按照这个限制进行的，
这里的限制都很宽。未来如果字段的限制放宽了，这里的表就不用修改了。
*/

-- 用户表
create table if not exists user (
    user_id bigint unsigned auto_increment,
    user_email varchar(63), -- 63 × 4 = 252 < 255 = 8-bit
    user_name varchar(63),
    user_password varchar(63),
    user_status tinyint,
    create_time int unsigned not null,
    last_mod_time int unsigned,
    delete_time int unsigned,
    primary key (user_id)
) engine=InnoDB default charset=utf8mb4;

create table if not exists admin (
    admin_id int unsigned auto_increment,
    admin_email varchar(63),
    admin_name varchar(63),
    admin_password varchar(63),
    admin_status tinyint,
    create_time int unsigned not null,
    last_mod_time int unsigned,
    delete_time int unsigned,
    primary key (admin_id)
) engine=InnoDB default charset=utf8mb4;

create table if not exists post (
    post_id bigint unsigned auto_increment,
    click_number int,
    last_reply_time int unsigned,
    last_reply_user_id int,
    -- varchar必须设定长度；charset=utf8mb4时，设定成16383是不行的，因为虽然满足了字段<65535的规则，
    -- 但是row size大于65535，所以也不能创建表，所以具体最多设定成多大还要看整行的大小。
    -- 这里可以不需要设定的那么大，15000就可以了。
    post_title varchar(15000),
    post_user_id bigint unsigned,
    reply_number int,
    post_status tinyint,
    create_time int unsigned not null,
    delete_time int unsigned,
    primary key (post_id)
) engine=InnoDB default charset=utf8mb4;

create table if not exists comment (
    comment_id bigint unsigned auto_increment,
    comment_user_id bigint unsigned,
    post_id bigint unsigned,
    comment_content varchar(15000),
    floor_number int unsigned,
    reply_floor_number int unsigned,
    comment_status tinyint,
    create_time int unsigned not null,
    delete_time int unsigned,
    primary key (comment_id)
) engine=InnoDB default charset=utf8mb4;

create table if not exists tag (
    tag_id bigint unsigned auto_increment,
    create_user_id bigint unsigned,
    tag_name varchar(63),
    tag_status tinyint,
    create_time int unsigned not null,
    delete_time int unsigned,
    primary key (tag_id)
) engine=InnoDB default charset=utf8mb4;

create table if not exists post_tag (
    post_id bigint unsigned,
    tag_id bigint unsigned,
    post_tag_status tinyint,
    create_time int unsigned not null,
    delete_time int unsigned,
    primary key (tag_id, post_id)
) engine=InnoDB default charset=utf8mb4;

create table if not exists side_bar (
    side_bar_id bigint unsigned auto_increment,
    side_bar_post_list varchar(15000),
    side_bar_title varchar(63),
    side_bar_status tinyint,
    create_time int unsigned not null,
    last_mod_time int unsigned,
    delete_time int unsigned,
    primary key (side_bar_id)
) engine=InnoDB default charset=utf8mb4;

create table if not exists db_log (
    log_id bigint unsigned auto_increment comment 'log id',
    opt_name varchar(63) comment '操作名称',
    user_id bigint unsigned comment '用户id',
    request_uri varchar(3000) comment '请求的uri',
    method varchar(3000) comment '请求访问的controller层方法',
    -- 如果是保存一个帖子的内容，那么方法的参数就会非常的大，所以需要能在注解中标注是否要保存这个字段？
    params varchar(3000) comment '被访问的方法的传入参数',
    ip varchar(63) comment '用户IP地址',
    user_agent varchar(3000) comment '浏览器UA，从request中的header拿到',
    error_msg varchar(3000) comment '报错信息',
    create_time int unsigned not null comment '日志触发时间',
    execute_time smallint unsigned comment '方法执行所用的时间，毫秒为单位；0~65535，给一个方法65秒的执行时间完全够用了',
    log_status tinyint comment '日志状态：1-成功； -1-失败',
    primary key (log_id)
    -- varchar每行长度最大65535bytes，所以全部设置成varchar(15000)是否会报错的
) engine=InnoDB DEFAULT CHARSET=utf8mb4;