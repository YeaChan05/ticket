package org.yechan.testdata;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.yechan.dto.request.ShowScheduleRegisterRequest;
import org.yechan.dto.request.TicketGradeRequest;

public class ShowInfoGenerator {
    public static UUID generateHallKey() {
        return UUID.randomUUID();
    }

    public static String generateUrl() {
        return "https://example.com/show/" + UUID.randomUUID();
    }

    public static String generateDescription() {
        return "This is a sample description for the show.";
    }

    public static String generateTitle() {
        return "Sample Show Title " + UUID.randomUUID();
    }

    public static ShowScheduleRegisterRequest generateSchedule(int plusDay) {
        var hours = 1 + ThreadLocalRandom.current().nextInt(5);
        return new ShowScheduleRegisterRequest(
                LocalDateTime.now().plusDays(plusDay),
                LocalDateTime.now().plusDays(plusDay).plusHours(hours)
        );
    }

    public static TicketGradeRequest generateTicketGrade(String grade) {
        return new TicketGradeRequest(
                grade,
                BigDecimal.valueOf(10000 + 1000.0 * Math.random() * 900),
                (int) (50 + 50 * Math.random())
        );
    }
}
