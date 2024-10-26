package pewpew.smash.game.objects;

import lombok.Getter;

@Getter
public enum SpecialType {
    SCOPE_X1(1f, "Default Zoom", "Normal field of view."),
    SCOPE_X2(0.5f, "Zoom Level 2", "Increases field of view by 2x."),
    SCOPE_X3(0.25f, "Zoom Level 3", "Increases field of view by 4x."),
    SCOPE_X4(0.125f, "Zoom Level 4", "Increases field of view by 8x.");

    // Add more SpecialTypes here following the same pattern

    private final float value;
    private final String name;
    private final String description;

    SpecialType(float value, String name, String description) {
        this.value = value;
        this.name = name;
        this.description = description;
    }
}