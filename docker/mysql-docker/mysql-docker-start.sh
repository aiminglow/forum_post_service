#!/bin/bash

docker run --rm --name mysql-dev-ut-it -d -p 33061:3306 -e MYSQL_DATABASE=forum_microservice -e MYSQL_ROOT_PASSWORD=maisekou mysql:8 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
