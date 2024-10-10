package pewpew.smash.game.audio;

public enum AudioClip {
    BUTTON_HOVERED("ButtonHovered"),
    BUTTON_PRESSED("ButtonPressed"),
    CHECKED("checked"),
    MAIN_THEME("MainTheme"),
    SWAPPED("swapped");

    private final String fileName;

    AudioClip(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
