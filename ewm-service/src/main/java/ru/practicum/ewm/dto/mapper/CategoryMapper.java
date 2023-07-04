package ru.practicum.ewm.dto.mapper;

import ru.practicum.ewm.dto.api.Category;
import ru.practicum.ewm.dto.entities.CategoryDto;

public class CategoryMapper {

    public static Category toCategory(CategoryDto categoryDto) {
        Category category = new Category();
        if (categoryDto.getId() != null) {
            category.setId(categoryDto.getId());
        }
        if (categoryDto.getName() != null) {
            category.setName(categoryDto.getName());
        }
        return category;
    }

    public static CategoryDto toCategoryDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        if (category.getId() != null) {
            categoryDto.setId(category.getId());
        }
        if (category.getName() != null) {
            categoryDto.setName(category.getName());
        }
        return categoryDto;
    }
}
