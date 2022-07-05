MERGE INTO genre KEY (genre_id) VALUES (1, 'Комедия');
MERGE INTO genre KEY (genre_id) VALUES (2, 'Драма');
MERGE INTO genre KEY (genre_id) VALUES (3, 'Мультфильм');
MERGE INTO genre KEY (genre_id) VALUES (4, 'Триллер');
MERGE INTO genre KEY (genre_id) VALUES (5, 'Документальный');
MERGE INTO genre KEY (genre_id) VALUES (6, 'Боевик');

MERGE INTO mpa_rating KEY (mpa_rating_id) VALUES (1, 'G', 'Без возрастных ограничений');
MERGE INTO mpa_rating KEY (mpa_rating_id) VALUES (2, 'PG', 'Рекомендуется просмтотр с родителями');
MERGE INTO mpa_rating KEY (mpa_rating_id) VALUES (3, 'PG-13', 'До 13 лет просмотр не желателен');
MERGE INTO mpa_rating KEY (mpa_rating_id) VALUES (4, 'R', 'До 17 лет просмотр разрешен только в присутствии взрослых');
MERGE INTO mpa_rating KEY (mpa_rating_id) VALUES (5, 'NC-17', 'До 18 лет просмотр запрещен');

MERGE INTO friendship_status KEY (friendship_status_id) VALUES (1, 'CONF', 'Подтвержденная');
MERGE INTO friendship_status KEY (friendship_status_id) VALUES (2, 'UNCONF', 'Неподтвержденная');