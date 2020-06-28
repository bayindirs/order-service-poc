package com.example.orderservicepoc.util;

import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

  static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

  public static String toString(Date date) {
    if (date != null) {
      return sdf.format(date);
    }
    return null;
  }

  public static Date fromString(String stringDate) throws ParseException {
    if (!StringUtils.isEmpty(stringDate)) {
      return sdf.parse(stringDate);
    }
    return null;
  }

  public static Date addDay(Date date, int amount) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.DAY_OF_MONTH, amount);
    return calendar.getTime();
  }
}
