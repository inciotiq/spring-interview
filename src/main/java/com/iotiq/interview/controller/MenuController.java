package com.iotiq.interview.controller;

import com.iotiq.interview.controller.messages.CreateResponse;
import com.iotiq.interview.controller.messages.MenuRequest;
import com.iotiq.interview.controller.messages.MenuResponse;
import com.iotiq.interview.domain.Menu;
import com.iotiq.interview.domain.MenuFilter;
import com.iotiq.interview.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/menus")
public class MenuController {

    private final MenuService menuService;
    @GetMapping
    public Page<MenuResponse> getAll(MenuFilter filter, Pageable pageable) {
        return menuService.getFiltered(filter, pageable)
                .map(MenuResponse::of);
    }

    @PutMapping("/{id}")
    public MenuResponse update(@PathVariable UUID id, @Valid @RequestBody MenuRequest request) {
        Menu menu = menuService.update(id, request);
        return MenuResponse.of(menu);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateResponse create(@Valid @RequestBody MenuRequest request) {
        Menu menu = menuService.create(request);
        return CreateResponse.builder().id(menu.getId()).build();
    }
}
