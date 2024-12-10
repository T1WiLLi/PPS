package pewpew.smash.game.network.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pewpew.smash.game.audio.AudioClip;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AudioPacket extends BasePacket {
    private AudioClip clip;
    private double volume;
    private double pan;
}
