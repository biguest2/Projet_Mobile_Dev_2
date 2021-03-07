package com.example.delgado_rodrigue_mobile_app_dev_project.data.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class UsersAccountsDBUtil extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "users_accounts.db";
    private static final String USERS_TABLE_NAME = "users";
    private static final String ACCOUNTS_TABLE_NAME = "accounts";
    // Users Table Columns names
    private static final String USERS_ID_COLUMN = "user_id";
    private static final String USERS_NAME_COLUMN = "user_name";
    private static final String USERS_LASTNAME_COLUMN = "user_lastname";
    // Accounts Table Columns names
    private static final String ACCOUNTS_ID_COLUMN = "account_user_id";
    private static final String ACCOUNTS_NAME_COLUMN = "account_name";
    private static final String ACCOUNTS_AMOUNT_COLUMN = "account_amount";
    private static final String ACCOUNTS_IBAN_COLUMN = "account_iban";
    private static final String ACCOUNTS_CURRENCY_COLUMN = "account_currency";
    // Create table SQL queries
    private static final String CREATE_USERS_TABLE_QUERY =
        "CREATE TABLE " + USERS_TABLE_NAME + "(" +
        USERS_ID_COLUMN + " INTEGER PRIMARY KEY NOT NULL, " +
        USERS_NAME_COLUMN + " TEXT, " +
        USERS_LASTNAME_COLUMN + " TEXT" +
        ")";
    private static final String CREATE_ACCOUNTS_TABLE_QUERY =
        "CREATE TABLE " + ACCOUNTS_TABLE_NAME + "(" +
        ACCOUNTS_ID_COLUMN + " INTEGER PRIMARY KEY NOT NULL, " +
        ACCOUNTS_NAME_COLUMN + " TEXT, " +
        ACCOUNTS_AMOUNT_COLUMN + " REAL, " +
        ACCOUNTS_IBAN_COLUMN + " TEXT, " +
        ACCOUNTS_CURRENCY_COLUMN + " TEXT" +
        ")";
    // Drop table SQL queries
    private static final String DROP_USERS_TABLE_QUERY = "DROP TABLE IF EXISTS " + USERS_TABLE_NAME;
    private static final String DROP_ACCOUNTS_TABLE_QUERY = "DROP TABLE IF EXISTS " + ACCOUNTS_TABLE_NAME;

    public UsersAccountsDBUtil(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

//        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL(DROP_USERS_TABLE_QUERY);
//        db.execSQL(DROP_ACCOUNTS_TABLE_QUERY);
//        db.execSQL(CREATE_USERS_TABLE_QUERY);
//        db.execSQL(CREATE_ACCOUNTS_TABLE_QUERY);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE_QUERY);
        db.execSQL(CREATE_ACCOUNTS_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop tables if exist
        db.execSQL(DROP_USERS_TABLE_QUERY);
        db.execSQL(DROP_ACCOUNTS_TABLE_QUERY);
        // Create tables again
        onCreate(db);
    }

    //region User DB Functions
    public void addUser(User user) throws Exception {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();

            values.put(USERS_ID_COLUMN, user.getUserID());
            values.put(USERS_NAME_COLUMN, user.getUserName());
            values.put(USERS_LASTNAME_COLUMN, user.getUserLastname());

            db.insert(USERS_TABLE_NAME, null, values);
        }
    }

    public void deleteUser(User user) throws Exception {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.delete(
                USERS_TABLE_NAME,
                USERS_ID_COLUMN + " = ?",
                new String[]{String.valueOf(user.getUserID())}
            );
        }
    }

    public void updateUser(User user) throws Exception {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(USERS_NAME_COLUMN, user.getUserName());
            values.put(USERS_LASTNAME_COLUMN, user.getUserLastname());

            db.update(
                USERS_TABLE_NAME,
                values,
                USERS_ID_COLUMN + " = ?",
                new String[]{String.valueOf(user.getUserID())}
            );
        }
    }

    public boolean doesUserExist(String userID) {
        // array of columns to fetch
        String[] columns = {
                USERS_ID_COLUMN
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = USERS_LASTNAME_COLUMN + " = ?";
        String[] selectionArgs = {userID};

        Cursor cursor = db.rawQuery(
            "SELECT count(*)" +
            " FROM " + USERS_TABLE_NAME +
            " WHERE " + USERS_ID_COLUMN + "=" + userID,
            null
        );
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();

        return count > 0;
    }

    public void updateUserDB(ArrayList<User> users) throws Exception {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.execSQL(DROP_USERS_TABLE_QUERY);
            db.execSQL(CREATE_USERS_TABLE_QUERY);
            for (User user : users) {
                ContentValues values = new ContentValues();
                values.put(USERS_ID_COLUMN, user.getUserID());
                values.put(USERS_NAME_COLUMN, user.getUserName());
                values.put(USERS_LASTNAME_COLUMN, user.getUserLastname());

                db.insert(USERS_TABLE_NAME, null, values);
            }
        }
    }

    public User findUserByID(String userID) throws Exception {
        User user = null;

        // array of columns to fetch
        String columns =
            USERS_ID_COLUMN + ", " +
            USERS_NAME_COLUMN + ", " +
            USERS_LASTNAME_COLUMN;
        // String sortOrder = USERS_ID_COLUMN + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + columns +
                " FROM " + USERS_TABLE_NAME +
                " WHERE " + USERS_ID_COLUMN + "=" + userID,
                null
        );

        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            user = new User(
                Integer.parseInt(cursor.getString(cursor.getColumnIndex(USERS_ID_COLUMN))),
                cursor.getString(cursor.getColumnIndex(USERS_NAME_COLUMN)),
                cursor.getString(cursor.getColumnIndex(USERS_LASTNAME_COLUMN))
            );
        }
        cursor.close();
        db.close();
        return user;
    }
    //endregion

    //region Account DB Functions
    public List<Account> getAccountsByUserID(String userID) throws Exception {
        List<Account> accounts = new ArrayList<Account>();

        // columns to fetch
        String columns =
            ACCOUNTS_ID_COLUMN + ", " +
            ACCOUNTS_NAME_COLUMN + ", " +
            ACCOUNTS_AMOUNT_COLUMN + ", " +
            ACCOUNTS_IBAN_COLUMN + ", " +
            ACCOUNTS_CURRENCY_COLUMN;
        String sortOrder = ACCOUNTS_ID_COLUMN + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
            "SELECT " + columns +
            " FROM " + ACCOUNTS_TABLE_NAME +
            " WHERE " + ACCOUNTS_ID_COLUMN + "=" + userID +
            " ORDER BY " + sortOrder,
            null
        );

        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Account newAccount = new Account(
                    Integer.parseInt(cursor.getString(cursor.getColumnIndex(ACCOUNTS_ID_COLUMN))),
                    cursor.getString(cursor.getColumnIndex(ACCOUNTS_NAME_COLUMN)),
                    cursor.getFloat(cursor.getColumnIndex(ACCOUNTS_AMOUNT_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(ACCOUNTS_IBAN_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(ACCOUNTS_CURRENCY_COLUMN))
                );
                accounts.add(newAccount);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return accounts;
    }

    public void addAccount(Account account) throws Exception {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(ACCOUNTS_ID_COLUMN, account.getAccountID());
            values.put(ACCOUNTS_NAME_COLUMN, account.getAccountName());
            values.put(ACCOUNTS_AMOUNT_COLUMN, account.getAccountAmount());
            values.put(ACCOUNTS_IBAN_COLUMN, account.getAccountIBAN());
            values.put(ACCOUNTS_CURRENCY_COLUMN, account.getAccountCurrency());

            db.insert(ACCOUNTS_TABLE_NAME, null, values);
        }
    }

    public void deleteAccount(Account account) throws Exception {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.delete(
                ACCOUNTS_TABLE_NAME,
                ACCOUNTS_ID_COLUMN + "= ?",
                new String[]{ String.valueOf(account.getAccountID()) }
            );
        }
    }

    public void updateAccount(Account account) throws Exception {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(ACCOUNTS_ID_COLUMN, account.getAccountID());
            values.put(ACCOUNTS_NAME_COLUMN, account.getAccountName());
            values.put(ACCOUNTS_AMOUNT_COLUMN, account.getAccountAmount());
            values.put(ACCOUNTS_IBAN_COLUMN, account.getAccountIBAN());
            values.put(ACCOUNTS_CURRENCY_COLUMN, account.getAccountCurrency());

            db.update(
                    ACCOUNTS_TABLE_NAME,
                    values,
                    ACCOUNTS_ID_COLUMN + " = ?",
                    new String[]{String.valueOf(account.getAccountID())}
            );
        }
    }

    public void updateAccountDB(ArrayList<Account> accounts) throws Exception {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.execSQL(DROP_ACCOUNTS_TABLE_QUERY);
            db.execSQL(CREATE_ACCOUNTS_TABLE_QUERY);
            for (Account account : accounts) {
                ContentValues values = new ContentValues();
                values.put(ACCOUNTS_ID_COLUMN, account.getAccountID());
                values.put(ACCOUNTS_NAME_COLUMN, account.getAccountName());
                values.put(ACCOUNTS_AMOUNT_COLUMN, account.getAccountAmount());
                values.put(ACCOUNTS_IBAN_COLUMN, account.getAccountIBAN());
                values.put(ACCOUNTS_CURRENCY_COLUMN, account.getAccountCurrency());

                db.insert(ACCOUNTS_TABLE_NAME, null, values);
            }
        }
    }
    //endregion

//    private void resetDBs() {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL(DROP_USERS_TABLE_QUERY);
//        db.execSQL(CREATE_USERS_TABLE_QUERY);
//    }
}
