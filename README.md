# Task Management System

REST API для управления задачами на Spring Boot. Позволяет создавать задачи, назначать исполнителей, переводить задачи по статусам и получать список с фильтром и пагинацией.

## Технологии

- Java 25
- Spring Boot 4.1.2 (Web MVC, Data JPA, Validation)
- PostgreSQL
- springdoc-openapi (Swagger UI)
- Maven (wrapper уже в проекте)

## Быстрый старт

### 1. Требования

- JDK 25
- PostgreSQL на `localhost:5432`

### 2. Настройка базы данных

Параметры подключения лежат в `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
```

Измените их под свою среду. Таблица `tasks` создаётся автоматически при первом запуске (`ddl-auto=update`).

### 3. Запуск

```bash
./mvnw spring-boot:run
```

На Windows: `mvnw.cmd spring-boot:run`.

Приложение стартует на `http://localhost:8080`.

### 4. Документация API

Swagger UI: `http://localhost:8080/swagger-ui/index.html`

## Модель задачи

| Поле | Тип | Описание |
|---|---|---|
| `id` | Long | Идентификатор, генерируется БД |
| `creatorId` | Long | Автор задачи (обязательно) |
| `assignedUserId` | Long | Исполнитель (обязательно) |
| `status` | enum | `CREATED`, `IN_PROGRESS`, `DONE` |
| `createDateTime` | date | Дата создания, сегодня или позже (обязательно) |
| `deadlineDate` | date | Дедлайн, в будущем (обязательно) |
| `priority` | enum | `LOW`, `MEDIUM`, `HIGH` (обязательно) |
| `doneDateTime` | datetime | Время завершения, ставится при `complete` |

## Эндпоинты

Базовый путь: `/tasks`

| Метод | Путь | Описание |
|---|---|---|
| GET | `/tasks/{id}` | Получить задачу по id |
| GET | `/tasks` | Список задач. Параметры: `creatorId`, `pageSize` (по умолчанию 10), `pageNumber` (по умолчанию 0) |
| POST | `/tasks` | Создать задачу (статус задаётся автоматически как `CREATED`) |
| PUT | `/tasks/{id}` | Обновить задачу |
| POST | `/tasks/{id}/start` | Перевести задачу в `IN_PROGRESS` |
| POST | `/tasks/{id}/complete` | Завершить задачу (`DONE`) |
| DELETE | `/tasks/{id}` | Удалить задачу |

### Пример создания задачи

```bash
curl -X POST http://localhost:8080/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "creatorId": 1,
    "assignedUserId": 2,
    "createDateTime": "2026-11-04",
    "deadlineDate": "2026-11-20",
    "priority": "HIGH"
  }'
```

## Бизнес-правила

- При создании поле `status` должно быть пустым, задача получает `CREATED`.
- Дата создания должна быть раньше дедлайна.
- Завершённую задачу (`DONE`) изменять нельзя.
- У одного исполнителя не может быть больше 5 задач в статусе `IN_PROGRESS` одновременно.
- Для старта задачи должны быть заданы автор и исполнитель.

## Обработка ошибок

Все ошибки возвращаются в едином формате:

```json
{
  "message": "Not found error",
  "detailedMessage": "Not found task by id: 42",
  "errorTime": "2026-11-04T13:50:00"
}
```

| Код | Когда |
|---|---|
| 400 | Ошибка валидации или нарушено бизнес-правило |
| 404 | Задача не найдена |
| 500 | Непредвиденная ошибка |

## Структура проекта

```
src/main/java/com/example/task_Management
├── Task      # контроллер, сервис, репозиторий, сущность, маппер, enum'ы
└── web       # глобальный обработчик ошибок и DTO ошибки
```

## Тесты

```bash
./mvnw test
```
