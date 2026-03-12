## 📝 Guía de Uso - Order Service

### 🚀 Endpoints Disponibles

#### 1. **Crear un Pedido**
```
POST /pedidos
```

**Request Body:**
```json
{
  "usuarioId": 1,
  "items": [
    {
      "productoId": 5,
      "cantidad": 2
    },
    {
      "productoId": 3,
      "cantidad": 1
    }
  ]
}
```

**Response (201 OK):**
```json
{
  "id": 1,
  "usuarioId": 1,
  "estado": "PENDING",
  "total": 150.50,
  "fechaCreacion": "2026-03-12T03:23:47.709Z",
  "fechaActualizacion": "2026-03-12T03:23:47.709Z",
  "items": [
    {
      "id": 1,
      "productoId": 5,
      "productoNombre": "Cien Años de Soledad",
      "cantidad": 2,
      "precioUnitario": 50.00,
      "subtotal": 100.00
    },
    {
      "id": 2,
      "productoId": 3,
      "productoNombre": "Don Quijote",
      "cantidad": 1,
      "precioUnitario": 50.50,
      "subtotal": 50.50
    }
  ]
}
```

**Posibles Errores:**
- `400 Bad Request`: Stock insuficiente, producto no existe, datos inválidos
- `500 Internal Server Error`: Error de conexión con Catalog Service

---

#### 2. **Obtener un Pedido**
```
GET /pedidos/{id}
```

**Example:** `GET /pedidos/1`

**Response (200 OK):**
```json
{
  "id": 1,
  "usuarioId": 1,
  "estado": "PENDING",
  "total": 150.50,
  "fechaCreacion": "2026-03-12T03:23:47.709Z",
  "fechaActualizacion": "2026-03-12T03:23:47.709Z",
  "items": [...]
}
```

**Posibles Errores:**
- `404 Not Found`: Pedido no existe

---

#### 3. **Listar Pedidos de un Usuario**
```
GET /pedidos/usuario/{usuarioId}
```

**Example:** `GET /pedidos/usuario/1`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "usuarioId": 1,
    "estado": "PENDING",
    "total": 150.50,
    ...
  },
  {
    "id": 2,
    "usuarioId": 1,
    "estado": "CONFIRMED",
    "total": 75.25,
    ...
  }
]
```

**Posibles Errores:**
- `400 Bad Request`: Usuario ID inválido

---

#### 4. **Actualizar Estado de Pedido**
```
PUT /pedidos/{id}
```

**Example:** `PUT /pedidos/1`

**Request Body:**
```json
{
  "estado": "CONFIRMED"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "usuarioId": 1,
  "estado": "CONFIRMED",
  "total": 150.50,
  ...
}
```

**Estados válidos:** PENDING, CONFIRMED, DELIVERED, CANCELLED

**Posibles Errores:**
- `404 Not Found`: Pedido no existe
- `400 Bad Request`: Transición de estado inválida

---

### 🔄 Estados del Pedido

| Estado | Descripción | Transiciones Permitidas |
|--------|-------------|------------------------|
| `PENDING` | Pedido creado, pendiente de confirmación | → CONFIRMED, CANCELLED |
| `CONFIRMED` | Pedido confirmado | → DELIVERED, CANCELLED |
| `DELIVERED` | Pedido entregado | ❌ No permite cambios |
| `CANCELLED` | Pedido cancelado | ❌ No permite cambios |

---

### 💡 Casos de Uso

#### Crear un pedido de 2 libros:
```bash
curl -X POST http://localhost:8080/pedidos \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 1,
    "items": [
      {"productoId": 5, "cantidad": 2},
      {"productoId": 3, "cantidad": 1}
    ]
  }'
```

#### Ver todos los pedidos del usuario 1:
```bash
curl http://localhost:8080/pedidos/usuario/1
```

#### Confirmar el pedido 1:
```bash
curl -X PUT http://localhost:8080/pedidos/1 \
  -H "Content-Type: application/json" \
  -d '{"estado": "CONFIRMED"}'
```

---

### ⚠️ Manejo de Errores

Todos los errores responden con formato consistente:

```json
{
  "error": "Stock insuficiente",
  "mensaje": "Stock insuficiente para producto 'Cien Años de Soledad' (ISBN: 123456). Disponibles: 2, solicitados: 5",
  "timestamp": "2026-03-12T03:23:47.709Z"
}
```

---

### 🔗 Integración con Otros Servicios

**Catalog Service (8082):**
- El Order Service consulta automáticamente productos
- Valida existencia y stock
- Guarda snapshot del precio actual

**API Gateway (8080):**
- Todos los endpoints son accesibles a través del gateway
- Las rutas se reenvían automáticamente
