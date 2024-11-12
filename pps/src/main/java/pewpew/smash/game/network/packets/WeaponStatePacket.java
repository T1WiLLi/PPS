package pewpew.smash.game.network.packets;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pewpew.smash.game.objects.WeaponType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class WeaponStatePacket extends BasePacket {
    private int ownerID;
    private int itemID;
    private WeaponType weaponType;
    private Map<String, Object> weaponStateData;
}
