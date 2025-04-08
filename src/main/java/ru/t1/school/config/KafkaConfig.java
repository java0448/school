package ru.t1.school.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;
import ru.t1.school.dto.TaskStatusDTO;

import java.util.HashMap;
import java.util.Map;

/**
 * Конфигурация Kafka для настройки продюсеров, консьюмеров и топиков.
 */
@EnableKafka
@Configuration
public class KafkaConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.group-id}")
    private String groupId;

    @Value("${kafka.error-handler.interval}")
    private long interval;

    @Value("${kafka.error-handler.max-attempts}")
    private long maxAttempts;

    @Value("${kafka.session-timeout-ms}")
    private int sessionTimeoutMs;

    @Value("${kafka.max-partition-fetch-bytes}")
    private int maxPartitionFetchBytes;

    @Value("${kafka.max-poll-records}")
    private int maxPollRecords;

    @Value("${kafka.max-poll-interval-ms}")
    private int maxPollIntervalMs;

    @Value("${kafka.enable-auto-commit}")
    private boolean enableAutoCommit;

    @Value("${kafka.auto-offset-reset}")
    private String autoOffsetReset;

    @Value("${kafka.topic.client}")
    private String taskStatusTopic;

    @Value("${kafka.topic.num-partitions}")
    private int numPartitions;

    @Value("${kafka.topic.replication-factor}")
    private short replicationFactor;

    @Value("${kafka.producer.acks}")
    private String acks;

    /**
     * Создает фабрику продюсеров Kafka.
     *
     * @return фабрика продюсеров Kafka.
     */
    @Bean
    public ProducerFactory<String, TaskStatusDTO> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, acks);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Создает шаблон Kafka для отправки сообщений.
     *
     * @return шаблон Kafka.
     */
    @Bean
    public KafkaTemplate<String, TaskStatusDTO> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * Создает фабрику контейнеров слушателей Kafka.
     *
     * @return фабрика контейнеров слушателей Kafka.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TaskStatusDTO> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TaskStatusDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }

    /**
     * Создает фабрику консьюмеров Kafka.
     *
     * @return фабрика консьюмеров Kafka.
     */
    @Bean
    public ConsumerFactory<String, TaskStatusDTO> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class.getName());
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, TaskStatusDTO.class.getName());
        configProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeoutMs);
        configProps.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, maxPartitionFetchBytes);
        configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        configProps.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollIntervalMs);
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    /**
     * Создает новый топик Kafka.
     *
     * @return новый топик Kafka.
     */
    @Bean
    public NewTopic topic() {
        return new NewTopic(taskStatusTopic, numPartitions, replicationFactor);
    }

    /**
     * Создает обработчик ошибок для Kafka.
     *
     * @return обработчик ошибок для Kafka.
     */
    @Bean
    public DefaultErrorHandler errorHandler() {
        FixedBackOff fixedBackOff = new FixedBackOff(interval, maxAttempts);
        return new DefaultErrorHandler(fixedBackOff);
    }
}