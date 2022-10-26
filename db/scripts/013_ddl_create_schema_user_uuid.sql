create table user_uuid(
    id serial primary key,
    uuid_id int references uuid(id),
    user_id int references auto_user(id)
)