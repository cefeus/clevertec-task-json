package service.deserialize;

import service.deserialize.parser.JsonParser;
import service.deserialize.parser.impl.JsonParserImpl;

import java.util.Map;

public class Deserializer {

    private final JsonParser parser = new JsonParserImpl();

    public Object deserialize(String input, Class<?> clazz) {
        Map<String, Object> map = parser.deserializeRecursively(input);
        return FieldSetter.deserialize(map, clazz);
    }
}
