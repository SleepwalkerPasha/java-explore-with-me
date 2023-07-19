package ru.practicum.ewm.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.admin.service.AdminsCategoryService;
import ru.practicum.ewm.dto.api.Category;
import ru.practicum.ewm.admin.dto.api.NewCategory;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class AdminsCategoryController {

    private final AdminsCategoryService categoryService;

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public Category addCategory(@RequestBody @Valid NewCategory newCategory) {
        return categoryService.addCategory(newCategory);
    }

    @DeleteMapping(path = "/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        categoryService.deleteCategory(catId);
    }

    @PatchMapping(path = "/{catId}")
    public Category updateCategory(@PathVariable Long catId, @RequestBody @Valid NewCategory newCategory) {
        return categoryService.updateCategory(catId, newCategory);
    }

}
