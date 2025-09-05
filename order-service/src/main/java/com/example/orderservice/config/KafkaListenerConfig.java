package com.example.orderservice.config;

import com.example.orderservice.event.OrderCreatedEvent;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaListenerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    private Map<String, Object> baseConsumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.*");
        // Do not use type headers; we will fix the target type per-factory
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        return props;
    }

    @Bean
    public ConsumerFactory<String, OrderCreatedEvent> orderEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>(baseConsumerProps());
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, OrderCreatedEvent.class.getName());
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> orderEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderEventConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, Long> longConsumerFactory() {
        Map<String, Object> props = new HashMap<>(baseConsumerProps());
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, Long.class.getName());
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Long> longKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Long> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(longConsumerFactory());
        return factory;
    }
}


