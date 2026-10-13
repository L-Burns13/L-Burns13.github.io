package com.example.inventorytrackingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {


    //Database configuration
    private static final String DATABASE_NAME = "inventory_app.db";
    private static final int DATABASE_VERSION = 2;

    //User table configuration
    private static final String TABLE_USERS = "users";
    private static final String COL_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";

    //Inventory Table Configuration
    private static final String TABLE_INVENTORY = "inventory";
    private static final String COL_ITEM_ID = "item_id";
    private static final String COL_ITEM_NAME = "item_name";
    private static final String COL_QTY = "qty";
    private static final String COL_USER = "username";


    //Constructor initializes SQLite database
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Creates required database tables on first launch
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable =
                "CREATE TABLE " + TABLE_USERS + " (" +
                        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_USERNAME + " TEXT UNIQUE NOT NULL, " +
                        COL_PASSWORD + " TEXT NOT NULL" +
                        ")";
        db.execSQL(createUsersTable);

        String createInventoryTable =
                "CREATE TABLE " + TABLE_INVENTORY + " (" +
                        COL_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_ITEM_NAME + " TEXT NOT NULL, " +
                        COL_QTY + " INTEGER NOT NULL, " +
                        COL_USER + " TEXT NOT NULL" +
                        ")";
        db.execSQL(createInventoryTable);

    }

    //Handles database updates
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY);
        onCreate(db);
    }

    //Checks if username already exists
    public boolean userExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM " + TABLE_USERS + " WHERE " + COL_USERNAME + " = ?",
                new String[]{username}
        );

        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    //Creates a new user account
    public boolean createUser(String username, String password) {
        if (userExists(username)) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    //Validates login credentials
    public boolean validateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM " + TABLE_USERS +
                        " WHERE " + COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?",
                new String[]{username, password}
        );

        boolean valid = cursor.moveToFirst();
        cursor.close();
        return valid;
    }

    //Inserts a new inventory item for user logged in
    public boolean addInventoryItem(String username, String itemName, int qty) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ITEM_NAME, itemName);
        values.put(COL_QTY, qty);
        values.put(COL_USER, username);

        long result = db.insert(TABLE_INVENTORY, null, values);
        return result != -1;
    }

    //Retrieves all inventory items for user logged in
    public Cursor getInventoryItems(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT " + COL_ITEM_ID + ", " + COL_ITEM_NAME + ", " + COL_QTY +
                        " FROM " + TABLE_INVENTORY +
                        " WHERE " + COL_USER + " = ?",
                new String[]{username}
        );
    }

    //Deletes an inventory item
    public boolean deleteInventoryItem(int itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(
                TABLE_INVENTORY,
                COL_ITEM_ID + " = ?",
                new String[]{String.valueOf(itemId)}
        );
        return rows > 0;
    }

    //Updates an inventory item
    public boolean updateInventoryItem(int itemId, String itemName, int qty) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ITEM_NAME, itemName);
        values.put(COL_QTY, qty);

        int rows = db.update(
                TABLE_INVENTORY,
                values,
                COL_ITEM_ID + " = ?",
                new String[]{String.valueOf(itemId)}
        );
        return rows > 0;
    }



}
