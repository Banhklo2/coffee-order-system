package com.example.coffeeordersystem.domain.pointhistory.repository;

import com.example.coffeeordersystem.domain.pointhistory.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    List<PointHistory> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
