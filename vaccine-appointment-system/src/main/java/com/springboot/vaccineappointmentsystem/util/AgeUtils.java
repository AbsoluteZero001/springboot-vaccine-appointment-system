package com.springboot.vaccineappointmentsystem.util;

import java.time.LocalDate;
import java.time.Period;

public final class AgeUtils {

    private AgeUtils() {
    }

    /**
     * 根据出生日期计算周岁年龄
     */
    public static int calculateAge(LocalDate birthday) {
        if (birthday == null) return 0;
        return Period.between(birthday, LocalDate.now()).getYears();
    }

    /**
     * 根据出生日期计算精确年龄描述（含月/天）
     */
    public static String calculateAgeDetail(LocalDate birthday) {
        if (birthday == null) return "未知";
        Period period = Period.between(birthday, LocalDate.now());
        int years = period.getYears();
        int months = period.getMonths();
        if (years > 0) {
            return months > 0 ? years + "岁" + months + "个月" : years + "岁";
        }
        return months > 0 ? months + "个月" : period.getDays() + "天";
    }

    /**
     * 判断年龄是否在年龄范围描述内
     * ageRange 格式如 "8月龄以上", "3月龄至3周岁", "60岁以上"
     * 返回 true 表示符合接种年龄
     */
    public static boolean isAgeInRange(LocalDate birthday, String ageRange) {
        if (birthday == null || ageRange == null || ageRange.isBlank()) return true;
        int ageMonths = Period.between(birthday, LocalDate.now()).getYears() * 12
                + Period.between(birthday, LocalDate.now()).getMonths();
        try {
            String lower = ageRange;
            // 解析下限
            int lowerMonths = 0;
            if (lower.contains("月")) {
                lowerMonths = Integer.parseInt(lower.replaceAll("[^0-9]", ""));
            } else if (lower.contains("岁")) {
                lowerMonths = Integer.parseInt(lower.replaceAll("[^0-9]", "")) * 12;
            } else if (lower.contains("天")) {
                lowerMonths = 0; // 新生儿，月龄不足1月
            }
            // 解析上限（如果有"至"）
            int upperMonths = Integer.MAX_VALUE;
            if (ageRange.contains("至")) {
                String upper = ageRange.substring(ageRange.indexOf("至") + 1);
                if (upper.contains("岁")) {
                    upperMonths = Integer.parseInt(upper.replaceAll("[^0-9]", "")) * 12;
                } else if (upper.contains("月")) {
                    upperMonths = Integer.parseInt(upper.replaceAll("[^0-9]", ""));
                }
            }
            return ageMonths >= lowerMonths && ageMonths <= upperMonths;
        } catch (NumberFormatException e) {
            return true; // 解析失败时默认允许
        }
    }
}
