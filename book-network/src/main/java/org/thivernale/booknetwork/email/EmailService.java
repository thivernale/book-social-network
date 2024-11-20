package org.thivernale.booknetwork.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    @Value("${application.email.from:admin@bsn.com}")
    private String from;

    @Async
    public void sendEmail(
        String to,
        String subject,
        EmailTemplateName emailTemplateName,
        Map<String, Object> properties
    ) throws MessagingException {
        String template = emailTemplateName != null ? emailTemplateName.name() : "confirm_email";

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
            mimeMessage,
            MimeMessageHelper.MULTIPART_MODE_MIXED,
            StandardCharsets.UTF_8.name()
        );

        Context context = new Context();
        context.setVariables(properties);

        helper.setSubject(subject);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setText(templateEngine.process(template, context), true);

        mailSender.send(mimeMessage);
    }
}
