package com.example.accountbook.category.controller;

import com.example.accountbook.category.dto.CategoryRequest;
import com.example.accountbook.category.entity.AccountCategory;
import com.example.accountbook.category.service.CategoryService;
import com.example.accountbook.common.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books/{bookId}/categories")
public class CategoryController {
    private final CategoryService categoryService;
    public CategoryController(CategoryService categoryService) { this.categoryService = categoryService; }

    @GetMapping
    public Result<List<AccountCategory>> list(@PathVariable Long bookId, @RequestParam(required = false) String type) {
        return Result.success(categoryService.list(bookId, type));
    }

    @PostMapping
    public Result<AccountCategory> create(@PathVariable Long bookId, @Valid @RequestBody CategoryRequest request) {
        return Result.success(categoryService.create(bookId, request));
    }

    @PutMapping("/{categoryId}")
    public Result<AccountCategory> update(@PathVariable Long bookId, @PathVariable Long categoryId, @Valid @RequestBody CategoryRequest request) {
        return Result.success(categoryService.update(bookId, categoryId, request));
    }

    @DeleteMapping("/{categoryId}")
    public Result<Void> delete(@PathVariable Long bookId, @PathVariable Long categoryId) {
        categoryService.delete(bookId, categoryId);
        return Result.success(null);
    }
}
