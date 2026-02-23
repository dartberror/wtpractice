DROP TABLE IF EXISTS schedule CASCADE;
DROP TABLE IF EXISTS course_students CASCADE;
DROP TABLE IF EXISTS course_teachers CASCADE;
DROP TABLE IF EXISTS courses CASCADE;
DROP TABLE IF EXISTS teachers CASCADE;
DROP TABLE IF EXISTS students CASCADE;
DROP TABLE IF EXISTS companies CASCADE;
--ROLLBACK;

BEGIN;
INSERT INTO companies (name, address) VALUES
('ООО «СкиллПро»',          'г. Москва, ул. Тверская, д. 1'),
('АО «ТехТренинг»',         'г. Санкт-Петербург, Невский пр., д. 10'),
('ООО «ИнфоКурс»',          'г. Казань, ул. Баумана, д. 5'),
('ООО «Учебные Решения»',   'г. Екатеринбург, пр. Ленина, д. 25'),
('ИП «Петрова Е.А.»',       'г. Новосибирск, ул. Красный проспект, д. 12');

INSERT INTO students (full_name) VALUES
('Кузнецов Кирилл Константинович'),
('Смирнова Мария Олеговна'),
('Васильев Алексей Дмитриевич'),
('Попова Анна Игоревна'),
('Иванова Дарья Сергеевна');

INSERT INTO teachers (full_name, company_id) VALUES
('Иванов Иван Иванович',     1),
('Петров Пётр Петрович',     2),
('Сидорова Анна Сергеевна',  3),
('Орлова Елена Викторовна',  4),
('Никитин Сергей Андреевич', 5);

INSERT INTO courses (title, duration_unit, duration_value, intensity, company_id) VALUES
('Введение в SQL',           'day',    1,  4, 1),
('Основы Git',               'days',   3,  3, 2),
('Java Core',                'week',   1,  4, 1),
('Тестирование ПО',          'weeks',  2,  3, 3),
('Базы данных: проектирование', 'month', 1, 2, 4),
('Python для начинающих',    'months', 2,  2, 5);

INSERT INTO course_teachers (course_id, teacher_id) VALUES
(1, 1),  -- Введение в SQL — Иванов
(2, 2),  -- Основы Git — Петров
(3, 1),  -- Java Core — Иванов
(4, 3),  -- Тестирование ПО — Сидорова
(6, 5),  -- Python для начинающих — Никитин
(5, 4);  -- Проектирование БД — Орлова

INSERT INTO course_students (student_id, course_id) VALUES
(1, 1),
(2, 1),
(3, 3),
(4, 4),
(5, 6),
(1, 4),
(2, 2);

INSERT INTO schedule (course_id, teacher_id, start_at, end_at) VALUES
(1, 1, '2026-03-02 10:00+03', '2026-03-02 14:00+03'),
(1, 1, '2026-03-03 10:00+03', '2026-03-03 14:00+03');
INSERT INTO schedule (course_id, teacher_id, start_at, end_at) VALUES
(2, 2, '2026-03-04 11:00+03', '2026-03-04 14:00+03');
INSERT INTO schedule (course_id, teacher_id, start_at, end_at) VALUES
(3, 1, '2026-03-05 15:00+03', '2026-03-05 19:00+03');
INSERT INTO schedule (course_id, teacher_id, start_at, end_at) VALUES
(4, 3, '2026-03-06 16:00+03', '2026-03-06 19:00+03'),
(4, 3, '2026-03-09 16:00+03', '2026-03-09 19:00+03');
INSERT INTO schedule (course_id, teacher_id, start_at, end_at) VALUES
(6, 5, '2026-03-10 12:00+03', '2026-03-10 14:00+03');
COMMIT;

SELECT *
FROM companies
ORDER BY id;

SELECT
  c.title AS курс,
  t.full_name AS преподаватель
FROM course_teachers ct
JOIN courses c ON c.id = ct.course_id
JOIN teachers t ON t.id = ct.teacher_id
ORDER BY c.title;