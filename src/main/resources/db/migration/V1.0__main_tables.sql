create table users
(
    id bigserial primary key
);

create table file
(
    id       bigserial primary key,
    filename text not null,
    data     bytea
);

create table advertisement
(
    id          bigserial primary key,
    name        text not null,
    description text,
    link        text,
    file_id     bigint references file (id),
    user_id     bigint references users (id),
    enabled boolean
);
create index advertisement_user_id_idx on advertisement (user_id);

create table user_advertisement
(
    user_id bigint references users(id),
    advertisement_id bigint references advertisement(id)
);
create index user_advertisement_user_id_idx on user_advertisement (user_id);

create table user_group
(
    id bigserial primary key
);

create table user_group_advertisement
(
    advertisement_id bigint references advertisement (id),
    user_group_id bigint references user_group (id)
);
create index user_group_advertisement_advertisement_id on user_group_advertisement(advertisement_id);
create index user_group_advertisement_user_group_id on user_group_advertisement(user_group_id);
