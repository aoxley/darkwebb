CREATE SCHEMA CW;
CREATE SCHEMA DW;

CREATE TABLE CW.Onion_Links_Found_On_Clear_Web (
  id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  version INTEGER NOT NULL,
  created TIMESTAMP NOT NULL,
  updated TIMESTAMP NOT NULL,
  url_link_was_found_on VARCHAR(512) NOT NULL,
  onion_url VARCHAR(512) NOT NULL,
  PRIMARY KEY(id)
);

CREATE TABLE DW.Onion_Links_Found_On_Dark_Web (
  id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  version INTEGER NOT NULL,
  created TIMESTAMP NOT NULL,
  updated TIMESTAMP NOT NULL,
  url_link_was_found_on VARCHAR(512) NOT NULL,
  onion_url VARCHAR(512) NOT NULL,
  PRIMARY KEY(id)
);