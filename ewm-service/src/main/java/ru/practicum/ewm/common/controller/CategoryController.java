package ru.practicum.ewm.common.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.common.service.CategoryService;
import ru.practicum.ewm.dto.api.Category;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<Category> getCategories(@RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                        @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        return categoryService.getCategories(from, size);
    }

    @GetMapping(path = "/{catId}")
    public Category getCategoryById(@PathVariable(name = "catId") long catId) {
        return categoryService.getCategoryById(catId);
    }
}
