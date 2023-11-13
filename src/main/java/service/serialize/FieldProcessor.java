package service.serialize;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static util.constants.StringLiterals.COLON;
import static util.constants.StringLiterals.COMMA;
import static util.constants.StringLiterals.LEFT_FIGURE_BRACKET;
import static util.constants.StringLiterals.QUOTATION_MARK;
import static util.constants.StringLiterals.RIGHT_FIGURE_BRACKET;

public class FieldProcessor {

    private static final Serializer serializer = new Serializer();

    public static String consume(String name, Class<?> type, Object value) {
        if (value instanceof Collection<?>) {
            return asCollection(name, value);
        } else if (value instanceof Map<?, ?>) {
            return asMap(name, value);
        }
        else
            switch(Objects.requireNonNull(value).getClass().getSimpleName()) {
            case "Byte", "Short", "Integer", "Long", "Float", "Double", "Character", "Boolean",
                    "BigInteger", "BigDecimal" -> {
                return asNumber(name, value);
            }
            case "String", "UUID" -> {
                return asString(name, value);
            }
            case "LocalTime", "LocalDate", "LocalDateTime", "OffsetTime", "OffsetDateTime", "ZonedDateTime" -> {
                return asDate(name, value);
            }
            default -> {
                return serializer.serialize(value);
            }
        }
    }

    private static String asDate(String name, Object value) {
        return QUOTATION_MARK + name + QUOTATION_MARK + COLON + QUOTATION_MARK + value.toString() + QUOTATION_MARK;
    }

    private static String asString(String name, Object value) {
        return QUOTATION_MARK + name + QUOTATION_MARK + COLON  + QUOTATION_MARK + value + QUOTATION_MARK;
    }

    private static String asNumber(String name, Object value) {
        return QUOTATION_MARK + name + QUOTATION_MARK + COLON  + value.toString();
    }

    private static String asCollection(String name, Object value) {
        Collection<?> values = (Collection<?>) value;
        String serializedCollection = values.stream()
                .map(val -> consume(null, val.getClass(), val))
                .collect(Collectors.joining(COMMA));
        return QUOTATION_MARK + name + QUOTATION_MARK + COLON + LEFT_FIGURE_BRACKET+ serializedCollection + RIGHT_FIGURE_BRACKET;
    }

    private static String asMap(String name, Object value) {
        Map<?, ?> values = (Map<?, ?>) value;
        String serializedMap = values.entrySet().stream()
                .map(val -> consume(val.getKey().toString(), value.getClass(), val.getValue()))
                .collect(Collectors.joining(COMMA));
        return QUOTATION_MARK + name + QUOTATION_MARK + COLON + LEFT_FIGURE_BRACKET + serializedMap + RIGHT_FIGURE_BRACKET;
    }
}
