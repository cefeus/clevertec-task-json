package service.serialize;

import org.junit.jupiter.api.Test;
import util.TestDataBuilder;

import static org.assertj.core.api.Assertions.assertThat;


class FieldProcessorTest {

    @Test
    void consume_shouldReturnJsonReprezentatingValueType_WithoutBrackets() {
        // given
        String expected = TestDataBuilder.JSON_ID_WITH_UUID;
        String name = TestDataBuilder.ID_FIELD_NAME;
        String value = TestDataBuilder.UUID_VALUE;

        // when
        String actual = FieldProcessor.consume(name, value);

        // then
        assertThat(actual).isEqualTo(expected);
    }
}