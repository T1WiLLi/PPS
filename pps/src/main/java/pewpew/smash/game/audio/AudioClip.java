package pewpew.smash.game.audio;

public enum AudioClip {
    BUTTON_HOVERED("ButtonHovered"),
    BUTTON_PRESSED("ButtonPressed"),
    CHECKED("checked"),
    MAIN_THEME("MainTheme"),
    SWAPPED("swapped"),
    WALKING_GRASS("sfx/WalkingGrass"),
    PLAYER_DEATH("sfx/playerDead"),
    BULLET_SHOT("sfx/bullet"),
    PLAYER_DAMAGE("sfx/playerDamage"),
    BULLET_EXPLODE("sfx/bulletExplode"),
    CASE_DESTROYED("sfx/caseDestroyed"),
    WEAPON_SWAPPED("sfx/weaponSwapped"),
    WOOSH("sfx/woosh");

    private final String fileName;

    AudioClip(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
