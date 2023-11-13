package service.serialize;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import entities.Customer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import util.TestDataBuilder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SerializerTest {

    private final Serializer serializer = new Serializer();

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
    void serialize_shouldReturnCorrectJsonFromObject() {
        // given
        Customer customer = TestDataBuilder.builder().build().buildCustomer();
        String expected = mapper.writeValueAsString(customer);

        // when
        String actual = serializer.serialize(customer);

        // then
        assertThat(actual)
                .isEqualTo(expected);
    }
}