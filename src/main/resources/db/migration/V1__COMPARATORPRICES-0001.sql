create table if not exists user_entity
(
    id       bigint not null
        constraint user_entity_pkey
            primary key,
    password varchar(255),
    username varchar(255)
);

create table if not exists role_entity
(
    id   bigint not null
        constraint role_entity_pkey
            primary key,
    name varchar(255)
);

create table if not exists users_roles
(
    user_entity_id bigint not null
        constraint fkk6ghip4mlrx4jud84u60h4cfg
            references user_entity,
    role_entity_id bigint not null
        constraint fka2hw9gudy6sh1ied3rjv0qt31
            references role_entity,
    constraint users_roles_pkey
        primary key (user_entity_id, role_entity_id)
);

alter table users_roles
    owner to adi;

alter table role_entity
    owner to adi;

alter table user_entity
    owner to adi;

INSERT INTO role_entity(id, name)
VALUES (1, 'ROLE_USER'),
       (2, 'ROLE_ADMIN');

INSERT INTO user_entity(id, username, password)
VALUES (1, 'USER', '$2a$10$PExnvwd0/PneOmbGkksSwuWOaj/YM4Gx0fOGH0hpsHTYyk5lYgwJO')
     , (2, 'ADMIN', '$2a$10$PExnvwd0/PneOmbGkksSwuWOaj/YM4Gx0fOGH0hpsHTYyk5lYgwJO');

INSERT INTO users_roles(role_entity_id, user_entity_id)
VALUES (1, 1),
       (2, 2);