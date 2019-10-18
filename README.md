# BillingService
Сервис тарификации. Реализованные методы:

- PUT /activate/{number} - активация сим-карты. Возвращает объект Sim
- PUT /deactivate/{number} - блокировка сим-карты. Возвращает объект Sim
- GET /minutes/{number}  - получить данных о пакете минут. Возвращает объект SimPackage
- GET /megabytes/{number} - получение данных о пакете траффика (в мегабайтах). Возвращает объект SimPackage
- PUT /addminutes/{number} - добавление пакета минут. Принимает объект SimPackag, возвращает объект Sim
- PUT /addmegabytes/{number} - добавление пакета мегабайт. Принимает объект SimPackag, возвращает объект Sim
- PUT /writeoffminutes/{number} - списание минут. Принимает числовой парамет запроса value (размер списания), возвращает объект Sim
- PUT /writeoffmegabytes/{number} - списание мегабайт. Принимает числовой парамет запроса value (размер списания), возвращает объект Sim

Sim - представление данных о сим-карте, имеет следущую структуру:

    -  long number; // номер сим-карты;
    -  boolean status; // флаг, является ли сим-карта активной
    -  SimPackage minutes; // пакет минут
    -  SimPackage megabytes; // пакет мегабайт
    
SimPackage - пакет, привязанный к сим-карте. С каждым номером ассациированно два пакета : минуты и мегабайты. Имеет следущую структуру:

    -  int value; // номер сим-карты;
    -  Date  exp; // дата окончания действия пакета

Добавление нового пакета к номеру ведёт к полной замене предыдущего пакета.


Объекты Sim и SimPackage передаются в формате JSON.

Сервис реализован с помощью Spring Boot, в качестве базы данных используется H2 в embedded mode.
Коннструирование  и  инициализация таблиц прописаны в data.sql и schema.sql. Для работы достаточно запустить полученный в ходе сборки jar-файл. Сервис разворачивается на localhost:8080
