package pewpew.smash.game.overlay;

import java.awt.Color;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.constants.Constants;
import pewpew.smash.game.gamemode.GameModeManager;
import pewpew.smash.game.gamemode.GameModeType;
import pewpew.smash.game.states.GameStateType;
import pewpew.smash.game.states.StateManager;
import pewpew.smash.game.ui.Button;
import pewpew.smash.game.ui.Checkbox;
import pewpew.smash.game.ui.Cycler;
import pewpew.smash.game.ui.TextField;
import pewpew.smash.game.utils.FontFactory;
import pewpew.smash.game.utils.ResourcesLoader;

public class HostOverlay extends Overlay {
    private Button playButton, backButton;
    private TextField serverNameField, portField;
    private Cycler gamemodeSelector;

    // Sandbox settings
    private Checkbox sandboxMultiplayerOn;

    // Battle Royale settings
    private Cycler playerCountSelector;
    private Checkbox npcSpawnEnabled;
    private Checkbox structureSpawnEnabled;
    private Cycler gameDurationSelector;

    // Arena settings
    private Cycler mapSelector;
    private Cycler healthPointsSelector;
    private Checkbox respawnEnabled;

    private String selectedMode = "Sandbox";

    public HostOverlay(int x, int y, int width, int height) {
        super(x, y, width, height);
        loadAll();
    }

    private void loadAll() {
        loadBackground();
        loadButtons();
        loadTextField();
        loadGameModeSelector();
        loadSandboxSettings();
        loadBattleRoyaleSettings();
        loadArenaSettings();
    }

    @Override
    public void update() {
        updateCommonComponents();
        updateGameModeSettings();
    }

    private void updateCommonComponents() {
        updateButtons();
        updateTextField();
        gamemodeSelector.update();
    }

    private void updateGameModeSettings() {
        switch (selectedMode) {
            case "Sandbox" -> updateSandboxSettings();
            case "Battle Royale" -> updateBattleRoyaleSettings();
            case "Arena" -> updateArenaSettings();
        }
    }

    @Override
    public void render(Canvas canvas) {
        renderBackground(canvas);
        renderCommonComponents(canvas);
        renderGameModeSettings(canvas);
    }

    private void renderBackground(Canvas canvas) {
        canvas.renderImage(background, x, y, width, height);
        canvas.renderLine(0, 250, 800, 250, 2, Color.WHITE);
    }

    private void renderCommonComponents(Canvas canvas) {
        renderButtons(canvas);
        renderTextFields(canvas);
        renderLabels(canvas);
    }

    private void renderGameModeSettings(Canvas canvas) {
        switch (selectedMode) {
            case "Sandbox" -> renderSandboxSettings(canvas);
            case "Battle Royale" -> renderBattleRoyaleSettings(canvas);
            case "Arena" -> renderArenaSettings(canvas);
        }
    }

    private void loadBackground() {
        this.background = ResourcesLoader.getImage(ResourcesLoader.BACKGROUND_PATH, "overlay");
    }

    private void loadButtons() {
        playButton = new Button(Constants.LEFT_PADDING + 60, 500,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/playButton"),
                () -> loadGame());

        backButton = new Button(Constants.RIGHT_PADDING - 60, 500,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/backButton"), () -> {
                    close();
                    OverlayManager.getInstance().push(OverlayFactory.getOverlay(OverlayType.PLAY));
                });
    }

    private void loadTextField() {
        serverNameField = new TextField(50, 120, 500, 30);
        portField = new TextField(50, 180, 500, 30);
    }

    private void loadGameModeSelector() {
        gamemodeSelector = new Cycler(250, 220, 20, 20,
                new String[] { "Sandbox", "Battle Royale", "Arena" },
                selectedMode, () -> {
                    selectedMode = gamemodeSelector.getCurrentCycle();
                });
    }

    private void loadSandboxSettings() {
        sandboxMultiplayerOn = new Checkbox(50, 280,
                () -> System.out.println("Multiplayer toggled"));
    }

    private void loadBattleRoyaleSettings() {
        playerCountSelector = new Cycler(50, 280, 20, 20,
                new String[] { "4", "8", "16" }, "4",
                () -> System.out.println("Player count changed"));

        npcSpawnEnabled = new Checkbox(50, 320,
                () -> System.out.println("NPC Spawn toggled"));

        structureSpawnEnabled = new Checkbox(50, 360,
                () -> System.out.println("Structure Spawn toggled"));

        gameDurationSelector = new Cycler(50, 400, 20, 20,
                new String[] { "Fast", "Normal", "Long" }, "Normal",
                () -> System.out.println("Duration changed"));
    }

    private void loadArenaSettings() {
        mapSelector = new Cycler(50, 280, 20, 20,
                new String[] { "Desert", "Forest", "Castle" }, "Desert",
                () -> System.out.println("Map changed"));

        healthPointsSelector = new Cycler(50, 320, 20, 20,
                new String[] { "100", "150", "200" }, "100",
                () -> System.out.println("HP changed"));

        respawnEnabled = new Checkbox(50, 360,
                () -> System.out.println("Respawn toggled"));
    }

    private void updateButtons() {
        playButton.update();
        backButton.update();
    }

    private void updateTextField() {
        serverNameField.update();
        portField.update();
    }

    private void updateSandboxSettings() {
        sandboxMultiplayerOn.update();
    }

    private void updateBattleRoyaleSettings() {
        playerCountSelector.update();
        npcSpawnEnabled.update();
        structureSpawnEnabled.update();
        gameDurationSelector.update();
    }

    private void updateArenaSettings() {
        mapSelector.update();
        healthPointsSelector.update();
        respawnEnabled.update();
    }

    private void renderButtons(Canvas canvas) {
        playButton.render(canvas);
        backButton.render(canvas);
    }

    private void renderTextFields(Canvas canvas) {
        serverNameField.render(canvas);
        portField.render(canvas);
        gamemodeSelector.render(canvas);
    }

    private void renderLabels(Canvas canvas) {
        canvas.setColor(Color.WHITE);
        renderTitle(canvas);
        renderFieldLabels(canvas);
    }

    private void renderTitle(Canvas canvas) {
        FontFactory.IMPACT_LARGE.applyFont(canvas);
        String title = "Create a game";
        int titleWidth = FontFactory.IMPACT_LARGE.getFontWidth(title, canvas);
        canvas.renderString(title, (width - titleWidth) / 2, 50);
    }

    private void renderFieldLabels(Canvas canvas) {
        FontFactory.IMPACT_SMALL.applyFont(canvas);
        canvas.renderString("Server name", 50, 105);
        canvas.renderString("Port", 50, 172);
        canvas.renderString("Game mode: " + gamemodeSelector.getCurrentCycle(), 50, 235);
        FontFactory.resetFont(canvas);
    }

    private void renderSandboxSettings(Canvas canvas) {
        if (!selectedMode.equals("Sandbox"))
            return;

        FontFactory.IMPACT_SMALL.applyFont(canvas);
        canvas.renderString("Multiplayer: " + (sandboxMultiplayerOn.isChecked() ? "Yes" : "No"), 100, 295);
        sandboxMultiplayerOn.render(canvas);
        FontFactory.resetFont(canvas);
    }

    private void renderBattleRoyaleSettings(Canvas canvas) {
        if (!selectedMode.equals("Battle Royale"))
            return;

        FontFactory.IMPACT_SMALL.applyFont(canvas);

        canvas.renderString("Player Count: " + playerCountSelector.getCurrentCycle(), 100, 295);
        renderPlayerCountDisclaimer(canvas);

        canvas.renderString("NPC Spawn: " + (npcSpawnEnabled.isChecked() ? "Yes" : "No"), 100, 335);
        canvas.renderString("Structure Spawn: " + (structureSpawnEnabled.isChecked() ? "Yes" : "No"), 100, 375);
        canvas.renderString("Game Duration: " + gameDurationSelector.getCurrentCycle(), 100, 415);
        renderDurationDescription(canvas);
        renderBRComponents(canvas);

        FontFactory.resetFont(canvas);
    }

    private void renderPlayerCountDisclaimer(Canvas canvas) {
        if (playerCountSelector.getCurrentCycle().equals("16")) {
            canvas.setColor(Color.YELLOW);
            canvas.renderString("Warning: Hosting 16 players requires a powerful PC", 240, 295);
            canvas.setColor(Color.WHITE);
        }
    }

    private void renderBRComponents(Canvas canvas) {
        playerCountSelector.render(canvas);
        npcSpawnEnabled.render(canvas);
        structureSpawnEnabled.render(canvas);
        gameDurationSelector.render(canvas);
    }

    private void renderDurationDescription(Canvas canvas) {
        String description = switch (gameDurationSelector.getCurrentCycle()) {
            case "Fast" -> "Storm advances quickly (3 minutes)";
            case "Normal" -> "Standard storm speed (8 minutes)";
            case "Long" -> "Slower storm progression (15 minutes)";
            default -> "";
        };
        canvas.renderString(description, 100, 435);
    }

    private void renderArenaSettings(Canvas canvas) {
        if (!selectedMode.equals("Arena"))
            return;

        FontFactory.IMPACT_SMALL.applyFont(canvas);

        canvas.renderString("Map: " + mapSelector.getCurrentCycle(), 100, 295);
        canvas.renderString("Player HP: " + healthPointsSelector.getCurrentCycle(), 100, 335);
        canvas.renderString("Enable Respawn: " + (respawnEnabled.isChecked() ? "Yes" : "No"), 100, 375);
        renderArenaComponents(canvas);

        FontFactory.resetFont(canvas);
    }

    private void renderArenaComponents(Canvas canvas) {
        mapSelector.render(canvas);
        healthPointsSelector.render(canvas);
        respawnEnabled.render(canvas);
    }

    private void loadGame() {
        StateManager.getInstance().setState(GameStateType.PLAYING);
        GameModeManager.getInstance().setGameMode(GameModeType.SANDBOX);
        GameModeManager.getInstance().getCurrentGameMode().build(new String[] { "127.0.0.1", "12345", "true" });
        close();
    }
}