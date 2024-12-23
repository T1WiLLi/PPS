package pewpew.smash.game;

public class App {
    public static void main(String[] args) {
        // System.setProperty("sun.java2d.opengl", "True");
        // System.setProperty("sun.awt.noerasebackground", "True");
        Launcher.loadResources();
        new PewPewSmash().start();
    }
}
