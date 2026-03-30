package com.example.coffeeordersystem.domain.menu.controller;

import com.example.coffeeordersystem.domain.menu.dto.response.MenuResponse;
import com.example.coffeeordersystem.domain.menu.dto.response.PopularMenuResponse;
import com.example.coffeeordersystem.domain.menu.service.MenuService;
import com.example.coffeeordersystem.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuService menuService;

    // 메뉴 목록 조회
    @GetMapping
    public ApiResponse<List<MenuResponse>> getMenus() {
        return ApiResponse.ok(menuService.getMenus());
    }

    // 메뉴 단건 조회
    @GetMapping("/{menuId}")
    public ApiResponse<MenuResponse> getMenu(@PathVariable Long menuId) {
        return ApiResponse.ok(menuService.getMenu(menuId));
    }

    // 인기 메뉴 조회 (최근 7일, 상위 3개)
    @GetMapping("/popular")
    public ApiResponse<List<PopularMenuResponse>> getPopularMenus() {
        return ApiResponse.ok(menuService.getPopularMenus());
    }
}
