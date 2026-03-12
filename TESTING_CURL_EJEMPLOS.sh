#!/bin/bash
# 📝 EJEMPLOS DE CURL - Order Service Testing
# Ejecutar estos comandos para probar todos los endpoints

echo "================================"
echo "🧪 ORDER SERVICE - TESTING GUIDE"
echo "================================"
echo ""

# ========== CREAR PEDIDO ==========
echo "1️⃣ CREAR PEDIDO"
echo "Command:"
echo 'curl -X POST http://localhost:8080/pedidos \'
echo '  -H "Content-Type: application/json" \'
echo "  -d '{'"
echo '    "usuarioId": 1,'
echo '    "items": ['
echo '      {"productoId": 5, "cantidad": 2},'
echo '      {"productoId": 3, "cantidad": 1}'
echo '    ]'
echo '  }'"
echo ""
echo "Expected Response (201 OK):"
echo '{'
echo '  "id": 1,'
echo '  "usuarioId": 1,'
echo '  "estado": "PENDING",'
echo '  "total": 150.50,'
echo '  "fechaCreacion": "2026-03-12T03:23:47Z",'
echo '  "fechaActualizacion": "2026-03-12T03:23:47Z",'
echo '  "items": ['
echo '    {'
echo '      "id": 1,'
echo '      "productoId": 5,'
echo '      "productoNombre": "Cien Años de Soledad",'
echo '      "cantidad": 2,'
echo '      "precioUnitario": 50.00,'
echo '      "subtotal": 100.00'
echo '    },'
echo '    {'
echo '      "id": 2,'
echo '      "productoId": 3,'
echo '      "productoNombre": "Don Quijote",'
echo '      "cantidad": 1,'
echo '      "precioUnitario": 50.50,'
echo '      "subtotal": 50.50'
echo '    }'
echo '  ]'
echo '}'
echo ""
echo "-------------------------------------------"
echo ""

# ========== OBTENER PEDIDO ==========
echo "2️⃣ OBTENER DETALLE DE UN PEDIDO"
echo "Command:"
echo "curl http://localhost:8080/pedidos/1"
echo ""
echo "Expected Response (200 OK):"
echo "Mismo formato que Crear Pedido"
echo ""
echo "-------------------------------------------"
echo ""

# ========== LISTAR PEDIDOS DEL USUARIO ==========
echo "3️⃣ LISTAR TODOS LOS PEDIDOS DEL USUARIO"
echo "Command:"
echo "curl http://localhost:8080/pedidos/usuario/1"
echo ""
echo "Expected Response (200 OK):"
echo '['
echo '  {'
echo '    "id": 1,'
echo '    "usuarioId": 1,'
echo '    "estado": "PENDING",'
echo '    "total": 150.50,'
echo '    ...'
echo '  },'
echo '  {'
echo '    "id": 2,'
echo '    "usuarioId": 1,'
echo '    "estado": "CONFIRMED",'
echo '    "total": 75.25,'
echo '    ...'
echo '  }'
echo ']'
echo ""
echo "-------------------------------------------"
echo ""

# ========== ACTUALIZAR ESTADO ==========
echo "4️⃣ ACTUALIZAR ESTADO DE PEDIDO"
echo "Command:"
echo "curl -X PUT http://localhost:8080/pedidos/1 \\"
echo "  -H \"Content-Type: application/json\" \\"
echo "  -d '{\"estado\": \"CONFIRMED\"}'"
echo ""
echo "Expected Response (200 OK):"
echo "Mismo formato que Crear Pedido, pero con estado: CONFIRMED"
echo ""
echo "-------------------------------------------"
echo ""

# ========== ERRORES ==========
echo "⚠️ EJEMPLOS DE ERRORES"
echo ""
echo "a) Stock Insuficiente (400 Bad Request):"
echo '{"error": "Stock insuficiente", ...}'
echo ""
echo "b) Producto No Encontrado (404 Not Found):"
echo '{"error": "Producto no encontrado", ...}'
echo ""
echo "c) Pedido No Encontrado (404 Not Found):"
echo '{"error": "Pedido no encontrado", ...}'
echo ""
echo "d) Transición de Estado Inválida (400 Bad Request):"
echo '{"error": "...", "mensaje": "No se puede cambiar un pedido entregado", ...}'
echo ""
echo "-------------------------------------------"
echo ""

# ========== TRANSICIONES DE ESTADO ==========
echo "🔄 TRANSICIONES DE ESTADO VÁLIDAS"
echo ""
echo "PENDING ──→ CONFIRMED ──→ DELIVERED (final)"
echo "   ↓"
echo "CANCELLED (final)"
echo ""
echo "Ejemplos válidos:"
echo "  PENDING → CONFIRMED  ✅"
echo "  PENDING → CANCELLED  ✅"
echo "  CONFIRMED → DELIVERED  ✅"
echo "  CONFIRMED → CANCELLED  ✅"
echo ""
echo "Ejemplos inválidos:"
echo "  DELIVERED → CONFIRMED  ❌ (error)"
echo "  CANCELLED → PENDING  ❌ (error)"
echo "  PENDING → PENDING  ❌ (ya está en ese estado)"
echo ""
echo "-------------------------------------------"
echo ""

# ========== VALIDACIONES ==========
echo "✅ VALIDACIONES IMPLEMENTADAS"
echo ""
echo "Al crear pedido:"
echo "  ✓ usuarioId debe ser > 0"
echo "  ✓ items no puede estar vacío"
echo "  ✓ cantidad de cada item > 0"
echo "  ✓ Producto debe existir en Catalog"
echo "  ✓ Producto debe estar activo"
echo "  ✓ Stock >= cantidad requerida"
echo ""
echo "-------------------------------------------"
echo ""

# ========== NOTAS ==========
echo "📝 NOTAS IMPORTANTES"
echo ""
echo "1. Reemplaza las URLs según tu configuración:"
echo "   - Desarrollo local: http://localhost:8080"
echo "   - Docker Compose: http://api-gateway:8080"
echo "   - Producción: tu-dominio.com"
echo ""
echo "2. Headers necesarios:"
echo "   - Content-Type: application/json (para POST/PUT)"
echo "   - Accept: application/json (para GET)"
echo ""
echo "3. El servicio integrará automáticamente con Catalog Service"
echo "   (Si Catalog no está disponible, recibirás error 500)"
echo ""
echo "4. Todos los precios se guardan al momento de la compra"
echo "   (Cambios futuros en Catalog NO afectan órdenes existentes)"
echo ""
echo "5. El stock se valida pero NO se descuenta"
echo "   (El Catalog Service mantiene su propia BD)"
echo ""
echo "================================"
echo "✅ Testing completado"
echo "================================"
