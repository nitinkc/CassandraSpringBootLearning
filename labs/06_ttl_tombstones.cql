-- Lab 06: TTL and tombstones
-- TTL creates tombstones on expiry; high churn can bloat SSTables until compaction removes them.
-- Deletes also create tombstones; avoid range scans over many tombstones to prevent read timeouts.

-- docker compose -f scripts/docker-compose.yml exec cassandra cqlsh -f /labs/06_ttl_tombstones.cql
USE cassandra_labs;

CREATE TABLE IF NOT EXISTS sessions_by_user (
  user_id uuid,
  session_id uuid,
  created_at timestamp,
  metadata text,
  PRIMARY KEY ((user_id), session_id)
);

INSERT INTO sessions_by_user (user_id, session_id, created_at, metadata)
VALUES (44444444-4444-4444-4444-444444444444, bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb, toTimestamp(now()), 'ip=10.0.0.1')
USING TTL 300;

SELECT TTL(metadata), WRITETIME(metadata)
FROM sessions_by_user
WHERE user_id = 44444444-4444-4444-4444-444444444444;

-- Update with TTL extends the row but creates another tombstone for replaced cells if TTL differs.
INSERT INTO sessions_by_user (user_id, session_id, created_at, metadata)
VALUES (44444444-4444-4444-4444-444444444444, cccccccc-cccc-cccc-cccc-cccccccccccc, toTimestamp(now()), 'ip=10.0.0.2')
USING TTL 120;

DELETE FROM sessions_by_user
WHERE user_id = 44444444-4444-4444-4444-444444444444
  AND session_id = bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb;

-- To clean up tombstones sooner in test/dev, lower gc_grace_seconds on non-replicated tables (never do this blindly in prod).
