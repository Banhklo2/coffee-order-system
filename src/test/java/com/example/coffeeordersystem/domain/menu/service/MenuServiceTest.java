package com.example.coffeeordersystem.domain.menu.service;

import com.example.coffeeordersystem.domain.menu.dto.response.MenuResponse;
import com.example.coffeeordersystem.domain.menu.dto.response.PopularMenuResponse;
import com.example.coffeeordersystem.domain.menu.entity.Menu;
import com.example.coffeeordersystem.domain.menu.repository.MenuRepository;
import com.example.coffeeordersystem.global.exception.ErrorCode;
import com.example.coffeeordersystem.global.exception.ServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @InjectMocks
    private MenuService menuService;

    @Mock
    private MenuRepository menuRepository;

    @Test
    @DisplayName("메뉴 목록 조회 성공")
    void getMenus_success() {
        // given
        Menu menu1 = Menu.builder()
                .id(1L)
                .name("아메리카노")
                .price(BigDecimal.valueOf(4500))
                .build();

        Menu menu2 = Menu.builder()
                .id(2L)
                .name("카페라떼")
                .price(BigDecimal.valueOf(5000))
                .build();

        when(menuRepository.findAll()).thenReturn(List.of(menu1, menu2));

        // when
        List<MenuResponse> result = menuService.getMenus();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getMenuId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("아메리카노");
        assertThat(result.get(0).getPrice()).isEqualByComparingTo(BigDecimal.valueOf(4500));

        assertThat(result.get(1).getMenuId()).isEqualTo(2L);
        assertThat(result.get(1).getName()).isEqualTo("카페라떼");
        assertThat(result.get(1).getPrice()).isEqualByComparingTo(BigDecimal.valueOf(5000));

        verify(menuRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("메뉴 단건 조회 성공")
    void getMenu_success() {
        // given
        Long menuId = 1L;

        Menu menu = Menu.builder()
                .id(menuId)
                .name("아메리카노")
                .price(BigDecimal.valueOf(4500))
                .build();

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));

        // when
        MenuResponse result = menuService.getMenu(menuId);

        // then
        assertThat(result.getMenuId()).isEqualTo(menuId);
        assertThat(result.getName()).isEqualTo("아메리카노");
        assertThat(result.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(4500));

        verify(menuRepository, times(1)).findById(menuId);
    }

    @Test
    @DisplayName("메뉴 단건 조회 실패 - 존재하지 않는 메뉴")
    void getMenu_fail_notFound() {
        // given
        Long menuId = 999L;
        when(menuRepository.findById(menuId)).thenReturn(Optional.empty());

        // when
        ServiceException exception = assertThrows(ServiceException.class,
                () -> menuService.getMenu(menuId));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MENU_NOT_FOUND);
        verify(menuRepository, times(1)).findById(menuId);
    }

    @Test
    @DisplayName("인기 메뉴 조회 성공")
    void getPopularMenus_success() {
        // given
        PopularMenuResponse menu1 =
                new PopularMenuResponse(1L, "아메리카노", 10L);
        PopularMenuResponse menu2 =
                new PopularMenuResponse(2L, "카페라떼", 7L);
        PopularMenuResponse menu3 =
                new PopularMenuResponse(3L, "바닐라라떼", 5L);

        when(menuRepository.findPopularMenus(any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(menu1, menu2, menu3));

        // when
        List<PopularMenuResponse> result = menuService.getPopularMenus();

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getMenuId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("아메리카노");
        assertThat(result.get(0).getOrderCount()).isEqualTo(10L);

        assertThat(result.get(1).getMenuId()).isEqualTo(2L);
        assertThat(result.get(1).getName()).isEqualTo("카페라떼");
        assertThat(result.get(1).getOrderCount()).isEqualTo(7L);

        assertThat(result.get(2).getMenuId()).isEqualTo(3L);
        assertThat(result.get(2).getName()).isEqualTo("바닐라라떼");
        assertThat(result.get(2).getOrderCount()).isEqualTo(5L);

        verify(menuRepository, times(1))
                .findPopularMenus(any(LocalDateTime.class), any(Pageable.class));
    }
}
