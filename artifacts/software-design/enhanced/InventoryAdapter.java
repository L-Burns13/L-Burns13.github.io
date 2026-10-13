package com.example.inventorytrackingapp;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import android.widget.Toast;
import android.widget.EditText;


public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ItemViewHolder> {

    //List of inventory items displayed in RecyclerView
    private final List<InventoryItem> items;
    //Database helper for delete and update
    private final DatabaseHelper dbHelper;

    public InventoryAdapter(List<InventoryItem>items, DatabaseHelper dbHelper) {
        this.items = items;
        this.dbHelper = dbHelper;
    }

    //Inflates the layout for each item in the list
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventory_row, parent, false);
        return new ItemViewHolder(view);
    }

    //Binds data to each item in the list and handles edit/delete actions
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        InventoryItem item = items.get(position);

        //Display item name and quantity
        holder.itemNameText.setText(item.getName());
        holder.itemQuantityEdit.setText(String.valueOf(item.getQuantity()));

        //Changed Name and quantity fields to read only until edit pressed
        //For Milestone Two
        holder.itemNameText.setEnabled(false);
        holder.itemNameText.setFocusable(false);
        holder.itemNameText.setFocusableInTouchMode(false);

        holder.itemQuantityEdit.setEnabled(false);
        holder.itemQuantityEdit.setFocusable(false);
        holder.itemQuantityEdit.setFocusableInTouchMode(false);

        holder.editButton.setText("Edit");

        //Handle delete and edit actions
        holder.deleteButton.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) {
                return;
            }

            InventoryItem itemToDelete = items.get(pos);
            boolean deleted = dbHelper.deleteInventoryItem(itemToDelete.getId());

            if (deleted) {
                items.remove(pos);
                notifyItemRemoved(pos);
            } else {
                Toast.makeText(v.getContext(), "Delete failed", Toast.LENGTH_SHORT).show();
            }
        });

        //Handle edit actions
        holder.editButton.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) {
                return;
            }

            InventoryItem current = items.get(pos);

            String buttonText = holder.editButton.getText().toString();

            //Enables inline editing
            //Modified for Milestone Two
            if (buttonText.equals("Edit")) {
                holder.itemNameText.setEnabled(true);
                holder.itemNameText.setFocusable(true);
                holder.itemNameText.setFocusableInTouchMode(true);

                holder.itemQuantityEdit.setEnabled(true);
                holder.itemQuantityEdit.setFocusable(true);
                holder.itemQuantityEdit.setFocusableInTouchMode(true);

                holder.itemNameText.requestFocus();
                holder.itemNameText.setSelection(holder.itemNameText.getText().length());

                holder.editButton.setText("Save");

            } else {
                //Saves edited quantity
                //Modified for milestone two
                String newName = holder.itemNameText.getText().toString().trim();
                String quantityText = holder.itemQuantityEdit.getText().toString().trim();

                if (newName.isEmpty()) {
                    holder.itemNameText.setError("Item name cannot be empty");
                    return;
                }

                if (quantityText.isEmpty()) {
                    holder.itemQuantityEdit.setError("Quantity cannot be empty");
                    return;
                }

                int newQty = Integer.parseInt(quantityText);

                boolean updated =
                        dbHelper.updateInventoryItem(current.getId(), newName, newQty);

                if (updated) {
                    items.set(pos, new InventoryItem(current.getId(), newName, newQty));
                    notifyItemChanged(pos);
                    Toast.makeText(v.getContext(), "Updated", Toast.LENGTH_SHORT).show();

                    //Triggers SMS notification if inventory is zero
                    if (newQty < 1) {
                        Toast.makeText(v.getContext(), "Low inventory alert triggered", Toast.LENGTH_SHORT).show();
                    }

                    if (newQty < 1 && v.getContext() instanceof InventoryActivity) {
                        ((InventoryActivity) v.getContext()).sendLowInventorySms(newName);
                    }
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //ViewHolder for inventory row
    static class ItemViewHolder extends RecyclerView.ViewHolder {
        EditText itemNameText;
        EditText itemQuantityEdit;

        Button deleteButton;
        Button editButton;


        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameText = itemView.findViewById(R.id.itemNameText);
            itemQuantityEdit = itemView.findViewById(R.id.itemQuantityEdit);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            editButton = itemView.findViewById(R.id.editButton);

        }
    }
}
