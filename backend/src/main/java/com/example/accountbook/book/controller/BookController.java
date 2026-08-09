package com.example.accountbook.book.controller;

import com.example.accountbook.book.dto.BookRequest;
import com.example.accountbook.book.entity.AccountBook;
import com.example.accountbook.book.service.BookService;
import com.example.accountbook.common.Result;
import com.example.accountbook.config.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;
    private final FileStorageService fileStorageService;

    public BookController(BookService bookService, FileStorageService fileStorageService) {
        this.bookService = bookService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/cover")
    public Result<String> uploadCover(@RequestParam("file") MultipartFile file) {
        return Result.success(fileStorageService.save(file));
    }

    @GetMapping
    public Result<List<AccountBook>> list() { return Result.success(bookService.list()); }

    @GetMapping("/{bookId}")
    public Result<AccountBook> get(@PathVariable Long bookId) { return Result.success(bookService.getExisting(bookId)); }

    @PostMapping
    public Result<AccountBook> create(@Valid @RequestBody BookRequest request) { return Result.success(bookService.create(request)); }

    @PutMapping("/{bookId}")
    public Result<AccountBook> update(@PathVariable Long bookId, @Valid @RequestBody BookRequest request) { return Result.success(bookService.update(bookId, request)); }

    @DeleteMapping("/{bookId}")
    public Result<Void> delete(@PathVariable Long bookId) { bookService.delete(bookId); return Result.success(null); }

    @PutMapping("/{bookId}/default")
    public Result<Void> setDefault(@PathVariable Long bookId) { bookService.setDefault(bookId); return Result.success(null); }
}
