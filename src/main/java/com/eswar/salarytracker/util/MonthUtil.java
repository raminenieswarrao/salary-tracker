package com.eswar.salarytracker.util;

import java.time.LocalDate;
import java.time.YearMonth;

public final class MonthUtil {
    private MonthUtil() { }

    public static LocalDate parseMonth(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return YearMonth.parse(value).atDay(1);
    }

    public static String formatMonthInput(LocalDate value) {
        return value == null ? "" : YearMonth.from(value).toString();
    }
}
