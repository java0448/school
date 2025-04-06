package ru.t1.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * Потребляет сообщения из темы "task-status" и отправляет уведомления.
     *
     * @param message сообщение, полученное из темы Kafka.
     * @throws ListenerExecutionFailedException если обработка сообщения завершилась ошибкой.
     */
    @KafkaListener(topics = "task-status", groupId = "group_id")
    public void consume(String message) {
        try {
            // Логирование полученного сообщения
            logger.info("Received message: {}", message);

            // Отправка уведомления
            notificationService.sendNotification(notificationEmail, "Task Status Update", message);
        } catch (MailAuthenticationException e) {
            logger.error("Mail authentication failed for message: {}", message, e);
            // Возможно, стоит пробросить это исключение, чтобы остановить обработку
            throw new ListenerExecutionFailedException("Mail authentication failed", e);
        } catch (RuntimeException e) {
            logger.error("Runtime exception occurred while processing message: {}", message, e);
            // Возможно, стоит пробросить это исключение, чтобы остановить обработку
            throw new ListenerExecutionFailedException("Runtime exception occurred", e);
        } catch (Exception e) {
            logger.error("Unexpected error processing message: {}", message, e);
            throw new ListenerExecutionFailedException("Unexpected error occurred", e);
        }
    }
}