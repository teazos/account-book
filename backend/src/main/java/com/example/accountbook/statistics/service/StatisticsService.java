package com.example.accountbook.statistics.service;

import com.example.accountbook.book.entity.AccountBook;
import com.example.accountbook.book.mapper.AccountBookMapper;
import com.example.accountbook.common.BizException;
import com.example.accountbook.common.TransactionType;
import com.example.accountbook.common.UserContext;
import com.example.accountbook.statistics.dto.MonthlySummary;
import com.example.accountbook.transaction.mapper.AccountTransactionMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class StatisticsService {
    private final AccountTransactionMapper transactionMapper;
    private final AccountBookMapper bookMapper;

    public StatisticsService(AccountTransactionMapper transactionMapper, AccountBookMapper bookMapper) {
        this.transactionMapper = transactionMapper;
        this.bookMapper = bookMapper;
    }

    public MonthlySummary monthly(Long bookId, YearMonth month) {
        ensureBook(bookId);
        Map<String, BigDecimal> sums = sums(bookId, month.atDay(1), month.atEndOfMonth());
        return new MonthlySummary(sums.get(TransactionType.INCOME.name()), sums.get(TransactionType.EXPENSE.name()));
    }

    public List<Map<String, Object>> category(Long bookId, String type, LocalDate startDate, LocalDate endDate) {
        ensureBook(bookId);
        String parsedType = parseType(type);
        return transactionMapper.categorySummary(bookId, parsedType, startDate, endDate)
                .stream()
                .map(row -> Map.of(
                        "categoryId", value(row, "categoryId"),
                        "categoryName", value(row, "categoryName"),
                        "total", value(row, "total")
                ))
                .toList();
    }

    public List<Map<String, Object>> dailyTrend(Long bookId, LocalDate startDate, LocalDate endDate) {
        ensureBook(bookId);
        return normalizeTrend(transactionMapper.dailyTrend(bookId, startDate, endDate));
    }

    public List<Map<String, Object>> yearly(Long bookId, Integer year) {
        ensureBook(bookId);
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        return normalizeTrend(transactionMapper.dailyTrend(bookId, start, end));
    }

    private List<Map<String, Object>> normalizeTrend(List<Map<String, Object>> rows) {
        return rows.stream()
                .map(row -> Map.of(
                        "transactionDate", value(row, "transactionDate"),
                        "type", value(row, "type"),
                        "total", value(row, "total")
                ))
                .toList();
    }

    private Map<String, BigDecimal> sums(Long bookId, LocalDate start, LocalDate end) {
        Map<String, BigDecimal> sums = new HashMap<>();
        sums.put(TransactionType.INCOME.name(), BigDecimal.ZERO);
        sums.put(TransactionType.EXPENSE.name(), BigDecimal.ZERO);
        for (Map<String, Object> row : transactionMapper.sumByType(bookId, start, end)) {
            Object type = value(row, "type");
            Object total = value(row, "total");
            if (type != null && total instanceof BigDecimal decimal) {
                sums.put(String.valueOf(type), decimal);
            }
        }
        return sums;
    }

    private String parseType(String type) {
        try {
            return TransactionType.valueOf(type).name();
        } catch (Exception e) {
            throw new BizException("类型必须是 INCOME 或 EXPENSE");
        }
    }

    private Object value(Map<String, Object> row, String key) {
        if (row.containsKey(key)) return row.get(key);
        String lower = key.toLowerCase(Locale.ROOT);
        if (row.containsKey(lower)) return row.get(lower);
        String upper = key.toUpperCase(Locale.ROOT);
        if (row.containsKey(upper)) return row.get(upper);
        return null;
    }

    private void ensureBook(Long bookId) {
        AccountBook book = bookMapper.selectById(bookId);
        if (book == null) throw new BizException("账本不存在");
        if (!book.getUserId().equals(UserContext.requireUserId())) throw new BizException("无权访问该账本");
    }
}
