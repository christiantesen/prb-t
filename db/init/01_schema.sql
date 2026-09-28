CREATE DATABASE IF NOT EXISTS cine
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

SET NAMES utf8mb4;
USE cine;

CREATE TABLE IF NOT EXISTS premiere (
  id INT NOT NULL AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL,
  synopsis TEXT NOT NULL,
  image_url VARCHAR(500) NOT NULL,
  display_order INT NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS candy_product (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(200) NOT NULL,
  description TEXT NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS app_user (
  id INT NOT NULL AUTO_INCREMENT,
  email VARCHAR(200) NOT NULL,
  full_name VARCHAR(200) NOT NULL,
  password_hash VARCHAR(100) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_app_user_email (email)
);

CREATE TABLE IF NOT EXISTS purchase (
  id INT NOT NULL AUTO_INCREMENT,
  email VARCHAR(200) NOT NULL,
  full_name VARCHAR(200) NOT NULL,
  document_type VARCHAR(20) NOT NULL,
  document_number VARCHAR(30) NOT NULL,
  operation_date VARCHAR(64) NOT NULL,
  transaction_id VARCHAR(64) NOT NULL,
  total DECIMAL(10,2) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_purchase_transaction (transaction_id)
);

CREATE TABLE IF NOT EXISTS purchase_item (
  id INT NOT NULL AUTO_INCREMENT,
  purchase_id INT NOT NULL,
  product_id INT NOT NULL,
  product_name VARCHAR(200) NOT NULL,
  quantity INT NOT NULL,
  unit_price DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_purchase_item_purchase
    FOREIGN KEY (purchase_id) REFERENCES purchase (id)
);
