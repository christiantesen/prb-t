# Prueba técnica Cineplanet

Tienda de dulcería para una cadena de cines. El cliente ve la cartelera, entra con su cuenta o como invitado, arma el pedido y paga con la tarjeta de prueba de PayU.

El enunciado pide medir React, Java y Node en el mismo trabajo, con servicios REST, JWT, Swagger, procedimientos almacenados y Docker. Por eso la solución no es una sola aplicación: la web está en React, la cartelera y la dulcería en Node, y el pago junto con la sesión en Spring Boot.

## Flujo de compra

El menú de arriba siempre muestra Home, Dulcería y Login.

1. **Home.** El servicio de cartelera devuelve imagen y texto. La imagen queda a la izquierda y el texto a la derecha. Cualquier póster abre Login.
2. **Login.** Se puede entrar con correo y clave, con Google, o con el botón Invitado. Si hay sesión, aparece un aviso de bienvenida con el nombre y el botón Aceptar, que lleva a Dulcería. Invitado entra a Dulcería sin sesión.
3. **Dulcería.** El servicio de dulcería devuelve nombre, descripción y precio. Se pueden llevar varias unidades, del mismo producto o de productos distintos. Abajo se ve el total. Continuar abre el pago.
4. **Pago.** Se piden los 16 dígitos de la tarjeta, la expiración, el CVV, el correo, el nombre y el documento. Si la persona inició sesión, el correo y el nombre ya vienen escritos. La tarjeta se envía al sandbox de PayU. Si PayU aprueba, se guarda la compra con el `transactionId` y el `operationDate` que devolvió PayU, y el servicio responde con el código `0`. Entonces aparece el aviso de compra correcta.

## Cómo verlo con Docker

Hace falta Docker Desktop encendido. En la carpeta del proyecto:

```powershell
docker compose up --build
```

- Tienda: http://localhost:8088
- Swagger: http://localhost:8080/swagger-ui/index.html

MySQL de Docker publica el puerto 3307, para no ocupar el 3306 si ya hay un MySQL en la máquina.

Para apagarlo: `docker compose down`

## Cómo verlo sin Docker

Hace falta JDK 11, Node y MySQL 8.

1. Copia `.env.example` a `.env` y pon la clave de MySQL.
2. Crea la base con los tres archivos de `db/init`, en este orden: `01_schema.sql`, `02_procedures.sql`, `03_seed.sql`.
3. Si vas a probar Google, copia el identificador público a `frontend/.env` como `VITE_GOOGLE_CLIENT_ID`.
4. Arranca todo:

```powershell
.\scripts\start-dev.ps1
```

La tienda queda en http://localhost:5173.

## Cuenta y tarjeta de prueba

- Correo: `cliente@cine.com`
- Clave: `cine123`
- Nombre: Lucía Mendoza
- Tarjeta de PayU: `4907840000000005`, expiración `05/27`, CVV `777`. El botón **Tarjeta de prueba** los rellena.
- La casilla **Simular rechazo de PayU** muestra el camino en el que el pago no se aprueba. No guarda la compra.

El número de tarjeta y el CVV no se guardan. Lo que sí se guarda es el correo, el nombre, el documento, el total, los productos y los dos datos que devuelve PayU: el identificador de la transacción y la fecha de operación.

## Cómo se resolvió cada punto

### Home, Dulcería, Pago y el menú

React con Vite y React Router. Son cuatro pantallas y un menú. No hacía falta otro framework de estado: la sesión y el carrito viven en el navegador y se leen en Login, Dulcería y Pago.

Vite encaja mejor que Create React App para este tamaño. El proyecto arranca rápido y el build de Docker es corto. Una librería grande de estado habría sido peor: el problema no tiene pantallas que compartan un estado complicado.

Los pósters son archivos propios, servidos por la web. Así la cartelera no depende de un sitio externo el día de la revisión.

### Login, invitado y los datos que pasan a Pago

El enunciado mezcla dos ideas en el mismo párrafo: Google, que está marcado como opcional, y pedir correo y clave. El pago, en cambio, sí exige correo y nombre. Por eso hay tres entradas, y las tres cubren el flujo:

- Correo y clave. Es la que se puede probar sin configurar Google. La clave se compara con un hash, y la respuesta trae el nombre y el correo para rellenar Pago.
- Google. Opcional, como dice el enunciado. Si la persona acepta, el aviso de bienvenida usa el nombre de esa cuenta y esos mismos datos pasan a Pago.
- Invitado. No crea sesión. Dulcería se abre igual, y en Pago el correo y el nombre se escriben a mano.

El aviso de bienvenida solo aparece cuando hubo sesión. Aceptar lleva a Dulcería. Esa es la diferencia que pide el enunciado entre quien entra y quien sigue como invitado.

### Google

El botón de Google está en Login. El navegador pide la credencial y el servidor comprueba con Google que esa credencial sea de esta aplicación. Si cuadra, se entrega la misma sesión que con el correo y la clave.

El secreto del cliente de Google no está en el repositorio. Este flujo no lo necesita: basta el identificador público y revisar que la credencial pertenezca a esta aplicación. Guardar el secreto en git habría sido la peor opción, sobre todo si el repositorio se comparte.

Para que el botón funcione, en la consola de Google hay que autorizar estos orígenes:

- `http://localhost:5173`
- `http://127.0.0.1:5173`
- `http://localhost:8088`

Si el `.env` no trae `GOOGLE_CLIENT_ID`, el botón no se muestra y el resto de la tienda sigue igual.

### Por qué no hay Firebase

Firebase aparece como opción de base y también como extra opcional del front. El mismo enunciado pide procedimientos almacenados en SQL Server o MySQL. Firebase no ejecuta esos procedimientos. Usarlo habría cumplido una línea y dejado sin cumplir la otra.

MySQL cubre las dos. Además ya estaba instalado en la máquina de desarrollo. PostgreSQL también habría servido para los procedimientos, pero no había un PostgreSQL corriendo. MySQL era la mejor opción entre las que el enunciado acepta y las que se podían usar de verdad.

### Tres servicios, y en qué lenguaje va cada uno

El enunciado pide tres microservicios, en Java o en Node, y a la vez quiere ver Java y Node.

- `premieres` y `candystore` están en Node. Solo leen un procedimiento y devuelven la lista. Poner ahí Java no aportaba al problema y escondía Node, que el enunciado también quiere ver.
- `complete` está en Spring Boot. Ahí está PayU, la validación de la tarjeta y el procedimiento que cierra la compra. Es la parte que justifica Java.
- Hay un cuarto servicio, también Spring Boot, que es la puerta de la tienda. El navegador habla solo con él. Ahí viven el login, el JWT y Swagger, que el enunciado pide en Spring. Los otros tres servicios no tienen que repetir eso.

Juntar todo en un solo proceso habría sido más simple de arrancar y peor para el enunciado, porque deja de haber microservicios. Hacer los tres en Java, o los tres en Node, también habría sido peor: el reto pide las dos cosas.

### Java 11, JWT y Swagger

El enunciado acepta Java 8 o Java 11. Se usó Java 11 con Spring Boot 2.7. Spring Boot 3 pide Java 17, y 17 queda fuera de lo que el reto permite. Entre 8 y 11, 11 es la mejor de las dos versiones admitidas.

La sesión es un JWT que dura dos horas. Se firma al entrar y, si la persona pagó con sesión, se revisa en el pago. El invitado puede pagar sin token: el enunciado lo permite, así que el pago no exige sesión.

No se montó el filtro completo de Spring Security. El problema solo necesita emitir una sesión y rechazar una sesión inválida. El filtro completo obliga a abrir excepciones para la cartelera, la dulcería y el invitado, y no cambia el resultado. Para este enunciado, el JWT directo es la mejor medida.

Swagger está en la puerta, no repartido en cada servicio. Quien revisa abre una sola página y ve el login y el pago. En **Tienda**, la operación **Pagar la dulcería** muestra el cuerpo del pago y la respuesta con código `0`, el identificador de PayU, la fecha de operación y el total. Reenviar el JSON como texto, sin esquema, dejaba Swagger abierto pero inútil para el punto que el enunciado pide.

La página es http://localhost:8080/swagger-ui/index.html.

### Base de datos y procedimientos

Hay cuatro procedimientos, y los servicios no escriben SQL suelto para esas tareas:

- listar la cartelera
- listar la dulcería
- buscar una cuenta por correo
- cerrar la compra

El de la compra recibe el pedido, el correo, el nombre, el documento, el total y los dos datos de PayU. Si ese `transactionId` ya existe, no crea otra compra y vuelve a responder `0`. PayU puede reintentar el mismo cobro. Duplicar la venta en ese caso habría sido el peor comportamiento. El código `0` en el reintento es el correcto: el pago ya estaba registrado.

El precio no se acepta el que mande el navegador. Se vuelve a leer de la dulcería. Si se confiara en el precio del cliente, alguien podría pagar cero. Para un pago, esa decisión es la que corresponde.

La prueba está en `db/pruebas/prueba_del_procedimiento_de_compra.sql`. Borra una prueba anterior, registra 2 canchas y 1 chocolate, llama al procedimiento, muestra lo guardado y lo llama otra vez con el mismo identificador. La última fila debe decir `LA PRUEBA PASO`, con una sola compra y dos productos.

Contra el MySQL de la máquina, puerto 3306:

```powershell
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -uroot -p -h127.0.0.1 -P3306 --default-character-set=utf8mb4 cine -e "source db/pruebas/prueba_del_procedimiento_de_compra.sql"
```

Contra el MySQL de Docker, el mismo comando con el puerto `3307`.

### PayU y el servicio que cierra la compra

El formulario de pago llega al servicio `complete`. Ese servicio llama al sandbox de PayU Perú y, solo si el estado es `APPROVED`, ejecuta el procedimiento. La respuesta hacia la pantalla es el código `0` más el `transactionId` y el `operationDate` que devolvió PayU.

PayU firma el cobro con un MD5. El monto tiene que ir sin ceros de más: 100.00 se firma como 100, 100.50 como 100.5 y 100.55 como 100.55. Si se firma siempre con dos decimales, PayU rechaza un monto que en pantalla está bien. Hay una prueba automática de ese formato.

La tarjeta de prueba que sí aprueba en este sandbox es `4907840000000005`, con CVV `777` y una expiración cuyo mes sea anterior a junio. Otras tarjetas genéricas responden que el adquirente no contestó. Por eso el botón de la pantalla rellena esa tarjeta y no una Visa cualquiera.

El enunciado dice que, después de PayU, `complete` recibe correo, nombre, documento, `operationDate` y `transactionId`. Esos datos son los que se guardan. La llamada a PayU no se hizo desde el navegador. La clave del comercio tendría que ir dentro del JavaScript, y la tarjeta pasaría por un lugar de más. Llamar a PayU desde `complete` cumple el sandbox, guarda exactamente lo que el enunciado pide guardar y deja la clave y la tarjeta en el servidor. Para un pago, esa es la mejor de las dos lecturas.

La casilla de rechazo existe porque el sandbox de Perú aprueba `4907840000000005` aunque el nombre de la tarjeta se envíe como rechazo. Se probó. No hay forma, con esa tarjeta, de mostrar el camino fallido. En modo de prueba la casilla corta el pago antes de guardarlo y responde que no fue aprobado. Si el modo de prueba está apagado, la casilla no sirve para saltarse un cobro real.

### Logs

Los tres microservicios escriben log, como pide el enunciado. Node usa Winston en JSON. Java usa el log de Spring. Cada pedido lleva un identificador para seguirlo de la puerta al servicio. No se escribe la tarjeta ni el CVV.

### Docker

La web, la puerta, los tres servicios y MySQL suben con un solo `docker compose up --build`. El enunciado pide desplegar la aplicación y los servicios en contenedores. Dejar MySQL fuera obligaría a quien revise a instalar la base a mano. Meterlo en el mismo compose es mejor para una prueba que se tiene que levantar y recorrer.

El puerto 3307 es a propósito. El 3306 ya lo usa el MySQL de la máquina, y chocar con él habría tumbado el arranque.

## Carpetas

- `frontend`: las pantallas.
- `gateway`: recibe a la tienda, inicia sesión y muestra Swagger.
- `services/premieres`: la cartelera.
- `services/candystore`: la dulcería.
- `services/complete`: habla con PayU y guarda la compra.
- `db/init`: tablas, procedimientos y datos de ejemplo.
- `db/pruebas`: la prueba del procedimiento de compra.
