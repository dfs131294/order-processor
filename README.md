# 📦 Orders Processor API

Aplicación Spring Boot 3 que implementa una arquitectura hexagonal para procesar la carga masiva de pedidos mediante archivos CSV con validación estricta, idempotencia y persistencia por lotes utilizando PostgreSQL.

---
# ⚙️Stack tecnológico

- Java 17+
- Spring Boot 3
- PostgreSQL
- Flyway
- Spring Security OAuth2
- OpenAPI 3
- Logstash JSON logs

---

# 📥 API

## POST /pedidos/cargar

Headers:
- Content-Type: multipart/form-data
- Authorization: Bearer <JWT>
- Idempotency-Key: <clave-única>

Body:
- file: archivo CSV

---

# 📄 Formato CSV

numeroPedido,clienteId,fechaEntrega,estado,zonaEntrega,requiereRefrigeracion\
P001,CLI-123,2025-08-10,PENDIENTE,ZONA1,true

---

# 📌 Notas

- Uso de procesamiento en lotes
- Uso de clave de idempotencia + SHA-256
- Uso de arquitectura hexagonal

# 📦 Estrategia de batch

- La estrategia de batch que utiliza el microservicio se basa en procesar, mediante una configuración, el archivo csv por lotes.
  Es decir, de acuerdo al tamaño de registros, el microservicio va a procesar de manera secuencial lotes hasta completar el total.
  Ej: CSV de tamaño 100\, Tamaño de batch: 10, el microservcios procesará en 10 pasos el total del archivo.
- La configuración del tamaño del batch se encuentra en el archivo "application.yml",
  el cual es configurable.

```yaml
app:
  batch-process:
    batch-size: 
```
---

# 🧪 Instrucciones para ejecutar localmente

## Ejecución con Docker

### 1. Requisitos previos
- Docker + Docker Compose
---

### 2. Despliegue

```bash
docker-compose up --build
```

## Ejecución con stack tecnológico

### 1. Requisitos previos
- Java 17+
- Maven 3.9+
- PostgreSQL
---

## 2. Configuración de la base de datos
```yaml
spring.datasource.url=jdbc:postgresql://localhost:5432/retotecnico
spring.datasource.username=postgres
spring.datasource.password=postgres
```
---

## 3. Compilar proyecto
 ```bash
mvn clean install
```

---

## 4. Ejecutar aplicación

```bash
mvn spring-boot:run
```
---
# Probar API

Swagger:\
http://localhost:8080/orders-processor/swagger-ui/index.html

OpenAPI 3:\
http://localhost:8080/orders-processor/v3/api-docs

Endpoint:\
POST http://localhost:8080/pedidos/cargar

Curl de ejemplo:\
Obtener Token (se está manejando un único usuario):\
curl --location --request POST 'localhost:8080/orders-processor/auth/login' \
--header 'Content-Type: application/json' \
--data-raw '{
"username": "test",
"password": "test"
}'

Procesamiento de pedidos:\
curl --location --request POST 'localhost:8080/orders-processor/pedidos/cargar' \
--header 'Idempotency-Key: 324324324' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IiwiaWF0IjoxNzgxNDYzOTExLCJleHAiOjE3ODE1NTAzMTF9.i6VSWdvmX_MmIGkj7mXu7hgKNrkyBKe0-NlJEkKgVTQ' \
--form 'file=@"/{ruta de archivo}"'

Nota: \
- En la ruta "/samples" se encuentra un archivo de ejemplo "pedidos.csv".
- En la ruta "/collection" se encuentra un archivo de coleccion de Postman.
