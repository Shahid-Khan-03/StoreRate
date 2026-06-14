insert into users (name, email, password, address, role)
values
    ('Default Admin Test Account', 'admin@example.com', '$2a$10$f6gOIoLwAXlXK2wq1nMbG.FxwQZQjZN9GgtDo8xkzgfWGT2rj0Kla', 'Admin test account', 'ADMIN'),
    ('Default Owner Test Account', 'owner@example.com', '$2a$10$l6ZlmL.KJ6jOq1IoBbJlQ.ktvTzWVuG5pAHfwG7JqRZf43c0xMbiO', 'Owner test account', 'STORE_OWNER'),
    ('Default User Test Account', 'user@example.com', '$2a$10$AlxfzjBDWyw.0cJBkUzXx.cVTkKM7hG.TWGIw93vIhJ0yiFQuyFD2', 'User test account', 'USER')
on conflict (email) do update
set password = excluded.password,
    role = excluded.role,
    name = excluded.name,
    address = excluded.address;
