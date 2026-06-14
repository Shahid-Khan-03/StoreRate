alter table users
    add column if not exists role varchar(30) not null default 'USER';

update users u
set role = case
    when exists (
        select 1 from user_roles ur
        where ur.user_id = u.id and ur.role = 'ROLE_ADMIN'
    ) then 'ADMIN'
    when exists (
        select 1 from user_roles ur
        where ur.user_id = u.id and ur.role = 'ROLE_STORE_OWNER'
    ) then 'STORE_OWNER'
    else 'USER'
end
where exists (
    select 1 from information_schema.tables
    where table_name = 'user_roles'
);

drop table if exists user_roles;
