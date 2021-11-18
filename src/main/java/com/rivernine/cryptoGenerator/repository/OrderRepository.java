package com.rivernine.cryptoGenerator.repository;

import java.util.List;
import java.util.Map;

import com.rivernine.cryptoGenerator.domain.order.Order;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderRepository {
  // List<Map<String, Object>> getOrder();
  Order getOrder();
  void insertOrder(Order order);
  void deleteOrder();
}