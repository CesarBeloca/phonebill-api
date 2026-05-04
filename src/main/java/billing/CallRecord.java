package billing;

import java.time.LocalDateTime;

public class CallRecord {
    final String number;
    final LocalDateTime start;
    final LocalDateTime end;

    CallRecord(String number, LocalDateTime start, LocalDateTime end) {
        this.number = number;
        this.start = start;
        this.end = end;
    }
}
