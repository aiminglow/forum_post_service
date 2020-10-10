package com.aiming.low.forum_post_service.mq;

import com.aiming.low.forum_db_log_service.entity.ImmutableDbLog;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import static com.aiming.low.forum_post_service.config.KafkaTopicConfig.DB_LOG_TOPIC;

/**
 * @ClassName KafkaDbLogProducer
 * @Description
 * @Author aiminglow
 */
@Component
public class KafkaDbLogProducer {
    private final KafkaTemplate kafkaTemplate;

    public KafkaDbLogProducer(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendDbLog(String topicName, ImmutableDbLog dbLog) {
        kafkaTemplate.send(topicName, dbLog);
    }

    public void sendDbLog(@Header ImmutableDbLog dbLog) {
        sendDbLog(DB_LOG_TOPIC, dbLog);
    }
}
