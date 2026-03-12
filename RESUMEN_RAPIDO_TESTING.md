# ⚡ RESUMEN RÁPIDO: Testing + Commit (5 minutos)

## 🚀 PARTE 1: LEVANTAR DOCKER (2 minutos)

```bash
# 1. Abre PowerShell
# 2. Ve a la carpeta
cd C:\Users\ADMIN\IdeaProjects\Reto_Plataforma-de-Pedidos-por-Microservicios

# 3. Levanta todo
docker-compose up -d

# 4. ESPERA 45 SEGUNDOS

# 5. Verifica
docker-compose ps

# Si ves 8 contenedores "Up" → ✅ LISTO
```

---

## 📮 PARTE 2: POSTMAN (2 minutos)

### Abre Postman y haz estos 5 requests:

**1️⃣ POST /productos**
```json
{
  "titulo": "Cien Años de Soledad",
  "autor": "García Márquez",
  "isbn": "978-84",
  "categoria": "Novela",
  "descripcion": "Clásico",
  "precio": 50.00,
  "stock": 10
}
```
✅ Response 200 → Copia el ID (ej: 1)

**2️⃣ POST /pedidos** ⭐
```json
{
  "usuarioId": 1,
  "items": [
    {"productoId": 1, "cantidad": 2}
  ]
}
```
✅ Response 200 con total: 100.00

**3️⃣ GET /pedidos/1**
```
Sin body
```
✅ Response 200

**4️⃣ GET /pedidos/usuario/1**
```
Sin body
```
✅ Response 200 (array)

**5️⃣ PUT /pedidos/1**
```json
{
  "estado": "CONFIRMED"
}
```
✅ Response 200 con estado CONFIRMED

---

## 🔀 PARTE 3: COMMIT EN GIT (1 minuto)

```bash
# 1. Ver cambios
git status

# 2. Crear rama (o cambiar a ella)
git checkout -b feature/order-service

# 3. Agregar cambios
git add .

# 4. Hacer commit
git commit -m "feat: Implementar Order Service completo

- OrderEntity y OrderItemEntity
- OrderService con CRUD
- 4 endpoints REST
- Integración Catalog Service
- Manejo de errores
- DTOs completos
- Documentación

Co-authored-by: Copilot <223556219+Copilot@users.noreply.github.com>"

# 5. Verificar
git log --oneline -5
git branch

# ✅ Deberías ver tu rama "feature/order-service" con *
```

---

## ✅ CHECKLIST

- [ ] Docker: 8 contenedores UP
- [ ] Postman: 5 requests todos 200 OK
- [ ] Errores: Probaste stock insuficiente (400)
- [ ] Git: Commit hecho en rama feature/order-service

---

## 🎉 ¡LISTO!

Tu Order Service está:
✅ Corriendo en Docker
✅ Probado en Postman
✅ Guardado en Git

**Próximo paso:** Si quieres mergear a main, haz un Pull Request.
