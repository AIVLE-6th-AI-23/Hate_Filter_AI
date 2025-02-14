package com.github.aivle6th.ai23.springboot_backend.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.github.aivle6th.ai23.springboot_backend.dto.AnalysisCompleteRequestDTO;
import com.github.aivle6th.ai23.springboot_backend.dto.UserVerifyRequestDto;
import com.github.aivle6th.ai23.springboot_backend.util.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {
    
    private final JavaMailSender mailSender;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Value("${server.fe.url}")
    private String frontServerUrl;
    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendContentAnalysisNotificationEmail(String toEmail, AnalysisCompleteRequestDTO analysisResult) {
        String postUrl = frontServerUrl + "/boards/" + analysisResult.getBoardId() + "/posts/" + analysisResult.getPostId(); 

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("[NERO] 📊 콘텐츠 분석이 완료 되었습니다!");
        message.setText(
            "안녕하세요,\n\n"+
            "요청하신 콘텐츠 분석이 완료되었습니다. 아래의 분석 결과를 확인해 주세요.\n\n"+
            analysisResult.getResultSummary()+"\n"+
            "🔗 세부 분석 결과 보기: " + postUrl + "\n\n"+
            "추가 문의 사항이 있으시면 언제든지 문의해 주세요.\n\n"+
            "감사합니다.\n"+
            "- 콘텐츠 분석팀 -"
        );
        mailSender.send(message);
    }

    public void sendPasswordResetEmail(UserVerifyRequestDto userVerifyRequestDto){
        // jwt token
        String token = jwtTokenProvider.generateToken(userVerifyRequestDto.getEmployeeId());
        // reset link
        String resetLink = frontServerUrl + "/reset-password?token=" + token;
            
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(userVerifyRequestDto.getEmail());
        message.setSubject("[Nero] 비밀 번호 재설정 ");
        message.setText("""
        안녕하세요, 

        비밀번호 재설정을 요청하셨나요? 아래 링크를 클릭하여 새 비밀번호를 설정해 주세요.
        
        🔗 비밀번호 재설정 링크: """ + resetLink +
        """ 
        
        이 요청을 수행하지 않았다면, 보안을 위해 비밀번호를 변경하지 말고 이 이메일을 삭제해주세요.  

        감사합니다.
        """
        );

        mailSender.send(message);
    }
}
