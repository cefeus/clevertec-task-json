package service.deserialize;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import entities.Order;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import util.TestDataBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class DeserializerTest {

    private final Deserializer deserializer = new Deserializer();
    private static ObjectMapper mapper;
    @BeforeAll
    static void customizeJackson() {
        mapper = new ObjectMapper();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @SneakyThrows
    @Test
    void deserialize_shouldReturnFilledObject_WhenCorrectJsonPassed() {
        // given
        String json = TestDataBuilder.ordersJson;
        Order expected = mapper.readValue(TestDataBuilder.ordersJson, Order.class);
        // when
        Order actual = (Order) deserializer.deserialize(json, Order.class);
        // then
        assertThat(actual).isEqualTo(expected);
    }
}