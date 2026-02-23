-- Для удобства пересоздания
DROP TABLE IF EXISTS schedule CASCADE;
DROP TABLE IF EXISTS course_students CASCADE;
DROP TABLE IF EXISTS course_teachers CASCADE;
DROP TABLE IF EXISTS courses CASCADE;
DROP TABLE IF EXISTS teachers CASCADE;
DROP TABLE IF EXISTS students CASCADE;
DROP TABLE IF EXISTS companies CASCADE;

-- Компании
CREATE TABLE companies (
  id      BIGSERIAL PRIMARY KEY,
  name    VARCHAR(200) NOT NULL,
  address VARCHAR(300) NOT NULL
);

-- Обучающиеся
CREATE TABLE students (
  id        BIGSERIAL PRIMARY KEY,
  full_name VARCHAR(200) NOT NULL
);

-- Преподаватели
CREATE TABLE teachers (
  id         BIGSERIAL PRIMARY KEY,
  full_name  VARCHAR(200) NOT NULL,
  company_id BIGINT NOT NULL,
  CONSTRAINT fk_teachers_company
    FOREIGN KEY (company_id) REFERENCES companies(id)
    ON DELETE RESTRICT
);

-- Курсы
CREATE TABLE courses (
  id            BIGSERIAL PRIMARY KEY,
  title         VARCHAR(200) NOT NULL,
  duration_unit VARCHAR(20)  NOT NULL, -- day/days/week/weeks/month/months
  duration_value INTEGER     NOT NULL,
  intensity     INTEGER      NOT NULL,
  company_id    BIGINT       NOT NULL,
  CONSTRAINT fk_courses_company
    FOREIGN KEY (company_id) REFERENCES companies(id)
    ON DELETE RESTRICT,
  CONSTRAINT ck_duration_unit
    CHECK (duration_unit IN ('day','days','week','weeks','month','months')),
  CONSTRAINT ck_duration_value
    CHECK (duration_value > 0),
  CONSTRAINT ck_intensity
    CHECK (intensity > 0)
);

-- курс-преподаватель
CREATE TABLE course_teachers (
  course_id  BIGINT NOT NULL,
  teacher_id BIGINT NOT NULL,
  PRIMARY KEY (course_id, teacher_id),
  CONSTRAINT fk_ct_course
    FOREIGN KEY (course_id) REFERENCES courses(id)
    ON DELETE CASCADE,
  CONSTRAINT fk_ct_teacher
    FOREIGN KEY (teacher_id) REFERENCES teachers(id)
    ON DELETE RESTRICT
);

-- обучающийся-курс
CREATE TABLE course_students (
  student_id  BIGINT NOT NULL,
  course_id   BIGINT NOT NULL,
  enrolled_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  PRIMARY KEY (student_id, course_id),
  CONSTRAINT fk_cs_student
    FOREIGN KEY (student_id) REFERENCES students(id)
    ON DELETE CASCADE,
  CONSTRAINT fk_cs_course
    FOREIGN KEY (course_id) REFERENCES courses(id)
    ON DELETE CASCADE
);

-- расписание
CREATE TABLE schedule (
  id         BIGSERIAL PRIMARY KEY,
  course_id  BIGINT NOT NULL,
  teacher_id BIGINT NOT NULL,
  start_at   TIMESTAMPTZ NOT NULL,
  end_at     TIMESTAMPTZ NOT NULL,
  CONSTRAINT fk_schedule_course
    FOREIGN KEY (course_id) REFERENCES courses(id)
    ON DELETE CASCADE,
  CONSTRAINT fk_schedule_teacher
    FOREIGN KEY (teacher_id) REFERENCES teachers(id)
    ON DELETE RESTRICT,
  CONSTRAINT ck_schedule_time
    CHECK (end_at > start_at)
);

