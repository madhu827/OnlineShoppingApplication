-- =====================================================
-- Cassandra keyspace initialization script
-- Auto-executed when the Docker container first starts
-- =====================================================

-- Create keyspace with SimpleStrategy (single-node dev)
-- Change to NetworkTopologyStrategy for production clusters
CREATE KEYSPACE IF NOT EXISTS online_shopping
  WITH replication = {
    'class': 'SimpleStrategy',
    'replication_factor': 1
  }
  AND durable_writes = true;

-- Switch to our keyspace
USE online_shopping;

-- Create products table
-- Partition key  : category  (distribute by category for even load)
-- Clustering key : id DESC   (newest products first within a category)
CREATE TABLE IF NOT EXISTS products (
    category        text,
    id              uuid,
    name            text,
    description     text,
    price           decimal,
    stock_quantity  int,
    brand           text,
    image_url       text,
    sku             text,
    active          boolean,
    created_at      timestamp,
    updated_at      timestamp,
    PRIMARY KEY ((category), id)
) WITH CLUSTERING ORDER BY (id DESC)
  AND comment = 'Product catalog - partitioned by category';

