package util;

import exception.FormatException;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

public class DateAndTimeFormatter {

    public Object getFormattedDateTime(Object o, Type objType) {
       if(objType.equals(LocalDate.class))
           return LocalDate.parse(o.toString());
        else if (objType.equals(LocalDateTime.class)) {
            return  LocalDateTime.parse(o.toString());
        } else if (objType.equals(OffsetDateTime.class)) {
            return  OffsetDateTime.parse(o.toString());
        } else if (objType.equals(ZonedDateTime.class)) {
            return  ZonedDateTime.parse(o.toString());
        } else {
            throw new FormatException("Unsupported DateTime class: " + objType);
        }
    }

}
