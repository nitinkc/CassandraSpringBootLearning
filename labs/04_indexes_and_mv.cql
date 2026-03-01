-- Lab 04: Secondary indexes and materialized views
-- Secondary indexes: ok for low-cardinality or single-partition fan-out; avoid on high-cardinality/high-selectivity columns.
-- Materialized views: server-managed denormalization; can drift and add repair cost. Prefer manual dual writes when possible.

-- docker compose -f scripts/docker-compose.yml exec cassandra cqlsh -f /labs/04_indexes_and_mv.cql
USE cassandra_labs;

CREATE INDEX IF NOT EXISTS users_by_id_email_idx
ON users_by_id (email);

SELECT * FROM users_by_id WHERE email = 'sam@example.com';

CREATE MATERIALIZED VIEW IF NOT EXISTS users_by_email_mv AS
  SELECT email, user_id, full_name, created_at
  FROM users_by_id
  WHERE email IS NOT NULL AND user_id IS NOT NULL
  PRIMARY KEY ((email), user_id);

SELECT * FROM users_by_email_mv WHERE email = 'sam@example.com';

-- Safer alternative: keep using the dedicated users_by_email table (manual dual writes) from lab 03.
