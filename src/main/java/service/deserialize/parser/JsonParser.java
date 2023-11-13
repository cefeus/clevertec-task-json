package service.deserialize.parser;

import java.util.Map;

public interface JsonParser {

    Map<String, Object> deserializeRecursively(String input);
}
