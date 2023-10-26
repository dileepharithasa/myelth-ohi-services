package com.myelth.ohi.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateFormatConversion {
    private DateFormatConversion() {
    }
    public static String convertDateToOhiFormat(String inputDate) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate date = LocalDate.parse(inputDate, inputFormatter);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(outputFormatter);
    }
}