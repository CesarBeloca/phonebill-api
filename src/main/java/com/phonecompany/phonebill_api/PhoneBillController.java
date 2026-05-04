package com.phonecompany.phonebill_api;

import billing.TelephoneBillCalculator;
import billing.TelephoneBillCalculatorImpl;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PhoneBillController {

    private final TelephoneBillCalculator calculator = new TelephoneBillCalculatorImpl();

    @PostMapping("/calculate")
    public BillResponse calculateBill(@RequestBody BillRequest request) {
        BigDecimal total = calculator.calculate(request.getPhoneLog());
        return new BillResponse(total);
    }

    // Inner classes for request/response
    public static class BillRequest {
        private String phoneLog;

        public String getPhoneLog() { return phoneLog; }
        public void setPhoneLog(String phoneLog) { this.phoneLog = phoneLog; }
    }

    public static class BillResponse {
        private BigDecimal total;

        public BillResponse(BigDecimal total) { this.total = total; }
        public BigDecimal getTotal() { return total; }
        public void setTotal(BigDecimal total) { this.total = total; }
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
