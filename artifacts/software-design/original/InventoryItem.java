package com.example.inventorytrackingapp;

public class InventoryItem {
    private final String name;
    private final int quantity;

    private final int id;


    public InventoryItem(int id, String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
        this.id = id;
}
    public String getName() {
        return name;
    }
    public int getQuantity() {
        return quantity;
    }

    public int getId() { return id; }
}
