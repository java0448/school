package ru.t1.school.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@TestPropertySource("classpath:application.yml")
public class NotificationServiceTest implements AutoCloseable {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationService notificationService;

    @Value("${notification.email-from}")
    private String fromEmail;

    @Value("${notification.email}")
    private String toEmail;

    private AutoCloseable mocks;

    @BeforeEach
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        notificationService = new NotificationService(mailSender, fromEmail);
    }

    @Test
    @DisplayName("Успешная отправка уведомления")
    public void testSendNotificationSuccess() {
        // Подготовка тестовых данных
        String subject = "Test Subject";
        String text = "Test Text";

        // Тестирование проверяемой функциональности
        notificationService.sendNotification(toEmail, subject, text);

        // Проверка полученных результатов
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals(fromEmail, sentMessage.getFrom());
        assertEquals(toEmail, Objects.requireNonNull(sentMessage.getTo())[0]);
        assertEquals(subject, sentMessage.getSubject());
        assertEquals(text, sentMessage.getText());
    }

    @Test
    @DisplayName("Ошибка аутентификации при отправке уведомления")
    public void testSendNotificationMailAuthenticationException() {
        // Подготовка тестовых данных
        String subject = "Test Subject";
        String text = "Test Text";

        // Моделирование исключения MailAuthenticationException
        doThrow(new MailAuthenticationException("Authentication failed")).when(mailSender).send(any(SimpleMailMessage.class));

        // Тестирование проверяемой функциональности и проверка исключения
        MailAuthenticationException exception = assertThrows(MailAuthenticationException.class, () -> {
            notificationService.sendNotification(toEmail, subject, text);
        });

        // Проверка полученных результатов
        assertEquals("Authentication failed", exception.getMessage());
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Ошибка при отправке уведомления")
    public void testSendNotificationRuntimeException() {
        // Подготовка тестовых данных
        String subject = "Test Subject";
        String text = "Test Text";

        // Моделирование исключения RuntimeException
        doThrow(new RuntimeException("Send failed")).when(mailSender).send(any(SimpleMailMessage.class));

        // Тестирование проверяемой функциональности и проверка исключения
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            notificationService.sendNotification(toEmail, subject, text);
        });

        // Проверка полученных результатов
        assertEquals("Failed to send email", exception.getMessage());
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Override
    public void close() throws Exception {
        mocks.close();
    }
}