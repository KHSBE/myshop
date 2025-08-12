package org.example.myshop.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.myshop.entity.OrderItem;

@Getter
@Setter
public class OrderItemDto {
    public OrderItemDto(OrderItem orderItem, String imgUrl){
        this.itemName = orderItem.getItem().getItemName();
        this.count = orderItem.getCount();
        this.orderPrice = orderItem.getOrderPrice();
        this.imgUrl = imgUrl;
    }
    private String itemName; //상품명
    private int count; //주문 수량
    private int orderPrice; //주문 금액
    private String imgUrl; //상품 이미지 경로
}
