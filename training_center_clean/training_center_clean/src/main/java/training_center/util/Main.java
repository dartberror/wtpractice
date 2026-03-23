package training_center.util;

public class Main {

    public static void main(String[] args) {
        try {
            HibernateUtil.getSessionFactory();
            System.out.println("Hibernate успешно запущен!");
        } catch (Exception e) {
            System.out.println("Ошибка запуска Hibernate:");
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
        }
    }
}
