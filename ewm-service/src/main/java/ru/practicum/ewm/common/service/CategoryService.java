package ru.practicum.ewm.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.api.Category;
import ru.practicum.ewm.dto.entities.CategoryDto;
import ru.practicum.ewm.dto.mapper.CategoryMapper;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.pagination.PageRequester;
import ru.practicum.ewm.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getCategories(int from, int size) {
        log.info("public: get categories");
        return categoryRepository.getCategoryDtos(PageRequester.of(from, size))
                .stream()
                .map(CategoryMapper::toCategory)
                .collect(Collectors.toList());
    }

    public Category getCategoryById(long catId) {
        Optional<CategoryDto> byId = categoryRepository.findById(catId);
        if (byId.isEmpty()) {
            throw new NotFoundException(String.format("Category with id=%d was not found", catId));
        }
        log.info("public: get category by Id {}", catId);
        return CategoryMapper.toCategory(byId.get());
    }
}
