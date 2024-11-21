package pewpew.smash.game;

public class App {
    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "True");
        Launcher.loadResources();
        new PewPewSmash().start();
    }
}
