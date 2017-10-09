CREATE TABLE "user_info" (
	"id" serial NOT NULL,
	"name" TEXT NOT NULL,
	"username" TEXT NOT NULL UNIQUE,
	"email" TEXT NOT NULL UNIQUE,
	"password" TEXT NOT NULL,
	CONSTRAINT user_info_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "message_cache" (
	"id" serial NOT NULL,
	"content" TEXT NOT NULL,
	"sender_id" bigint NOT NULL,
	"receiver_id" bigint NOT NULL,
	CONSTRAINT message_cache_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);




ALTER TABLE "message_cache" ADD CONSTRAINT "message_cache_fk0" FOREIGN KEY ("sender_id") REFERENCES "user_info"("id");
ALTER TABLE "message_cache" ADD CONSTRAINT "message_cache_fk1" FOREIGN KEY ("receiver_id") REFERENCES "user_info"("id");

