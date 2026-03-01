-- Lab 05: Consistency levels, LWT, and batching
-- CL guidance: LOCAL_QUORUM/QUORUM for prod (RF>=3) to balance consistency/latency; ONE for local dev/testing.
-- LWT (IF clauses) use Paxos; keep them for rare/critical constraints (e.g., uniqueness) due to higher latency.
-- Logged batch = atomic across partitions (more I/O). Unlogged batch = network grouping; use only when partitions share the same key.

-- docker compose -f scripts/docker-compose.yml exec cassandra cqlsh -f /labs/05_consistency_lwt_batch.cql
USE cassandra_labs;

CONSISTENCY ONE; -- dev/local; switch to QUORUM or LOCAL_QUORUM with RF>=3 in real environments.

INSERT INTO users_by_id (user_id, email, full_name, created_at)
VALUES (33333333-3333-3333-3333-333333333333, 'pat@example.com', 'Pat Morgan', toTimestamp(now()))
IF NOT EXISTS;

UPDATE users_by_id
SET full_name = 'Pat M.'
WHERE user_id = 33333333-3333-3333-3333-333333333333
IF full_name = 'Pat Morgan';

BEGIN UNLOGGED BATCH
  UPDATE users_by_id SET full_name = 'Pat Morgan' WHERE user_id = 33333333-3333-3333-3333-333333333333;
  UPDATE users_by_id SET full_name = 'Pat Morgan' WHERE user_id = 33333333-3333-3333-3333-333333333333;
APPLY BATCH;

-- Logged batch example (use sparingly; adds batch log):
BEGIN LOGGED BATCH
  INSERT INTO users_by_id (user_id, email, full_name, created_at)
  VALUES (44444444-4444-4444-4444-444444444444, 'dev@example.com', 'Dev User', toTimestamp(now()));
  INSERT INTO users_by_email (email, user_id, full_name, created_at)
  VALUES ('dev@example.com', 44444444-4444-4444-4444-444444444444, 'Dev User', toTimestamp(now()));
APPLY BATCH;
