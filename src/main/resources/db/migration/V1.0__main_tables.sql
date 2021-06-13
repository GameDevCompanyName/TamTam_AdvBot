create table users
(
    id bigint primary key,
    payload text not null
);

create table advert
(
    id              bigserial primary key,
    title           text not null,
    text            text,
    users_id        bigint not null references users (id)
);
create index advert_user_id_idx on advert (users_id);

create table platform
(
    id              bigint primary key,
    availability    boolean not null,
    users_id        bigint not null references users (id)
);
create index platform_user_id_idx on platform (users_id);
