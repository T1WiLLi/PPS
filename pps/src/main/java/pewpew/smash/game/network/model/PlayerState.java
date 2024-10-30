package pewpew.smash.game.network.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PlayerState {
    private int id;
    private int health;
}
