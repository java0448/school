package ru.t1.school.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.t1.school.dto.TaskStatusDTO;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@TestPropertySource("classpath:application.yml")
public class KafkaConsumerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private KafkaConsumer kafkaConsumer;

    private TaskStatusDTO taskStatusDTO;

    @Value("${notification.email}")
    private String notificationEmail;

    @BeforeEach
    public void setUp() {
        taskStatusDTO = new TaskStatusDTO(1L, "NEW", "Задача создана");
        kafkaConsumer = new KafkaConsumer(notificationService, notificationEmail);
    }

    @Test
    @DisplayName("Успешное потребление сообщения и отправка уведомления")
    public void testConsumeWhenValidMessageThenNotificationSentAndAcknowledged() {
        // Подготовка тестовых данных
        doNothing().when(notificationService).sendNotification(anyString(), anyString(), anyString());

        // Тестирование проверяемой функциональности
        kafkaConsumer.consume(taskStatusDTO, acknowledgment);

        // Проверка полученных результатов
        verify(notificationService).sendNotification(eq(notificationEmail), eq("Task Status Update"), eq("Задача создана"));
        verify(acknowledgment).acknowledge();
    }

    @Test
    @DisplayName("Потребление сообщения при возникновении исключения и подтверждение")
    public void testConsumeWhenExceptionThrownThenAcknowledged() {
        // Подготовка тестовых данных
        doThrow(new RuntimeException("Unexpected error")).when(notificationService).sendNotification(anyString(), anyString(), anyString());

        // Тестирование проверяемой функциональности
        kafkaConsumer.consume(taskStatusDTO, acknowledgment);

        // Проверка полученных результатов
        verify(acknowledgment).acknowledge();
    }

    @Test
    @DisplayName("Асинхронная отправка уведомления при валидном сообщении")
    public void testSendNotificationAsyncWhenValidMessageThenNotificationSent() {
        // Подготовка тестовых данных
        doNothing().when(notificationService).sendNotification(anyString(), anyString(), anyString());

        // Тестирование проверяемой функциональности
        kafkaConsumer.sendNotificationAsync(taskStatusDTO);

        // Проверка полученных результатов
        verify(notificationService).sendNotification(eq(notificationEmail), eq("Task Status Update"), eq("Задача создана"));
    }

    @Test
    @DisplayName("Асинхронная отправка уведомления при MailAuthenticationException")
    public void testSendNotificationAsyncWhenMailAuthenticationExceptionThrownThenLogged() {
        // Подготовка тестовых данных
        doThrow(new MailAuthenticationException("Authentication failed")).when(notificationService).sendNotification(anyString(), anyString(), anyString());

        // Тестирование проверяемой функциональности
        kafkaConsumer.sendNotificationAsync(taskStatusDTO);

        // Проверка полученных результатов
        verify(notificationService).sendNotification(eq(notificationEmail), eq("Task Status Update"), eq("Задача создана"));
    }

    @Test
    @DisplayName("Асинхронная отправка уведомления при неожиданном исключении")
    public void testSendNotificationAsyncWhenUnexpectedExceptionThrownThenLogged() {
        // Подготовка тестовых данных
        doThrow(new RuntimeException("Unexpected error")).when(notificationService).sendNotification(anyString(), anyString(), anyString());

        // Тестирование проверяемой функциональности
        kafkaConsumer.sendNotificationAsync(taskStatusDTO);

        // Проверка полученных результатов
        verify(notificationService).sendNotification(eq(notificationEmail), eq("Task Status Update"), eq("Задача создана"));
    }
}