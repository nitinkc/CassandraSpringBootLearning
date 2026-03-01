-- Lab 07: Aggregation and filtering
-- COUNT(*) scans data; only efficient when scoped to a single partition. Avoid cluster-wide COUNT in prod.
-- ALLOW FILTERING triggers server-side scans; design tables to avoid it.

-- docker compose -f scripts/docker-compose.yml exec cassandra cqlsh -f /labs/07_aggregation_filtering.cql
USE cassandra_labs;

SELECT COUNT(*) FROM users_by_id;

-- Safe partition-scoped aggregation (bounded):
SELECT COUNT(*) FROM orders_by_user
WHERE user_id = 11111111-1111-1111-1111-111111111111;

-- Bad pattern: full scan + ALLOW FILTERING. Use users_by_email table instead (lab 03).
SELECT * FROM users_by_id WHERE full_name = 'Sam Lee' ALLOW FILTERING;

-- Alternative to ALLOW FILTERING: maintain a dedicated lookup table (users_by_email) or pre-aggregate counters per key.
