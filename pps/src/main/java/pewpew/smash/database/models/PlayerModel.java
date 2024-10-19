package pewpew.smash.database.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayerModel {
    private int id;
    private String username;
    private String passwordHash;
    private Rank rank;
    private List<Achievement> achievements;
}