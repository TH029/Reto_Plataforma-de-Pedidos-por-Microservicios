# 🏗️ Plataforma de Pedidos por Microservicios

Sistema completo de microservicios para gestión de pedidos con **Auth**, **Catalog** y **Order Services**, todo orquestado con **Docker Compose** y comunicación mediante **API Gateway**.

---

## 📋 Contenidos

### 🚀 Getting Started
- **[POSTMAN_QUICKSTART.md](./POSTMAN_QUICKSTART.md)** ⭐ **COMIENZA AQUÍ** - Quick start de 5 minutos para testear todo en Postman
- **[QUICKSTART.md](./QUICKSTART.md)** - Guía rápida inicial

### 📚 Documentación Completa
- **[POSTMAN_TESTING_COMPLETE.md](./POSTMAN_TESTING_COMPLETE.md)** - Guía exhaustiva de testing con Postman para todos los servicios
- **[ENDPOINTS_MAP.md](./ENDPOINTS_MAP.md)** - Mapa visual de todos los endpoints, estados y flujos
- **[GUIA_COMPLETA_TESTING.txt](./GUIA_COMPLETA_TESTING.txt)** - Testing completo con instrucciones paso a paso

### 📮 Archivos Postman
- **[Postman_Collection.json](./Postman_Collection.json)** - Colección lista para importar en Postman
- **[Postman_Environment.json](./Postman_Environment.json)** - Variables de ambiente pre-configuradas

### 📋 Documentación de Servicios Individuales
- **[README_ORDER_SERVICE.md](./README_ORDER_SERVICE.md)** - Detalles del Order Service
- **[GUIA_ORDER_SERVICE.md](./GUIA_ORDER_SERVICE.md)** - Guía específica del Order Service
- **[RESUMEN_ORDER_SERVICE.md](./RESUMEN_ORDER_SERVICE.md)** - Resumen de implementación Order Service
- **[ARQUITECTURA_ORDER_SERVICE.md](./ARQUITECTURA_ORDER_SERVICE.md)** - Arquitectura del Order Service

### 🧪 Testing
- **[TESTING_CURL_EJEMPLOS.sh](./TESTING_CURL_EJEMPLOS.sh)** - Scripts curl para testing (legacy)
- **[TESTING_Y_COMMIT.md](./TESTING_Y_COMMIT.md)** - Testing integrado con Git
- **[RESUMEN_RAPIDO_TESTING.md](./RESUMEN_RAPIDO_TESTING.md)** - Resumen rápido de testing

### 📊 Visualización
- **[DIAGRAMAS_VISUALES.md](./DIAGRAMAS_VISUALES.md)** - Diagramas de arquitectura
- **[VISUAL_TESTING_RESUMEN.txt](./VISUAL_TESTING_RESUMEN.txt)** - Resumen visual de testing

### 📌 Estado del Proyecto
- **[COMPLETADO.txt](./COMPLETADO.txt)** - Estado actual de implementación

---

## 🏛️ Arquitectura de Microservicios

```
┌─────────────────────────────────────────────────────────────┐
│                     API GATEWAY (8080)                      │
│                  (Spring Cloud Gateway)                     │
└────────┬──────────────┬──────────────┬──────────────────────┘
         │              │              │
    ┌────▼────┐  ┌─────▼────┐  ┌─────▼────┐
    │  Auth   │  │ Catalog  │  │  Order   │
    │ Service │  │ Service  │  │ Service  │
    │  (8081) │  │  (8082)  │  │  (8083)  │
    └────┬────┘  └─────┬────┘  └─────┬────┘
         │              │              │
    ┌────▼────┐  ┌─────▼────┐  ┌─────▼────┐
    │ AuthDB  │  │CatalogDB │  │ OrdersDB │
    │(PgSQL)  │  │ (PgSQL)  │  │ (PgSQL)  │
    └─────────┘  └──────────┘  └──────────┘
         
    ┌──────────────────────────────────────┐
    │         RabbitMQ (Message Queue)     │
    │              (5672)                  │
    └──────────────────────────────────────┘
```

---

## 🔐 Servicios

### **Auth Service** (Puerto 8081)
Autenticación y gestión de usuarios con JWT.

**Endpoints:**
- `POST /auth/register` - Registrar usuario
- `POST /auth/login` - Login y obtener JWT
- `GET /auth/users/{id}` - Obtener datos del usuario

### **Catalog Service** (Puerto 8082)
Catálogo de productos (libros) con gestión de inventario.

**Endpoints:**
- `GET /productos` - Listar productos
- `GET /productos/{id}` - Obtener detalles
- `POST /productos` - Crear producto
- `PUT /productos/{id}` - Actualizar producto
- `DELETE /productos/{id}` - Eliminar producto

### **Order Service** (Puerto 8083)
Gestión de pedidos con integración a Catalog Service.

**Endpoints:**
- `POST /pedidos` - Crear pedido
- `GET /pedidos/{id}` - Ver detalles
- `GET /pedidos/usuario/{usuarioId}` - Listar pedidos del usuario
- `PUT /pedidos/{id}` - Cambiar estado
- `DELETE /pedidos/{id}` - Eliminar pedido

**Estados:** `PENDING` → `CONFIRMED` → `DELIVERED` o `CANCELLED`

### **API Gateway** (Puerto 8080)
Punto de entrada único para todos los servicios.

---

## 🚀 Quick Start (5 minutos)

### 1️⃣ Levantar Docker
```bash
docker-compose up -d
```

Espera ~45 segundos, luego verifica:
```bash
docker-compose ps
```

Deberías ver **8 contenedores activos**.

### 2️⃣ Importar en Postman
1. Abre Postman
2. Click en **Import**
3. Carga `Postman_Collection.json`
4. Carga también `Postman_Environment.json` (variables)

### 3️⃣ Testear
Sigue el flujo en **[POSTMAN_QUICKSTART.md](./POSTMAN_QUICKSTART.md)**

---

## 🛠️ Stack Tecnológico

| Componente | Versión | Descripción |
|------------|---------|-------------|
| **Java** | 21 | Lenguaje de programación |
| **Spring Boot** | 3.x | Framework web/microservicios |
| **Spring Cloud** | 2023.0.0 | Gateway y descubrimiento |
| **PostgreSQL** | 16 | Base de datos |
| **RabbitMQ** | 3 | Message queue |
| **Docker** | Latest | Containerización |
| **Maven** | 3.9 | Build tool |

---

## 📁 Estructura del Proyecto

```
Reto_Plataforma-de-Pedidos-por-Microservicios/
├── services/
│   ├── auth-service/
│   │   ├── src/main/java/com/reto/auth/
│   │   ├── src/main/resources/application.yml
│   │   └── pom.xml
│   ├── catalog-service/
│   │   ├── src/main/java/com/reto/catalog/
│   │   ├── src/main/resources/application.yml
│   │   └── pom.xml
│   ├── order-service/
│   │   ├── src/main/java/com/reto/order/
│   │   ├── src/main/resources/application.yml
│   │   └── pom.xml
│   └── api-gateway/
│       ├── src/main/java/com/reto/gateway/
│       ├── src/main/resources/application.yml
│       └── pom.xml
├── docker-compose.yml
├── Postman_Collection.json
├── Postman_Environment.json
├── POSTMAN_QUICKSTART.md
├── POSTMAN_TESTING_COMPLETE.md
├── ENDPOINTS_MAP.md
└── [Documentación y guías]
```

---

## ✅ Checklist Inicial

- [ ] Docker instalado
- [ ] Postman instalado
- [ ] Docker Compose ejecutado exitosamente
- [ ] Todos los 8 contenedores activos
- [ ] Colección de Postman importada
- [ ] Testing manual completado (22 requests)

---

## 🧪 Testing

### Manual (Postman)
→ Ver **[POSTMAN_QUICKSTART.md](./POSTMAN_QUICKSTART.md)** para flujo rápido
→ Ver **[POSTMAN_TESTING_COMPLETE.md](./POSTMAN_TESTING_COMPLETE.md)** para guía completa

### Con curl
```bash
# Crear producto
curl -X POST http://localhost:8082/productos \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Libro Test",
    "autor": "Test Author",
    "precio": 50.00,
    "stock": 100
  }'

# Crear pedido
curl -X POST http://localhost:8083/pedidos \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 1,
    "items": [{"productoId": 1, "cantidad": 2}]
  }'
```

---

## 🐛 Troubleshooting

### Docker no arranca
```bash
docker-compose down -v
docker-compose up -d
docker-compose logs -f
```

### Servicios no responden
```bash
docker-compose ps
curl http://localhost:8080/health
```

### Limpiar completamente
```bash
docker-compose down -v
docker volume prune
docker-compose up -d
```

---

## 📚 Documentación Adicional

- **Arquitectura**: Ver [ARQUITECTURA_ORDER_SERVICE.md](./ARQUITECTURA_ORDER_SERVICE.md)
- **Diagramas**: Ver [DIAGRAMAS_VISUALES.md](./DIAGRAMAS_VISUALES.md)
- **Estado**: Ver [COMPLETADO.txt](./COMPLETADO.txt)

---

## 🔗 URLs Rápidas

```
Auth Service:       http://localhost:8081
Catalog Service:    http://localhost:8082
Order Service:      http://localhost:8083
API Gateway:        http://localhost:8080
RabbitMQ UI:        http://localhost:15672
```

---

## 📝 Próximos Pasos

1. **Testing Manual**: Completa el testing en Postman (22 requests)
2. **Tests Unitarios**: Implementar JUnit 5 + Mockito
3. **Tests Integración**: Spring Boot Test + TestContainers
4. **Circuit Breaker**: Agregar Resilience4j
5. **Logging Centralizado**: ELK Stack
6. **Métricas**: Prometheus + Grafana

---

## 📞 Support

Si algo no funciona:
1. Revisar logs: `docker-compose logs -f {servicio}`
2. Verificar puertos: `netstat -ano | findstr :{puerto}`
3. Resetear todo: `docker-compose down -v && docker-compose up -d`

---

**¡Bienvenido a la Plataforma de Microservicios!** 🚀

Para comenzar: **[POSTMAN_QUICKSTART.md](./POSTMAN_QUICKSTART.md)**
