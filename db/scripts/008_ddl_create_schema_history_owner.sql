create table history_owner (
    id serial primary key,
    driver_id int references driver(id),
    car_id int references car(id)
)