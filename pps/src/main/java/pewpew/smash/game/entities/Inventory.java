package pewpew.smash.game.entities;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

import pewpew.smash.game.objects.Consumable;
import pewpew.smash.game.objects.Item;
import pewpew.smash.game.objects.Weapon;
import pewpew.smash.game.objects.special.AmmoStack;

public class Inventory {
    private static final int MAX_SLOTS = 9;
    private final Map<Integer, InventorySlot> slots;

    private static class InventorySlot {
        Item item;
        int quantity;

        InventorySlot(Item item, int quantity) {
            this.item = item;
            this.quantity = quantity;
        }
    }

    public Inventory() {
        this.slots = new HashMap<>();
    }

    public boolean addItem(Item item) {
        if (item instanceof Consumable) {
            Optional<Integer> stackSlot = findStackableSlot(item);
            if (stackSlot.isPresent()) {
                slots.get(stackSlot.get()).quantity++;
                return true;
            }
        }

        Optional<Integer> emptySlot = findFirstEmptySlot();
        if (emptySlot.isPresent()) {
            slots.put(emptySlot.get(), new InventorySlot(item, 1));
            return true;
        }
        return false; // Inv is full :(
    }

    public Optional<Item> removeItem(int slotNumber) {
        InventorySlot slot = slots.get(slotNumber);
        if (slot != null) {
            Item removedItem = slot.item;
            if (slot.quantity > 1) {
                slot.quantity--;
            } else {
                slots.remove(slotNumber);
            }
            return Optional.of(removedItem);
        }
        return Optional.empty();
    }

    public Optional<Item> getItem(int slotNumber) {
        InventorySlot slot = slots.get(slotNumber);
        return slot != null ? Optional.of(slot.item) : Optional.empty();
    }

    public int getQuantity(int slotNumber) {
        InventorySlot slot = slots.get(slotNumber);
        return slot != null ? slot.quantity : 0;
    }

    public boolean isSlotEmpty(int slotNumber) {
        return !slots.containsKey(slotNumber);
    }

    public Map<Integer, InventorySlot> getOccupiedSlots() {
        return new HashMap<>(slots);
    }

    public boolean hasSpace() {
        return slots.size() < MAX_SLOTS;
    }

    public int size() {
        return slots.size();
    }

    public boolean isEmpty() {
        return slots.isEmpty();
    }

    public void clear() {
        slots.clear();
    }

    private Optional<Integer> findStackableSlot(Item newItem) {
        for (Map.Entry<Integer, InventorySlot> entry : slots.entrySet()) {
            if (canStack(entry.getValue().item, newItem)) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

    private Optional<Integer> findFirstEmptySlot() {
        for (int i = 0; i < MAX_SLOTS; i++) {
            if (!slots.containsKey(i)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    private boolean canStack(Item existing, Item newItem) {
        if (existing instanceof Weapon || newItem instanceof Weapon) {
            return false;
        }

        if (existing instanceof AmmoStack || newItem instanceof AmmoStack) {
            return false;
        }

        if (existing instanceof Consumable && newItem instanceof Consumable) {
            return existing.getName().equals(newItem.getName());
        }

        return false;
    }
}
