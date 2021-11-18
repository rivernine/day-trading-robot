package com.rivernine.cryptoGenerator.schedule;

import java.util.List;

import com.google.gson.JsonObject;
import com.rivernine.cryptoGenerator.config.ScaleTradeStatusProperties;
import com.rivernine.cryptoGenerator.config.StatusProperties;
import com.rivernine.cryptoGenerator.schedule.analysisForScaleTrading.AnalysisForScaleTradingJobConfiguration;
import com.rivernine.cryptoGenerator.schedule.getCandle.GetCandleJobConfiguration;
import com.rivernine.cryptoGenerator.schedule.orders.OrdersJobConfiguration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ch.qos.logback.core.joran.conditional.ElseAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class DayTradingJobScheduler {

  @Value("${upbit.markets}")
  private List<String> markets;
  @Value("${upbit.candleMinutes}")
  private String candleMinutes;

  private final StatusProperties statusProperties;
  private final ScaleTradeStatusProperties scaleTradeStatusProperties;

  private final GetCandleJobConfiguration getCandleJobConfiguration;
  private final AnalysisForScaleTradingJobConfiguration analysisForScaleTradingJobConfiguration;
  private final OrdersJobConfiguration ordersJobConfiguration;

  public Boolean checkBollinger(JsonObject[] objs) {
    Double sum = 0.0;
    Double avg = 0.0;
    Double var = 0.0;
    Double std = 0.0;

    for (JsonObject obj: objs)
      sum += obj.get("trade_price").getAsDouble();
    avg = sum / objs.length;
    for (JsonObject obj: objs)
      var += Math.pow(obj.get("trade_price").getAsDouble() - avg, 2);
    var = var / (objs.length);
    std = Math.sqrt(var);

    Double lo = avg - std * 2;
    Double me = avg;
    Double hi = avg + std * 2;
    Double pos = (objs[0].get("trade_price").getAsDouble() - lo) / (hi - lo);
    log.info("lo: " + String.format("%.0f", lo) + " me: " + String.format("%.0f", me) + " hi: " + String.format("%.0f", hi) + " pos: " + String.format("%.2f", pos));
    
    return (pos <= 0.1) ? true : false;
  }

  // public Double calculateMFI(JsonObject[] objs){
  //   for(JsonObject obj: objs) {

  //   }
  // }

  // public Boolean checkMFI(JsonObject[] objs){

  // }

  @Scheduled(fixedDelay = 1000)
  public void runGetMultipleCandlesJob() {
    for(String market: markets) {
      JsonObject[] result = getCandleJobConfiguration.getCandlesJob(market, "5", "20");
      log.info("{}", result[0]);
      System.out.println(checkBollinger(result));
    }
  }
}
