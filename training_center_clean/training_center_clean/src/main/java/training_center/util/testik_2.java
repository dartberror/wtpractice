package training_center.util;

import java.time.OffsetDateTime;
import java.util.List;

import training_center.dao.ScheduleDao;
import training_center.dao.StudentDao;
import training_center.entity.Schedule;
import training_center.entity.Student;

public class testik_2 {

    public static void main(String[] args) {
        StudentDao studentDao = new StudentDao();
        ScheduleDao scheduleDao = new ScheduleDao();

        try {
            System.out.println("Список обучающихся по курсу");
            List<Student> students = studentDao.findByCourseId(1L);

            for (Student student : students) {
                System.out.println(
                        "ID: " + student.getId() +
                        ", ФИО: " + student.getFullName()
                );
            }

            System.out.println();
            System.out.println("Расписание преподавателя на интервал");

            OffsetDateTime from = OffsetDateTime.parse("2026-03-01T00:00:00+03:00");
            OffsetDateTime to = OffsetDateTime.parse("2026-03-31T23:59:59+03:00");

            List<Schedule> scheduleList = scheduleDao.findByTeacherAndInterval(1L, from, to);

            for (Schedule schedule : scheduleList) {
                System.out.println(
                        "Занятие ID: " + schedule.getId() +
                        ", Курс: " + schedule.getCourse().getTitle() +
                        ", Начало: " + schedule.getStartAt() +
                        ", Конец: " + schedule.getEndAt()
                );
            }

        } catch (Exception e) {
            System.out.println("Ошибка при тестировании DAO-методов:");
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
        }
    }
}
