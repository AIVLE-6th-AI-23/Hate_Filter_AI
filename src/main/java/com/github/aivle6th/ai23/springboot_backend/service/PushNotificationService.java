package com.github.aivle6th.ai23.springboot_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PushNotificationService {

    @Value("${vapid.public-key}")
    private String publicKey;

    @Value("${vapid.private-key}")
    private String privateKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sendPushNotification(Subscription subscription, String body) throws Exception {
        if (subscription == null) {
            System.out.println("푸시 구독 정보가 없습니다.");
            return;
        }

        PushService pushService = new PushService();
        pushService.setPublicKey(publicKey);
        pushService.setPrivateKey(privateKey);

        Notification notification = new Notification(subscription.endpoint, subscription.keys.p256dh, subscription.keys.auth, 
                        objectMapper.writeValueAsString(new PushMessage("Title", body)));

        HttpResponse response = pushService.send(notification);
        System.out.println("푸시 알림 전송 상태: " + response.getStatusLine());
    }

    static class PushMessage {
        public String title;
        public String body;

        public PushMessage(String title, String body) {
            this.title = title;
            this.body = body;
        }
    }
}
