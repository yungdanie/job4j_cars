create table driver (
    id serial primary key,
    name text,
    user_id int references auto_user(id)
)