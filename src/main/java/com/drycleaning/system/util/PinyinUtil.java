package com.drycleaning.system.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 拼音工具类 - 支持中文转拼音
 */
public class PinyinUtil {

    /**
     * 将中文转换为拼音（首字母缩写）
     * 例如："张三" -> "zs"
     */
    public static String toPinyinInitials(String chinese) {
        if (chinese == null || chinese.trim().isEmpty()) {
            return "";
        }
        
        StringBuilder pinyin = new StringBuilder();
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        
        char[] chars = chinese.toCharArray();
        try {
            for (char c : chars) {
                if (c >= 0x4e00 && c <= 0x9fa5) {
                    String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    if (pinyinArray != null && pinyinArray.length > 0) {
                        pinyin.append(pinyinArray[0].charAt(0));
                    } else {
                        pinyin.append(c);
                    }
                } else if (Character.isLetterOrDigit(c)) {
                    pinyin.append(c);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        
        return pinyin.toString().toLowerCase();
    }

    /**
     * 将中文转换为完整拼音
     * 例如："张三" -> "zhangsan"
     */
    public static String toFullPinyin(String chinese) {
        if (chinese == null || chinese.trim().isEmpty()) {
            return "";
        }
        
        StringBuilder pinyin = new StringBuilder();
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        
        char[] chars = chinese.toCharArray();
        try {
            for (char c : chars) {
                if (c >= 0x4e00 && c <= 0x9fa5) {
                    String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    if (pinyinArray != null && pinyinArray.length > 0) {
                        pinyin.append(pinyinArray[0]);
                    } else {
                        pinyin.append(c);
                    }
                } else if (Character.isLetterOrDigit(c)) {
                    pinyin.append(c);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        
        return pinyin.toString().toLowerCase();
    }

    /**
     * 检查中文是否匹配拼音（支持首字母和全拼）
     * 例如："张三" 匹配 "zs", "zhangsan", "zhang", "san"
     */
    public static boolean matchesPinyin(String chinese, String pinyinInput) {
        if (chinese == null || pinyinInput == null) {
            return false;
        }
        
        // 直接包含匹配
        if (chinese.contains(pinyinInput)) {
            return true;
        }
        
        String input = pinyinInput.toLowerCase().trim();
        String initials = toPinyinInitials(chinese);
        String fullPinyin = toFullPinyin(chinese);
        
        // 首字母匹配
        if (initials.contains(input)) {
            return true;
        }
        
        // 全拼匹配
        if (fullPinyin.contains(input)) {
            return true;
        }
        
        return false;
    }
}
