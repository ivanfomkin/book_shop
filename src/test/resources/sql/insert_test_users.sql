WITH newUser as (
    insert into users (balance, hash, name, reg_time, password)
        VALUES (0, gen_random_uuid()::varchar, 'Test User 1', now(),
                '{noop}qwerty') returning id)
INSERT
INTO user_contact(approved, code, contact, type, user_id)
SELECT 1, '1', 'test@user.ru', 'EMAIL', newUser.id
FROM newUser;
