package pewpew.smash.game.audio;

public enum AudioClip {
    BUTTON_HOVERED("ButtonHovered"),
    BUTTON_PRESSED("ButtonPressed"),
    CHECKED("checked"),
    MAIN_THEME("MainTheme"),
    SWAPPED("swapped"),
    WALKING_GRASS("sfx/WalkingGrass");

    private final String fileName;

    AudioClip(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
