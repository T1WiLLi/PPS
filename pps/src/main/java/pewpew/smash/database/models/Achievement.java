package pewpew.smash.database.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Achievement {
    private int id;
    private String name;
    private String description;
}
