# Задание 1 (дедлайн 31.03.2025)
1. Создать новый репозиторий (пусть init-коммит содержит только _README.MD_ и _.gitignore_).
2. Добавить в проект преподавателя _shumilin2604@gmail.com_ ([инструкция](https://docs.github.com/ru/account-and-profile/setting-up-and-managing-your-personal-account-on-github/managing-access-to-your-personal-repositories/inviting-collaborators-to-a-personal-repository) как пример).
3. В новой ветке разработать CRUD-приложение + логи через аспекты.

Необходимо создать простой RESTful-сервис для управления задачами - класс **Task** с полями _id_, _title_, _description_, _userId_ и следующие эндпоинты:
  
    POST /tasks — создание новой задачи;
    GET /tasks/{id} — получение задачи по ID;
    PUT /tasks/{id} — обновление задачи;
    DELETE /tasks/{id} — удаление задачи;
    GET /tasks — получение списка всех задач.

4. Реализовать класс-аспект со следующими _advice_:
- Before
- AfterThrowing
- AfterReturning
- Around (замер выполнения)

В приложении должна быть реализована следующая логика: на каждый advice - отдельный метод.

Можно сделать больше, использовать несколько advice на отдельные методы, но меньше нельзя.

5. Задание необходимо сдать в виде _PullRequest_ на **GitHub**.
6. Прислать ссылку на PR в личном кабинете на LMS для проверки.

### Примеры запросов
Создание новой задачи (POST /tasks)

    curl -X POST -H "Content-Type: application/json" -d '{"title":"Task 1","description":"Description 1","userId":1}' http://localhost:8080/tasks

Получение задачи по ID (GET /tasks/{id})

    curl -X GET http://localhost:8080/tasks/1

Обновление задачи (PUT /tasks/{id})

    curl -X PUT -H "Content-Type: application/json" -d '{"title":"Updated Task 1","description":"Updated Description 1","userId":1}' http://localhost:8080/tasks/1

Удаление задачи (DELETE /tasks/{id})

    curl -X DELETE http://localhost:8080/tasks/1

Получение списка всех задач (GET /tasks)

    curl -X GET http://localhost:8080/tasks

Вызов метода, чтобы сработал аспект @AfterThrowing

    curl -X GET http://localhost:8080/tasks/exception

# Задание 2 (дедлайн 09.04.2025)
1. Использовать docker-compose для установки Kafka в Docker (пример демонстрируется на уроке)
2. Установить необходимые инструменты для работы с Kafka (Big Data Tools, Offset Explorer и др.) на свое усмотрение. Результатом должна быть возможность "заглянуть" в Кафку.
3. Создать тестовый топик установленными средствами.
3. Отправить в топик тестовые сообщения.
4. Убедиться в их наличии.
5. Сконфигурировать Kafka, Producer, Consumer в вашем сервисе работы с Task.
6. Продюсер пишет в топик id и новый статус task у которых он изменился при соответствующем входящем запросе (обновления task).
7. Консьюмер слушает этот топик, читает оттуда сообщения, и отправляет в NotificationService. Этот класс должен отправлять уведомление на почту о смене статуса, полученном от консьюмера.
8. Использовать spring-boot-starter-mail для отправки email в NotificationService.