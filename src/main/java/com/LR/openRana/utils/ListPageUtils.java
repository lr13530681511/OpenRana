package com.LR.openRana.utils;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 分页用，如果用Java，List来表示结果
 */
public final class ListPageUtils {

    private static List<?> split(final List<?> list, final int currentPage,
                                 int pageSize) {
        long start = (long) (currentPage - 1) * pageSize;
        long end = start + pageSize < list.size() ? start + pageSize
                : list.size();
        return list.subList((int) start, (int) end);
    }

    public static PageUtils getSplitResult(final List<?> list, final int currentPage,
                                           int pageSize) {
        return new PageUtils(split(list, currentPage, pageSize), list.size(), pageSize,
                currentPage);
    }

    public static PageUtils getSplitResult(Page<?> page) {
        return PageUtils.of(page);
    }
}
