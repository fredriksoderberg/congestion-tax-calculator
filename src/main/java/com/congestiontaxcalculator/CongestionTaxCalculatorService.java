package com.congestiontaxcalculator;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CongestionTaxCalculatorService {


  @Value("${vehicles.tollfree}")
  private List<String> tollFreeVehicles;

  @Value("#{${toll.times}}")
  private Map<String, Integer> tollsByTime = new HashMap<>();
  
  public int getTax(String vehicleType, List<LocalDateTime> dateTimes) {
    Collections.sort(dateTimes);

    LocalDateTime intervalStart = dateTimes.get(0);
    int totalFee = 0;

    for (var dateTime : dateTimes) {
      int nextFee = GetTollFee(dateTime, vehicleType);
      int tempFee = GetTollFee(intervalStart, vehicleType);

      long minutes = ChronoUnit.MINUTES.between(intervalStart, dateTime);

      if (minutes <= 60) {
        if (totalFee > 0) {
          totalFee -= tempFee;
        }
        if (nextFee >= tempFee) {
          tempFee = nextFee;
        }
        totalFee += tempFee;
      } else {
        totalFee += nextFee;
      }
    }

    if (totalFee > 60) {
      totalFee = 60;
    }
    return totalFee;
  }

  private boolean IsTollFreeVehicle(String vehicleType) {
    return tollFreeVehicles.contains(vehicleType);
  }

  public int GetTollFee(LocalDateTime dateTime, String vehicleType) {
    if (IsTollFreeDate(dateTime) || IsTollFreeVehicle(vehicleType)) {
      return 0;
    }

    for (var entry : tollsByTime.entrySet()) {
      String[] split = entry.getKey().split("-");
      if (inTimeRange(dateTime, split[0], split[1])) {
        return entry.getValue();
      }
    }
    return 0;
  }

  private boolean inTimeRange(LocalDateTime targetDateTime, String startTime, String endTime) {
    LocalTime target = targetDateTime.toLocalTime();
    LocalTime start = LocalTime.parse(startTime);
    LocalTime end = LocalTime.parse(endTime);
    return target.isAfter(start) && target.isBefore(end);
  }

  private boolean IsTollFreeDate(LocalDateTime date) {
    int year = date.getYear();
    int month = date.getMonth().getValue();
    int day = date.getDayOfWeek().getValue();
    int dayOfMonth = date.getDayOfMonth();

    if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
      return true;
    }

    if (year == 2013) {
      if ((month == 1 && dayOfMonth == 1) || (month == 3 && (dayOfMonth == 28 || dayOfMonth == 29))
          || (month == 4 && (dayOfMonth == 1 || dayOfMonth == 30)) || (month == 5 && (
          dayOfMonth == 1 || dayOfMonth == 8 || dayOfMonth == 9)) || (month == 6 && (dayOfMonth == 5
          || dayOfMonth == 6 || dayOfMonth == 21)) || (month == 7) || (month == 11
          && dayOfMonth == 1) || (month == 12 && (dayOfMonth == 24 || dayOfMonth == 25
          || dayOfMonth == 26 || dayOfMonth == 31))) {
        return true;
      }
    }
    return false;
  }
}
