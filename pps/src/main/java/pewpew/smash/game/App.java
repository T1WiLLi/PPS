package pewpew.smash.game;

public class App {
    public static void main(String[] args) {
        Launcher.loadResources();
        new PewPewSmash().start();
    }
}
