package ru.practicum.ewm.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.api.Category;
import ru.practicum.ewm.admin.dto.api.NewCategory;
import ru.practicum.ewm.dto.entities.CategoryDto;
import ru.practicum.ewm.dto.entities.EventDto;
import ru.practicum.ewm.dto.mapper.CategoryMapper;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminsCategoryService {

    private final CategoryRepository categoryRepository;

    private final EventRepository eventRepository;

    @Transactional
    public Category addCategory(NewCategory newCategory) {
        checkForCategory(newCategory.getName());
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName(newCategory.getName());
        log.info("admin: add category");
        return CategoryMapper.toCategory(categoryRepository.save(categoryDto));
    }

    @Transactional
    public void deleteCategory(long catId) {
        checkForCategory(catId);
        List<EventDto> eventsDtoByCategory = eventRepository.findEventsDtoByCategory(catId);
        if (!eventsDtoByCategory.isEmpty()) {
            throw new ConflictException("events to category already bound");
        }
        categoryRepository.deleteById(catId);
        log.info("admin: delete category {}", catId);
    }

    @Transactional
    public Category updateCategory(long catId, NewCategory newCategory) {
        CategoryDto categoryDto = checkForCategory(catId);
        if (categoryDto.getName().equals(newCategory.getName())) {
            return CategoryMapper.toCategory(categoryDto);
        }
        checkForCategory(newCategory.getName());
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

    private void checkForCategory(String name) {
        Optional<CategoryDto> categoryDtoByName = categoryRepository.getCategoryDtoByName(name);
        if (categoryDtoByName.isPresent()) {
            throw new ConflictException("category with this name already exist");
        }
    }
}
