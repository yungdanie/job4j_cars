create table uuid(
    id serial primary key,
    uuid varchar(36),
    user_agent varchar(1000),
    user_id int references auto_user(id)
)