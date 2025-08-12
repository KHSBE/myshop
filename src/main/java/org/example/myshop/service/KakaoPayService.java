package org.example.myshop.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.myshop.config.KakaoPayConfig;
import org.example.myshop.dto.OrderDto;
import org.example.myshop.entity.Item;
import org.example.myshop.exception.OutOfStockException;
import org.example.myshop.repository.ItemRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KakaoPayService {

    private final KakaoPayConfig kakaoPayConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ItemRepository itemRepository;

    private String tid; // 결제 고유번호
    private String orderId; // 임시 주문 번호 저장

    public String kakaoPayReady(OrderDto orderDto, String email) {
        String url = "https://open-api.kakaopay.com/online/v1/payment/ready";

        // 상품 정보 조회
        Item item = itemRepository.findById(orderDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

        // 재고 검사
        if (item.getStockNumber() < orderDto.getCount()) {
            throw new OutOfStockException("상품의 재고가 부족 합니다. (현재 재고 수량:" + item.getStockNumber() + ")");
        }

        int totalAmount = item.getPrice() * orderDto.getCount();
        this.orderId = UUID.randomUUID().toString(); // 고유 주문번호 생성

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "SECRET_KEY " + kakaoPayConfig.getAdminKey());

        Map<String, Object> payload = new HashMap<>();
        payload.put("cid", kakaoPayConfig.getCid());
        payload.put("partner_order_id", orderId);
        payload.put("partner_user_id", email);
        payload.put("item_name", item.getItemName());
        payload.put("quantity", orderDto.getCount());
        payload.put("total_amount", totalAmount);
        payload.put("tax_free_amount", 0);
        payload.put("vat_amount", totalAmount / 10); // 부가세 10% 기준
        payload.put("approval_url", kakaoPayConfig.getApprovalUrl());
        payload.put("cancel_url", kakaoPayConfig.getCancelUrl());
        payload.put("fail_url", kakaoPayConfig.getFailUrl());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(url, entity, JsonNode.class);
            JsonNode json = response.getBody();

            tid = json.get("tid").asText();
            return json.get("next_redirect_pc_url").asText();
        } catch (Exception e) {
            throw new RuntimeException("카카오페이 결제 준비 실패", e);
        }
    }

    public String kakaoPayApprove(String pgToken, String email) {
        String url = "https://open-api.kakaopay.com/online/v1/payment/approve";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "SECRET_KEY " + kakaoPayConfig.getAdminKey());

        Map<String, Object> payload = new HashMap<>();
        payload.put("cid", kakaoPayConfig.getCid());
        payload.put("tid", tid);
        payload.put("partner_order_id", orderId); // kakaoPayReady에서 생성한 주문번호 사용
        payload.put("partner_user_id", email);
        payload.put("pg_token", pgToken);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            return response.getBody(); // 필요 시 JSON 파싱해서 승인 결과 확인 가능
        } catch (Exception e) {
            throw new RuntimeException("카카오페이 결제 승인 실패", e);
        }
    }
}
