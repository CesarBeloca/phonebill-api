package billing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class TelephoneBillCalculatorImpl implements TelephoneBillCalculator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private static final LocalTime PEAK_START = LocalTime.of(8, 0, 0);
    private static final LocalTime PEAK_END = LocalTime.of(16, 0, 0);
    private static final BigDecimal PEAK_RATE = BigDecimal.valueOf(1.0);
    private static final BigDecimal OFF_PEAK_RATE = BigDecimal.valueOf(0.5);
    private static final BigDecimal LONG_CALL_DISCOUNT_RATE = BigDecimal.valueOf(0.2);
    private static final int FREE_MINUTES_THRESHOLD = 5;

    @Override
    public BigDecimal calculate(String phoneLog) {
        if (phoneLog == null || phoneLog.isBlank()) {
            return BigDecimal.ZERO;
        }

        List<CallRecord> calls = parseCallRecords(phoneLog);

        if (calls.isEmpty()) {
            return BigDecimal.ZERO;
        }

        String promoNumber = findPromoNumber(calls);

        BigDecimal total = BigDecimal.ZERO;
        for (CallRecord call : calls) {
            if (!call.number.equals(promoNumber)) {
                total = total.add(calculateCallCost(call));
            }
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private List<CallRecord> parseCallRecords(String phoneLog) {
        List<CallRecord> records = new ArrayList<>();
        String[] lines = phoneLog.split("\\r?\\n");
        for (String line : lines) {
            if (line.isBlank()) continue;
            String[] parts = line.split(",");
            if (parts.length != 3) {
                // Invalid line, skip (should not happen per problem statement)
                continue;
            }
            String number = parts[0].trim();
            LocalDateTime start = LocalDateTime.parse(parts[1].trim(), DATE_FORMATTER);
            LocalDateTime end = LocalDateTime.parse(parts[2].trim(), DATE_FORMATTER);
            records.add(new CallRecord(number, start, end));
        }
        return records;
    }

    private String findPromoNumber(List<CallRecord> calls) {
        Map<String, Long> frequency = calls.stream()
                .collect(Collectors.groupingBy(call -> call.number, Collectors.counting()));

        long maxFreq = frequency.values().stream()
                .max(Long::compareTo)
                .orElse(0L);

        List<String> candidates = frequency.entrySet().stream()
                .filter(entry -> entry.getValue() == maxFreq)
                .map(Map.Entry::getKey)
                .toList();

        return candidates.stream()
                .max((a, b) -> {
                    java.math.BigInteger ba = new java.math.BigInteger(a);
                    java.math.BigInteger bb = new java.math.BigInteger(b);
                    return ba.compareTo(bb);
                })
                .orElseThrow(() -> new IllegalStateException("No candidate found"));
    }

    private BigDecimal calculateCallCost(CallRecord call) {
        long totalSeconds = Duration.between(call.start, call.end).getSeconds();
        int startedMinutes = (int) Math.ceil(totalSeconds / 60.0);

        BigDecimal cost = BigDecimal.ZERO;
        for (int minuteIdx = 0; minuteIdx < startedMinutes; minuteIdx++) {
            LocalDateTime minuteStart = call.start.plusMinutes(minuteIdx);
            BigDecimal rate;
            if (minuteIdx < FREE_MINUTES_THRESHOLD) {
                rate = isPeak(minuteStart) ? PEAK_RATE : OFF_PEAK_RATE;
            } else {
                rate = LONG_CALL_DISCOUNT_RATE;
            }
            cost = cost.add(rate);
        }
        return cost;
    }

    private boolean isPeak(LocalDateTime dateTime) {
        LocalTime time = dateTime.toLocalTime();
        return !time.isBefore(PEAK_START) && time.isBefore(PEAK_END);
    }
}