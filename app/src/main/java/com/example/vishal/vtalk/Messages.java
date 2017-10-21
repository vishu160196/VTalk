package com.example.vishal.vtalk;

/**
 * Class for Messages table
 */

import android.provider.BaseColumns;

public final class Messages {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private Messages() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "message_info";
        public static final String id = "id";

        public static final String content = "content";
        public static final String time = "time";
        public static final String state = "state";
        public static final String sender_id = "sender_id";
        public static final String receiver_id = "receiver_id";
    }
}
