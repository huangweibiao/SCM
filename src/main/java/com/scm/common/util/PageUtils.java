package com.scm.common.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scm.common.result.PageResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分页工具类
 *
 * @author SCM System
 * @since 2026-04-06
 */
public class PageUtils {

    /**
     * MyBatis-Plus分页转换为自定义分页结果
     */
    public static <T> PageResult<T> toPageResult(IPage<T> page) {
        return PageResult.of(page.getTotal(), page.getRecords());
    }

    /**
     * MyBatis-Plus分页转换并映射
     */
    public static <T, R> PageResult<R> toPageResult(IPage<T> page, Function<T, R> mapper) {
        List<R> records = page.getRecords().stream()
                .map(mapper)
                .collect(Collectors.toList());
        return PageResult.of(page.getTotal(), records);
    }

    /**
     * 列表分页
     */
    public static <T> PageResult<T> paginate(List<T> list, int pageNum, int pageSize) {
        int total = list.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);

        if (fromIndex >= total) {
            return PageResult.of((long) total, new ArrayList<>());
        }

        List<T> subList = list.subList(fromIndex, toIndex);
        return PageResult.of((long) total, subList);
    }
}
