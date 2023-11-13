package service.deserialize.parser.impl;

import exception.JsonSerializationException;
import org.junit.jupiter.api.Test;
import service.deserialize.parser.JsonParser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonParserImplTest {

    private final JsonParser parser = new JsonParserImpl();

    @Test
    void deserializeRecursively_shouldThrowDeserializationException_whenInvalidJsonPassed() {
        // given
        String expected = "Invalid json";
        // when
        Exception exception = assertThrows(JsonSerializationException.class,
                () -> parser.deserializeRecursively("{\"key\":12,\"key\":{\"key1\":12, \"key2\":12}} }"));
        String actual = exception.getMessage();
        // then
        assertThat(actual).contains(expected);
    }
}