package service.serialize;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

import static util.constants.StringLiterals.*;

public class Serializer {

    public  String serialize(Object object) {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        String serialized = Arrays.stream(fields).map(field -> serializeField(field, object)).collect(Collectors.joining(COMMA));
        return LEFT_FIGURE_BRACKET + serialized + RIGHT_FIGURE_BRACKET;
    }

    private  String serializeField(Field field, Object o ) {
        try {
        String fieldName = field.getName();
        field.setAccessible(true);
        Object fieldValue = field.get(o);
        return FieldProcessor.consume(fieldName, fieldValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
