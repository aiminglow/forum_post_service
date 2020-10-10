package com.aiming.low.forum_post_service.mq;

import com.aiming.low.forum_db_log_service.entity.DbLog;
import com.aiming.low.forum_db_log_service.entity.ImmutableDbLog;
import com.aiming.low.forum_post_service.config.KafkaProducerConfig;
import com.aiming.low.forum_post_service.config.KafkaTopicConfig;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

@ActiveProfiles("devutit")
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan("com.aiming.low.forum_post_service.mq")
@ImportAutoConfiguration({KafkaAutoConfiguration.class})
@Import({KafkaProducerConfig.class, KafkaTopicConfig.class})
//@SpringBootTest
class KafkaDbLogProducerIT {
    @Autowired
    KafkaDbLogProducer kafkaDbLogProducer;

    @Test
    void sendDbLog_10dbLog() {
        ImmutableDbLog dbLog = ImmutableDbLog.builder().ip("192.168.31.6")
                .createTime(new Date()).method("PostController.getPostList")
                .params("pageNum=1").errorMsg("error message").executeTime((short) 128)
                .optName("查询post列表").userId(123L).userAgent("ua")
                .requestUri("/post/list/1").logStatus(DbLog.LogStatus.FAILURE).build();

        for (int i = 1; i <= 500; i++) {
            kafkaDbLogProducer.sendDbLog(dbLog);
        }
    }
}