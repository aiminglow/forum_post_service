package com.aiming.low.forum_post_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

/**
 * @ClassName KafkaProducerConfig
 * @Description
 * @Author aiminglow
 */
@Configuration
public class KafkaProducerConfig {
    /**
     * 本来是需要注入ProducerFactory和KafkaTemplate的，但是现在已经在yml里面配置了key-value的序列化方式，
     * 而且我也不需要制定泛型为具体的类型，所以我需要的就是<?, ?>的上面两个对象，
     * 而这两个对象KafkaAutoConfiguration类已经帮我们自动注入了，不需要在这里重新注入一遍了。
     */
    /*@Bean
    public KafkaTemplate<?, ?> kafkaTemplate(ProducerFactory<?, ?> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }*/
}
