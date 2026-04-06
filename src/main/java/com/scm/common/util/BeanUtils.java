package com.scm.common.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Bean工具类
 *
 * @author SCM System
 * @since 2026-04-06
 */
public class BeanUtils extends BeanUtils {

    /**
     * 复制属性
     *
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copyProperties(Object source, Object target) {
        try {
            BeanUtils.copyProperties(source, target);
        } catch (BeansException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 复制属性（忽略指定属性）
     *
     * @param source           源对象
     * @param target           目标对象
     * @param ignoreProperties 忽略的属性名
     */
    public static void copyProperties(Object source, Object target, String... ignoreProperties) {
        try {
            BeanUtils.copyProperties(source, target, ignoreProperties);
        } catch (BeansException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 复制对象
     *
     * @param source 源对象
     * @param clazz  目标对象类型
     * @param <T>    目标对象类型
     * @return 目标对象
     */
    public static <T> T copy(Object source, Class<T> clazz) {
        if (source == null) {
            return null;
        }
        try {
            T target = clazz.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 复制列表
     *
     * @param sourceList 源对象列表
     * @param clazz      目标对象类型
     * @param <S>        源对象类型
     * @param <T>        目标对象类型
     * @return 目标对象列表
     */
    public static <S, T> List<T> copyList(List<S> sourceList, Class<T> clazz) {
        if (sourceList == null || sourceList.isEmpty()) {
            return new ArrayList<>();
        }
        return sourceList.stream()
                .map(source -> copy(source, clazz))
                .collect(Collectors.toList());
    }
}
