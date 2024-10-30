package pewpew.smash.game.network.serializer;

import java.util.Map;
import java.util.Objects;

import pewpew.smash.game.objects.WeaponType;

public class WeaponStateSnapshot {
    private final WeaponType weaponType;
    private final Map<String, Object> stateData;

    public WeaponStateSnapshot(WeaponType weaponType, Map<String, Object> stateData) {
        this.weaponType = weaponType;
        this.stateData = stateData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        WeaponStateSnapshot that = (WeaponStateSnapshot) o;
        if (!Objects.equals(weaponType, that.weaponType))
            return false;

        for (Map.Entry<String, Object> entry : stateData.entrySet()) {
            Object thisValue = entry.getValue();
            Object thatValue = that.stateData.get(entry.getKey());

            if (thisValue instanceof Number && thatValue instanceof Number) {
                double diff = ((Number) thisValue).doubleValue() - ((Number) thatValue).doubleValue();
                if (Math.abs(diff) > 0.0001)
                    return false;
            } else if (!Objects.equals(thisValue, thatValue)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(weaponType, stateData);
    }
}
