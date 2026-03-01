-- Lab 03: Modeling by query
-- Tables are shaped per access pattern; duplicate data to satisfy reads without ALLOW FILTERING.
-- Dual writes keep lookup tables in sync (done in app/service layer or lightweight batch here).

-- docker compose -f scripts/docker-compose.yml exec cassandra cqlsh -f /labs/03_modeling_by_query.cql
USE cassandra_labs;

CREATE TABLE IF NOT EXISTS users_by_id (
  user_id uuid,
  email text,
  full_name text,
  created_at timestamp,
  PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS users_by_email (
  email text,
  user_id uuid,
  full_name text,
  created_at timestamp,
  PRIMARY KEY ((email), user_id)
) WITH CLUSTERING ORDER BY (user_id ASC);

BEGIN BATCH
  INSERT INTO users_by_id (user_id, email, full_name, created_at)
  VALUES (22222222-2222-2222-2222-222222222222, 'sam@example.com', 'Sam Lee', toTimestamp(now()));
  INSERT INTO users_by_email (email, user_id, full_name, created_at)
  VALUES ('sam@example.com', 22222222-2222-2222-2222-222222222222, 'Sam Lee', toTimestamp(now()));
APPLY BATCH;

SELECT * FROM users_by_id WHERE user_id = 22222222-2222-2222-2222-222222222222;
SELECT * FROM users_by_email WHERE email = 'sam@example.com';

-- Bad pattern (full scan):
-- SELECT * FROM users_by_id WHERE email = 'sam@example.com'; -- will time out or require ALLOW FILTERING
-- Correct pattern uses the lookup table above.
