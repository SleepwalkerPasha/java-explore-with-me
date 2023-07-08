package ru.practicum.ewm.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.api.Category;
import ru.practicum.ewm.admin.dto.api.NewCategory;
import ru.practicum.ewm.dto.entities.CategoryDto;
import ru.practicum.ewm.dto.mapper.CategoryMapper;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.repository.CategoryRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminsCategoryService {

    private final CategoryRepository categoryRepository;

    public Category addCategory(NewCategory newCategory) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName(newCategory.getName());
        log.info("admin: add category");
        return CategoryMapper.toCategory(categoryRepository.save(categoryDto));
    }

    public void deleteCategory(long catId) {
        checkForCategory(catId);
        categoryRepository.deleteById(catId);
        log.info("admin: delete category {}", catId);
    }

    public Category updateCategory(long catId, NewCategory newCategory) {
        CategoryDto categoryDto = checkForCategory(catId);
        categoryDto.setName(newCategory.getName());
        log.info("admin: update category {}", catId);
        return CategoryMapper.toCategory(categoryRepository.save(categoryDto));
    }

    private CategoryDto checkForCategory(long catId) {
        Optional<CategoryDto> categoryDtoOptional = categoryRepository.findById(catId);
        if (categoryDtoOptional.isEmpty()) {
            throw new NotFoundException(String.format("Category with id=%d was not found", catId));
        }
        return categoryDtoOptional.get();
    }

}
