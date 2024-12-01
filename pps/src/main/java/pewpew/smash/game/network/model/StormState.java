package pewpew.smash.game.network.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pewpew.smash.game.event.StormStage;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class StormState {
    private int centerX, centerY;
    private float radius;
    private StormStage stage;
}
