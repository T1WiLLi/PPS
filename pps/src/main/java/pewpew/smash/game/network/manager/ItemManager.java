package pewpew.smash.game.network.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pewpew.smash.game.objects.Item;

public class ItemManager {

    private static ItemManager serverManager;
    private static ItemManager clientManager;

    private final List<Item> items;

    private ItemManager() {
        this.items = Collections.synchronizedList(new ArrayList<Item>());
    }

    public synchronized static ItemManager getInstance(boolean isServer) {
        if (isServer) {
            if (serverManager == null) {
                synchronized (ItemManager.class) {
                    if (serverManager == null) {
                        serverManager = new ItemManager();
                    }
                }
            }
            return serverManager;
        } else {
            if (clientManager == null) {
                synchronized (ItemManager.class) {
                    if (clientManager == null) {
                        clientManager = new ItemManager();
                    }
                }
            }
            return clientManager;
        }
    }

    public synchronized void addItem(Item item) {
        this.items.add(item);
    }

    public synchronized boolean removeItem(Item item) {
        return this.items.remove(item);
    }

    public synchronized void removeItemByID(int id) {
        items.removeIf(item -> item.getId() == id);
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
}
