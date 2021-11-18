package com.rivernine.cryptoGenerator.domain.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class Order {
  private String date; 
  private String uuid;
  private String market;
  private String state;
  private Double price;
  private Double volume;

  public Order(String date, String uuid, String market, String state, Double price, Double volume) {
    this.date = date;
    this.uuid = uuid;
    this.market = market;
    this.state = state;
    this.price = price;
    this.volume = volume;
  }
}
