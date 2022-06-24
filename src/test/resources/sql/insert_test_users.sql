WITH new_user as (
    insert into users (balance, hash, name, reg_time, password)
        VALUES (0, gen_random_uuid()::varchar, 'Test User 1', now(),
                '{noop}qwerty') returning id),
     user_phone as (
         INSERT INTO user_contact (approved, code, contact, type, user_id)
             SELECT 1, '1', 'test@user.ru', 'EMAIL', new_user.id
             FROM new_user)
INSERT
INTO user_contact (approved, code, contact, type, user_id)
SELECT 1, '1', '+7 (999) 999-99-99', 'PHONE', new_user.id
FROM new_user;
