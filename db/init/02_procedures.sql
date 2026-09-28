SET NAMES utf8mb4;
USE cine;

DROP PROCEDURE IF EXISTS sp_list_premieres;
DROP PROCEDURE IF EXISTS sp_list_candy;
DROP PROCEDURE IF EXISTS sp_find_user_by_email;
DROP PROCEDURE IF EXISTS sp_complete_purchase;

DELIMITER $$

CREATE PROCEDURE sp_list_premieres()
BEGIN
  SELECT
    id,
    title,
    synopsis,
    image_url AS imageUrl,
    display_order AS displayOrder
  FROM premiere
  ORDER BY display_order;
END$$

CREATE PROCEDURE sp_list_candy()
BEGIN
  SELECT id, name, description, price
  FROM candy_product
  ORDER BY id;
END$$

CREATE PROCEDURE sp_find_user_by_email(IN p_email VARCHAR(200))
BEGIN
  SELECT
    id,
    email,
    full_name AS fullName,
    password_hash AS passwordHash
  FROM app_user
  WHERE email = p_email;
END$$

CREATE PROCEDURE sp_complete_purchase(
  IN p_email VARCHAR(200),
  IN p_full_name VARCHAR(200),
  IN p_document_type VARCHAR(20),
  IN p_document_number VARCHAR(30),
  IN p_operation_date VARCHAR(64),
  IN p_transaction_id VARCHAR(64),
  IN p_total DECIMAL(10,2),
  IN p_items_json JSON,
  OUT p_code CHAR(1)
)
BEGIN
  DECLARE v_count INT DEFAULT 0;
  DECLARE v_id INT;

  DECLARE EXIT HANDLER FOR SQLEXCEPTION
  BEGIN
    ROLLBACK;
    SET p_code = '1';
  END;

  START TRANSACTION;

  SELECT COUNT(*) INTO v_count
  FROM purchase
  WHERE transaction_id = p_transaction_id;

  IF v_count > 0 THEN
    SET p_code = '0';
    COMMIT;
  ELSE
    INSERT INTO purchase (
      email, full_name, document_type, document_number,
      operation_date, transaction_id, total
    ) VALUES (
      p_email, p_full_name, p_document_type, p_document_number,
      p_operation_date, p_transaction_id, p_total
    );

    SET v_id = LAST_INSERT_ID();

    INSERT INTO purchase_item (purchase_id, product_id, product_name, quantity, unit_price)
    SELECT
      v_id,
      jt.product_id,
      jt.product_name,
      jt.quantity,
      jt.unit_price
    FROM JSON_TABLE(
      p_items_json,
      '$[*]' COLUMNS (
        product_id INT PATH '$.productId',
        product_name VARCHAR(200) PATH '$.productName',
        quantity INT PATH '$.quantity',
        unit_price DECIMAL(10,2) PATH '$.unitPrice'
      )
    ) AS jt;

    SET p_code = '0';
    COMMIT;
  END IF;
END$$

DELIMITER ;
