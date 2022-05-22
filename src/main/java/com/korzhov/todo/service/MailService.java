package com.korzhov.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
  private final JavaMailSender mailSender;

  @Value("${spring.mail.username}")
  private String emailFrom;

  public void sendMessage(String to, String subject, String message) {
    SimpleMailMessage mailMessage = new SimpleMailMessage();
    mailMessage.setFrom(emailFrom);
    mailMessage.setTo(to);
    mailMessage.setSubject(subject);
    mailMessage.setText(message);
    try {
      mailSender.send(mailMessage);
      log.debug("Sending email from... {},\r\n subject: \r\n{}", emailFrom, subject);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }
}
