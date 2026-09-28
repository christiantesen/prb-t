-- Prueba del procedimiento que cierra una compra.
-- Sirve para ver, paso a paso, que la compra se guarda y que no se duplica
-- si llega otra vez el mismo identificador de PayU.
--
-- Como ejecutarla, desde la carpeta del proyecto:
--
--   mysql -uroot -p -h127.0.0.1 -P3306 --default-character-set=utf8mb4 cine < db/pruebas/prueba_del_procedimiento_de_compra.sql
--
-- Si la base es la de Docker, cambia el puerto 3306 por 3307.
--
-- Que debe verse al final:
--   resultado = LA PRUEBA PASO
--   compras_con_ese_identificador = 1
--   productos_guardados = 2

USE cine;

-- Paso 1. Borra una prueba anterior con el mismo identificador, para poder repetir este archivo.
DELETE item
FROM purchase_item AS item
INNER JOIN purchase AS compra ON compra.id = item.purchase_id
WHERE compra.transaction_id = 'PRUEBA-COMPRA-001';

DELETE FROM purchase
WHERE transaction_id = 'PRUEBA-COMPRA-001';

-- Paso 2. Arma el pedido: 2 canchas y 1 chocolate. El total es 21.00.
SET @pedido = JSON_ARRAY(
  JSON_OBJECT('productId', 1, 'productName', 'Cancha crocante', 'quantity', 2, 'unitPrice', 6.50),
  JSON_OBJECT('productId', 2, 'productName', 'Chocolate de cacao 70%', 'quantity', 1, 'unitPrice', 8.00)
);

-- Paso 3. Primera vez que llega el pago aprobado. El codigo tiene que ser 0.
CALL sp_complete_purchase(
  'prueba@cine.com',
  'Cliente de prueba',
  'DNI',
  '87654321',
  '2026-09-27',
  'PRUEBA-COMPRA-001',
  21.00,
  @pedido,
  @codigo_primera
);

SELECT
  'Primera vez: el codigo tiene que ser 0' AS que_revisamos,
  @codigo_primera AS codigo;

-- Paso 4. La compra y sus productos deben haber quedado guardados.
SELECT
  email,
  full_name AS nombre,
  document_number AS dni,
  operation_date AS fecha_de_operacion,
  transaction_id AS identificador_payu,
  total
FROM purchase
WHERE transaction_id = 'PRUEBA-COMPRA-001';

SELECT
  product_name AS producto,
  quantity AS cantidad,
  unit_price AS precio
FROM purchase_item
WHERE purchase_id = (
  SELECT id FROM purchase WHERE transaction_id = 'PRUEBA-COMPRA-001'
);

-- Paso 5. Llega otra vez el mismo identificador. No debe crearse una segunda compra.
-- El codigo sigue siendo 0, porque el pago ya estaba registrado.
CALL sp_complete_purchase(
  'prueba@cine.com',
  'Cliente de prueba',
  'DNI',
  '87654321',
  '2026-09-27',
  'PRUEBA-COMPRA-001',
  21.00,
  @pedido,
  @codigo_segunda
);

SELECT COUNT(*) INTO @compras
FROM purchase
WHERE transaction_id = 'PRUEBA-COMPRA-001';

SELECT COUNT(*) INTO @productos
FROM purchase_item
WHERE purchase_id = (
  SELECT id FROM purchase WHERE transaction_id = 'PRUEBA-COMPRA-001'
);

SELECT
  @codigo_primera AS codigo_primera_vez,
  @codigo_segunda AS codigo_segunda_vez,
  @compras AS compras_con_ese_identificador,
  @productos AS productos_guardados,
  CASE
    WHEN @codigo_primera = '0'
     AND @codigo_segunda = '0'
     AND @compras = 1
     AND @productos = 2
    THEN 'LA PRUEBA PASO'
    ELSE 'LA PRUEBA NO PASO'
  END AS resultado;
