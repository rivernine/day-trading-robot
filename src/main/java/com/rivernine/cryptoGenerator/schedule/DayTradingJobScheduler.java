package com.rivernine.cryptoGenerator.schedule;

import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonObject;
import com.rivernine.cryptoGenerator.config.ScaleTradeStatusProperties;
import com.rivernine.cryptoGenerator.config.StatusProperties;
import com.rivernine.cryptoGenerator.domain.history.History;
import com.rivernine.cryptoGenerator.domain.order.Order;
import com.rivernine.cryptoGenerator.repository.HistoryRepository;
import com.rivernine.cryptoGenerator.repository.OrderRepository;
import com.rivernine.cryptoGenerator.schedule.getCandle.GetCandleJobConfiguration;
import com.rivernine.cryptoGenerator.schedule.orders.OrdersJobConfiguration;
import com.rivernine.cryptoGenerator.schedule.orders.dto.OrdersResponseDto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

  private final OrderRepository orderRepository;
  private final HistoryRepository historyRepository;
  private final StatusProperties statusProperties;
  private final ScaleTradeStatusProperties scaleTradeStatusProperties;
  private final GetCandleJobConfiguration getCandleJobConfiguration;
  // private final AnalysisForScaleTradingJobConfiguration
  // analysisForScaleTradingJobConfiguration;
  private final OrdersJobConfiguration ordersJobConfiguration;

  private Double boll = 0.0;
  private Double mfi = 0.0;

  public String checkBollinger(JsonObject[] objs) {
    Double sum = 0.0;
    Double avg = 0.0;
    Double var = 0.0;
    Double std = 0.0;

    for (JsonObject obj : objs)
      sum += obj.get("trade_price").getAsDouble();
    avg = sum / objs.length;
    for (JsonObject obj : objs)
      var += Math.pow(obj.get("trade_price").getAsDouble() - avg, 2);
    var = var / (objs.length);
    std = Math.sqrt(var);

    Double lo = avg - std * 2;
    Double me = avg;
    Double hi = avg + std * 2;
    Double pos = (objs[0].get("trade_price").getAsDouble() - lo) / (hi - lo);
    log.info("lo: " + String.format("%.0f", lo) + " me: " + String.format("%.0f", me) + " hi: "
        + String.format("%.0f", hi) + " pos: " + String.format("%.2f", pos));
    boll = Math.round(pos * 100) / 100.0;
    if (pos <= 0.1) {
      return "bid";
    } else if (pos >= 0.9) {
      return "ask";
    } else {
      return "none";
    }
  }

  public String checkMFI(JsonObject[] objs) {
    Double[] tpList = new Double[11];
    Double[] volList = new Double[11];

    for (int i = 0; i < objs.length; i++) {
      Double tp = (objs[i].get("high_price").getAsDouble() + objs[i].get("low_price").getAsDouble()
          + objs[i].get("trade_price").getAsDouble()) / 3;
      Double vol = objs[i].get("candle_acc_trade_volume").getAsDouble();
      tpList[i] = tp;
      volList[i] = vol;
    }

    Double positiveRMF = 0.0;
    Double negativeRMF = 0.0;
    for (int i = 0; i < objs.length - 2; i++) {
      Double tp = tpList[i];
      Double befTp = tpList[i + 1];
      if (tp > befTp)
        positiveRMF += tp * volList[i];
      else
        negativeRMF += tp * volList[i];
    }
    Double curMfi = positiveRMF / (positiveRMF + negativeRMF) * 100;

    positiveRMF = 0.0;
    negativeRMF = 0.0;
    for (int i = 1; i < objs.length - 1; i++) {
      Double tp = tpList[i];
      Double befTp = tpList[i + 1];
      if (tp > befTp)
        positiveRMF += tp * volList[i];
      else
        negativeRMF += tp * volList[i];
    }
    Double befMfi = positiveRMF / (positiveRMF + negativeRMF) * 100;
    log.info(curMfi.toString());
    mfi = Math.round(curMfi * 100) / 100.0;
    if (curMfi < 20 && curMfi - befMfi > 0) {
      return "bid";
    } else if (curMfi > 80) {
      return "ask";
    } else {
      return "none";
    }
  }

  @Scheduled(fixedDelay = 1000000)
  public void runGetMultipleCandlesJob() {
    String market = "KRW-BTC";
    JsonObject[] result = getCandleJobConfiguration.getCandlesJob(market, "10", "20");
    log.info("{}", result[0]);
    // log.info("boll: {}", checkBollinger(result));
    // log.info("mfi: {}", checkMFI(Arrays.copyOfRange(result, 0, 11)));
    String bollFlag = checkBollinger(result);
    String mfiFlag = checkMFI(Arrays.copyOfRange(result, 0, 11));
    Double price = result[0].get("trade_price").getAsDouble();

    Order basketOrder = orderRepository.getOrder();
    log.info("{}", basketOrder);
    if (basketOrder == null) {
      log.info("Bid step");
      if (bollFlag.equals("bid") && mfiFlag.equals("bid")) {
      // if (true) {
        try {
          OrdersResponseDto bidOrder = ordersJobConfiguration.bidJob(market, "10000");
          Order newOrder = new Order(bidOrder.getDate(), bidOrder.getUuid(), market,
                                      bidOrder.getState(), boll, mfi, price, null);
          log.info("{}", newOrder);
          orderRepository.insertOrder(newOrder);
        } catch (Exception e) {
          log.info(e.getMessage());
        }
      }
    } else {
      log.info("Ask step");
      // if (true) {
      if (bollFlag.equals("ask") || mfiFlag.equals("ask")) {
        try {
          OrdersResponseDto upbitOrder = ordersJobConfiguration.getOrderJob(basketOrder.getUuid());
          Double volume = upbitOrder.getExecuted_volume();
          OrdersResponseDto askOrder = ordersJobConfiguration.askJob(market, volume.toString());
          log.info("upbitOrder {}", upbitOrder);
          log.info("askOrder {}", askOrder);
          History history = new History(askOrder.getDate(), market, volume, basketOrder.getBoll(), basketOrder.getMfi(), basketOrder.getPrice(), 
                                        boll, mfi, price, ((price / basketOrder.getPrice()) - 1) * 100);
          orderRepository.deleteOrder();
          log.info("history {}", history);
          historyRepository.insertHistory(history);
        } catch (Exception e) {
          log.info(e.getMessage());
        }
      }
    }

  }
}
