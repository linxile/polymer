package com.polymer.framework.common.utils;

import com.polymer.framework.common.core.text.StrFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * @author polymer
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {
    private static final Logger log = LoggerFactory.getLogger(StringUtils.class);
    private static final Pattern UNDERLINE_PATTERN = Pattern.compile("_([a-z])");
    private static final Pattern HYPHEN_PATTERN = Pattern.compile("-([a-z])");
    /**
     * 空字符串
     */
    private static final String NULLSTR = "";

    /**
     * 下划线
     */
    private static final char SEPARATOR = '_';

    /**
     * 星号
     */
    private static final char ASTERISK = '*';

    /**
     * 字符串常量：逗号 {@code ","}
     */
    public static final String COMMA = ",";

    /**
     * * 判断一个Collection是否为空， 包含List，Set，Queue
     *
     * @param coll 要判断的Collection
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(Collection<?> coll) {
        return isNull(coll) || coll.isEmpty();
    }

    /**
     * * 判断一个Collection是否非空，包含List，Set，Queue
     *
     * @param coll 要判断的Collection
     * @return true：非空 false：空
     */
    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

    /**
     * * 判断一个对象数组是否为空
     *
     * @param objects 要判断的对象数组
     *                * @return true：为空 false：非空
     */
    public static boolean isEmpty(Object[] objects) {
        return isNull(objects) || (objects.length == 0);
    }

    /**
     * * 判断一个对象数组是否非空
     *
     * @param objects 要判断的对象数组
     * @return true：非空 false：空
     */
    public static boolean isNotEmpty(Object[] objects) {
        return !isEmpty(objects);
    }

    /**
     * * 判断一个Map是否为空
     *
     * @param map 要判断的Map
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return isNull(map) || map.isEmpty();
    }

    /**
     * * 判断一个Map是否为空
     *
     * @param map 要判断的Map
     * @return true：非空 false：空
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * * 判断一个字符串是否为空串
     *
     * @param str String
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(String str) {
        return isNull(str) || NULLSTR.equals(str.trim());
    }

    /**
     * * 判断一个字符串是否为非空串
     *
     * @param str String
     * @return true：非空串 false：空串
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * * 判断一个对象是否为空
     *
     * @param object Object
     * @return true：为空 false：非空
     */
    public static boolean isNull(Object object) {
        return object == null;
    }

    /**
     * * 判断一个对象是否非空
     *
     * @param object Object
     * @return true：非空 false：空
     */
    public static boolean isNotNull(Object object) {
        return !isNull(object);
    }

    /**
     * 去空格
     */
    public static String trim(String str) {
        return (str == null ? "" : str.trim());
    }

    /**
     * 截取字符串
     *
     * @param str   字符串
     * @param start 开始
     * @return 结果
     */
    public static String substring(final String str, int start) {
        if (str == null) {
            return NULLSTR;
        }

        if (start < 0) {
            start = str.length() + start;
        }

        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return NULLSTR;
        }

        return str.substring(start);
    }

    /**
     * 截取字符串
     *
     * @param str   字符串
     * @param start 开始
     * @param end   结束
     * @return 结果
     */
    public static String substring(final String str, int start, int end) {
        if (str == null) {
            return NULLSTR;
        }

        if (end < 0) {
            end = str.length() + end;
        }
        if (start < 0) {
            start = str.length() + start;
        }

        if (end > str.length()) {
            end = str.length();
        }

        if (start > end) {
            return NULLSTR;
        }

        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
    }

    /**
     * 格式化文本, {} 表示占位符<br>
     * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
     * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
     * 例：<br>
     * 通常使用：format("this is {} for {}", "a", "b") -> this is a for b<br>
     * 转义{}： format("this is \\{} for {}", "a", "b") -> this is \{} for a<br>
     * 转义\： format("this is \\\\{} for {}", "a", "b") -> this is \a for b<br>
     *
     * @param template 文本模板，被替换的部分用 {} 表示
     * @param params   参数值
     * @return 格式化后的文本
     */
    public static String format(String template, Object... params) {
        if (isEmpty(params) || isEmpty(template)) {
            return template;
        }
        return StrFormatter.format(template, params);
    }

    /**
     * 格式化文本，使用 {varName} 占位<br>
     * map = {a: "aValue", b: "bValue"} format("{a} and {b}", map) ---=》 aValue and bValue
     *
     * @param template   文本模板，被替换的部分用 {key} 表示
     * @param map        参数值对
     * @return 格式化后的文本
     */
    public static String replaceTemplate(String template, Map<String, Object> map) {
        if (isEmpty(map) || isEmpty(template)) {
            return template;
        }
        return StrFormatter.replaceTemplate(template, map);
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        return (T) obj;
    }

    /**
     * 如果给定对象为{@code null}返回默认值
     *
     * <pre>
     * ObjectUtil.defaultIfNull(null, null)      = null
     * ObjectUtil.defaultIfNull(null, "")        = ""
     * ObjectUtil.defaultIfNull(null, "zz")      = "zz"
     * ObjectUtil.defaultIfNull("abc", *)        = "abc"
     * ObjectUtil.defaultIfNull(Boolean.TRUE, *) = Boolean.TRUE
     * </pre>
     *
     * @param <T>          对象类型
     * @param object       被检查对象，可能为{@code null}
     * @param defaultValue 被检查对象为{@code null}返回的默认值，可以为{@code null}
     * @return 被检查对象为{@code null}返回默认值，否则返回原值
     * @since 3.0.7
     */
    public static <T> T defaultIfNull(final T object, final T defaultValue) {
        return isNull(object) ? defaultValue : object;
    }


    /**
     * 下划线转驼峰命名
     */
    public static String underlineToCamel(String underlineStr) {
        if (isEmpty(underlineStr)) {
            return underlineStr;
        }

        underlineStr = underlineStr.toLowerCase();
        Matcher matcher = UNDERLINE_PATTERN.matcher(underlineStr);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            matcher.appendReplacement(result, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(result);

        return result.toString();
    }

    /**
     * 转换为 PascalCase（帕斯卡命名法/大驼峰命名法）- 替换 NamingCase.toPascalCase
     * 示例: "user_name" -> "UserName", "user-name" -> "UserName", "userName" -> "UserName"
     * @param str 输入字符串
     * @return PascalCase 格式的字符串
     */
    public static String toPascalCase(String str) {
        if (!org.springframework.util.StringUtils.hasText(str)) {
            return str;
        }

        try {
            // 先转换为驼峰命名
            String camelCase = toCamelCase(str);
            // 然后将首字母大写
            return upperFirst(camelCase);
        } catch (Exception e) {
            log.warn("转换为PascalCase失败: {}", str, e);
            return str;
        }
    }

    /**
     * 转换为 CamelCase（驼峰命名法）- 首字母小写
     * 示例: "user_name" -> "userName", "user-name" -> "userName", "UserName" -> "userName"
     */
    public static String toCamelCase(String str) {
        if (!org.springframework.util.StringUtils.hasText(str)) {
            return str;
        }

        // 处理下划线命名
        String result = str;
        Matcher matcher = UNDERLINE_PATTERN.matcher(result.toLowerCase());
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        result = sb.toString();

        // 处理连字符命名
        matcher = HYPHEN_PATTERN.matcher(result);
        sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        result = sb.toString();

        // 如果已经是驼峰命名，确保首字母小写
        return lowerFirst(result);
    }

    /**
     * 首字母小写
     */
    public static String lowerFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        char firstChar = str.charAt(0);
        if (Character.isLowerCase(firstChar)) {
            return str;
        }

        return Character.toLowerCase(firstChar) + str.substring(1);
    }

    /**
     * 首字母大写（替换 StrUtil.upperFirst）
     * @param str 输入字符串
     * @return 首字母大写的字符串
     */
    public static String upperFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        char firstChar = str.charAt(0);
        if (Character.isUpperCase(firstChar)) {
            return str;
        }

        return Character.toUpperCase(firstChar) + str.substring(1);
    }

    /**
     * 截取指定分隔符第一次出现后的子字符串 - 替换 StrUtil.subAfter
     * @param str 源字符串
     * @param separator 分隔符
     * @param isWithSeparator 是否包含分隔符
     * @return 分隔符后的子字符串
     */
    public static String subAfter(CharSequence str, CharSequence separator, boolean isWithSeparator) {
        if (isEmpty(str)) {
            return null;
        }

        if (separator == null) {
            return str.toString();
        }

        String strStr = str.toString();
        String separatorStr = separator.toString();

        int index = strStr.indexOf(separatorStr);
        if (index == -1) {
            return "";
        }

        if (isWithSeparator) {
            return strStr.substring(index);
        } else {
            return strStr.substring(index + separatorStr.length());
        }
    }

    /**
     * 截取指定分隔符最后一次出现后的子字符串
     */
    public static String subAfterLast(CharSequence str, CharSequence separator, boolean isWithSeparator) {
        if (isEmpty(str)) {
            return null;
        }

        if (separator == null) {
            return str.toString();
        }

        String strStr = str.toString();
        String separatorStr = separator.toString();

        int index = strStr.lastIndexOf(separatorStr);
        if (index == -1) {
            return "";
        }

        if (isWithSeparator) {
            return strStr.substring(index);
        } else {
            return strStr.substring(index + separatorStr.length());
        }
    }

    /**
     * 截取指定分隔符第一次出现前的子字符串
     */
    public static String subBefore(CharSequence str, CharSequence separator, boolean isWithSeparator) {
        if (isEmpty(str)) {
            return null;
        }

        if (separator == null) {
            return str.toString();
        }

        String strStr = str.toString();
        String separatorStr = separator.toString();

        int index = strStr.indexOf(separatorStr);
        if (index == -1) {
            return strStr;
        }

        if (isWithSeparator) {
            return strStr.substring(0, index + separatorStr.length());
        } else {
            return strStr.substring(0, index);
        }
    }

    /**
     * 截取指定分隔符最后一次出现前的子字符串
     */
    public static String subBeforeLast(CharSequence str, CharSequence separator, boolean isWithSeparator) {
        if (isEmpty(str)) {
            return null;
        }

        if (separator == null) {
            return str.toString();
        }

        String strStr = str.toString();
        String separatorStr = separator.toString();

        int index = strStr.lastIndexOf(separatorStr);
        if (index == -1) {
            return strStr;
        }

        if (isWithSeparator) {
            return strStr.substring(0, index + separatorStr.length());
        } else {
            return strStr.substring(0, index);
        }
    }

    /**
     * 移除字符串的前缀 - 替换 StrUtil.removePrefix
     * @param str 源字符串
     * @param prefix 要移除的前缀
     * @return 移除前缀后的字符串
     */
    public static String removePrefix(CharSequence str, CharSequence prefix) {
        if (isEmpty(str) || isEmpty(prefix)) {
            return str == null ? null : str.toString();
        }

        String strStr = str.toString();
        String prefixStr = prefix.toString();

        if (strStr.startsWith(prefixStr)) {
            return strStr.substring(prefixStr.length());
        }

        return strStr;
    }


    /**
     * 移除字符串的后缀
     */
    public static String removeSuffix(CharSequence str, CharSequence suffix) {
        if (isEmpty(str) || isEmpty(suffix)) {
            return str == null ? null : str.toString();
        }

        String strStr = str.toString();
        String suffixStr = suffix.toString();

        if (strStr.endsWith(suffixStr)) {
            return strStr.substring(0, strStr.length() - suffixStr.length());
        }

        return strStr;
    }

    /**
     * 判断字符串是否以指定后缀结尾 - 替换 StrUtil.endWith
     * @param str 要检查的字符串
     * @param suffix 后缀
     * @return 如果以指定后缀结尾返回 true，否则返回 false
     */
    public static boolean endWith(CharSequence str, CharSequence suffix) {
        return endWith(str, suffix, false);
    }

    /**
     * 判断字符串是否以指定后缀结尾
     */
    public static boolean endWith(CharSequence str, CharSequence suffix, boolean ignoreCase) {
        if (str == null || suffix == null) {
            return false;
        }

        if (str instanceof String && suffix instanceof String) {
            if (ignoreCase) {
                return ((String) str).toLowerCase().endsWith(((String) suffix).toLowerCase());
            } else {
                return ((String) str).endsWith((String) suffix);
            }
        }

        int strLength = str.length();
        int suffixLength = suffix.length();

        if (suffixLength > strLength) {
            return false;
        }

        int strOffset = strLength - suffixLength;

        if (ignoreCase) {
            for (int i = 0; i < suffixLength; i++) {
                char c1 = Character.toLowerCase(str.charAt(strOffset + i));
                char c2 = Character.toLowerCase(suffix.charAt(i));
                if (c1 != c2) {
                    return false;
                }
            }
        } else {
            for (int i = 0; i < suffixLength; i++) {
                if (str.charAt(strOffset + i) != suffix.charAt(i)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 判断字符串是否以指定字符结尾
     */
    public static boolean endWith(CharSequence str, char suffix) {
        if (str == null || str.length() == 0) {
            return false;
        }
        return str.charAt(str.length() - 1) == suffix;
    }

    /**
     * 分割为 List
     */
    public static List<String> splitToList(String str, String separator) {
        if (str == null) {
            return new ArrayList<>();
        }

        String[] parts = split(str, separator);
        return Arrays.asList(parts);
    }


    /**
     * camelCase 转 kebab-case
     * <p>
     * 将驼峰命名的字符串转换为短横线（中划线）命名，用于生成前端文件名、目录名等。
     * <p>
     * 转换规则：
     * <ul>
     *     <li>遇到「小写字母或数字 + 大写字母」的边界，插入中划线</li>
     *     <li>整体转为小写</li>
     * </ul>
     * <p>
     * 示例：
     * <pre>
     * importExportRecord → import-export-record
     * dictType           → dict-type
     * logLogin           → log-login
     * smsPlatform        → sms-platform
     * baseClass          → base-class
     * </pre>
     *
     * @param str 待转换的 camelCase 字符串
     * @return 转换后的 kebab-case 字符串；若入参为 null 或空字符串，原样返回
     */
    public static String camelToKebab(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.replaceAll("([a-z0-9])([A-Z])", "$1-$2").toLowerCase();
    }
}
