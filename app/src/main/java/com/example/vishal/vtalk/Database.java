package com.example.vishal.vtalk;

/**
 * Access In-App database for storing contacts and messages
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "VTalk.db";

    private static final String SQL_CREATE_ENTRIES_CONTACTS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + ExpenseTable.FeedEntry.TABLE_NAME + "(" +
                    ExpenseTable.FeedEntry.type + " TEXT UNIQUE NOT NULL," +
                    ExpenseTable.FeedEntry.expense + " INT NOT NULL);";

    private static final String SQL_CREATE_ENTRIES_MESSAGES_TABLE =
            "CREATE TABLE IF NOT EXISTS " + ExpenseDescription.FeedEntry.TABLE_NAME + "(" +
                    ExpenseDescription.FeedEntry.type + " TEXT NOT NULL, " +
                    ExpenseDescription.FeedEntry.description + " TEXT NOT NULL, " + ExpenseDescription.FeedEntry.expense + " INT NOT NULL, " + ExpenseDescription.FeedEntry.date + " DATE NOT NULL, " +
                    "FOREIGN KEY(" + ExpenseDescription.FeedEntry.type + ") REFERENCES " + ExpenseTable.FeedEntry.TABLE_NAME + "(" +
                    ExpenseTable.FeedEntry.type +") ON DELETE CASCADE ON UPDATE CASCADE);";


    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_EXPENSE_DESCRIPTION);
        db.execSQL(SQL_CREATE_ENTRIES_EXPENSE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public void onOpen(SQLiteDatabase db){
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON");
    }

}
