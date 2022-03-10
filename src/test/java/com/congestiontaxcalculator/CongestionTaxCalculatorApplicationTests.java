package com.congestiontaxcalculator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CongestionTaxCalculatorApplicationTests {

  @Autowired
  CongestionTaxCalculatorService congestionTaxCalculatorService;

  @Test
  void shouldCalculateTaxes() {
    String[] dates = {"2013-02-08T01:27:00", "2013-02-08T06:20:27"};
    int calculatedTax = congestionTaxCalculatorService.getTax("Car",
        Arrays.stream(dates).map(LocalDateTime::parse).collect(Collectors.toList()));
    assertThat(calculatedTax, is(8));
  }

  @Test
  void shouldCalculateTaxesMaxForDay() {
    String[] dates = {"2013-02-08T05:15:00", "2013-02-08T06:45:27", "2013-02-08T07:50:27",
        "2013-02-08T08:52:00", "2013-02-08T09:55:00", "2013-02-08T15:29:00", "2013-02-08T15:47:00",
        "2013-02-08T16:01:00", "2013-02-08T16:48:00", "2013-02-08T17:49:00", "2013-02-08T18:29:00"};
    int calculatedTax = congestionTaxCalculatorService.getTax("Car",
        Arrays.stream(dates).map(LocalDateTime::parse).collect(Collectors.toList()));
    assertThat(calculatedTax, is(60));
  }

  @Test
  void shouldCalculateTaxesSingleCharge() {
    String[] dates = {"2013-02-08T07:20:00", "2013-02-08T07:27:27"};
    int calculatedTax = congestionTaxCalculatorService.getTax("Car",
        Arrays.stream(dates).map(LocalDateTime::parse).collect(Collectors.toList()));
    assertThat(calculatedTax, is(18));
  }

  @Test
  void shouldCalculateTaxesWeekendsOrHolidays() {
    String[] dates = {"2013-01-01T06:27:00", "2013-01-06T06:20:27"};
    int calculatedTax = congestionTaxCalculatorService.getTax("Car",
        Arrays.stream(dates).map(LocalDateTime::parse).collect(Collectors.toList()));
    assertThat(calculatedTax, is(0));
  }

  @Test
  void shouldCalculateTaxesTollFree() {
    String[] dates = {"2013-02-08T06:27:00", "2013-02-08T06:20:27", "2013-02-08T14:35:00",
        "2013-02-08T15:29:00", "2013-02-08T15:47:00", "2013-02-08T16:01:00", "2013-02-08T16:48:00",
        "2013-02-08T17:49:00", "2013-02-08T18:29:00"};
    int calculatedTax = congestionTaxCalculatorService.getTax("Emergency",
        Arrays.stream(dates).map(LocalDateTime::parse).collect(Collectors.toList()));
    assertThat(calculatedTax, is(0));
  }
}
