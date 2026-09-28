SET NAMES utf8mb4;
USE cine;

DELETE FROM premiere;
INSERT INTO premiere (title, synopsis, image_url, display_order) VALUES
(
  'La última función',
  'Una proyeccionista recorre de noche una sala a punto de cerrar y descubre que cada rollo guarda una despedida distinta. Drama íntimo sobre el oficio de exhibir películas.',
  '/posters/ultima-funcion.svg',
  1
),
(
  'Estación polar',
  'Dos desconocidos quedan varados en un tren detenido al borde de la cordillera. El silencio, el frío y una confesión los obligan a decidir si siguen viaje.',
  '/posters/estacion-polar.svg',
  2
),
(
  'Río de neón',
  'En un puerto que nunca duerme, una mensajera acepta un último encargo antes del amanecer. Thriller urbano con persecuciones entre muelles y azoteas.',
  '/posters/rio-neon.svg',
  3
),
(
  'El faro interior',
  'Una cartógrafa regresa a la caleta donde creció para trazar un mapa que nadie quiere publicar. Historia de memoria, mar y una familia que habla poco.',
  '/posters/faro-interior.svg',
  4
);

DELETE FROM candy_product;
INSERT INTO candy_product (name, description, price) VALUES
('Cancha crocante', 'Maíz tostado con sal de mar, porción individual.', 6.50),
('Chocolate de cacao 70%', 'Barra oscura de 40 g, sin relleno.', 8.00),
('Mix de gomas', 'Gomas ácidas surtidas en vaso de 180 g.', 12.50),
('Agua con gas', 'Botella de 500 ml, bien fría.', 5.00),
('Nachos con queso', 'Totopos y salsa tibia de queso para compartir.', 18.90),
('Combo pareja', 'Dos chocolates, un mix de gomas y dos aguas.', 32.00);

INSERT INTO app_user (email, full_name, password_hash) VALUES
(
  'cliente@cine.com',
  'Lucía Mendoza',
  '$2b$10$L5u9Na8nm1zSO2K0IGkbpeQfFxILfeq6v6r0aaymDaviEMIuS.JD.'
)
ON DUPLICATE KEY UPDATE
  full_name = VALUES(full_name),
  password_hash = VALUES(password_hash);
