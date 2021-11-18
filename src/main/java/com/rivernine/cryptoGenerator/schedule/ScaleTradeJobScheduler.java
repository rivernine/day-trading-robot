// package com.rivernine.cryptoGenerator.schedule;

// import java.util.List;

// import com.rivernine.cryptoGenerator.config.ScaleTradeStatusProperties;
// import com.rivernine.cryptoGenerator.config.StatusProperties;
// import com.rivernine.cryptoGenerator.schedule.analysisForScaleTrading.AnalysisForScaleTradingJobConfiguration;
// import com.rivernine.cryptoGenerator.schedule.getCandle.GetCandleJobConfiguration;
// import com.rivernine.cryptoGenerator.schedule.getCandle.dto.CandleDto;
// import com.rivernine.cryptoGenerator.schedule.orders.OrdersJobConfiguration;
// import com.rivernine.cryptoGenerator.schedule.orders.dto.OrdersChanceDto;
// import com.rivernine.cryptoGenerator.schedule.orders.dto.OrdersResponseDto;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// @RequiredArgsConstructor
// @Component
// public class ScaleTradeJobScheduler {

//   @Value("${upbit.markets}")
//   private List<String> markets;
//   @Value("${upbit.candleMinutes}")
//   private String candleMinutes;

//   private final StatusProperties statusProperties;
//   private final ScaleTradeStatusProperties scaleTradeStatusProperties;

//   private final GetCandleJobConfiguration getCandleJobConfiguration;
//   private final AnalysisForScaleTradingJobConfiguration analysisForScaleTradingJobConfiguration;
//   private final OrdersJobConfiguration ordersJobConfiguration;

//   @Scheduled(fixedDelay = 1000)
//   public void runGetMultipleCandlesJob() {
//     for(String market: markets) {
//       getCandleJobConfiguration.getCandlesJob(market, candleMinutes, "4");  
//     }
//   }

//   // @Scheduled(fixedDelay = 1000)
//   public void runScaleTradeJob() {
//     List<CandleDto> candles;
//     CandleDto lastCandle;
//     OrdersChanceDto orderChanceDtoForBid;
//     OrdersChanceDto orderChanceDtoForAsk;
//     OrdersResponseDto ordersBidResponseDto;
//     OrdersResponseDto ordersAskResponseDto;
//     OrdersResponseDto orderResponseDto;
//     OrdersResponseDto cancelOrderResponse;
//     int level;
//     String lastBidTime;
//     String uuid;
//     String askPrice;
//     String market = statusProperties.getMarket();

//     level = scaleTradeStatusProperties.getLevel();

//     try {
//       switch(statusProperties.getCurrentStatus()) {
//         case -1:  
//           // [ init step ]
//           lastCandle = analysisForScaleTradingJobConfiguration.getLastCandleJob(market);
//           log.info("[changeStatus: -1 -> 1] [select market step] ");
//           statusProperties.init();
//           scaleTradeStatusProperties.init();
//           statusProperties.setCurrentStatus(1);
//           // }
//           break;
//         case 0:
//           // [ crytpto-generator start step ]
//           log.info("[crypto-generator start step] ");
//           statusProperties.init();
//           scaleTradeStatusProperties.init();
//           log.info(scaleTradeStatusProperties.getBalancePerLevel().toString());
//           statusProperties.setCurrentStatus(1);
//           break;
//         case 1:
//           // [ select market step ]
//           for(String mkt: markets) {
//             log.info("< " + mkt + " >");
//             candles = analysisForScaleTradingJobConfiguration.getRecentCandlesJob(mkt, 3);
//             if(analysisForScaleTradingJobConfiguration.analysisCandlesJob(candles, 3)) {
//               log.info("It's time to bid!! My select : " + mkt);
//               log.info("[changeStatus: 1 -> 10] [bid step] ");
//               statusProperties.setMarket(mkt);
//               statusProperties.setCurrentStatus(10);
//               break;
//             }
//           }
//           break;
//         case 10:
//           // [ bid step ]
//           lastCandle = analysisForScaleTradingJobConfiguration.getLastCandleJob(market);
//           orderChanceDtoForBid = ordersJobConfiguration.getOrdersChanceForBidJob(market);
//           String bidBalance = scaleTradeStatusProperties.getBalancePerLevel().get(scaleTradeStatusProperties.getLevel());
//           Double myTotalBalance = Double.parseDouble(orderChanceDtoForBid.getBalance());
//           Double bidBalanced = Double.parseDouble(bidBalance);
//           if(myTotalBalance.compareTo(bidBalanced) != -1) {
//             Double endingPrice = lastCandle.getTradePrice();
//             String bidVolume = String.format("%.8f", bidBalanced / endingPrice);
            
//             ordersBidResponseDto = ordersJobConfiguration.bidJob(market, bidVolume, endingPrice.toString());
//             log.info(ordersBidResponseDto.toString());
//                                                                                                                                                                                                                                       
//           }
//           break;      
//         case 20:
//           // [ ask step ]
//           orderChanceDtoForAsk = ordersJobConfiguration.getOrdersChanceForAskJob(market);
//           log.info("orderChanceDtoForAsk: " + orderChanceDtoForAsk.toString());
//           if(Double.parseDouble(orderChanceDtoForAsk.getBalance()) * Double.parseDouble(orderChanceDtoForAsk.getAvgBuyPrice()) > 5000.0){
//             askPrice = analysisForScaleTradingJobConfiguration.getAskPriceJob(orderChanceDtoForAsk);
//             ordersAskResponseDto = ordersJobConfiguration.askJob(market, orderChanceDtoForAsk.getBalance(), askPrice);
//             if(ordersAskResponseDto.getSuccess()) {
//               log.info("[changeStatus: 20 -> 30] [wait step] ");
//               scaleTradeStatusProperties.addAskInfoPerLevel(ordersAskResponseDto);
//               scaleTradeStatusProperties.setWaitingAskOrder(true);
//               statusProperties.setCurrentStatus(30);
//             } else {
//               log.info("Error during asking");
//             }
//           } else {
//             log.info("Not enough coin balance");
//           }
//           break;
//         case 30:
//           // [ wait step ]
//           lastCandle = analysisForScaleTradingJobConfiguration.getLastCandleJob(market);
//           lastBidTime = scaleTradeStatusProperties.getBidTime();

//           log.info("lastCandle: " + lastCandle.toString());
//           // about bid
//           if(scaleTradeStatusProperties.getWaitingBidOrder()) {
//             log.info("Wait step about bid");
//             if(!scaleTradeStatusProperties.getStartTrading() && !lastBidTime.equals(lastCandle.getCandleDateTime())) {
//               log.info("Must find another chance..");
//               log.info("[changeStatus: 30 -> 40] [cancel bid step] ");
//               statusProperties.setCurrentStatus(40);
//               break;
//             }
//             else if(scaleTradeStatusProperties.getStartTrading() && !lastBidTime.equals(lastCandle.getCandleDateTime())) {
//               log.info("[changeStatus: 30 -> 40] [cancel bid step] ");
//               statusProperties.setCurrentStatus(40);
//               break;
//             }

//             ordersBidResponseDto = scaleTradeStatusProperties.getBidInfoPerLevel().get(level);
//             uuid = ordersBidResponseDto.getUuid();
//             OrdersResponseDto newOrders = ordersJobConfiguration.getOrderJob(uuid);
//             newOrders.setTradePrice(ordersBidResponseDto.getTradePrice());

//             log.info("ordersBidResponseDto: " + ordersBidResponseDto.toString());
//             log.info("uuid: " + uuid);
//             log.info("newOrders: " + newOrders.toString());

//             if(newOrders.getState().equals("done")) {
//               log.info("Success bidding!!");              
//               log.info("Finished wait for bid. Set the waitingBidOrder(false)");
//               scaleTradeStatusProperties.setStartTrading(true);
//               scaleTradeStatusProperties.updateBidInfoPerLevel(newOrders, level);
//               scaleTradeStatusProperties.updateNewTrade();
//               scaleTradeStatusProperties.setWaitingBidOrder(false);
//               if(scaleTradeStatusProperties.getWaitingAskOrder()) {
//                 log.info("[changeStatus: 30 -> 41] [cancel ask order for bid step] ");
//                 statusProperties.setCurrentStatus(41);
//               } else {
//                 log.info("[changeStatus: 30 -> 20] [ask step] ");
//                 statusProperties.setCurrentStatus(20);
//               }
//             } else {
//               log.info("Wait for bid");
//             }
//           }
//           // about ask
//           if(scaleTradeStatusProperties.getWaitingAskOrder()) {
//             log.info("Wait step about ask");
//             orderChanceDtoForAsk = ordersJobConfiguration.getOrdersChanceForAskJob(market);
//             ordersBidResponseDto = scaleTradeStatusProperties.getBidInfoPerLevel().get(level);
//             ordersAskResponseDto = scaleTradeStatusProperties.getAskInfoPerLevel().get(level);
//             uuid = ordersAskResponseDto.getUuid();
//             Double lossCutPrice = analysisForScaleTradingJobConfiguration.getLossCutPriceJob(orderChanceDtoForAsk.getAvgBuyPrice());

//             log.info("orderChanceDtoForAsk: " + orderChanceDtoForAsk.toString());
//             log.info("ordersBidResponseDto: " + ordersBidResponseDto.toString());
//             log.info("ordersAskResponseDto: " + ordersAskResponseDto.toString());
//             log.info(uuid);

//             if( level == 4 && 
//                 !lastBidTime.equals(lastCandle.getCandleDateTime()) &&
//                 lastCandle.getTradePrice().compareTo(lossCutPrice) == -1) {
//               log.info("Loss cut.");
//               log.info("[changeStatus: 30 -> 999] [loss cut step] ");
//               statusProperties.setCurrentStatus(999);
//             } else if(!lastBidTime.equals(lastCandle.getCandleDateTime()) && 
//                       lastCandle.getFlag() == -1 &&
//                       analysisForScaleTradingJobConfiguration.compareCurPriceLastBidTradePrice(lastCandle.getTradePrice(), ordersBidResponseDto.getTradePrice())) {
//               log.info("[changeStatus: 30 -> 42] [cancel ask order step] ");
//               statusProperties.setCurrentStatus(42);
//             } else {
//               orderResponseDto = ordersJobConfiguration.getOrderJob(uuid);
//               log.info("orderResponseDto: " + orderResponseDto.toString());

//               if(orderResponseDto.getState().equals("wait")) {
//                 log.info("Wait for ask");
//               } else if(orderResponseDto.getState().equals("done")) {
//                 log.info("Success asking!!");
//                 log.info("[changeStatus: 30 -> -1] [init step] ");
//                 scaleTradeStatusProperties.setLastConclusionTime(lastCandle.getCandleDateTime());
//                 statusProperties.setCurrentStatus(-1);
//                 // log.info("Rest for a few minutes");                
//               }
//             }
//           }
//           break;      
//         case 40:
//           // [ cancel bid order ]
//           uuid = scaleTradeStatusProperties.getBidInfoPerLevel().get(level).getUuid();
//           cancelOrderResponse = ordersJobConfiguration.deleteOrderJob(uuid);
//           if(cancelOrderResponse.getSuccess()){
//             log.info("Success cancel bid order!!");
//             if(!scaleTradeStatusProperties.getStartTrading()) {
//               log.info("[changeStatus: 40 -> -1] [init step] ");
//               scaleTradeStatusProperties.setWaitingBidOrder(false);
//               statusProperties.setCurrentStatus(-1);
//               // log.info("Rest for a few minutes");
//             } else {
//               log.info("Cannot bid whild scale Trading. Decrease Level!!");
//               log.info("[changeStatus: 40 -> 20] [ask step] ");
//               scaleTradeStatusProperties.decreaseLevel();
//               scaleTradeStatusProperties.setWaitingBidOrder(false);
//               statusProperties.setCurrentStatus(20);
//             }
//           } else {
//             log.info("Error during cancelOrder");
//           }
//           break;      
//         case 41:
//           // [ cancel ask order for bid step ]
//           uuid = scaleTradeStatusProperties.getAskInfoPerLevel().get(level).getUuid();
//           cancelOrderResponse = ordersJobConfiguration.deleteOrderJob(uuid);
//           if(cancelOrderResponse.getSuccess()){
//             log.info("Success cancel ask order for bid!!");
//             log.info("[changeStatus: 41 -> 20] [ask step] ");
//             scaleTradeStatusProperties.setWaitingAskOrder(false);
//             statusProperties.setCurrentStatus(20);
//           } else {
//             log.info("Error during cancelOrder");
//           }
//           break;  
//         case 42:
//           // [ cancel ask order for scale trade step ]
//           uuid = scaleTradeStatusProperties.getAskInfoPerLevel().get(level).getUuid();
//           cancelOrderResponse = ordersJobConfiguration.deleteOrderJob(uuid);
//           if(cancelOrderResponse.getSuccess()){
//             log.info("Success cancel ask order for scale trade!!. Increase Level!!");
//             log.info("[changeStatus: 42 -> 10] [bid step] ");
//             scaleTradeStatusProperties.increaseLevel();
//             scaleTradeStatusProperties.setWaitingAskOrder(false);
//             statusProperties.setCurrentStatus(10);
//           } else {
//             log.info("Error during cancelOrder");
//           }
//           break;
          
//         case 999:   
//           // [ loss cut step ]
//           uuid = scaleTradeStatusProperties.getAskInfoPerLevel().get(level).getUuid();
//           cancelOrderResponse = ordersJobConfiguration.deleteOrderJob(uuid);
//           if(cancelOrderResponse.getSuccess()){
//             log.info("Success cancel order!!");
//             scaleTradeStatusProperties.setWaitingAskOrder(false);
//             orderChanceDtoForAsk = ordersJobConfiguration.getOrdersChanceForAskJob(market);
//             log.info(orderChanceDtoForAsk.toString());
//             if(Double.parseDouble(orderChanceDtoForAsk.getBalance()) * Double.parseDouble(orderChanceDtoForAsk.getAvgBuyPrice()) > 5000.0){
//               ordersAskResponseDto = ordersJobConfiguration.askJob(market, orderChanceDtoForAsk.getBalance());
//               log.info(ordersAskResponseDto.toString());
//               if(ordersAskResponseDto.getSuccess()) {
//                 log.info("Success loss cut..");
//                 log.info("[changeStatus: 999 -> -1] [init step] ");
//                 statusProperties.setCurrentStatus(-1);
//                 // log.info("Rest for a few minutes");
//               } else {
//                 log.info("Error during asking");
//               }
//             } else {
//               log.info("Not enough coin balance");
//             }
//           } else {
//             log.info("Already success ask order!!");
//             log.info("[changeStatus: 999 -> -1] [init step] ");
//             statusProperties.setCurrentStatus(-1);
//             // log.info("Rest for a few minutes");
//           }
//           break; 
//       }
//     } catch (Exception e) {
//       log.info(e.getMessage());
//     }
//   }
// }
