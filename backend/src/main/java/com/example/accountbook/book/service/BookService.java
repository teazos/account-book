package com.example.accountbook.book.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.accountbook.book.dto.BookRequest;
import com.example.accountbook.book.entity.AccountBook;
import com.example.accountbook.book.mapper.AccountBookMapper;
import com.example.accountbook.category.service.CategoryService;
import com.example.accountbook.common.BizException;
import com.example.accountbook.common.UserContext;
import com.example.accountbook.config.FileStorageService;
import com.example.accountbook.transaction.service.TransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookService {
    private final AccountBookMapper bookMapper;
    private final CategoryService categoryService;
    private final TransactionService transactionService;
    private final FileStorageService fileStorageService;

    public BookService(AccountBookMapper bookMapper, CategoryService categoryService, TransactionService transactionService, FileStorageService fileStorageService) {
        this.bookMapper = bookMapper;
        this.categoryService = categoryService;
        this.transactionService = transactionService;
        this.fileStorageService = fileStorageService;
    }

    public List<AccountBook> list() {
        return bookMapper.selectList(new LambdaQueryWrapper<AccountBook>()
                .eq(AccountBook::getUserId, UserContext.requireUserId())
                .orderByDesc(AccountBook::getIsDefault)
                .orderByDesc(AccountBook::getCreatedAt));
    }

    public AccountBook getExisting(Long id) {
        AccountBook book = bookMapper.selectById(id);
        if (book == null) throw new BizException("账本不存在");
        ensureOwner(book);
        return book;
    }

    @Transactional
    public AccountBook create(BookRequest req) {
        Long userId = UserContext.requireUserId();
        if (Boolean.TRUE.equals(req.getIsDefault()) || bookMapper.selectCount(new LambdaQueryWrapper<AccountBook>().eq(AccountBook::getUserId, userId)) == 0) {
            clearDefault(userId);
        }
        AccountBook book = new AccountBook();
        book.setUserId(userId);
        book.setName(req.getName());
        book.setDescription(req.getDescription());
        book.setCurrency(req.getCurrency() == null || req.getCurrency().isBlank() ? "CNY" : req.getCurrency());
        book.setCover(req.getCover());
        book.setIsDefault(Boolean.TRUE.equals(req.getIsDefault()) || bookMapper.selectCount(new LambdaQueryWrapper<AccountBook>().eq(AccountBook::getUserId, userId)) == 0);
        book.setCreatedAt(LocalDateTime.now());
        book.setUpdatedAt(LocalDateTime.now());
        book.setDeleted(false);
        bookMapper.insert(book);
        categoryService.initDefaultCategories(book.getId());
        return book;
    }

    @Transactional
    public AccountBook update(Long id, BookRequest req) {
        AccountBook book = getExisting(id);
        if (Boolean.TRUE.equals(req.getIsDefault())) clearDefault(book.getUserId());
        String oldCover = book.getCover();
        book.setName(req.getName());
        book.setDescription(req.getDescription());
        book.setCurrency(req.getCurrency() == null || req.getCurrency().isBlank() ? "CNY" : req.getCurrency());
        book.setCover(req.getCover());
        book.setIsDefault(Boolean.TRUE.equals(req.getIsDefault()) || Boolean.TRUE.equals(book.getIsDefault()));
        book.setUpdatedAt(LocalDateTime.now());
        bookMapper.updateById(book);
        if (oldCover != null && !oldCover.equals(book.getCover())) fileStorageService.deleteIfUploaded(oldCover);
        return book;
    }

    @Transactional
    public void setDefault(Long id) {
        AccountBook book = getExisting(id);
        clearDefault(book.getUserId());
        book.setIsDefault(true);
        book.setUpdatedAt(LocalDateTime.now());
        bookMapper.updateById(book);
    }

    @Transactional
    public void delete(Long id) {
        AccountBook book = getExisting(id);
        if (transactionService.countByBook(id) > 0) throw new BizException("账本下已有账目，请先删除账目或保留账本");
        categoryService.deleteByBook(id);
        bookMapper.deleteById(id);
        fileStorageService.deleteIfUploaded(book.getCover());
    }

    private void clearDefault(Long userId) {
        List<AccountBook> defaults = bookMapper.selectList(new LambdaQueryWrapper<AccountBook>()
                .eq(AccountBook::getUserId, userId)
                .eq(AccountBook::getIsDefault, true));
        for (AccountBook b : defaults) {
            b.setIsDefault(false);
            b.setUpdatedAt(LocalDateTime.now());
            bookMapper.updateById(b);
        }
    }

    private void ensureOwner(AccountBook book) {
        if (!book.getUserId().equals(UserContext.requireUserId())) throw new BizException("无权访问该账本");
    }
}
