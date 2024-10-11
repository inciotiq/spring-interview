package com.iotiq.interview.service;

import com.iotiq.interview.controller.messages.MenuRequest;
import com.iotiq.interview.domain.Menu;
import com.iotiq.interview.domain.MenuFilter;
import com.iotiq.interview.exception.DuplicateMenuNameException;
import com.iotiq.interview.exception.MenuNotFoundException;
import com.iotiq.interview.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    @Transactional
    public Menu create(MenuRequest request) {
        if (menuRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateMenuNameException();
        }
        Menu menu = new Menu();
        menu.setName(request.getName());
        return menuRepository.save(menu);
    }

    @Transactional
    public Menu update(UUID id, MenuRequest request) {
        Menu menu = getById(id);
        
        if (menuRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), id)) {
            throw new DuplicateMenuNameException();
        }
        
        menu.setName(request.getName());
        return menuRepository.save(menu);
    }

    public List<Menu> getAll() {
        return menuRepository.findAll();
    }

    public Menu getById(UUID id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new MenuNotFoundException(id));
    }

    public Page<Menu> getFiltered(MenuFilter filter, Pageable pageable) {
        Specification<Menu> spec = Specification.where(null);

        if (filter.getName() != null && !filter.getName().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")),
                    "%" + filter.getName().toLowerCase() + "%"));
        }

        return menuRepository.findAll(spec, pageable);
    }
}