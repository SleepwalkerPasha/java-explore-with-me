drop table if exists endpoints;
-- endpoints
create table if not exists endpoints (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    app VARCHAR(255) NOT NULL,
    uri VARCHAR(512) NOT NULL,
    ip varchar(255) not null,
    created timestamp without time zone not null,
    CONSTRAINT pk_endpoint PRIMARY KEY (id)
);