package pewpew.smash.engine.controls;

public enum Direction {

    LEFT(-1, 0),
    RIGHT(1, 0),
    UP(0, -1),
    DOWN(0, 1),
    UP_LEFT(-1, -1),
    UP_RIGHT(1, -1),
    DOWN_LEFT(-1, 1),
    DOWN_RIGHT(1, 1),
    NONE(0, 0);

    private final int xMultiplier;
    private final int yMultiplier;

    Direction(int xMultiplier, int yMultiplier) {
        this.xMultiplier = xMultiplier;
        this.yMultiplier = yMultiplier;
    }

    public int getVelocityX(int speed) {
        if (xMultiplier != 0 && yMultiplier != 0) {
            return (int) (xMultiplier * speed / Math.sqrt(2));
        }
        return xMultiplier * speed;
    }

    public int getVelocityY(int speed) {
        if (xMultiplier != 0 && yMultiplier != 0) {
            return (int) (yMultiplier * speed / Math.sqrt(2));
        }
        return yMultiplier * speed;
    }
}