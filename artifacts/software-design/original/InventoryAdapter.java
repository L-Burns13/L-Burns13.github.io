package com.example.inventorytrackingapp;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

        //Quantity field is read only until edit pressed
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
            if (buttonText.equals("Edit")) {
                holder.itemQuantityEdit.setEnabled(true);
                holder.itemQuantityEdit.setFocusable(true);
                holder.itemQuantityEdit.setFocusableInTouchMode(true);
                holder.itemQuantityEdit.requestFocus();
                holder.itemQuantityEdit.setSelection(holder.itemQuantityEdit.getText().length());
                holder.editButton.setText("Save");

            } else {
                //Saves edited quantity
                String text = holder.itemQuantityEdit.getText().toString().trim();
                if (text.isEmpty()) {
                    Toast.makeText(v.getContext(), "Quantity cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                int newQty = Integer.parseInt(text);
                boolean updated = dbHelper.updateInventoryItem(current.getId(), current.getName(), newQty);

                if (updated) {
                    items.set(pos, new InventoryItem(current.getId(), current.getName(), newQty));
                    notifyItemChanged(pos);
                    Toast.makeText(v.getContext(), "Updated", Toast.LENGTH_SHORT).show();

                    //Triggers SMS notification if inventory is zero
                    if (newQty < 1) {
                        Toast.makeText(v.getContext(), "Low inventory alert triggered", Toast.LENGTH_SHORT).show();
                    }

                    if (newQty < 1 && v.getContext() instanceof InventoryActivity) {
                        ((InventoryActivity) v.getContext()).sendLowInventorySms(current.getName());
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
        TextView itemNameText;
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
