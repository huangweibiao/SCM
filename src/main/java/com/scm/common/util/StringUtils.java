package com.scm.common.util;

/**
 * 字符串工具类
 *
 * @author SCM System
 * @since 2026-04-06
 */
public class StringUtils {

    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 判断字符串是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 判断字符串是否为null
     */
    public static boolean isNull(String str) {
        return str == null;
    }

    /**
     * 判断字符串是否不为null
     */
    public static boolean isNotNull(String str) {
        return str != null;
    }

    /**
     * 如果字符串为空，返回默认值
     */
    public static String defaultIfEmpty(String str, String defaultValue) {
        return isEmpty(str) ? defaultValue : str;
    }

    /**
     * 如果字符串为null，返回默认值
     */
    public static String defaultIfNull(String str, String defaultValue) {
        return str == null ? defaultValue : str;
    }

    /**
     * 去除字符串两端的空格
     */
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    /**
     * 比较两个字符串是否相等
     */
    public static boolean equals(String str1, String str2) {
        if (str1 == str2) {
            return true;
        }
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equals(str2);
    }

    /**
     * 比较两个字符串是否相等（忽略大小写）
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        if (str1 == str2) {
            return true;
        }
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equalsIgnoreCase(str2);
    }

    /**
     * 判断字符串是否以指定前缀开头
     */
    public static boolean startsWith(String str, String prefix) {
        if (str == null || prefix == null) {
            return false;
        }
        return str.startsWith(prefix);
    }

    /**
     * 判断字符串是否以指定后缀结尾
     */
    public static boolean endsWith(String str, String suffix) {
        if (str == null || suffix == null) {
            return false;
        }
        return str.endsWith(suffix);
    }

    /**
     * 将字符串转换为指定编码的字节数组
     */
    public static byte[] getBytes(String str, String charset) {
        if (str == null) {
            return null;
        }
        try {
            return str.getBytes(charset);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将字节数组转换为字符串
     */
    public static String newString(byte[] bytes, String charset) {
        if (bytes == null) {
            return null;
        }
        try {
            return new String(bytes, charset);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成订单号
     * 格式: 前缀 + yyyyMMdd + 6位序号
     *
     * @param prefix  前缀
     * @param seq     序号
     * @return 订单号
     */
    public static String generateOrderNo(String prefix, Long seq) {
        String date = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        return prefix + date + String.format("%06d", seq);
    }
}
