package pewpew.smash.game.network;

import java.util.List;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pewpew.smash.database.models.Achievement;
import pewpew.smash.database.models.Rank;

@Getter
@Setter
@ToString
public final class User {
    private volatile static User instance = new User();

    private boolean isConnected = false;

    private AtomicInteger localID = new AtomicInteger(Integer.MIN_VALUE);
    private boolean isDead = false;

    private String username = "Guest";
    private Rank rank = new Rank(-1, "none", "none", "none", 0, 0, 0);
    private List<Achievement> achievements;

    public synchronized static User getInstance() {
        return instance;
    }

    public void build(String username, Rank rank, List<Achievement> achievements) {
        this.username = username;
        this.rank = rank;
        this.achievements = achievements;
    }

    public void setID(int id) {
        this.localID.set(id);
    }
}
