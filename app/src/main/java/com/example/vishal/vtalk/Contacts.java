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

        public static final String id = "id";
        public static final String contact_id = "contact_id";
        public static final String name = "name";
        public static final String username = "username";
    }
}
