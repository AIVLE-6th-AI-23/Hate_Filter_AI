package com.github.aivle6th.ai23.springboot_backend.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {
    
    private final JavaMailSender mailSender;

    public void sendContentAnalysisNotificationEmail(String toEmail, String analysisResult) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("📊 Content Analysis 완료 알림");
        message.setText("안녕하세요,\n\n분석이 완료되었습니다. 결과는 다음과 같습니다:\n\n" + analysisResult);

        mailSender.send(message);
    }
}
