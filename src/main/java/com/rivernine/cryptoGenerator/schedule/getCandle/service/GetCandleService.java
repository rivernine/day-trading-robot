package com.rivernine.cryptoGenerator.schedule.getCandle.service;

import java.time.LocalDateTime;

import com.google.gson.JsonObject;
import com.rivernine.cryptoGenerator.common.CryptoApi;
import com.rivernine.cryptoGenerator.config.ScaleTradeStatusProperties;
import com.rivernine.cryptoGenerator.schedule.getCandle.dto.CandleDto;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GetCandleService {

  private final ScaleTradeStatusProperties scaleTradeStatusProperties;
  private final CryptoApi cryptoApi;

  public JsonObject[] getCandles(String market, String minutes, String count) {
    return cryptoApi.getCandles(market, minutes, count);
  }
}
