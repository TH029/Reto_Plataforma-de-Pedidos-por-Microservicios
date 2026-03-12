# 🏛️ Order Service - Diagrama de Componentes

## Diagrama 1: Arquitectura General

```
┌─────────────────────────────────────────────────────────────────────┐
│                         CLIENTE (Browser/App)                       │
└────────────────────────────────┬────────────────────────────────────┘
                                 │
                                 ▼
                    ┌────────────────────────┐
                    │    API Gateway         │
                    │    (Puerto 8080)       │
                    └────────────┬───────────┘
                                 │
                ┌────────────────┼────────────────┐
                ▼                ▼                ▼
        ┌────────────────┐ ┌──────────────┐ ┌──────────────┐
        │ Auth Service   │ │Catalog Service│ │Order Service │
        │  (8081)        │ │   (8082)      │ │  (8083) ◄─┐  │
        └────────────────┘ └──────────────┘ └────────────┼┘  │
                                               │          │   │
                                               │HTTP Calls│   │
                                               ├──────────┘   │
                                               │              │
                    ┌──────────────────────────┴──────────┐   │
                    ▼                                      ▼   │
              ┌─────────────────┐                 ┌────────────┴──┐
              │   auth-db       │                 │  orders-db    │
              │ (PostgreSQL)    │                 │ (PostgreSQL)  │
              └─────────────────┘                 └───────────────┘
                                                           │
                                                   ┌───────┴────────┐
                                                   │  orders        │
                                                   │  order_items   │
                                                   └────────────────┘
```

## Diagrama 2: Flujo de Creación de Pedido

```
┌──────────────────────────────────────────────────────────────────┐
│ POST /pedidos                                                    │
│ {                                                                │
│   "usuarioId": 1,                                                │
│   "items": [{"productoId": 5, "cantidad": 2}]                  │
│ }                                                                │
└────────────────────────────┬─────────────────────────────────────┘
                             │
                             ▼
        ┌────────────────────────────────────┐
        │    OrderController                 │
        │   (Recibe JSON)                    │
        └────────────────┬───────────────────┘
                         │
                         ▼
        ┌────────────────────────────────────┐
        │    OrderService                    │
        │   (Lógica de negocio)              │
        │                                    │
        │  1. Valida usuarioId               │
        │  2. Valida items no vacío          │
        │  3. Para cada item:                │
        │     - Consulta Catalog             │
        │     - Valida stock                 │
        │     - Crea OrderItemEntity         │
        │  4. Calcula total                  │
        │  5. Guarda en BD                   │
        └────────────┬──────────────┬────────┘
                     │              │
          ┌──────────▼──┐          │
          │  HTTP GET   │          │
          │catalog:8082/│          │
          │productos/5  │          │
          └──────┬──────┘          │
                 │                 │
                 ▼                 ▼
        ┌─────────────────┐  ┌──────────────────┐
        │ Catalog Service │  │ OrderRepository  │
        │ (Valida)        │  │ .save(orden)     │
        │ Retorna:        │  │                  │
        │ - id            │  └──────┬───────────┘
        │ - titulo        │         │
        │ - precio        │         ▼
        │ - stock         │    ┌──────────────┐
        │ - activo        │    │  orders-db   │
        └────────┬────────┘    │ PostgreSQL   │
                 │              └──────────────┘
        ┌────────▼─────────────────────────┐
        │ Respuesta: OrderResponse         │
        │ (JSON con orden completa)        │
        └────────────────────────────────────┘
```

## Diagrama 3: Estructura de Datos

```
┌─────────────────────────────────────────────────────────────────┐
│                    ORDEN (OrderEntity)                          │
├─────────────────────────────────────────────────────────────────┤
│ id: 1                                                           │
│ usuarioId: 1                                                    │
│ estado: "PENDING"                                               │
│ total: 150.50                                                   │
│ fechaCreacion: 2026-03-12T03:23:47                              │
│ fechaActualizacion: 2026-03-12T03:23:47                         │
├─────────────────────────────────────────────────────────────────┤
│                        ITEMS (1:N)                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────────────────────┐                               │
│  │ OrderItemEntity #1           │                               │
│  ├──────────────────────────────┤                               │
│  │ id: 1                        │                               │
│  │ orden_id: 1 (FK)             │                               │
│  │ productoId: 5                │                               │
│  │ productoNombre: "Cien Años.."│                               │
│  │ cantidad: 2                  │                               │
│  │ precioUnitario: 50.00        │                               │
│  │ subtotal: 100.00             │                               │
│  └──────────────────────────────┘                               │
│                                                                 │
│  ┌──────────────────────────────┐                               │
│  │ OrderItemEntity #2           │                               │
│  ├──────────────────────────────┤                               │
│  │ id: 2                        │                               │
│  │ orden_id: 1 (FK)             │                               │
│  │ productoId: 3                │                               │
│  │ productoNombre: "Don Quijote"│                               │
│  │ cantidad: 1                  │                               │
│  │ precioUnitario: 50.50        │                               │
│  │ subtotal: 50.50              │                               │
│  └──────────────────────────────┘                               │
│                                                                 │
│                 TOTAL: 150.50                                   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Diagrama 4: Flujo de Validación

```
                        ORDEN ENTRADA
                             │
                             ▼
                    ┌─────────────────┐
                    │ ¿usuarioId > 0? │
                    └────────┬────────┘
                             │ NO ──→ ERROR (400)
                             │ SÍ
                             ▼
                    ┌─────────────────────┐
                    │ ¿Items no vacío?    │
                    └────────┬────────────┘
                             │ NO ──→ ERROR (400)
                             │ SÍ
                             ▼
                    ┌─────────────────────┐
                    │ Para cada item:     │
                    └────────┬────────────┘
                             │
                             ▼
                    ┌─────────────────────┐
                    │¿Cantidad > 0?       │
                    └────────┬────────────┘
                             │ NO ──→ ERROR (400)
                             │ SÍ
                             ▼
            ╔════════════════════════════════════╗
            ║ HTTP: GET Catalog/productos/{id}   ║
            ╚════════════════════════════════════╝
                             │
                    ┌────────┴────────┐
                    │ NO EXISTE       │ EXISTE
                    ▼                 ▼
            ERROR (404)         ┌──────────────┐
            Producto no         │¿Activo=true?│
            encontrado          └─────┬────────┘
                                      │ NO ──→ ERROR (404)
                                      │ SÍ
                                      ▼
                                ┌──────────────┐
                                │¿Stock >=     │
                                │ Cantidad?    │
                                └─────┬────────┘
                                      │ NO ──→ ERROR (400)
                                      │        Stock insuficiente
                                      │ SÍ
                                      ▼
                        ✅ VALIDACIÓN OK
                        Crear OrderItemEntity
                        con snapshot del producto
```

## Diagrama 5: Estados del Pedido

```
                    ┌──────────────┐
                    │  CREADO      │
                    │  PENDING     │
                    └────┬─────────┘
                         │
            ┌────────────┬┴────────────┐
            │                         │
            ▼                         ▼
      ┌──────────────┐          ┌──────────────┐
      │ CONFIRMADO   │          │ CANCELADO    │
      │ CONFIRMED    │          │ CANCELLED    │
      └────┬─────────┘          └──────────────┘
           │                     (FIN - NO CAMBIA)
           │
           ▼
      ┌──────────────┐
      │ ENTREGADO    │
      │ DELIVERED    │
      └──────────────┘
      (FIN - NO CAMBIA)
```

## Diagrama 6: Capas de la Aplicación

```
┌────────────────────────────────────────────────────────────┐
│              CAPA DE PRESENTACIÓN                          │
│  OrderController                                           │
│  ┌─ POST /pedidos                                         │
│  ├─ GET /pedidos/{id}                                     │
│  ├─ GET /pedidos/usuario/{usuarioId}                      │
│  └─ PUT /pedidos/{id}                                     │
└─────────────────────┬──────────────────────────────────────┘
                      │
┌─────────────────────▼──────────────────────────────────────┐
│              CAPA DE NEGOCIO                               │
│  OrderService                                              │
│  ├─ crearPedido()                                         │
│  ├─ obtenerPedido()                                       │
│  ├─ listarPedidosPorUsuario()                             │
│  ├─ actualizarEstado()                                    │
│  └─ convertirAResponse()                                  │
│                                                            │
│  CatalogServiceClient                                      │
│  ├─ obtenerProducto()                                     │
│  └─ validarStock()                                        │
└─────────────────────┬──────────────────────────────────────┘
                      │
┌─────────────────────▼──────────────────────────────────────┐
│              CAPA DE DATOS                                 │
│  OrderRepository (JPA)                                     │
│  ├─ findById()                                            │
│  ├─ save()                                                │
│  └─ findByUsuarioId()                                     │
│                                                            │
│  OrderItemRepository (JPA)                                 │
│  └─ findByOrdenId()                                       │
└─────────────────────┬──────────────────────────────────────┘
                      │
┌─────────────────────▼──────────────────────────────────────┐
│         BASE DE DATOS - PostgreSQL                         │
│  ┌────────────────────┐    ┌────────────────────┐         │
│  │ orders table       │    │ order_items table  │         │
│  ├────────────────────┤    ├────────────────────┤         │
│  │ id                 │◄───│ id                 │         │
│  │ usuario_id         │    │ orden_id (FK)      │         │
│  │ estado             │    │ producto_id        │         │
│  │ total              │    │ producto_nombre    │         │
│  │ fecha_creacion     │    │ cantidad           │         │
│  │ fecha_actualizacion│    │ precio_unitario    │         │
│  │                    │    │ subtotal           │         │
│  └────────────────────┘    └────────────────────┘         │
└────────────────────────────────────────────────────────────┘
```

## Diagrama 7: Manejo de Errores

```
                        EXCEPCIÓN LANZADA
                             │
            ┌────────────────┬┴────────────────┬─────────────────┐
            │                │                │                 │
            ▼                ▼                ▼                 ▼
    ProductoNoEnc-    StockInsufic-    PedidoNoEnc-       Exception
    ontradoException   ienteException   ontradoException   (Generic)
            │                │                │                 │
            ▼                ▼                ▼                 ▼
    ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌──────────────┐
    │ HTTP 404    │  │ HTTP 400    │  │ HTTP 404    │  │ HTTP 500     │
    │ NOT FOUND   │  │ BAD REQUEST │  │ NOT FOUND   │  │ SERVER ERROR │
    └────────┬────┘  └────────┬────┘  └────────┬────┘  └──────┬───────┘
             │               │               │               │
             └───────────────┴───────────────┴───────────────┘
                             │
                             ▼
         ┌─────────────────────────────────────────┐
         │   GlobalExceptionHandler                │
         │   @RestControllerAdvice                 │
         └────────────────┬────────────────────────┘
                          │
                          ▼
         ┌──────────────────────────────────┐
         │  JSON Response (Consistente)     │
         │  {                               │
         │    "error": "...",               │
         │    "mensaje": "...",             │
         │    "timestamp": "2026-03-12..."  │
         │  }                               │
         └──────────────────────────────────┘
```

---

Estos diagramas ilustran:
1. La arquitectura general del sistema
2. El flujo de creación de pedido
3. La estructura de datos (relaciones)
4. El flujo de validaciones
5. Los estados del pedido
6. Las capas de la aplicación
7. El manejo centralizado de errores
