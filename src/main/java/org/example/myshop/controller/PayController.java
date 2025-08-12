package org.example.myshop.controller;

import lombok.RequiredArgsConstructor;
import org.example.myshop.dto.OrderDto;
import org.example.myshop.entity.Order;
import org.example.myshop.repository.OrderRepository;
import org.example.myshop.service.KakaoPayService;
import org.example.myshop.service.OrderService;
import org.example.myshop.service.SlackMessageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pay")
public class PayController {

    private final KakaoPayService kakaoPayService;
    private final OrderService orderService;
    private final SlackMessageService slackMessageService;
    private final OrderRepository orderRepository;


    @PostMapping("/ready")
    @ResponseBody
    public String kakaoPay(@RequestBody OrderDto orderDto, Principal principal, HttpSession session) {
        String email = principal.getName();
        session.setAttribute("orderDto", orderDto); // 결제 성공 시 사용할 주문 정보 저장
        return kakaoPayService.kakaoPayReady(orderDto, email); // 결제 URL 문자열 반환
    }

    @GetMapping("/success")
    public String kakaoPaySuccess(@RequestParam("pg_token") String pgToken, Principal principal, HttpSession session) {
        String email = principal.getName();

        // 1. 결제 승인 요청
        String approveResult = kakaoPayService.kakaoPayApprove(pgToken, email);
        System.out.println("카카오 결제 승인 결과: " + approveResult);

        // 2. 세션에서 주문 정보 꺼내기
        OrderDto orderDto = (OrderDto) session.getAttribute("orderDto");
        if (orderDto == null) {
            throw new IllegalStateException("주문 정보가 존재하지 않습니다.");
        }

        // 3. 실제 주문 저장
        long order_id = orderService.order(orderDto, email);
        Order order = orderRepository.findById(order_id)
                .orElseThrow(() -> new IllegalArgumentException("주문 조회 실패"));


        int totalPrice = order.getTotalPrice();

        String message = String.format(
                "[✔ 결제 성공!] 💳 5조 - 사용자 %s님이 %d원 결제 완료! (주문번호: %d)",
                order.getMember().getEmail(),
                totalPrice,
                order.getId()
        );
        slackMessageService.sendSlackMessage(message);

        System.out.println("[슬랙 전송 예정 메시지] " + message);


        // 4. 세션에서 삭제
        session.removeAttribute("orderDto");

        return "redirect:/orders";
    }

    @GetMapping("/cancel")
    public String cancelPage() {
        return "결제취소화면";
    }

    @GetMapping("/fail")
    public String failPage() {
        return "결제실패화면";
    }
}
