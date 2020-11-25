# --- !Ups

create table if not exists "CARS" (
  "MACHINE_KIND" VARCHAR,
  "MODEL" VARCHAR,
  "NUMBER" VARCHAR NOT NULL PRIMARY KEY,
  "COLOR" VARCHAR,
  "PRODUCTION_YEAR" INT
);

# --- !Downs

-- drop table "CARS" if exists;