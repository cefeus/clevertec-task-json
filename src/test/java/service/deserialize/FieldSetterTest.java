package service.deserialize;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import entities.Order;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import util.TestDataBuilder;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class FieldSetterTest {

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
    void deserialize_shouldReturnObjectOfPassedType_WithFilledFields() {
        // given
        Map<String, Object> mapToPass = mapper.readValue(TestDataBuilder.ordersJson, Map.class);
        Class<?> clazz = Order.class;
        // when
        Object actual = FieldSetter.deserialize(mapToPass, clazz);
        // then
        assertThat(actual).isInstanceOf(clazz)
                .hasFieldOrProperty(Order.Fields.id).isNotNull()
                .hasFieldOrProperty(Order.Fields.createDate).isNotNull()
                .hasFieldOrProperty(Order.Fields.products).isNotNull();
    }
}