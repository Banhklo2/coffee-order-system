package com.example.coffeeordersystem.domain.menu.service;

import com.example.coffeeordersystem.domain.menu.dto.response.MenuResponse;
import com.example.coffeeordersystem.domain.menu.dto.response.PopularMenuResponse;
import com.example.coffeeordersystem.domain.menu.entity.Menu;
import com.example.coffeeordersystem.domain.menu.repository.MenuRepository;
import com.example.coffeeordersystem.global.exception.ErrorCode;
import com.example.coffeeordersystem.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MenuService {

    private final MenuRepository menuRepository;

    // 메뉴 목록 조회
    public List<MenuResponse> getMenus() {
        return menuRepository.findAll().stream()
                .map(MenuResponse::from)
                .toList();
    }

    // 메뉴 단건 조회
    public MenuResponse getMenu(Long menuId) {
        Menu menu = findMenu(menuId);
        return MenuResponse.from(menu);
    }

    // 인기 메뉴 조회 (최근 7일, 상위 3개)
    @Cacheable(value = "popularMenus")
    public List<PopularMenuResponse> getPopularMenus() {
        log.info("인기 메뉴 DB 조회 실행");
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        return menuRepository.findPopularMenus(sevenDaysAgo, PageRequest.of(0, 3));
    }

    // 메뉴 조회 공통 메서드
    // - menuId로 조회 후 없으면 예외 발생
    private Menu findMenu(Long menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new ServiceException(ErrorCode.MENU_NOT_FOUND));
    }
}
