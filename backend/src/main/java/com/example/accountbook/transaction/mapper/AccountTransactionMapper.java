package com.example.accountbook.transaction.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.accountbook.transaction.entity.AccountTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface AccountTransactionMapper extends BaseMapper<AccountTransaction> {
    @Select("""
            SELECT type, COALESCE(SUM(amount), 0) total
            FROM account_transaction
            WHERE book_id = #{bookId} AND transaction_date >= #{startDate} AND transaction_date <= #{endDate} AND deleted = false
            GROUP BY type
            """)
    List<Map<String, Object>> sumByType(@Param("bookId") Long bookId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Select("""
            SELECT c.id categoryId, c.name categoryName, COALESCE(SUM(t.amount), 0) total
            FROM account_transaction t
            JOIN account_category c ON t.category_id = c.id
            WHERE t.book_id = #{bookId} AND t.type = #{type} AND t.transaction_date >= #{startDate} AND t.transaction_date <= #{endDate}
              AND t.deleted = false AND c.deleted = false
            GROUP BY c.id, c.name
            ORDER BY total DESC
            """)
    List<Map<String, Object>> categorySummary(@Param("bookId") Long bookId, @Param("type") String type, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Select("""
            SELECT transaction_date transactionDate, type, COALESCE(SUM(amount), 0) total
            FROM account_transaction
            WHERE book_id = #{bookId} AND transaction_date >= #{startDate} AND transaction_date <= #{endDate} AND deleted = false
            GROUP BY transaction_date, type
            ORDER BY transaction_date
            """)
    List<Map<String, Object>> dailyTrend(@Param("bookId") Long bookId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
