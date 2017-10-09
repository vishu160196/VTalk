package com.example.vishal.vtalk;

/**
 * Class for Contacts table
 */

import android.provider.BaseColumns;

public final class Contacts {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private Contacts() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "contact";

        public static final String type = "type";
        public static final String description = "description";
        public static final String expense = "expense";
        public static final String date = "date_spent";
    }
}
