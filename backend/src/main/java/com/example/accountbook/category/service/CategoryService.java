package com.example.accountbook.category.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.accountbook.book.entity.AccountBook;
import com.example.accountbook.book.mapper.AccountBookMapper;
import com.example.accountbook.category.dto.CategoryRequest;
import com.example.accountbook.category.entity.AccountCategory;
import com.example.accountbook.category.mapper.AccountCategoryMapper;
import com.example.accountbook.common.BizException;
import com.example.accountbook.common.TransactionType;
import com.example.accountbook.common.UserContext;
import com.example.accountbook.transaction.mapper.AccountTransactionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryService {
    private final AccountCategoryMapper categoryMapper;
    private final AccountBookMapper bookMapper;
    private final AccountTransactionMapper transactionMapper;

    public CategoryService(AccountCategoryMapper categoryMapper, AccountBookMapper bookMapper, AccountTransactionMapper transactionMapper) {
        this.categoryMapper = categoryMapper;
        this.bookMapper = bookMapper;
        this.transactionMapper = transactionMapper;
    }

    public List<AccountCategory> list(Long bookId, String type) {
        ensureBook(bookId);
        LambdaQueryWrapper<AccountCategory> wrapper = new LambdaQueryWrapper<AccountCategory>()
                .eq(AccountCategory::getBookId, bookId)
                .orderByAsc(AccountCategory::getSortOrder)
                .orderByAsc(AccountCategory::getCreatedAt);
        if (type != null && !type.isBlank()) wrapper.eq(AccountCategory::getType, parseType(type));
        return categoryMapper.selectList(wrapper);
    }

    @Transactional
    public AccountCategory create(Long bookId, CategoryRequest req) {
        ensureBook(bookId);
        String type = parseType(req.getType());
        ensureNameUnique(bookId, req.getName(), type, null);
        AccountCategory category = new AccountCategory();
        category.setBookId(bookId);
        category.setName(req.getName());
        category.setType(type);
        category.setIcon(req.getIcon());
        category.setColor(req.getColor());
        category.setSortOrder(req.getSortOrder() == null ? 0 : req.getSortOrder());
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        category.setDeleted(false);
        categoryMapper.insert(category);
        return category;
    }

    @Transactional
    public AccountCategory update(Long bookId, Long categoryId, CategoryRequest req) {
        ensureBook(bookId);
        AccountCategory category = getExisting(bookId, categoryId);
        String type = parseType(req.getType());
        ensureNameUnique(bookId, req.getName(), type, categoryId);
        category.setName(req.getName());
        category.setType(type);
        category.setIcon(req.getIcon());
        category.setColor(req.getColor());
        category.setSortOrder(req.getSortOrder() == null ? 0 : req.getSortOrder());
        category.setUpdatedAt(LocalDateTime.now());
        categoryMapper.updateById(category);
        return category;
    }

    @Transactional
    public void delete(Long bookId, Long categoryId) {
        ensureBook(bookId);
        getExisting(bookId, categoryId);
        Long used = transactionMapper.selectCount(new LambdaQueryWrapper<com.example.accountbook.transaction.entity.AccountTransaction>()
                .eq(com.example.accountbook.transaction.entity.AccountTransaction::getBookId, bookId)
                .eq(com.example.accountbook.transaction.entity.AccountTransaction::getCategoryId, categoryId));
        if (used > 0) throw new BizException("分类已被账目使用，不建议删除");
        categoryMapper.deleteById(categoryId);
    }

    public AccountCategory getExisting(Long bookId, Long categoryId) {
        AccountCategory category = categoryMapper.selectById(categoryId);
        if (category == null || !bookId.equals(category.getBookId())) throw new BizException("分类不存在或不属于当前账本");
        return category;
    }

    @Transactional
    public void initDefaultCategories(Long bookId) {
        String[][] expense = {{"餐饮","🍜","#f56c6c"},{"交通","🚌","#e6a23c"},{"购物","🛒","#909399"},{"住房","🏠","#409eff"},{"娱乐","🎮","#9c27b0"},{"医疗","💊","#67c23a"},{"教育","📚","#00bcd4"},{"其他","📦","#606266"}};
        String[][] income = {{"工资","💰","#67c23a"},{"奖金","🎁","#409eff"},{"理财","📈","#e6a23c"},{"副业","🧰","#9c27b0"},{"其他","📦","#606266"}};
        int order = 0;
        for (String[] item : expense) insertDefault(bookId, item, TransactionType.EXPENSE.name(), order++);
        order = 0;
        for (String[] item : income) insertDefault(bookId, item, TransactionType.INCOME.name(), order++);
    }

    @Transactional
    public void deleteByBook(Long bookId) {
        List<AccountCategory> list = categoryMapper.selectList(new LambdaQueryWrapper<AccountCategory>().eq(AccountCategory::getBookId, bookId));
        for (AccountCategory category : list) categoryMapper.deleteById(category.getId());
    }

    private void insertDefault(Long bookId, String[] item, String type, int order) {
        AccountCategory c = new AccountCategory();
        c.setBookId(bookId); c.setName(item[0]); c.setIcon(item[1]); c.setColor(item[2]); c.setType(type); c.setSortOrder(order);
        c.setCreatedAt(LocalDateTime.now()); c.setUpdatedAt(LocalDateTime.now()); c.setDeleted(false);
        categoryMapper.insert(c);
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

    private void ensureNameUnique(Long bookId, String name, String type, Long excludeId) {
        LambdaQueryWrapper<AccountCategory> wrapper = new LambdaQueryWrapper<AccountCategory>()
                .eq(AccountCategory::getBookId, bookId).eq(AccountCategory::getName, name).eq(AccountCategory::getType, type);
        if (excludeId != null) wrapper.ne(AccountCategory::getId, excludeId);
        if (categoryMapper.selectCount(wrapper) > 0) throw new BizException("同一账本同类型下分类名称不能重复");
    }
}
