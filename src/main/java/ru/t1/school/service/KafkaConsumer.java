package ru.t1.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.t1.school.dto.TaskStatusDTO;

/**
 * Сервис для потребления сообщений из Kafka и отправки уведомлений по электронной почте.
 */
@Service
public class KafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private final NotificationService notificationService;
    private final String notificationEmail;

    /**
     * Конструктор для внедрения зависимостей.
     *
     * @param notificationService сервис для отправки уведомлений.
     * @param notificationEmail   адрес электронной почты для отправки уведомлений.
     */
    @Autowired
    public KafkaConsumer(NotificationService notificationService, @Value("${notification.email}") String notificationEmail) {
        this.notificationService = notificationService;
        this.notificationEmail = notificationEmail;
    }

    /**
     * Потребляет сообщения из темы Kafka и отправляет уведомления.
     * <p>
     * Этот метод аннотирован как {@link KafkaListener}, что позволяет ему автоматически получать сообщения
     * из указанной темы Kafka. После получения сообщения оно логируется и асинхронно отправляется уведомление.
     * </p>
     *
     * @param taskStatusDTO сообщение, полученное из темы Kafka.
     * @param ack           объект для подтверждения обработки сообщения.
     */
    @KafkaListener(topics = "${kafka.topic.client}", groupId = "${kafka.group-id}")
    public void consume(TaskStatusDTO taskStatusDTO, Acknowledgment ack) {
        // Логирование полученного сообщения
        logger.info("Received message: {}", taskStatusDTO);

        try {
            // Асинхронная отправка уведомления
            sendNotificationAsync(taskStatusDTO);
        } catch (Exception e) {
            logger.error("Error while sending notification asynchronously for message: {}", taskStatusDTO, e);
        }

        // Подтверждение обработки сообщения
        ack.acknowledge();
    }

    /**
     * Асинхронно отправляет уведомление по электронной почте.
     * <p>
     * Этот метод аннотирован как {@link Async}, что позволяет ему выполняться в отдельном потоке.
     * Он использует {@link NotificationService} для отправки уведомления по электронной почте.
     * </p>
     *
     * @param taskStatusDTO объект {@link TaskStatusDTO}, содержащий информацию о задаче.
     */
    @Async
    public void sendNotificationAsync(TaskStatusDTO taskStatusDTO) {
        try {
            notificationService.sendNotification(notificationEmail, "Task Status Update", taskStatusDTO.getDescription());
        } catch (MailAuthenticationException e) {
            logger.error("Mail authentication failed for message: {}", taskStatusDTO, e);
        } catch (Exception e) {
            logger.error("Unexpected error sending notification for message: {}", taskStatusDTO, e);
        }
    }
}
