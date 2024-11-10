package pewpew.smash.game.network.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pewpew.smash.game.objects.Item;

public class ItemManager {

    private static ItemManager instance;

    private final List<Item> items;

    public synchronized static ItemManager getInstance() {
        if (instance == null) {
            synchronized (ItemManager.class) {
                if (instance == null) {
                    instance = new ItemManager();
                }
            }
        }
        return instance;
    }

    public synchronized void addItem(Item item) {
        this.items.add(item);
    }

    public synchronized boolean removeItem(Item item) {
        return this.items.remove(item);
    }

    public synchronized void removeItemByID(int id) {
        this.items.removeIf(i -> i.getId() == id);
    }

    public synchronized Item getItem(int id) {
        return this.items.get(id);
    }

    public synchronized List<Item> getItems() {
        return this.items;
    }

    public synchronized int size() {
        return this.items.size();
    }

    private ItemManager() {
        this.items = Collections.synchronizedList(new ArrayList<Item>());
    }
}
