package org.example.myshop.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class ItemDto {
    private Long id;
    private String itemName;
    private Integer price;
    private String itemDetail;
    private String sellStatCd;
    private LocalDateTime regTime;
    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public Integer getPrice() {
        return price;
    }
    public void setPrice(Integer price) {
        this.price = price;
    }
    public String getItemDetail() {
        return itemDetail;
    }
    public void setItemDetail(String itemDetail) {
        this.itemDetail = itemDetail;
    }
    public String getSellStatCd() {
        return sellStatCd;
    }
    public void setSellStatCd(String sellStatCd) {
        this.sellStatCd = sellStatCd;
    }
    public LocalDateTime getRegTime() {
        return regTime;
    }
    public void setRegTime(LocalDateTime regTime) {
        this.regTime = regTime;
    }
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

}
