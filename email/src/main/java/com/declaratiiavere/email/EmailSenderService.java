package com.declaratiiavere.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Service for sending emails.
 *
 * @author Razvan Dani
 */
@Service
@PropertySource("classpath:email.properties")
public class EmailSenderService {
    @Value("${smtp.host}")
    private String smtpHost;

    @Value("${smtp.port}")
    private String smtpPort;

    @Value("${smtp.userName}")
    private String smtpUserName;

    @Value("${smtp.fromAddress}")
    private String fromAddress;

    @Value("${smtp.password}")
    private String smtpPassword;

    public void sendEmail(String from, String to, String subject, String body) throws MessagingException {
        Properties props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.connectiontimeout", "7000");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");

        Session session = Session.getDefaultInstance(props);

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));

        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

        msg.setSubject(subject);
//        msg.setText(body);
        msg.setContent(body, "text/html; charset=utf-8");

        Transport transport = session.getTransport();
        transport.connect(smtpHost, smtpUserName, smtpPassword);

        transport.sendMessage(msg, msg.getAllRecipients());
    }

    public void sendEmail(String to, String subject, String body) throws MessagingException {
        sendEmail(fromAddress, to, subject, body);
    }
}
