# Cassandra Interview Q&A (Most Important First)

## Core Concepts
- What is a partition key, and why is it critical?
  - It defines data placement and parallelism; choose it to evenly distribute load and bound partitions. Example:
    ```sql
    CREATE TABLE orders_by_user (
      user_id UUID,
      order_id TIMEUUID,
      amount decimal,
      PRIMARY KEY (user_id, order_id)
    );
    -- user_id is the partition key: all of a user's orders colocate; order_id clusters for ordering.
    ```
- How do clustering columns affect on-disk ordering and query patterns?
  - They sort rows within a partition and enable range queries. Order matters for filtering and paging.
    ```sql
    PRIMARY KEY (user_id, order_ts DESC, status)
    -- Enables queries like: SELECT * FROM ... WHERE user_id=? AND order_ts >= ? LIMIT 20;
    ```
- Why is data modeled by query in Cassandra?
  - No joins; tables are shaped per access pattern to keep reads single-partition and predictable latency.
- What are common causes of hot partitions?
  - Skewed partition keys (e.g., country=US, date only), monotonically increasing keys, or small RF with high traffic.
- How does replication factor impact availability and reads/writes?
  - RF defines copies per DC. With RF=3 and CL=QUORUM, you can lose one node and still read/write. Higher RF improves availability at storage cost.

## Data Modeling and Querying
- When would you denormalize and duplicate data?
  - To serve multiple queries without ALLOW FILTERING or secondary indexes; storage is cheap, latency is not. Example (user lookup by email):
    ```sql
    CREATE TABLE users_by_id (
      user_id UUID PRIMARY KEY,
      email text,
      full_name text
    );
    CREATE TABLE users_by_email (
      email text,
      user_id UUID,
      full_name text,
      PRIMARY KEY (email, user_id)
    );
    ```
- How do you design tables for time-series data?
  - Use a compound key with a bounded time bucket to prevent wide partitions.
    ```sql
    CREATE TABLE readings_by_device (
      device_id UUID,
      day date,
      ts TIMEUUID,
      reading double,
      PRIMARY KEY ((device_id, day), ts)
    ) WITH CLUSTERING ORDER BY (ts DESC);
    ```
- What are anti-patterns for secondary indexes?
  - High-cardinality partitions, filtering most rows, using as a replacement for proper primary keys, or indexing columns rarely queried.
- What are materialized views and when are they risky?
  - Server-managed denormalization; risk of divergence and repair overhead. Prefer manual dual writes unless the view is small and strictly needed.
- Why is `ALLOW FILTERING` discouraged?
  - It scans partitions or the whole table, causing unpredictable latency and load; it is a last-resort debugging tool.

## Consistency and Transactions
- Explain consistency levels and the R + W > RF rule.
  - If read CL plus write CL exceed RF, a quorum of replicas overlap, giving strong consistency. Example RF=3: QUORUM+QUORUM => 2+2>3.
- What are lightweight transactions (LWT) and when do you use them?
  - Compare-and-set across replicas using Paxos; use for uniqueness or infrequent conditional updates.
    ```sql
    INSERT INTO users_by_email (email, user_id, full_name)
    VALUES ('alex@example.com', 1e111d10-1111-1111-1111-111111111111, 'Alex') IF NOT EXISTS;
    ```
- What is the difference between logged and unlogged batches?
  - Logged batches write a batch log for atomicity across partitions (extra I/O). Unlogged batches are just a network grouping for same-partition mutations.

## Performance and Operations
- What are tombstones and how do they impact reads?
  - Markers for deletes/TTL; many tombstones cause heavy reads and timeouts until purged by compaction.
- How does TTL affect storage and compaction?
  - Expired cells become tombstones; large TTL churn can bloat SSTables until compaction clears them.
- How do you troubleshoot slow queries?
  - Check coordinator tracing, profile partition size, ensure single-partition access, verify correct CL, and inspect tombstone warnings.
- What is speculative retry?
  - Coordinator sends a duplicate request after a delay when a replica is slow, improving tail latency at the cost of extra load.

## Advanced Topics
- How does compaction strategy affect reads/writes?
  - STCS: good for write-heavy, but read amplification; LCS: better read latency, more write/amplification; TWCS: best for time-series with TTL.
- What is read repair and when does it happen?
  - Background reconciliation during reads; either blocking (on digests mismatch at CL>ONE) or speculative per configuration.
- How do you handle schema changes at scale?
  - Apply via migrations, wait for schema agreement, roll out in steps (add new tables, dual write, cut over, retire old tables).
- How do you model many-to-many relationships?
  - Use two denormalized tables to keep queries single-partition.
    ```sql
    CREATE TABLE users_by_group (
      group_id UUID,
      user_id UUID,
      full_name text,
      PRIMARY KEY (group_id, user_id)
    );
    CREATE TABLE groups_by_user (
      user_id UUID,
      group_id UUID,
      group_name text,
      PRIMARY KEY (user_id, group_id)
    );
    ```
