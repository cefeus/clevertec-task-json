package util;

import entities.Customer;
import entities.Order;
import entities.Product;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

@Builder
@Data
public class TestDataBuilder {

    public static final String UUID_VALUE = "5c8a26d8-15a2-4a83-b43f-22e5997b1ffc";
    public static final String ID_FIELD_NAME = "id";
    public static final String JSON_ID_WITH_UUID = "\""+ID_FIELD_NAME+"\":\"" + UUID_VALUE +"\"";
    @Builder.Default
    private  UUID uuid = UUID.fromString("5c8a26d8-15a2-4a83-b43f-22e5997b1ffc");
    @Builder.Default
    private  String firstName = "Reuben";
    @Builder.Default
    private  String lastName = "Martin";
    @Builder.Default
    private  LocalDate dateBirth = LocalDate.parse("2003-12-01");
    @Builder.Default
    private  String productName = "Apple";
    @Builder.Default
    private  Double productPrice = Double.valueOf("1234.5");
    @Builder.Default
    private  OffsetDateTime createTime = OffsetDateTime.parse("2021-09-30T15:30+01:00");

    public static final String ordersJson = "{\"id\":\""+ UUID_VALUE +"\"," +
            "\"products\":[{\"id\":\""+UUID_VALUE+"\",\"name\":\"Reuben\",\"price\": 100.0},{\"id\":\""+UUID_VALUE+"\",\"name\":\"Martin\",\"price\": 101.0}], \"createDate\":\"2021-09-30T15:30:00+01:00\"}";

    public Product buildProduct() {
        return new Product(uuid, productName, productPrice);
    }


    public Order buildOrder() {
        return new Order(uuid, List.of(buildProduct(), buildProduct()), createTime);
    }

    public Customer buildCustomer() {
        return new Customer(uuid, firstName, lastName, dateBirth, List.of(buildOrder(), buildOrder()));
    }

}
