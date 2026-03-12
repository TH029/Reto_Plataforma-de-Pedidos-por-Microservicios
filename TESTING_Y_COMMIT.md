# 🧪 GUÍA COMPLETA: Testing con Docker, Postman y Commit

## PARTE 1: LEVANTAR DOCKER COMPOSE ✅

### Paso 1: Verificar que Docker está corriendo

```bash
# Verificar Docker
docker --version

# Verificar Docker Compose
docker-compose --version
```

### Paso 2: Posicionarse en la carpeta correcta

```bash
cd C:\Users\ADMIN\IdeaProjects\Reto_Plataforma-de-Pedidos-por-Microservicios
```

### Paso 3: Levantar todos los servicios

```bash
# Levantar en background
docker-compose up -d

# Esperar 30-45 segundos (tiempo de startup)
```

### Paso 4: Verificar que todo está corriendo

```bash
# Ver estado de los contenedores
docker-compose ps

# Deberías ver algo como:
# NAME                  STATUS
# auth-db               Up 20 seconds
# catalog-db            Up 20 seconds
# orders-db             Up 20 seconds
# rabbitmq              Up 20 seconds
# auth-service          Up 15 seconds
# catalog-service       Up 15 seconds
# order-service         Up 15 seconds (puede tardar más)
# api-gateway           Up 10 seconds
```

### Paso 5: Ver logs del Order Service para confirmar que inició correctamente

```bash
# Ver logs en vivo
docker-compose logs -f order-service

# Deberías ver algo como:
# order-service  | 2026-03-12 04:00:09.000 INFO ... OrderServiceApplication : Started OrderServiceApplication
# order-service  | 2026-03-12 04:00:10.000 INFO ... Tomcat started on port(s): 8083
```

---

## PARTE 2: TESTING EN POSTMAN 📮

### Paso A: Descargar Postman

Si no lo tienes:
- Descarga desde: https://www.postman.com/downloads/
- O usa la versión web: https://web.postman.co/

### Paso B: Crear una Colección de Requests

1. **Abre Postman**
2. **Clic en "+" → New Collection**
3. **Nombre:** "Order Service E-Commerce"
4. **Clic en Create**

### Paso C: Crear Environment (Variables)

1. **Clic en el ícono de engranaje (Settings) → Environments**
2. **Clic en "Create Environment"**
3. **Nombre:** "Local Docker"
4. **Agregar variables:**
   ```
   base_url    = http://localhost:8080
   order_id    = 1
   usuario_id  = 1
   ```
5. **Save**

---

## PARTE 3: LOS 4 REQUESTS EN POSTMAN

### REQUEST 1: Crear Producto en Catalog (requisito previo)

```
METHOD: POST
URL: {{base_url}}/productos
Headers:
  Content-Type: application/json

Body (JSON):
{
  "titulo": "Cien Años de Soledad",
  "autor": "Gabriel García Márquez",
  "isbn": "978-8439706755",
  "categoria": "Novela",
  "descripcion": "Una novela clásica de la literatura latinoamericana",
  "precio": 50.00,
  "stock": 10
}

Expected Response (200 OK):
{
  "id": 1,
  "titulo": "Cien Años de Soledad",
  "precio": 50.00,
  "stock": 10,
  "activo": true,
  ...
}
```

**⚠️ IMPORTANTE:** Copia el ID del producto retornado. Lo usaremos en el siguiente request.

---

### REQUEST 2: Crear un Pedido ⭐

```
METHOD: POST
URL: {{base_url}}/pedidos
Headers:
  Content-Type: application/json

Body (JSON):
{
  "usuarioId": 1,
  "items": [
    {
      "productoId": 1,
      "cantidad": 2
    }
  ]
}

Expected Response (200 OK):
{
  "id": 1,
  "usuarioId": 1,
  "estado": "PENDING",
  "total": 100.00,
  "fechaCreacion": "2026-03-12T04:00:09Z",
  "fechaActualizacion": "2026-03-12T04:00:09Z",
  "items": [
    {
      "id": 1,
      "productoId": 1,
      "productoNombre": "Cien Años de Soledad",
      "cantidad": 2,
      "precioUnitario": 50.00,
      "subtotal": 100.00
    }
  ]
}
```

**💡 TIP:** Copia el ID del pedido (ej: 1) para los siguientes requests.

---

### REQUEST 3: Ver Detalle de un Pedido

```
METHOD: GET
URL: {{base_url}}/pedidos/{{order_id}}
Headers:
  Content-Type: application/json

Body: (vacío)

Expected Response (200 OK):
Mismo formato que REQUEST 2
```

---

### REQUEST 4: Listar Pedidos del Usuario

```
METHOD: GET
URL: {{base_url}}/pedidos/usuario/{{usuario_id}}
Headers:
  Content-Type: application/json

Body: (vacío)

Expected Response (200 OK):
[
  {
    "id": 1,
    "usuarioId": 1,
    "estado": "PENDING",
    "total": 100.00,
    ...
  }
]
```

---

### REQUEST 5: Actualizar Estado de Pedido

```
METHOD: PUT
URL: {{base_url}}/pedidos/{{order_id}}
Headers:
  Content-Type: application/json

Body (JSON):
{
  "estado": "CONFIRMED"
}

Expected Response (200 OK):
{
  "id": 1,
  "usuarioId": 1,
  "estado": "CONFIRMED",
  "total": 100.00,
  ...
}
```

**Estados válidos:** PENDING, CONFIRMED, DELIVERED, CANCELLED

---

## PARTE 4: PRUEBAS CON POSTMAN (Paso a Paso)

### ✅ Test 1: Crear Producto

1. Crea un REQUEST POST a `/productos` (Catalog)
2. Envía el JSON del producto
3. Verifica respuesta 200 OK
4. **Copia el ID del producto** (ej: productoId = 1)

### ✅ Test 2: Crear Pedido

1. Crea un REQUEST POST a `/pedidos`
2. En el JSON, usa el productoId del paso anterior
3. Verifica respuesta 200 OK
4. **Copia el ID del pedido** (ej: order_id = 1)
5. Verifica que el total es correcto: 50.00 × 2 = 100.00

### ✅ Test 3: Ver Detalles del Pedido

1. Crea un REQUEST GET a `/pedidos/1`
2. Verifica que retorna el pedido con estado PENDING
3. Verifica que los items coinciden

### ✅ Test 4: Listar Pedidos

1. Crea un REQUEST GET a `/pedidos/usuario/1`
2. Verifica que retorna un array con el pedido

### ✅ Test 5: Cambiar Estado

1. Crea un REQUEST PUT a `/pedidos/1`
2. Envía: `{"estado": "CONFIRMED"}`
3. Verifica que el estado cambió a CONFIRMED

### ✅ Test 6: Intentar cambiar estado nuevamente

1. Crea otro REQUEST PUT a `/pedidos/1`
2. Envía: `{"estado": "DELIVERED"}`
3. Verifica que funciona

### ✅ Test 7: Intentar cambiar estado inválido

1. Crea otro REQUEST PUT a `/pedidos/1`
2. Envía: `{"estado": "PENDING"}`
3. **Deberías recibir error 400** (transición inválida)

---

## PARTE 5: PROBAR ERRORES (Validaciones)

### ❌ Test: Stock Insuficiente

```
REQUEST: POST /pedidos
Body:
{
  "usuarioId": 1,
  "items": [
    {
      "productoId": 1,
      "cantidad": 100  // Stock insuficiente (solo hay 10)
    }
  ]
}

Expected: Error 400
{
  "error": "Stock insuficiente",
  "mensaje": "Stock insuficiente para producto...",
  "timestamp": "2026-03-12T04:00:09Z"
}
```

### ❌ Test: Producto No Existe

```
REQUEST: POST /pedidos
Body:
{
  "usuarioId": 1,
  "items": [
    {
      "productoId": 9999,  // No existe
      "cantidad": 1
    }
  ]
}

Expected: Error 404
{
  "error": "Producto no encontrado",
  "mensaje": "...",
  "timestamp": "..."
}
```

### ❌ Test: Pedido No Existe

```
REQUEST: GET /pedidos/9999

Expected: Error 404
{
  "error": "Pedido no encontrado",
  "mensaje": "Pedido no encontrado: 9999",
  "timestamp": "..."
}
```

---

## PARTE 6: EXPORTAR COLECCIÓN POSTMAN (Opcional)

Para guardar tus requests:

1. En Postman, haz clic derecho en la colección
2. Selecciona "Export"
3. Elige formato "Collection v2.1"
4. Guarda como: `Order_Service_Tests.postman_collection.json`

Luego puedes:
- Compartirla con el equipo
- Importarla en otra máquina
- Usarla para tests automáticos

---

## PARTE 7: COMANDO ALTERNATIVO (curl)

Si prefieres usar curl en lugar de Postman:

### Crear producto:
```bash
curl -X POST http://localhost:8080/productos \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Don Quijote",
    "autor": "Cervantes",
    "isbn": "978-84-9",
    "categoria": "Novela",
    "descripcion": "El ingenioso hidalgo",
    "precio": 45.00,
    "stock": 15
  }'
```

### Crear pedido:
```bash
curl -X POST http://localhost:8080/pedidos \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 1,
    "items": [
      {"productoId": 1, "cantidad": 2}
    ]
  }'
```

### Ver pedido:
```bash
curl http://localhost:8080/pedidos/1
```

### Listar pedidos:
```bash
curl http://localhost:8080/pedidos/usuario/1
```

### Cambiar estado:
```bash
curl -X PUT http://localhost:8080/pedidos/1 \
  -H "Content-Type: application/json" \
  -d '{"estado": "CONFIRMED"}'
```

---

## PARTE 8: DETENER DOCKER

Cuando termines las pruebas:

```bash
# Detener todo
docker-compose down

# Ver logs antes de detener
docker-compose logs

# Limpiar volúmenes (cuidado: borra datos)
docker-compose down -v
```

---

## PARTE 9: HACER EL COMMIT EN TU RAMA

### Paso 1: Ver el estado actual

```bash
git status
```

Deberías ver archivos nuevos en el Order Service

### Paso 2: Crear una rama local (si aún no existe)

```bash
# Ver ramas locales
git branch

# Crear una rama para tu trabajo
git checkout -b feature/order-service

# O si ya existe, simplemente cambiar a ella
git checkout feature/order-service
```

### Paso 3: Agregar los cambios

```bash
# Agregar todos los cambios
git add .

# O agregar solo Order Service
git add services/order-service/
git add GUIA_ORDER_SERVICE.md
git add ARQUITECTURA_ORDER_SERVICE.md
git add RESUMEN_ORDER_SERVICE.md
git add DIAGRAMAS_VISUALES.md
git add QUICKSTART.md
git add README_ORDER_SERVICE.md
git add TESTING_CURL_EJEMPLOS.sh
git add COMPLETADO.txt
```

### Paso 4: Ver qué se va a commitear

```bash
git status
```

### Paso 5: Hacer el commit

```bash
git commit -m "feat: Implementar Order Service completo

- Crear OrderEntity y OrderItemEntity con relación 1:N
- Implementar OrderService con CRUD y validaciones
- Crear OrderController con 4 endpoints REST
- Integrar con Catalog Service via RestTemplate
- Implementar GlobalExceptionHandler
- Agregar validaciones de stock y disponibilidad
- Crear DTOs para request/response
- Documentación completa (6 documentos)
- Listo para producción

Co-authored-by: Copilot <223556219+Copilot@users.noreply.github.com>"
```

### Paso 6: Verificar que el commit se creó

```bash
git log --oneline -5
```

Deberías ver tu commit en la lista

### Paso 7: Ver la rama actual

```bash
# Ver rama actual
git branch

# Deberías ver:
# * feature/order-service  (el * indica tu rama actual)
#   main
```

---

## PARTE 10: VERIFICAR EL COMMIT

```bash
# Ver detalles del commit
git show HEAD

# O más compacto
git log -1 --stat
```

---

## 🎯 RESUMEN DE TODO

### ✅ Levantar Docker:
```bash
cd C:\Users\ADMIN\IdeaProjects\Reto_Plataforma-de-Pedidos-por-Microservicios
docker-compose up -d
# Esperar 45 segundos
docker-compose ps
```

### ✅ Testing en Postman:
1. Crear producto en Catalog
2. Crear pedido en Order Service
3. Ver detalles del pedido
4. Listar pedidos del usuario
5. Cambiar estado
6. Probar errores

### ✅ Commit en tu rama:
```bash
git status
git checkout -b feature/order-service
git add .
git commit -m "feat: Implementar Order Service..."
git log --oneline -5
```

---

## 📊 Checklist Final

- [ ] Docker está instalado
- [ ] docker-compose ps muestra 8 contenedores UP
- [ ] Esperar 45 segundos para startup completo
- [ ] Postman instalado
- [ ] Crear variable de entorno base_url = http://localhost:8080
- [ ] Crear producto en Catalog
- [ ] Crear pedido en Order Service
- [ ] Ver detalles del pedido
- [ ] Cambiar estado del pedido
- [ ] Probar errores (stock, producto no existe)
- [ ] Hacer commit en rama feature/order-service
- [ ] Verificar que el commit aparece en git log

---

**¡Listo! Sigue estos pasos y tendrás todo testeado y comiteado en tu rama.** ✅
