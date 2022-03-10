package com.congestiontaxcalculator;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CongestionTaxCalculatorController {

  CongestionTaxCalculatorService congestionTaxCalculatorService;

  @Autowired
  CongestionTaxCalculatorController(CongestionTaxCalculatorService congestionTaxCalculatorService) {
    this.congestionTaxCalculatorService = congestionTaxCalculatorService;
  }

  // Example: curl -G http://localhost:8080/v1/congestiontax/calculate -d \
  // "vehicleType=Car&tollDateTimes=2013-02-08T08:27:00,2013-02-08T17:27:00"
  @GetMapping(path = "/v1/congestiontax/calculate", produces = {"application/json"})
  public Integer calculateCongestionTax(@RequestParam String vehicleType,
      @RequestParam String[] tollDateTimes) {
    return congestionTaxCalculatorService.getTax(vehicleType,
        Arrays.stream(tollDateTimes).map(LocalDateTime::parse).collect(Collectors.toList()));
  }
}
