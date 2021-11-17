package com.rivernine.cryptoGenerator.common;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import com.google.gson.JsonObject;
import com.rivernine.cryptoGenerator.common.service.CryptoApiService;
import com.rivernine.cryptoGenerator.schedule.orders.dto.OrdersResponseDto;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CryptoApi {

  private final CryptoApiService cryptoApiService;

  public JsonObject getMarket(String market) {    
    return cryptoApiService.getMarket(market);
  }

  public JsonObject[] getCandles(String market, String minutes, String count){
    return cryptoApiService.getCandles(market, minutes, count);
  }

  public Double getPrice(String market) {
    return cryptoApiService.getPrice(market);
  }

  public JsonObject getOrdersChanceForBid(String market) throws NoSuchAlgorithmException, UnsupportedEncodingException{
    return cryptoApiService.getOrdersChanceForBid(market);
  }

  public JsonObject getOrdersChanceForAsk(String market) throws NoSuchAlgorithmException, UnsupportedEncodingException{
    return cryptoApiService.getOrdersChanceForAsk(market);
  }

  public OrdersResponseDto getOrder(String uuid) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    return cryptoApiService.getOrder(uuid);
  }

  public OrdersResponseDto deleteOrder(String uuid) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    return cryptoApiService.deleteOrder(uuid);
  }

  // 지정가 매수
  public OrdersResponseDto bid(String market, String volume, String price) throws NoSuchAlgorithmException, UnsupportedEncodingException{
    return cryptoApiService.bid(market, volume, price);
  }

  // 지정가 매도
  public OrdersResponseDto ask(String market, String volume, String price) throws NoSuchAlgorithmException, UnsupportedEncodingException{
    return cryptoApiService.ask(market, volume, price);
  }

  // 시장가 매수
  public OrdersResponseDto bid(String market, String price) throws NoSuchAlgorithmException, UnsupportedEncodingException{
    return cryptoApiService.bid(market, price);
  }

  // 시장가 매도
  public OrdersResponseDto ask(String market, String volume) throws NoSuchAlgorithmException, UnsupportedEncodingException{
    return cryptoApiService.ask(market, volume);
  }
}
