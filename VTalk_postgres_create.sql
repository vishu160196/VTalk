CREATE TABLE "user_info" (
	"id" serial NOT NULL,
	"name" TEXT NOT NULL,
	"username" TEXT NOT NULL UNIQUE,
	"password" TEXT NOT NULL,
	"email" TEXT NOT NULL UNIQUE,
	"photo" TEXT NOT NULL UNIQUE,
	CONSTRAINT user_info_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "message_info" (
	"id" serial NOT NULL,
	"user_id" BOOLEAN NOT NULL,
	"content" TEXT NOT NULL,
	"state" BOOLEAN NOT NULL,
	"sender_id" bigint,
	CONSTRAINT message_info_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "contact" (
	"id" serial NOT NULL,
	"user_id" bigint NOT NULL,
	"contact" bigint NOT NULL,
	CONSTRAINT contact_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);




ALTER TABLE "message_info" ADD CONSTRAINT "message_info_fk0" FOREIGN KEY ("user_id") REFERENCES "user_info"("id");
ALTER TABLE "message_info" ADD CONSTRAINT "message_info_fk1" FOREIGN KEY ("sender_id") REFERENCES "user_info"("id");

ALTER TABLE "contact" ADD CONSTRAINT "contact_fk0" FOREIGN KEY ("user_id") REFERENCES "user_info"("id");
ALTER TABLE "contact" ADD CONSTRAINT "contact_fk1" FOREIGN KEY ("contact") REFERENCES "user_info"("id");

