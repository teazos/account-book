package com.example.accountbook.transaction.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.accountbook.book.entity.AccountBook;
import com.example.accountbook.book.mapper.AccountBookMapper;
import com.example.accountbook.category.entity.AccountCategory;
import com.example.accountbook.category.mapper.AccountCategoryMapper;
import com.example.accountbook.common.BizException;
import com.example.accountbook.common.PageResult;
import com.example.accountbook.common.TransactionType;
import com.example.accountbook.common.UserContext;
import com.example.accountbook.transaction.dto.TransactionRequest;
import com.example.accountbook.transaction.dto.TransactionView;
import com.example.accountbook.transaction.entity.AccountTransaction;
import com.example.accountbook.transaction.mapper.AccountTransactionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private final AccountTransactionMapper transactionMapper;
    private final AccountCategoryMapper categoryMapper;
    private final AccountBookMapper bookMapper;

    public TransactionService(AccountTransactionMapper transactionMapper, AccountCategoryMapper categoryMapper, AccountBookMapper bookMapper) {
        this.transactionMapper = transactionMapper;
        this.categoryMapper = categoryMapper;
        this.bookMapper = bookMapper;
    }

    public PageResult<TransactionView> page(Long bookId, LocalDate startDate, LocalDate endDate, String type, Long categoryId, long page, long size) {
        ensureBook(bookId);
        LambdaQueryWrapper<AccountTransaction> wrapper = new LambdaQueryWrapper<AccountTransaction>()
                .eq(AccountTransaction::getBookId, bookId)
                .orderByDesc(AccountTransaction::getTransactionDate)
                .orderByDesc(AccountTransaction::getCreatedAt);
        if (startDate != null) wrapper.ge(AccountTransaction::getTransactionDate, startDate);
        if (endDate != null) wrapper.le(AccountTransaction::getTransactionDate, endDate);
        if (type != null && !type.isBlank()) wrapper.eq(AccountTransaction::getType, parseType(type));
        if (categoryId != null) wrapper.eq(AccountTransaction::getCategoryId, categoryId);
        Page<AccountTransaction> result = transactionMapper.selectPage(Page.of(page, size), wrapper);
        return new PageResult<>(toViews(result.getRecords()), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public TransactionView get(Long bookId, Long id) {
        return toView(getExisting(bookId, id), loadCategories(bookId));
    }

    @Transactional
    public TransactionView create(Long bookId, TransactionRequest req) {
        ensureBook(bookId);
        AccountCategory category = ensureCategory(bookId, req.getCategoryId());
        String type = parseType(req.getType());
        if (!type.equals(category.getType())) throw new BizException("账目类型必须和分类类型一致");
        AccountTransaction transaction = new AccountTransaction();
        transaction.setBookId(bookId);
        transaction.setCategoryId(req.getCategoryId());
        transaction.setType(type);
        transaction.setAmount(req.getAmount());
        transaction.setTransactionDate(req.getTransactionDate());
        transaction.setNote(req.getNote());
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        transaction.setDeleted(false);
        transactionMapper.insert(transaction);
        return toView(transaction, Map.of(category.getId(), category));
    }

    @Transactional
    public TransactionView update(Long bookId, Long id, TransactionRequest req) {
        AccountTransaction transaction = getExisting(bookId, id);
        AccountCategory category = ensureCategory(bookId, req.getCategoryId());
        String type = parseType(req.getType());
        if (!type.equals(category.getType())) throw new BizException("账目类型必须和分类类型一致");
        transaction.setCategoryId(req.getCategoryId());
        transaction.setType(type);
        transaction.setAmount(req.getAmount());
        transaction.setTransactionDate(req.getTransactionDate());
        transaction.setNote(req.getNote());
        transaction.setUpdatedAt(LocalDateTime.now());
        transactionMapper.updateById(transaction);
        return toView(transaction, Map.of(category.getId(), category));
    }

    @Transactional
    public void delete(Long bookId, Long id) {
        getExisting(bookId, id);
        transactionMapper.deleteById(id);
    }

    public long countByBook(Long bookId) {
        return transactionMapper.selectCount(new LambdaQueryWrapper<AccountTransaction>().eq(AccountTransaction::getBookId, bookId));
    }

    private AccountTransaction getExisting(Long bookId, Long id) {
        ensureBook(bookId);
        AccountTransaction t = transactionMapper.selectById(id);
        if (t == null || !bookId.equals(t.getBookId())) throw new BizException("账目不存在或不属于当前账本");
        return t;
    }

    private AccountCategory ensureCategory(Long bookId, Long categoryId) {
        AccountCategory c = categoryMapper.selectById(categoryId);
        if (c == null || !bookId.equals(c.getBookId())) throw new BizException("分类不存在或不属于当前账本");
        return c;
    }

    private void ensureBook(Long bookId) {
        AccountBook book = bookMapper.selectById(bookId);
        if (book == null) throw new BizException("账本不存在");
        if (!book.getUserId().equals(UserContext.requireUserId())) throw new BizException("无权访问该账本");
    }

    private String parseType(String type) {
        try { return TransactionType.valueOf(type).name(); }
        catch (Exception e) { throw new BizException("类型必须是 INCOME 或 EXPENSE"); }
    }

    private List<TransactionView> toViews(List<AccountTransaction> transactions) {
        if (transactions.isEmpty()) return List.of();
        Map<Long, AccountCategory> categories = loadCategories(transactions.get(0).getBookId());
        return transactions.stream().map(t -> toView(t, categories)).collect(Collectors.toList());
    }

    private Map<Long, AccountCategory> loadCategories(Long bookId) {
        return categoryMapper.selectList(new LambdaQueryWrapper<AccountCategory>().eq(AccountCategory::getBookId, bookId))
                .stream().collect(Collectors.toMap(AccountCategory::getId, c -> c));
    }

    private TransactionView toView(AccountTransaction t, Map<Long, AccountCategory> categories) {
        TransactionView v = new TransactionView();
        v.setId(t.getId()); v.setBookId(t.getBookId()); v.setCategoryId(t.getCategoryId()); v.setType(t.getType());
        v.setAmount(t.getAmount()); v.setTransactionDate(t.getTransactionDate()); v.setNote(t.getNote());
        v.setCreatedAt(t.getCreatedAt()); v.setUpdatedAt(t.getUpdatedAt());
        AccountCategory c = categories.get(t.getCategoryId());
        if (c != null) { v.setCategoryName(c.getName()); v.setCategoryIcon(c.getIcon()); }
        return v;
    }
}
