# 🚀 QUICK START - Order Service

## ⚡ Inicio Rápido (5 minutos)

### Opción 1: Con Docker Compose (Recomendado)

```bash
# 1. En la carpeta raíz del proyecto
cd Reto_Plataforma-de-Pedidos-por-Microservicios

# 2. Levantar todos los servicios
docker-compose up -d

# 3. Verificar que está corriendo
docker-compose ps

# 4. Ver logs del Order Service
docker-compose logs -f order-service

# 5. Hacer un pedido (espera 30 segundos a que se inicie)
curl -X POST http://localhost:8080/pedidos \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 1,
    "items": [
      {"productoId": 1, "cantidad": 2}
    ]
  }'
```

---

## 🔍 Verificación de Estado

### Health Check
```bash
# Order Service
curl http://localhost:8083/actuator/health

# API Gateway
curl http://localhost:8080/ping

# Catalog Service (debe estar activo para pedidos)
curl http://localhost:8082/ping
```

---

## 📝 Ejemplos Rápidos

### 1. Crear Pedido
```bash
curl -X POST http://localhost:8080/pedidos \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 1,
    "items": [
      {"productoId": 1, "cantidad": 1},
      {"productoId": 2, "cantidad": 3}
    ]
  }'

# Respuesta: Retorna el pedido creado con ID
```

### 2. Ver Pedido
```bash
curl http://localhost:8080/pedidos/1
```

### 3. Listar Pedidos del Usuario
```bash
curl http://localhost:8080/pedidos/usuario/1
```

### 4. Confirmar Pedido
```bash
curl -X PUT http://localhost:8080/pedidos/1 \
  -H "Content-Type: application/json" \
  -d '{"estado": "CONFIRMED"}'
```

---

## 🛠️ Desarrollo Local (Sin Docker)

### Requisitos
- Java 21+
- Maven 3.8+
- PostgreSQL 16
- RabbitMQ 3+

### Pasos

1. **Iniciar PostgreSQL**
```bash
# Linux/Mac
sudo systemctl start postgresql

# Windows: Usar pgAdmin o servicios
```

2. **Crear base de datos**
```sql
CREATE DATABASE ordersdb;
CREATE USER reto WITH PASSWORD 'reto';
GRANT ALL PRIVILEGES ON DATABASE ordersdb TO reto;
```

3. **Iniciar RabbitMQ**
```bash
# Linux/Mac
sudo systemctl start rabbitmq-server

# Docker
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 \
  -e RABBITMQ_DEFAULT_USER=reto \
  -e RABBITMQ_DEFAULT_PASS=reto \
  rabbitmq:3-management
```

4. **Iniciar Catalog Service primero**
```bash
cd services/catalog-service
mvn clean install
mvn spring-boot:run
# Debe estar en puerto 8082
```

5. **Iniciar Order Service**
```bash
cd services/order-service
mvn clean install
mvn spring-boot:run
# Estará en puerto 8083
```

6. **Hacer un pedido (URL local)**
```bash
curl -X POST http://localhost:8083/pedidos \
  -H "Content-Type: application/json" \
  -d '{"usuarioId": 1, "items": [{"productoId": 1, "cantidad": 1}]}'
```

---

## 🔧 Solución de Problemas

### "Connection refused"
```
Problema: No se conecta a Catalog Service
Solución: 
  1. Verificar que Catalog Service está corriendo
  2. Verificar puerto 8082 disponible
  3. Ver logs: docker-compose logs catalog-service
```

### "Database connection failed"
```
Problema: No conecta a PostgreSQL
Solución:
  1. Verificar PostgreSQL corriendo
  2. Verificar credenciales en application.yml
  3. Ver puerto 5432 disponible
```

### "Stock insuficiente"
```
Problema: Error al crear pedido
Solución:
  1. Crear productos en Catalog primero
  2. POST http://localhost:8080/productos con stock > 0
  3. Luego crear el pedido
```

---

## 📊 Archivos de Configuración

### application.yml (Order Service)
```yaml
server:
  port: 8083

spring:
  application:
    name: order-service
  datasource:
    url: jdbc:postgresql://localhost:5432/ordersdb
    username: reto
    password: reto
  jpa:
    hibernate:
      ddl-auto: update
  rabbitmq:
    host: localhost
    username: reto
    password: reto
```

### docker-compose.yml
```yaml
version: '3.8'

services:
  orders-db:
    image: postgres:16
    environment:
      POSTGRES_DB: ordersdb
      POSTGRES_USER: reto
      POSTGRES_PASSWORD: reto
    ports:
      - "5432:5432"

  order-service:
    build: ./services/order-service
    ports:
      - "8083:8083"
    depends_on:
      - orders-db
      - rabbitmq
      - catalog-service
```

---

## 📚 Documentación Detallada

- **GUIA_ORDER_SERVICE.md** - Ejemplos y casos de uso
- **ARQUITECTURA_ORDER_SERVICE.md** - Diseño técnico
- **RESUMEN_ORDER_SERVICE.md** - Resumen del proyecto
- **DIAGRAMAS_VISUALES.md** - Diagramas ASCII

---

## 🧪 Testing

### Con Postman
1. Importar endpoints
2. Variables de entorno: `base_url = http://localhost:8080`
3. Ejecutar colección

### Con curl (scripts)
```bash
bash TESTING_CURL_EJEMPLOS.sh
```

### Con IntelliJ IDEA
1. Tools → HTTP Client → Create Request
2. Copiar curl de ejemplos
3. Ejecutar

---

## 🚨 Logs Importantes

### Ver logs en tiempo real
```bash
# Con Docker
docker-compose logs -f order-service

# Con Maven
mvn spring-boot:run | grep ERROR

# Con IDE
Console tab en IntelliJ
```

### Errores comunes en logs
```
ERROR ProductoNoEncontradoException
└─ El producto consultado no existe en Catalog

ERROR StockInsuficienteException
└─ Stock no disponible para el producto

ERROR Connection refused
└─ Catalog Service no disponible

ERROR Database connection failed
└─ PostgreSQL no disponible
```

---

## ✅ Checklist de Inicio

- [ ] PostgreSQL corriendo
- [ ] RabbitMQ corriendo
- [ ] Catalog Service corriendo (8082)
- [ ] Order Service corriendo (8083)
- [ ] API Gateway corriendo (8080)
- [ ] Crear productos en Catalog primero
- [ ] Hacer test POST /pedidos
- [ ] Ver pedido GET /pedidos/{id}

---

## 🔗 URLs Útiles

| Servicio | URL | Puerto |
|----------|-----|--------|
| API Gateway | http://localhost:8080 | 8080 |
| Order Service | http://localhost:8083 | 8083 |
| Catalog Service | http://localhost:8082 | 8082 |
| Auth Service | http://localhost:8081 | 8081 |
| PostgreSQL | localhost:5432 | 5432 |
| RabbitMQ | http://localhost:15672 | 15672 |
| RabbitMQ AMQP | localhost:5672 | 5672 |

---

## 📞 Contacto y Soporte

Para problemas:
1. Revisar documentación en GUIA_ORDER_SERVICE.md
2. Revisar logs: `docker-compose logs order-service`
3. Verificar base de datos: `psql -U reto -d ordersdb`
4. Reiniciar servicios: `docker-compose restart`

---

## 🎯 Siguientes Pasos

1. ✅ Order Service instalado
2. → Agregar tests
3. → Implementar autenticación JWT
4. → Agregar RabbitMQ events
5. → Desplegar a producción

---

**¡Listo para usar! 🚀**

El Order Service está completamente operacional.
Sigue los ejemplos en GUIA_ORDER_SERVICE.md para más detalles.
