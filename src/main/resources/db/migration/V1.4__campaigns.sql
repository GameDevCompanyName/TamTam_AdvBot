create table campaigns
(
    post_id      text primary key,
    ad_id        bigint not null references advert (id)
);
create index campaigns_ad_id_idx on campaigns (ad_id);