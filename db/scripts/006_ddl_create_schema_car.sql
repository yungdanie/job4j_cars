create table car (
    id serial primary key,
    name text,
    engine_id int references engine(id)
)