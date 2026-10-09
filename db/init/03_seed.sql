SET NAMES utf8mb4;
USE cine;

DELETE FROM premiere;
INSERT INTO premiere (title, synopsis, release_year, image_url, genre, stars, display_order) VALUES
('The Shawshank Redemption', 'Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.', 1994, 'https://devsapihub.com/img-movies/1.jpg', 'Drama', 5, 1),
('Jumanji', 'In Jumanji: The Next Level, the gang is back but the game has changed.', 2019, 'https://devsapihub.com/img-movies/2.jpg', 'Adventure, Fantasy, Comedy', 3.4, 2),
('The Godfather', 'The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son', 1972, 'https://devsapihub.com/img-movies/3.jpg', 'Crime, Drama', 2.8, 3),
('The Godfather: Part II', 'The early life and career of Vito Corleone in 1920s New York City is portrayed, while his son, Michael, expands and tightens his grip on the family crime syndicate.', 1974, 'https://devsapihub.com/img-movies/4.jpg', 'Crime, Drama', 4.3, 4),
('The Dark Knight', 'When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.', 2008, 'https://devsapihub.com/img-movies/5.jpg', 'Action, Crime, Drama', 4, 5),
('12 Angry Men', 'A jury holdout attempts to prevent a miscarriage of justice by forcing his colleagues to reconsider the evidence.', 1957, 'https://devsapihub.com/img-movies/6.jpg', 'Drama', 1.8, 6),
('No Hard Feelings', 'On the brink of losing her home, a woman agrees to date a wealthy couple''s introverted son before he leaves for college.', 2023, 'https://devsapihub.com/img-movies/7.jpg', 'Comedy, Romance', 2.4, 7),
('The Lord of the Rings: The Return of the King', 'Gandalf and Aragorn lead the World of Men against Sauron''s army to draw his gaze from Frodo and Sam as they approach Mount Doom with the One Ring.', 2003, 'https://devsapihub.com/img-movies/8.jpg', 'Fantasy, Adventure, Drama', 1.9, 8),
('Pulp Fiction', 'The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption.', 1994, 'https://devsapihub.com/img-movies/9.jpg', 'Crime, Drama', 5, 9),
('The Good, the Bad and the Ugly', 'A bounty hunting scam joins two men in an uneasy alliance against a third in a race to find a fortune in gold buried in a remote cemetery.', 1966, 'https://devsapihub.com/img-movies/10.jpg', 'Western', 4.3, 10),
('The Lord of the Rings: The Fellowship of the Ring', 'A meek Hobbit from the Shire and eight companions set out on a journey to destroy the powerful One Ring and save Middle-earth from the Dark Lord Sauron.', 2001, 'https://devsapihub.com/img-movies/11.jpg', 'Fantasy, Adventure, Drama', 5, 11),
('Fight Club', 'An insomniac office worker and a devil-may-care soapmaker form an underground fight club that evolves into something much, much more.', 1999, 'https://devsapihub.com/img-movies/12.jpg', 'Drama', 4.2, 12),
('Dune: Part Two', 'Paul Atreides unites with Chani and the Fremen to exact revenge against the conspirators who destroyed his family.', 2024, 'https://devsapihub.com/img-movies/13.jpg', 'Sci-Fi, Adventure, Action', 4.8, 13),
('Oppenheimer', 'The story of American scientist J. Robert Oppenheimer and his role in the development of the atomic bomb.', 2023, 'https://devsapihub.com/img-movies/14.jpg', 'Biography, Drama, History', 5, 14),
('Barbie', 'Barbie suffers a crisis that leads her to question her world and her existence.', 2023, 'https://devsapihub.com/img-movies/15.jpg', 'Comedy, Fantasy', 3.2, 15),
('Spider-Man: No Way Home', 'With Spider-Man’s identity revealed, Peter asks Doctor Strange for help, leading to multiverse chaos.', 2021, 'https://devsapihub.com/img-movies/16.jpg', 'Action, Adventure, Sci-Fi', 4.6, 16),
('Avatar: The Way of Water', 'Jake Sully lives with his newfound family formed on the planet of Pandora, facing new threats.', 2022, 'https://devsapihub.com/img-movies/17.jpg', 'Sci-Fi, Adventure, Action', 4.1, 17),
('The Batman', 'Batman uncovers corruption in Gotham City that connects to his own family while facing the Riddler.', 2022, 'https://devsapihub.com/img-movies/18.jpg', 'Action, Crime, Drama', 3.7, 18),
('Everything Everywhere All at Once', 'An aging Chinese immigrant is swept up in a wild adventure where she alone can save the world by exploring other universes.', 2022, 'https://devsapihub.com/img-movies/19.jpg', 'Adventure, Sci-Fi, Action', 5, 19),
('The Matrix', 'A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.', 1999, 'https://devsapihub.com/img-movies/20.jpg', 'Sci-Fi, Action', 5, 20),
('Mi Villano Favorito 4', 'Gru, Lucy, Margo, Edith y Agnes dan la bienvenida a un nuevo miembro de la familia, Gru Jr., mientras enfrentan una nueva amenaza liderada por Maxime Le Mal.', 2024, 'https://devsapihub.com/img-movies/21.jpg', 'Animation, Comedy, Family', 4.5, 21),
('Hotel Transylvania 2', 'Drácula intenta despertar los poderes de monstruo de su nieto Dennis antes de que su hija Mavis decida mudarse al mundo de los humanos.', 2015, 'https://devsapihub.com/img-movies/22.jpg', 'Animation, Comedy, Family', 4.2, 22),
('Merlina - Temporada 2', 'Merlina Addams regresa a la Academia Nunca Más para enfrentar nuevos misterios sobrenaturales, enemigos inesperados y oscuros secretos familiares.', 2025, 'https://devsapihub.com/img-movies/23.jpg', 'Fantasy, Mystery, Dark Comedy', 4.3, 23),
('Super Mario Bros. La Película', 'Mario y Luigi son transportados al Reino Champiñón, donde deberán enfrentarse a Bowser para salvar el mundo y rescatar a la Princesa Peach.', 2023, 'https://devsapihub.com/img-movies/24.jpg', 'Animation, Adventure, Comedy', 4.1, 24),
('Moana 2', 'Tras recibir una inesperada llamada de sus ancestros, Moana emprende una nueva travesía por los mares de Oceanía junto a Maui y una tripulación de navegantes.', 2024, 'https://devsapihub.com/img-movies/25.jpeg', 'Animation, Adventure, Family', 4, 25),
('Toy Story 4', 'Woody, Buzz Lightyear y el resto de los juguetes emprenden una nueva aventura cuando Forky, el nuevo juguete de Bonnie, se pierde durante un viaje familiar.', 2019, 'https://devsapihub.com/img-movies/26.jpg', 'Animation, Adventure, Comedy', 4.4, 26),
('El Botín', 'Un grupo de policías de Miami descubre millones de dólares ocultos en un escondite abandonado. La confianza se rompe y todos comienzan a sospechar de todos mientras intentan quedarse con el dinero.', 2026, 'https://devsapihub.com/img-movies/27.jpg', 'Action, Crime, Drama', 4.6, 27),
('Apex', 'Sasha, una experimentada escaladora, se adentra en la naturaleza salvaje de Australia para superar una tragedia personal. Lo que comienza como una expedición solitaria se convierte en una lucha por la supervivencia cuando un peligroso depredador humano comienza a cazarla.', 2026, 'https://devsapihub.com/img-movies/28.jpg', 'Action, Suspense, Survival', 4.8, 28),
('Máquina de Guerra', 'Durante la fase final del entrenamiento de los Army Rangers, un grupo de soldados de élite debe luchar por sobrevivir cuando se enfrenta a una gigantesca máquina alienígena letal.', 2026, 'https://devsapihub.com/img-movies/29.png', 'Action, Science Fiction, Suspense', 4.9, 29),
('Una batalla tras otra', 'Un antiguo grupo de revolucionarios se ve obligado a reunirse después de 16 años cuando reaparece su viejo enemigo y la hija de uno de ellos desaparece, iniciando una peligrosa misión de rescate.', 2025, 'https://devsapihub.com/img-movies/30.jpg', 'Action, Crime, Drama, Suspense', 4.7, 30);

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
