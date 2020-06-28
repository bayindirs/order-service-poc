package com.example.orderservicepoc.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

  static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

  public static String toString(Date date) {
    if (date != null) {
      return sdf.format(date);
    }
    return null;
  }
}
