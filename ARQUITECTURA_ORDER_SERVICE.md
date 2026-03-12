# 🏗️ Arquitectura Order Service - Diagrama y Documentación

## Flujo de Creación de Pedido

```
┌─────────────────────────────────────────────────────────────────────┐
│  CLIENTE (API Gateway - 8080)                                       │
│  POST /pedidos                                                      │
│  {                                                                  │
│    "usuarioId": 1,                                                  │
│    "items": [{"productoId": 5, "cantidad": 2}]                     │
│  }                                                                  │
└────────────────────────────┬────────────────────────────────────────┘
                             │
                             ▼
        ┌────────────────────────────────────────┐
        │  OrderController                       │
        │  @PostMapping("/pedidos")              │
        └────────────┬─────────────────────────┘
                     │
                     ▼
        ┌────────────────────────────────────────┐
        │  OrderService.crearPedido()            │
        │  1. Valida inputs                      │
        │  2. Para cada item:                    │
        │     - Consulta Catalog Service         │
        │     - Valida stock                     │
        │     - Crea OrderItemEntity             │
        │  3. Calcula total                      │
        │  4. Guarda en BD                       │
        └────────────┬─────────────────────────┘
                     │
          ┌──────────┴──────────┐
          │                     │
          ▼                     ▼
    ┌──────────────────┐  ┌─────────────────────────────┐
    │ OrderRepository  │  │ CatalogServiceClient        │
    │ .save()          │  │ HTTP: catalog-service:8082  │
    │                  │  │ GET /productos/{id}         │
    └──────────────────┘  └─────────────────────────────┘
          │                     │
          ▼                     ▼
    ┌──────────────────┐  ┌──────────────────────┐
    │  orders-db       │  │  Catalog Service     │
    │  (PostgreSQL)    │  │  (Validación)        │
    │                  │  │                      │
    │ orders table     │  │ Retorna:             │
    │ order_items tbl  │  │ - id, titulo, autor  │
    │                  │  │ - precio             │
    │                  │  │ - stock              │
    │                  │  │ - activo             │
    └──────────────────┘  └──────────────────────┘
```

## Estructura de Carpetas

```
order-service/
├── src/main/java/com/reto/order/
│   ├── OrderServiceApplication.java (Main + RestTemplate Bean)
│   ├── controller/
│   │   ├── OrderController.java (REST endpoints)
│   │   └── PingController.java (Health check)
│   ├── service/
│   │   ├── OrderService.java (Lógica de negocio)
│   │   └── CatalogServiceClient.java (Cliente HTTP)
│   ├── entity/
│   │   ├── OrderEntity.java (Tabla orders)
│   │   ├── OrderItemEntity.java (Tabla order_items)
│   │   ├── OrderRequest.java (DTO entrada)
│   │   ├── OrderResponse.java (DTO salida)
│   │   ├── OrderItemRequest.java (DTO item entrada)
│   │   ├── OrderItemResponse.java (DTO item salida)
│   │   ├── CatalogProductDTO.java (DTO Catalog)
│   │   ├── ProductoNoEncontradoException.java
│   │   ├── StockInsuficienteException.java
│   │   ├── PedidoNoEncontradoException.java
│   │   └── GlobalExceptionHandler.java
│   └── repository/
│       ├── OrderRepository.java (JPA)
│       └── OrderItemRepository.java (JPA)
├── src/main/resources/
│   └── application.yml (Config: BD, RabbitMQ)
└── pom.xml (Maven dependencies)
```

## Capas de Arquitectura

### 1️⃣ Capa de Presentación (Controller)
- **OrderController**: Expone 4 endpoints REST
- **Responsabilidad**: Recibir requests HTTP, validar entrada, devolver JSON

### 2️⃣ Capa de Negocio (Service)
- **OrderService**: Lógica principal
  - Crear pedidos
  - Validar con Catalog Service
  - Consultar pedidos
  - Actualizar estados
- **CatalogServiceClient**: Comunicación inter-microservicios
  - HTTP calls a Catalog Service
  - Manejo de errores

### 3️⃣ Capa de Datos (Repository + Entity)
- **OrderRepository**: Acceso a órdenes (JPA)
- **OrderItemRepository**: Acceso a items (JPA)
- **OrderEntity**: Mapeo tabla `orders`
- **OrderItemEntity**: Mapeo tabla `order_items`

### 4️⃣ DTOs (Transfer Objects)
- **OrderRequest/Response**: Serialización de órdenes
- **OrderItemRequest/Response**: Serialización de items
- **CatalogProductDTO**: Snapshot del producto

### 5️⃣ Manejo de Errores
- **GlobalExceptionHandler**: @RestControllerAdvice
  - Intercepta excepciones
  - Devuelve JSON consistente
- **Custom Exceptions**: Específicas del negocio

---

## Flujo de Datos

### Creación de Pedido (Happy Path)

```
OrderRequest
    ↓
    ├─ Valida usuarioId ✓
    ├─ Valida items no vacío ✓
    ├─ Para cada item:
    │  ├─ CatalogServiceClient.obtenerProducto(id)
    │  │  └─ HTTP GET a 8082/productos/5
    │  │     └─ Retorna CatalogProductDTO
    │  ├─ Valida activo = true ✓
    │  ├─ Valida stock >= cantidad ✓
    │  └─ Crea OrderItemEntity con snapshot
    ├─ Calcula total
    └─ Guarda en BD
        ↓
    OrderEntity guardada
        ↓
    OrderResponse
```

### Consulta de Pedido

```
GET /pedidos/{id}
    ↓
OrderController.obtenerPedido(id)
    ↓
OrderService.obtenerPedido(id)
    ├─ OrderRepository.findById(id)
    │  └─ Si no existe → PedidoNoEncontradoException (404)
    └─ Convierte OrderEntity → OrderResponse
        ↓
    OrderResponse (con items)
```

---

## Validaciones Implementadas

| Punto | Validación | Excepción |
|-------|-----------|-----------|
| Input | usuarioId > 0 | RuntimeException |
| Input | items no vacío | RuntimeException |
| Input | cantidad > 0 | RuntimeException |
| Catalog | Producto existe | ProductoNoEncontradoException (404) |
| Catalog | Producto activo | ProductoNoEncontradoException (404) |
| Catalog | Stock >= cantidad | StockInsuficienteException (400) |
| Catalog | Servicio disponible | ProductoNoEncontradoException (500) |
| Lectura | Pedido existe | PedidoNoEncontradoException (404) |
| Update | Estado válido | RuntimeException (400) |
| Update | Transición permitida | RuntimeException (400) |

---

## Base de Datos

### Tabla: `orders`
```sql
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    total DECIMAL(10,2) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP NOT NULL
);
```

### Tabla: `order_items`
```sql
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    orden_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    producto_nombre VARCHAR(255) NOT NULL,
    cantidad INTEGER NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (orden_id) REFERENCES orders(id)
);
```

---

## Configuración RabbitMQ (Preparado para eventos)

```yaml
spring:
  rabbitmq:
    host: rabbitmq
    port: 5672
    username: reto
    password: reto
```

Se puede expandir con:
- Event Publisher cuando se crea orden
- Listeners para eventos del Catalog
- Dead Letter Queues para reintentos

---

## Integración con API Gateway

El API Gateway (puerto 8080) enruta:
```
POST   http://localhost:8080/pedidos          → order-service:8083/pedidos
GET    http://localhost:8080/pedidos/{id}     → order-service:8083/pedidos/{id}
GET    http://localhost:8080/pedidos/usuario/{usuarioId}
PUT    http://localhost:8080/pedidos/{id}     → order-service:8083/pedidos/{id}
```

---

## Testing Manual (Ejemplos)

### Crear pedido:
```bash
curl -X POST http://localhost:8080/pedidos \
  -H "Content-Type: application/json" \
  -d '{"usuarioId":1,"items":[{"productoId":1,"cantidad":2}]}'
```

### Ver pedido:
```bash
curl http://localhost:8080/pedidos/1
```

### Ver todos los pedidos del usuario:
```bash
curl http://localhost:8080/pedidos/usuario/1
```

### Actualizar estado:
```bash
curl -X PUT http://localhost:8080/pedidos/1 \
  -H "Content-Type: application/json" \
  -d '{"estado":"CONFIRMED"}'
```

---

## ✅ Características Implementadas

- ✅ Validación de productos con Catalog Service
- ✅ Validación de stock en tiempo real
- ✅ Snapshot del precio (inmutable en la orden)
- ✅ Estados de pedido con transiciones validadas
- ✅ Auditoría (fechaCreacion, fechaActualizacion)
- ✅ Excepciones personalizadas
- ✅ Manejo centralizado de errores
- ✅ DTOs para transferencia de datos
- ✅ Transaccionalidad con @Transactional
- ✅ Logging preparado (se puede habilitar en properties)

---

## 🚀 Próximos Pasos (Opcionales)

- [ ] Agregar RabbitMQ Publisher para eventos de pedidos
- [ ] Agregar listeners para eventos del Catalog
- [ ] Implementar paginación en listarPedidosPorUsuario
- [ ] Agregar filtros por estado
- [ ] Autenticación/Autorización (JWT del Auth Service)
- [ ] Tests unitarios y de integración
- [ ] Logging con SLF4J
- [ ] Métricas con Micrometer
