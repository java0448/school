package ru.t1.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Сервис для отправки уведомлений по электронной почте.
 */
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final JavaMailSender mailSender;
    private final String fromEmail;

    /**
     * Конструктор для внедрения зависимостей.
     *
     * @param mailSender объект для отправки электронной почты.
     * @param fromEmail  адрес электронной почты отправителя, полученный из конфигурационного файла.
     */
    @Autowired
    public NotificationService(JavaMailSender mailSender, @Value("${notification.email-from}") String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }

    /**
     * Отправляет уведомление по электронной почте.
     *
     * @param to      адрес электронной почты получателя.
     * @param subject тема письма.
     * @param text    текст письма.
     * @throws MailAuthenticationException если аутентификация почты не удалась.
     * @throws RuntimeException если отправка письма не удалась.
     */
    public void sendNotification(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (MailAuthenticationException e) {
            logger.error("Mail authentication failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Failed to send email: {}", e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
}