CREATE TABLE price_history(
    id SERIAL PRIMARY KEY,
    before BIGINT not null,
    after BIGINT not null,
    created TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    p_user_id int references auto_user(id)
);