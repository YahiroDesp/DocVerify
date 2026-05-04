package kz.docverify.config;

import kz.docverify.kafka.DocumentEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic documentUploadedTopic() {
        return TopicBuilder.name("document.uploaded").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic documentValidatedTopic() {
        return TopicBuilder.name("document.validated").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic documentEventsLogTopic() {
        return TopicBuilder.name("document.events.log").partitions(1).replicas(1).build();
    }
}
