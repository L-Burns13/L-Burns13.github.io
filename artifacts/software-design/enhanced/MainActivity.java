package com.example.inventorytrackingapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    //Database Helper Class for Login and Create Account
    private DatabaseHelper dbHelper;
    //Input fields for login creds
    private EditText usernameText;
    private EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //Apply system window insets for layout spacing
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Initialize Database Helper
        dbHelper = new DatabaseHelper(this);


        //bind UI elements
        usernameText = findViewById(R.id.usernameText);
        passwordText = findViewById(R.id.passwordText);

        Button loginButton = findViewById(R.id.loginButton);
        Button createAccountButton = findViewById(R.id.createAccountButton);

        //Handle login attempt
        loginButton.setOnClickListener(view -> handleLogin());
        //Handle create account attempt
        createAccountButton.setOnClickListener(view -> handleCreateAccount());
    }

    //Validate user creds against database
    private void handleLogin() {
        String username = usernameText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        //Prevent empty login attempts
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter a username and password", Toast.LENGTH_SHORT).show();
            return;
        }


        //if creds are successful
        boolean valid = dbHelper.validateUser(username, password);
        if (valid) {
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

            //Pass logged in username to inventory screen
            Intent intent = new Intent(MainActivity.this, InventoryActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);

        //if creds are invalid
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }

    }

    //Create an account, stores creds in database
    private void handleCreateAccount() {
        String username = usernameText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        //Prevents empty account creation
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter a username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        //Attempt to create account
        boolean created = dbHelper.createUser(username, password);
        if (created) {
            //Successful account creation
            Toast.makeText(this, "Account created. You can log in now.", Toast.LENGTH_SHORT).show();
            usernameText.setText("");
            passwordText.setText("");

            //If username exists, prevent creation
        } else {
            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
        }
    }
}
