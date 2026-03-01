-- Lab 02: Partitioning and clustering
-- Partition key (event_day) drives distribution; clustering (event_ts) orders within the partition.
-- Avoid unbounded partitions (e.g., single key for all events). Use time buckets to cap size.

--  docker compose -f scripts/docker-compose.yml exec cassandra cqlsh -f /labs/02_partitioning_clustering.cql
USE cassandra_labs;

CREATE TABLE IF NOT EXISTS events_by_day (
  event_day date,
  event_ts timestamp,
  event_id uuid,
  event_type text,
  payload text,
  PRIMARY KEY ((event_day), event_ts)
) WITH CLUSTERING ORDER BY (event_ts DESC);

INSERT INTO events_by_day (event_day, event_ts, event_id, event_type, payload)
VALUES ('2026-02-23', toTimestamp(now()), uuid(), 'LOGIN', 'user=alex');

SELECT * FROM events_by_day WHERE event_day = '2026-02-23' LIMIT 5;

SELECT * FROM events_by_day
WHERE event_day = '2026-02-23'
  AND event_ts > '2026-02-22'
  AND event_ts < '2026-02-24';

-- Bounded partition example: day + hour bucket to prevent wide partitions on hot days.
CREATE TABLE IF NOT EXISTS events_by_day_hour (
  event_day date,
  hour_bucket tinyint,
  event_ts timeuuid,
  event_type text,
  payload text,
  PRIMARY KEY ((event_day, hour_bucket), event_ts)
) WITH CLUSTERING ORDER BY (event_ts DESC);

INSERT INTO events_by_day_hour (event_day, hour_bucket, event_ts, event_type, payload)
VALUES ('2026-02-23', 10, now(), 'LOGIN', 'user=beth');

SELECT * FROM events_by_day_hour WHERE event_day = '2026-02-23' AND hour_bucket = 10 LIMIT 5;
