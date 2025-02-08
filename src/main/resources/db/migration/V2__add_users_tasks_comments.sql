INSERT INTO users (username, email, password, role)
VALUES
  ('admin', 'admin@mail.ru', '$2a$12$KIvJch1/5pLy74mTxIl7UuZX549wJZ6odnW4UX/yhpvYuxpvrb4gu', 'ROLE_ADMIN'),
  ('user1', 'user1@mail.ru', '$2a$12$.RjccxuL2nDO1Be1drK/1uKVJTJqSdToyUrRdkpMRnvK7Xe/Od4XS', 'ROLE_USER'),
  ('user2', 'user2@mail.ru', '$2a$12$.RjccxuL2nDO1Be1drK/1uKVJTJqSdToyUrRdkpMRnvK7Xe/Od4XS', 'ROLE_USER');

INSERT INTO tasks (title, description, status, priority, created_at, updated_at, deadline_at, author_id, assignee_id)
VALUES
  (
    'Проверка тестового задания',
    'Проверка тестового задания у кандидата в компанию Effective Mobile.',
    'PENDING',
    'HIGH',
    '2023-03-01T10:00:00+03:00',
    '2023-03-01T10:00:00+03:00',
    '2023-03-05T10:00:00+03:00',
    (SELECT id FROM users WHERE username = 'admin'),
    (SELECT id FROM users WHERE username = 'user1')
  ),
  (
    'Написание документации Swagger',
    'Написать документацию используя yaml формат',
    'IN_PROGRESS',
    'MEDIUM',
    '2023-03-02T11:00:00+03:00',
    '2023-03-02T11:00:00+03:00',
    '2023-03-06T11:00:00+03:00',
    (SELECT id FROM users WHERE username = 'admin'),
    (SELECT id FROM users WHERE username = 'user2')
  );

INSERT INTO comments (user_id, task_id, text, created_at, updated_at)
VALUES
  (
    (SELECT id FROM users WHERE username = 'user1'),
    (SELECT id FROM tasks WHERE title = 'Проверка тестового задания'),
    'Кандидат молодец, стоит пригласить его на техническое интервью.',
    '2023-03-01T12:00:00+03:00',
    '2023-03-01T12:00:00+03:00'
  ),
  (
    (SELECT id FROM users WHERE username = 'user2'),
    (SELECT id FROM tasks WHERE title = 'Написание документации Swagger'),
    'Зачем писать, автоматической же хватает',
    '2023-03-02T12:30:00+03:00',
    '2023-03-02T12:30:00+03:00'
  );

