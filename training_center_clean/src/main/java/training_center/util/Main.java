package training_center.util;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import training_center.config.PersistenceConfig;

public class Main {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(PersistenceConfig.class)) {

            System.out.println("Spring context started successfully.");
        }
    }
}