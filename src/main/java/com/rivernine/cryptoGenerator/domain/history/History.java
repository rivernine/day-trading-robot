package com.rivernine.cryptoGenerator.domain.history;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class History {
  private String date; 
  private String market;
  private Double volume;
  private Double bid_boll;
  private Double bid_mfi;
  private Double bid_price;
  private Double ask_boll;
  private Double ask_mfi;
  private Double ask_price;
  private Double pnl;

  public History(String date, String market, Double volume, Double bid_boll, Double bid_mfi, Double bid_price, Double ask_boll, Double ask_mfi, Double ask_price, Double pnl) {
    this.date = date;
    this.market = market;
    this.volume = volume;
    this.bid_boll = bid_boll;
    this.bid_mfi = bid_mfi;
    this.bid_price = bid_price;
    this.ask_boll = ask_boll;
    this.ask_mfi = ask_mfi;
    this.ask_price = ask_price;
    this.pnl = pnl;
  }
}
