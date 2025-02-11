# Руководство пользователя Task Management System

## Описание

**Task Management System** - это API для управления задачами, включающее функции регистрации, авторизации, работы с задачами и комментариями. Система позволяет создавать, обновлять и удалять задачи, а также управлять их статусами и приоритетами.

## Требования

Для локального запуска проекта необходимы следующие инструменты:

- **Docker**: версия 20.10 или выше ([скачать Docker](https://www.docker.com/get-started))
- **Docker Compose**: обычно включен в состав Docker Desktop
- **Git**: для клонирования репозитория

Проверьте установку с помощью команд:

```
docker --version
docker-compose --version
git --version
```

## Локальный запуск проекта
#### Скопируйте репозиторий:

```
git clone https://github.com/madphyllomedusa/TaskManagementSystem.git](https://github.com/madphyllomedusa/TaskManagement
cd TaskManagement
```
#### Запустите приложение с помощью Docker Compose:

```
docker-compose up --build
```
#### Для запуска в фоновом режиме используйте флаг -d:

```
docker-compose up -d --build
```
#### Откройте приложение в браузере:

Приложение будет доступно по адресу:

```
http://localhost:8080
```
#### Остановка приложения:

```
docker-compose down
```

### API Документация

#### Документация Swagger доступна по адресу:

```
http://localhost:8080/swagger-ui/index.html
```

#### Так же предусмотрена коллекция в Postman

```
https://www.postman.com/joint-operations-architect-36770489/workspace/phyllomedusa/collection/37520314-cb3adce8-e69c-48d4-8ebf-ffd0309e4e9f?
```

Или json файл [Postman json](https://disk.yandex.ru/d/gV81jqBtrz9Qyg)

### Поддержка
### Если у вас возникли вопросы, обратитесь к разработчику:

Telegram: [@phyllomeduska](https://t.me/phyllomeduska)

Email: matthew.vladimir@mail.ru
