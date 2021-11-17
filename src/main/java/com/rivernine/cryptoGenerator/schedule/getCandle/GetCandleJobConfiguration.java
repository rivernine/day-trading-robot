package com.rivernine.cryptoGenerator.schedule.getCandle;

import com.google.gson.JsonObject;
import com.rivernine.cryptoGenerator.schedule.getCandle.service.GetCandleService;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class GetCandleJobConfiguration {

  private final GetCandleService getCandleService;

  public JsonObject[] getCandlesJob(String market, String minutes, String count) {
    return getCandleService.getCandles(market, minutes, count);
  }

}
