package aqa_2025.day6;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class Task6Model {
    @Id
    Integer int_field;
    String string_field;
    Boolean flag;
}
