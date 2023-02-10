#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER springboot WITH PASSWORD 'qwerty';
    CREATE DATABASE springboot;
    GRANT ALL PRIVILEGES ON DATABASE springboot TO springboot;
EOSQL