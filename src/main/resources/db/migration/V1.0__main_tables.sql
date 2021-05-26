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

--create table user_group
--(
--    id bigint primary key
--);
--
--create table user_group_advertisement
--(
--    advertisement_id bigint references advertisement (id),
--    user_group_id bigint references user_group (id)
--);
--create index user_group_advertisement_advertisement_id on user_group_advertisement(advertisement_id);
--create index user_group_advertisement_user_group_id on user_group_advertisement(user_group_id);
