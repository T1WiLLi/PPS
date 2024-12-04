package pewpew.smash.game;

public class Console implements Runnable {

    public Console() {
        run();
    }

    @Override
    public void run() {
        System.out.println("PewPewSmash Console Command: ");
    }
}
