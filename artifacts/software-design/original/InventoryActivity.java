package com.example.inventorytrackingapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.database.Cursor;
import android.widget.Toast;


import java.util.ArrayList;


public class InventoryActivity extends AppCompatActivity {

    private TextView smsStatusText;
    private ActivityResultLauncher<String> smsPermissionLauncher;
    private final ArrayList<InventoryItem> items = new ArrayList<>();
    private InventoryAdapter adapter;
    private int itemCounter = 1;
    private String username;
    private DatabaseHelper dbHelper;



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inventory);

        username = getIntent().getStringExtra("username");

        //Reads username from MainActivity
        if (username == null) {
            username = "";
        }

        //Initialize SQLite helper
        dbHelper = new DatabaseHelper(this);


        //Set up RecyclerView list
        RecyclerView recyclerView = findViewById(R.id.inventoryRecyclerView);
        Button addItemButton = findViewById(R.id.addItemButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InventoryAdapter(items, dbHelper);
        recyclerView.setAdapter(adapter);

        //Load saved items for user
        loadItemsFromDb();

        //Add new item to list
        addItemButton.setOnClickListener(v -> {
            int nextNumber = items.size() + 1;
            String name = "Item " + nextNumber;
            int qty = 0;

            boolean saved = dbHelper.addInventoryItem(username, name, qty);
            if (saved) {
                loadItemsFromDb();
                itemCounter = items.size() + 1;

            } else {
                Toast.makeText(this, "Could not save item", Toast.LENGTH_SHORT).show();
            }
        });


        //Apply system window insets for layout spacing
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //SMS Permission
        smsStatusText = findViewById(R.id.smsStatusText);
        Button smsPermissionButton = findViewById(R.id.smsPermissionButton);

        smsPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        smsStatusText.setText("SMS Permission: Granted");
                    } else {
                        smsStatusText.setText("SMS Permission: Denied");
                    }
                }
        );

        updateSmsStatus();

        smsPermissionButton.setOnClickListener(view -> requestSmsPermission());
    }

    //Updates on screen permission label
    @SuppressLint("SetTextI18n")
    private void updateSmsStatus() {
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            smsStatusText.setText("SMS Permission: Granted");
        } else {
            smsStatusText.setText("SMS Permission: Not Granted");
        }
    }

    //Requests SMS permission if not granted
    @SuppressLint("SetTextI18n")
    private void requestSmsPermission() {
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            smsStatusText.setText("SMS Permission: Granted");
        } else {
            smsPermissionLauncher.launch(Manifest.permission.SEND_SMS);
        }
    }

    //Loads items from database
    private void loadItemsFromDb() {
        items.clear();

        Cursor cursor = dbHelper.getInventoryItems(username);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            int qty = cursor.getInt(2);

            items.add(new InventoryItem(id, name, qty));
        }
        cursor.close();

        adapter.notifyDataSetChanged();
    }
    //Triggers SMS Notification
    @SuppressLint({"MissingPermission", "UnlocalizedSms"})
    public void sendLowInventorySms(String itemName) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //Confirmation message for SMS(Helps show trigger behavior)
        Toast.makeText(this, "Low inventory SMS triggered", Toast.LENGTH_SHORT).show();

        String message = "Low inventory alert: " + itemName + " is out of stock.";

        android.telephony.SmsManager smsManager =
                android.telephony.SmsManager.getDefault();

        smsManager.sendTextMessage("5554", null, message, null, null);
    }




}
