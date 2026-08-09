package com.example.accountbook.transaction.controller;

import com.example.accountbook.common.PageResult;
import com.example.accountbook.common.Result;
import com.example.accountbook.transaction.dto.TransactionRequest;
import com.example.accountbook.transaction.dto.TransactionView;
import com.example.accountbook.transaction.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/books/{bookId}/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    public TransactionController(TransactionService transactionService) { this.transactionService = transactionService; }

    @GetMapping
    public Result<PageResult<TransactionView>> page(
            @PathVariable Long bookId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        return Result.success(transactionService.page(bookId, startDate, endDate, type, categoryId, page, size));
    }

    @GetMapping("/{transactionId}")
    public Result<TransactionView> get(@PathVariable Long bookId, @PathVariable Long transactionId) {
        return Result.success(transactionService.get(bookId, transactionId));
    }

    @PostMapping
    public Result<TransactionView> create(@PathVariable Long bookId, @Valid @RequestBody TransactionRequest request) {
        return Result.success(transactionService.create(bookId, request));
    }

    @PutMapping("/{transactionId}")
    public Result<TransactionView> update(@PathVariable Long bookId, @PathVariable Long transactionId, @Valid @RequestBody TransactionRequest request) {
        return Result.success(transactionService.update(bookId, transactionId, request));
    }

    @DeleteMapping("/{transactionId}")
    public Result<Void> delete(@PathVariable Long bookId, @PathVariable Long transactionId) {
        transactionService.delete(bookId, transactionId);
        return Result.success(null);
    }
}
