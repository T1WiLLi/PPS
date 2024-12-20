package pewpew.smash.game.audio;

public enum AudioClip {
    BUTTON_HOVERED("ButtonHovered"),
    BUTTON_PRESSED("ButtonPressed"),
    CHECKED("checked"),
    MAIN_THEME("MainTheme"),
    SWAPPED("swapped"),
    WALKING_GRASS("sfx/WalkingGrass"),
    WALKING_WATER("sfx/Walkingwater"),
    WALKING_SAND("sfx/walkSand"),
    PLAYER_DEATH("sfx/playerDead"),
    BULLET_SHOT("sfx/bullet"),
    PLAYER_DAMAGE("sfx/playerDamage"),
    BULLET_EXPLODE("sfx/bulletExplode"),
    BULLET_EXPLODE_02("sfx/bulletExplode_02"),
    CASE_DESTROYED("sfx/caseDestroyed"),
    WEAPON_SWAPPED("sfx/weaponSwapped"),
    BIG_WPEAON_SHOT("sfx/bigGun"),
    HEALING("sfx/healing"),
    MEDIKIT("sfx/medikit"),
    RELOAD("sfx/reload"),
    AMMO_PICKUP("sfx/ammoPickup"),
    CONSUMABLE_PICKUP("sfx/consumablePickup"),
    WOOSH("sfx/woosh");

    private final String fileName;

    AudioClip(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
