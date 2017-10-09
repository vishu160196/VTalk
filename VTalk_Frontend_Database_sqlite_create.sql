CREATE TABLE message_info (
	id integer PRIMARY KEY AUTOINCREMENT,
	content text,
	state BOOLEAN NOT NULL,
	sender_id integer NOT NULL,
	receiver_id integer NOT NULL,
	FOREIGN KEY(sender_id) REFERENCES contact(contact_id) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY(receiver_id) REFERENCES contact(contact_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE contact (
	id integer PRIMARY KEY AUTOINCREMENT,
	contact_id integer UNIQUE NOT NULL,
	name text NOT NULL,
	username  text UNIQUE NOT NULL;

