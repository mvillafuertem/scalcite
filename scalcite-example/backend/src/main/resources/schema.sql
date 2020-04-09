DROP TABLE IF EXISTS "QUERIES";
DROP TABLE IF EXISTS "ERRORS";

CREATE TABLE IF NOT EXISTS "QUERIES"
(
    "UUID" UUID NOT NULL,
    "VALUE" VARCHAR NOT NULL,
    "ID" SERIAL NOT NULL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS "ERRORS"
(
    "UUID" UUID NOT NULL,
    "VALUE" VARCHAR NOT NULL,
    "DATE" TIMESTAMP WITH TIME ZONE NOT NULL,
    "ID" SERIAL NOT NULL PRIMARY KEY
);

CREATE UNIQUE INDEX IF NOT EXISTS "UUID_INDEX" ON "QUERIES" ("UUID");
CREATE UNIQUE INDEX IF NOT EXISTS "UUID_INDEX" ON "ERRORS" ("UUID");

INSERT INTO "QUERIES"("UUID", "VALUE") VALUES ('43bbbc0d-fa14-4003-ad15-ef5fdc6c1732', 'SELECT `string` FROM scalcite');
INSERT INTO "QUERIES"("UUID", "VALUE") VALUES ('ec7381a6-11a1-4261-af95-4b84a1a22bf0', 'SELECT `boolean` FROM scalcite');
INSERT INTO "QUERIES"("UUID", "VALUE") VALUES ('dbc20401-2821-44b6-b29d-bbae4313f922', 'SELECT `integer` FROM scalcite');
INSERT INTO "QUERIES"("UUID", "VALUE") VALUES ('b4e5d685-ee44-4f75-aa57-65d84238ee2b', 'SELECT `string` FROM scalcite');
INSERT INTO "QUERIES"("UUID", "VALUE") VALUES ('7097879d-2138-4d68-9bb5-f576a85f80f2', 'SELECT `boolean` FROM scalcite');
INSERT INTO "QUERIES"("UUID", "VALUE") VALUES ('80a309b1-4326-4fac-9e93-6465418d53e5', 'SELECT `notFound` FROM scalcite');
INSERT INTO "QUERIES"("UUID", "VALUE") VALUES ('696d07e5-01a2-455b-8aec-e5eaf6d54c6d', 'SELECT `integer` FROM scalcite');
