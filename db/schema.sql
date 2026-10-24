-- アライグマ目撃記録テーブル（PostgreSQL）
-- README.md の「セットアップ」の手順に従って実行する。

create table "raccoon"(
id serial,
sighted_date date not null,
sighted_time time not null,
count integer not null,
memo varchar(255),
created_at timestamp,
updated_at timestamp,
primary key(id)
);
