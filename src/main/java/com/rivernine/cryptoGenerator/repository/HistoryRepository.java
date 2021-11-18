package com.rivernine.cryptoGenerator.repository;

import com.rivernine.cryptoGenerator.domain.history.History;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HistoryRepository {
  void insertHistory(History history);
}