CREATE TABLE price_history(
    id SERIAL PRIMARY KEY,
    price BIGINT,
    created TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    post_id int references auto_post(id),
    user_id int references auto_user(id)
);