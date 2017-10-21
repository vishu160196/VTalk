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
            "CREATE TABLE IF NOT EXISTS " + Contacts.FeedEntry.TABLE_NAME + "(" +
                    Contacts.FeedEntry.id + " integer PRIMARY KEY AUTOINCREMENT, " +
                    Contacts.FeedEntry.contact_id + " integer UNIQUE NOT NULL, " + Contacts.FeedEntry.username +
                    " TEXT UNIQUE NOT NULL, " + Contacts.FeedEntry.name + " TEXT NOT NULL);";

    private static final String SQL_CREATE_ENTRIES_MESSAGES_TABLE =
            "CREATE TABLE IF NOT EXISTS " + Messages.FeedEntry.TABLE_NAME + "(" +
                    Messages.FeedEntry.id + " integer PRIMARY KEY AUTOINCREMENT, " +
                    Messages.FeedEntry.content + " TEXT NOT NULL, " + Messages.FeedEntry.state + " INTEGER NOT NULL, " +
                    Messages.FeedEntry.sender_id + " INTEGER, " + Messages.FeedEntry.receiver_id + " INTEGER, " + Messages.FeedEntry.time + " DATETIME NOT NULL, " + "FOREIGN KEY(" + Messages.FeedEntry.sender_id + ")" +
                    " REFERENCES " + Contacts.FeedEntry.TABLE_NAME + "(" + Contacts.FeedEntry.contact_id +") ON DELETE CASCADE ON UPDATE CASCADE, " +
                    "FOREIGN KEY(" + Messages.FeedEntry.receiver_id + ")" + " REFERENCES " + Contacts.FeedEntry.TABLE_NAME + "(" + Contacts.FeedEntry.contact_id +") ON DELETE CASCADE ON UPDATE CASCADE);";


    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_CONTACTS_TABLE);
        db.execSQL(SQL_CREATE_ENTRIES_MESSAGES_TABLE);

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
