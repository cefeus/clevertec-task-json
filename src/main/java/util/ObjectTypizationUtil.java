package util;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;

public class ObjectTypizationUtil {

    public static <T> boolean isDate(Class<T> type) {
        return type.equals(LocalTime.class) || type.equals(LocalDate.class) || type.equals(LocalDateTime.class)
                || type.equals(OffsetTime.class) || type.equals(OffsetDateTime.class)
                || type.equals(ZonedDateTime.class);
    }
}
