package org.example.myshop.service;

import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class SlackMessageService {
    private final String token = "xoxb-8283513953266-9170246296419-LV94VTOJHw0YPp7RS2C40CAw";
    private final String channelId = "C08JZLANKAA"; // 슬랙 채널 ID

    public void sendSlackMessage(String text) {
        String urlStr = "https://slack.com/api/chat.postMessage";
        String payload = String.format("{\"channel\":\"%s\", \"text\":\"%s\"}", channelId, text);

        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Authorization", "Bearer " + token);

            conn.getOutputStream().write(payload.getBytes("UTF-8"));

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            if (conn.getResponseCode() == 200 && response.toString().contains("\"ok\":true")) {
                System.out.println("슬랙 메시지 발송 성공");
            } else {
                System.out.println("슬랙 메시지 발송 실패");
                System.out.println(response.toString());
            }
            conn.disconnect();
        } catch (IOException e) {
            System.out.println("슬랙 메시지 전송 중 에러: " + e.getMessage());
        }
    }
}