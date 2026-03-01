# Cassandra CQL Labs — Guided Learning Path

These labs are incremental and intentionally query-first. Follow them in order to build a mental model of Cassandra's data modeling choices, trade-offs, and operational concerns.

Environment (assumptions)
- Cassandra 5.x (local Docker-based labs).
- Java 21 / Spring Boot app lives in `spring-boot-app/` (used separately from the labs).
- Schema and initialization live under `scripts/` and `labs/`.
- Conventions: schema uses query-first denormalized tables; each lab uses `IF NOT EXISTS` so it is safe to re-run.

Quick start
- Start the Docker Cassandra setup (from project root):

```bash
# start the cluster as defined in scripts/docker-compose.yml
docker compose -f scripts/docker-compose.yml up -d
```

- Run a lab file inside the running Cassandra container. 
- Note: the container does not automatically see your host `labs/` files unless they are mounted. Two safe options:

1) Use the provided init script (if present) that is already copied into the container:

```bash
# this runs the combined init that the container exposes (if scripts/init.cql exists)
docker compose -f scripts/docker-compose.yml exec cassandra cqlsh -f /init/init.cql
```

2) Copy an individual lab file into the container and run it there (recommended when iterating on a single lab):

```bash
# copy a file into the container, then run it with cqlsh
docker cp labs/01_keyspace_basics.cql $(docker compose -f scripts/docker-compose.yml ps -q cassandra):/tmp/01_keyspace_basics.cql
docker compose -f scripts/docker-compose.yml exec cassandra cqlsh -f /tmp/01_keyspace_basics.cql
```

Note: using `-f /labs/01_keyspace_basics.cql` directly fails when the container has no `/labs` mount ("No such file or directory").
Use the copy approach or update your compose file to mount the project root into the container.

Lab order (do not change — learning path is intentional)
1. `01_keyspace_basics.cql` — keyspace creation, SimpleStrategy, first table
2. `02_partitioning_clustering.cql` — partitioning and clustering keys; bounding partitions
3. `03_modeling_by_query.cql` — query-driven modeling and lookup tables (dual-write)
4. `04_indexes_and_mv.cql` — secondary indexes and materialized views (trade-offs)
5. `05_consistency_lwt_batch.cql` — consistency levels, lightweight transactions, batches
6. `06_ttl_tombstones.cql` — TTLs, tombstones, delete behavior, compaction notes
7. `07_aggregation_filtering.cql` — aggregation, ALLOW FILTERING caution, counters/aggregation patterns

Per-lab, table-by-table explanation and purpose

Lab 01 — Keyspace basics (`01_keyspace_basics.cql`)
- Keyspace: `cassandra_labs` created with `SimpleStrategy` replication_factor=1.
  - Why: fastest for single-node local dev. Simpler startup and predictable behavior for learning.
  - Alternatives: `NetworkTopologyStrategy` (NTS) for multi-DC setups (see "Replication strategies" below). Use RF >= 3 per DC in production.
- Table: `products_by_id`
  - Columns: `product_id` (uuid PK), `name`, `category`, `price`, `created_at`.
  - Primary key: simple primary key on `product_id` (good when access is by id). No clustering columns.
  - Demonstrates: basic inserts, selects, and TTL usage via UPDATE ... USING TTL to show TTL semantics.

Lab 02 — Partitioning and clustering (`02_partitioning_clustering.cql`)
- Table: `events_by_day`
  - PK: `((event_day), event_ts)` — partition key = `event_day`, clustering = `event_ts`.
  - Purpose: show distribution by day and ordered reads (recent-first using DESC clustering order).
  - Trade-offs: using only day as partition key can produce hot/unbounded partitions for high traffic; show time bucketing.
- Table: `events_by_day_hour`
  - PK: `((event_day, hour_bucket), event_ts)` — adds `hour_bucket` to cap partition size.
  - Purpose: demonstrates explicit bucketing to prevent hot partitions and to bound reads.

Lab 03 — Modeling by query (`03_modeling_by_query.cql`)
- Concept: design tables for access patterns rather than normalized relational joins.
- Table: `users_by_id`
  - PK: `user_id` (read by id).
- Table: `users_by_email`
  - PK: `((email), user_id)` (lookup by email, supports multiple users per email if needed).
  - Purpose: show denormalization; keep lookup tables synchronized via dual-writes or batches (example shows a logged batch).
  - Anti-pattern: don't scan `users_by_id` by email — that requires ALLOW FILTERING and is inefficient.

Lab 04 — Indexes and materialized views (`04_indexes_and_mv.cql`)
- Secondary index on `users_by_id(email)`
  - When to use: low-cardinality columns, queries that target small partitions; avoid for fan-out across many partitions.
- Materialized view `users_by_email_mv`
  - Why shown: server-side denormalization to support a query without dual-writes.
  - Caveats: MVs can drift and add repair cost; modern practice prefers explicit dual-writes from application code.

Lab 05 — Consistency, LWT, and batching (`05_consistency_lwt_batch.cql`)
- Demonstrates `CONSISTENCY` setting (the file uses `CONSISTENCY ONE` for dev).
- LWT examples (`IF NOT EXISTS`, `IF full_name = ...`) — explains Paxos cost and when to use.
- Logged vs Unlogged batch: shows atomic multi-table writes (logged) and performance-minded unlogged batches.

Lab 06 — TTL and tombstones (`06_ttl_tombstones.cql`)
- Table: `sessions_by_user` with TTLed inserts and explicit deletes.
- Teaches: TTL generates tombstones; high-churn patterns can cause read/compaction performance issues.
- Advice: don't lower `gc_grace_seconds` in prod without understanding repair; for local tests you may shorten it to observe tombstone cleanup.

Lab 07 — Aggregation and filtering (`07_aggregation_filtering.cql`)
- Shows that `COUNT(*)` over the entire cluster is a full scan — only safe when used per-partition.
- `ALLOW FILTERING` is included to demonstrate a dangerous pattern and reinforce proper modeling (use lookup tables or pre-aggregated counters).

Replication strategies — SimpleStrategy vs NetworkTopologyStrategy
- SimpleStrategy
  - Intended for single-DC deployments and local development only.
  - Example usage in labs: fast startup and minimal operational complexity.
  - Not suitable for multi-DC because it places replicas by token range only.
- NetworkTopologyStrategy (NTS)
  - Specify per-DC replication factors: e.g. `{'class':'NetworkTopologyStrategy', 'dc1':3, 'dc2':3}`.
  - Use when you have multiple data centers (cloud regions or physical DCs). Recommended RF per DC: 3 for typical production (lets you survive rack/node failures while maintaining quorum).
  - Testing vs Prod: In testing (single-host Docker) set RF=1 or 2 to save resources; in realistic prod demos use RF=3 per DC and run multiple nodes per DC.

Practical demo notes for multi-DC / racks
- For a richer multi-DC demo: spin up nodes labeled for each DC (container hostnames or container labels) and set RF per DC in the keyspace. Racks can remain implicit for a lab. Example: create multiple containers, set `broadcast_address` and `seed_provider` appropriately, and create keyspace with NTS specifying the DC names you used.
- For learning purposes the labs include a `cassandra_labs_nets` NTS example; ensure your compose and init scripts create matching DC names (the error "Unrecognized strategy option {dc2}" usually means the keyspace was created with a DC name that Cassandra does not know about — make sure your cluster defines that DC).

Common troubleshooting (concise)
- "Can't open 'labs/01_keyspace_basics.cql'": container has no `/labs` mount. Use `docker cp` into container or mount the repository root in docker-compose.
- `AlreadyExists: Keyspace ... already exists`: safe; the CQL files use `IF NOT EXISTS`. You can skip or edit to DROP if you want to reset.
- "Seed provider lists no seeds" on startup: your cassandra.yaml seed provider lists hostnames the node cannot resolve (e.g., `cassandra`). Ensure container names match the seed list or use IPs. In docker-compose use service name as hostname and ensure networks are correct. For two-rack demos, make sure at least one node lists a valid seed.
- `socket.gaierror: [Errno -2] Name or service not known` when running init container: the init script attempted to contact a host that doesn't resolve. Use the container name from `docker compose ps` or the container IP.

Docs and story (why these labs exist)
- Story: start with the simplest unit — keyspace and single-table reads by id — then introduce partitioning/clustering (how data is distributed and ordered), then move to modeling by query (the core Cassandra mindset), followed by optional server-side helpers (indexes/MV) and operational features (consistency, LWT, batching, TTL/tombstones), and finally aggregation and filtering pitfalls. Each lab intentionally introduces a small, testable concept and shows both the correct patterns and common anti-patterns you'd encounter in interviews.

Notes from the Copilot context (project conventions)
- This repo is intentionally query-first and denormalized.
- Keep the learning path (lab order) unchanged — the labs build on each other.
- Use `scripts/init.cql` for a consolidated init; if you prefer per-lab runs, run the specific lab files by copying them into the container as shown above.

If you'd like next steps I can:
- Consolidate `scripts/init.cql` and the individual `labs/*.cql` into a single `scripts/init.cql` (or remove duplicates) and update `docker-compose.yml` to mount the repository so you can run `cqlsh -f /labs/01_keyspace_basics.cql` directly from the container. (I can implement this now if you want.)
- Add a short appendix with command examples for copying files into the container, or update `scripts/docker-compose.yml` to mount the repo.

-- End of labs README --
