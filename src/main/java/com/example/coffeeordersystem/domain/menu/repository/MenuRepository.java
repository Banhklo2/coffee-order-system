package com.example.coffeeordersystem.domain.menu.repository;

import com.example.coffeeordersystem.domain.menu.dto.response.PopularMenuResponse;
import com.example.coffeeordersystem.domain.menu.entity.Menu;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    @Query("""
        select new com.example.coffeeordersystem.domain.menu.dto.response.PopularMenuResponse(
            m.id,
            m.name,
            count(oi.id)
        )
        from OrderItem oi
        join oi.menu m
        join oi.order o
        where o.orderedAt >= :sevenDaysAgo
        group by m.id, m.name
        order by count(oi.id) desc
    """)
    List<PopularMenuResponse> findPopularMenus(LocalDateTime sevenDaysAgo, Pageable pageable);
}
