package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldNameConstants
public class Customer {

    private UUID id;
    private String firstName;
    private String lastName;
    private LocalDate dateBirth;
    private List<Order> orders;
}
