-- Lab 01: Keyspace basics
-- Using SimpleStrategy + RF=1 because this lab targets a single-node local container.
-- For any multi-node or production deployment, prefer NetworkTopologyStrategy with RF >= 3 per DC, e.g.:
-- CREATE KEYSPACE cassandra_labs WITH replication = {'class': 'NetworkTopologyStrategy', 'dc1': 3};

-- docker compose -f scripts/docker-compose.yml exec cassandra cqlsh -f /labs/01_keyspace_basics.cql
CREATE KEYSPACE IF NOT EXISTS cassandra_labs
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

USE cassandra_labs;

CREATE TABLE IF NOT EXISTS products_by_id (
  product_id uuid,
  name text,
  category text,
  price decimal,
  created_at timestamp,
  PRIMARY KEY (product_id)
);

INSERT INTO products_by_id (product_id, name, category, price, created_at)
VALUES (aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa, 'Keyboard', 'peripherals', 89.99, toTimestamp(now()));

SELECT * FROM products_by_id LIMIT 5;

UPDATE products_by_id USING TTL 600
SET price = 79.99
WHERE product_id = aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa;

SELECT * FROM products_by_id LIMIT 5;
