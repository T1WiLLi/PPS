package pewpew.smash.game.network.packets;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pewpew.smash.game.objects.WeaponType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WeaponStatePacket extends BasePacket {
    private int ownerID;
    private WeaponType weaponType;
    private Map<String, Object> weaponStateData;
}
