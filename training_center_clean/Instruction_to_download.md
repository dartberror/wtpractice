#Руководство по запуску

## Что покрывают системные тесты
- Навигация и открытие основных страниц.
- Сценарии CRUD и валидации для организаций.
- Сценарии CRUD и валидации для обучающихся.
- Сценарии CRUD и валидации для преподавателей.
- Сценарии CRUD и валидации для курсов.
- Назначение и удаление обучающихся/преподавателей в курсе.
- Успешное создание занятия и проверки валидации.
- Расписание обучающегося/преподавателя для корректного и некорректного интервала.

Подробный список сценариев: `plots_of_tests.md`.

## Локальный запуск

1. Подготовьте базу данных.
- Создайте схему и заполните данными через SQL-скрипты (`Java_Roma.sql`, `Java_Roma_2.sql`).
- Проверьте, что PostgreSQL доступен с настройками проекта.

2. Запустите веб-приложение.
- Разверните приложение в Tomcat.
- Убедитесь, что приложение открывается по адресу:
`http://localhost:8080/training_center_clean`

3. Запустите тесты.
- Полный набор тестов:
`mvn test`
- Только Selenium-системные тесты:
`mvn -Dtest=training_center.system.WebSystemTest test`

4. Откройте отчет по тестам.
- `target/surefire-reports/index.html`

## Сборка WAR
- Собрать WAR:
`mvn clean package`
- Результат:
`target/training_center_clean.war`

## Сборка и деплой через Ant
Файл сборки: `build.xml`

Основной target:
- `full-build-and-deploy`

Запуск:
- `ant -f build.xml full-build-and-deploy`

Папка деплоя по умолчанию в `build.xml`:
- `C:/tomcat/wtpwebapps`

Если нужно переопределить параметры:
- `ant -f build.xml -Dmaven.cmd=C:/path/to/mvn.cmd -Dtomcat.deploy.dir=C:/tomcat/wtpwebapps full-build-and-deploy`


## Быстрый сценарий воспроизведения
1. Открыть проект.
2. Инициализировать БД SQL-скриптами.
3. Запустить Tomcat и развернуть приложение.
4. Выполнить `mvn test`.
5. Открыть `target/surefire-reports/index.html`.
6. Выполнить `ant -f build.xml full-build-and-deploy`.
