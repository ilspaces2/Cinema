create TABLE users(
    id       SERIAL PRIMARY KEY,
    username VARCHAR NOT NULL,
    email    VARCHAR NOT NULL UNIQUE,
    phone    VARCHAR NOT NULL UNIQUE
);

create TABLE sessions(
    id   SERIAL PRIMARY KEY,
    name text,
    number_of_row INT NOT NULL,
    number_of_cell INT NOT NULL
);

create TABLE ticket(
    id         SERIAL PRIMARY KEY,
    session_id INT NOT NULL REFERENCES sessions (id),
    pos_row    INT NOT NULL,
    cell       INT NOT NULL,
    user_id    INT NOT NULL REFERENCES users (id),
    unique (session_id, pos_row, cell)
);

insert into sessions (name, number_of_row ,number_of_cell) values ('Бэтмэн', 5, 5);
insert into sessions (name, number_of_row ,number_of_cell) values ('Такси', 10, 10);
insert into sessions (name, number_of_row ,number_of_cell) values ('Шрэк', 5, 5);
insert into sessions (name, number_of_row ,number_of_cell) values ('Приключения Шурика', 3, 6);

insert into users (username, email ,phone) values ('Admin', 'Admin', 'Admin');
