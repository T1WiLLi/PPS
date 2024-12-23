package pewpew.smash.game;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.RenderingEngine;
import pewpew.smash.game.Animation.PlayerAnimationManager;
import pewpew.smash.game.audio.AudioPlayer;
import pewpew.smash.game.constants.Constants;
import pewpew.smash.game.gamemode.GameModeFactory;
import pewpew.smash.game.objects.ItemFactory;
import pewpew.smash.game.overlay.OverlayFactory;
import pewpew.smash.game.settings.SettingsManager;
import pewpew.smash.game.states.StateFactory;
import pewpew.smash.game.ui.Loader;
import pewpew.smash.game.utils.FontFactory;
import pewpew.smash.game.utils.ResourcesLoader;

public class Launcher {
    private static final int TOTAL_RESOURCES = 70;
    private static AtomicInteger loadingProgress = new AtomicInteger(0);
    private static BufferedImage background;

    public static void loadResources() {
        RenderingEngine renderingEngine = RenderingEngine.getInstance();
        renderingEngine.start();

        loadInitialResources();

        Loader loader = new Loader(200, 250, 400, 50);

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        CompletableFuture<Void> resourceLoadingTask = preloadResourcesAsync(executor);

        showLoadingScreen(renderingEngine, loader, resourceLoadingTask);

        executor.shutdown();
    }

    private static CompletableFuture<Void> preloadResourcesAsync(ExecutorService executor) {
        return CompletableFuture.runAsync(() -> {
            loadSettings();
        }, executor).thenRunAsync(() -> {
            loadAudio();
        }, executor).thenRunAsync(() -> {
            loadStates();
        }, executor).thenRunAsync(() -> {
            loadGameModes();
        }, executor).thenRunAsync(() -> {
            loadOverlays();
        }, executor).thenRunAsync(() -> {
            loadItemPreview();
        }, executor).thenRunAsync(() -> {
            loadPlayerSprites();
        }, executor);
    }

    private static void showLoadingScreen(RenderingEngine renderingEngine, Loader loader,
            CompletableFuture<Void> resourceLoadingTask) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            loader.setProgress((loadingProgress.get() * 100) / TOTAL_RESOURCES);
            renderLoadingScreen(renderingEngine, loader);
        }, 0, 100, TimeUnit.MILLISECONDS);

        resourceLoadingTask.join();

        loader.setProgress(100);
        renderLoadingScreen(renderingEngine, loader);

        scheduler.shutdown();
    }

    private static void renderLoadingScreen(RenderingEngine renderingEngine, Loader loader) {
        Canvas canvas = renderingEngine.getCanvas();
        renderBackground(canvas);
        renderLoadingTitle(canvas);
        loader.render(canvas);
        FontFactory.resetFont(canvas);
        renderingEngine.renderCanvasOnScreen();
    }

    private static void renderLoadingTitle(Canvas canvas) {
        FontFactory.IMPACT_X_LARGE.applyFont(canvas);
        String title = "Loading...";
        canvas.renderString(
                title,
                (Constants.DEFAULT_SCREEN_WIDTH - FontFactory.IMPACT_X_LARGE.getFontWidth(title, canvas)) / 2,
                100,
                Color.WHITE);
    }

    private static void renderBackground(Canvas canvas) {
        canvas.renderImage(background, 0, 0, Constants.DEFAULT_SCREEN_WIDTH, Constants.DEFAULT_SCREEN_HEIGHT);
    }

    private static void loadSettings() {
        SettingsManager.getInstance();
        incrementLoadingProgress();
    }

    private static void loadAudio() {
        AudioPlayer.getInstance();
        SettingsManager.getInstance().updateGameSettings();
        incrementLoadingProgress();
    }

    private static void loadStates() {
        StateFactory.preLoadStates();
        incrementLoadingProgress();
    }

    private static void loadGameModes() {
        GameModeFactory.preLoadGameModes();
        incrementLoadingProgress();
    }

    private static void loadOverlays() {
        OverlayFactory.preLoadOverlays();
        incrementLoadingProgress();
    }

    private static void loadItemPreview() {
        ItemFactory.preloadItemPreviews();
        incrementLoadingProgress();
    }

    private static void loadPlayerSprites() {
        PlayerAnimationManager.preloadAllAnimations();
        incrementLoadingProgress();
    }

    private static void incrementLoadingProgress() {
        for (int i = 0; i < 10; i++) {
            loadingProgress.incrementAndGet();
        }
    }

    private static void loadInitialResources() {
        background = ResourcesLoader.getImage(ResourcesLoader.BACKGROUND_PATH, "loading");
    }
}
