CREATE TABLE message_info (
	id integer PRIMARY KEY AUTOINCREMENT,
	content text,
	state integer,
	sender_id integer,
	receiver_id integer,
	time datetime
);

CREATE TABLE contact (
	id integer PRIMARY KEY AUTOINCREMENT,
	contact_id integer,
	name text,
	username text
);

