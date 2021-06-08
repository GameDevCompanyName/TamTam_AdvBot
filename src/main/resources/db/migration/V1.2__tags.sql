create table tag
(
    id   bigserial primary key,
    name text not null
);

create table platform_tag
(
    id          bigserial primary key,
    platform_id bigint not null references platform (id),
    tag_id      bigint not null references tag (id)
);
create index platform_tag_platform_id_idx on platform_tag (platform_id);
create index platform_tag_tag_id_idx on platform_tag (tag_id);

insert into tag(name) values ('СПОРТ'), ('НОВОСТИ'), ('ИГРЫ'), ('ПОЛИТИКА'), ('ТЕХНОЛОГИИ'), ('ЭКОНОМИКА');