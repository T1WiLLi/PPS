package pewpew.smash.engine;

public abstract class Game {
    private boolean playing = true;
    protected RenderingEngine renderingEngine;

    public abstract void init();

    public abstract void update();

    public abstract void render(Canvas canvas);

    public abstract void conclude();

    public Game() {
        this.renderingEngine = RenderingEngine.getInstance();
    }

    public final void start() {
        init();
        run();
        conclude();
    }

    public final void stop() {
        playing = false;
    }

    private void run() {
        this.renderingEngine.start();
        GameTime.getInstance();

        while (playing) {
            while (GameTime.getInstance().shouldUpdate()) {
                update();
            }

            if (GameTime.getInstance().shouldRender()) {
                render(renderingEngine.getCanvas());
                this.renderingEngine.renderCanvasOnScreen();
            }
        }
        this.renderingEngine.stop();
    }
}