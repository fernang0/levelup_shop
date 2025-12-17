# LevelUp Shop - API Backend

API REST para tienda online con Spring Boot, MariaDB y Transbank.

## 🚀 Endpoints Principales

### 🔐 Autenticación (`/api/v1/auth`)

| Método | Endpoint | Descripción | Requiere Auth |
|--------|----------|-------------|---------------|
| POST | `/register` | Registrar nuevo usuario | ❌ |
| POST | `/login` | Iniciar sesión | ❌ |
| GET | `/me` | Obtener usuario actual | ✅ |

**Ejemplo POST `/auth/register`:**
```json
{
  "nombre": "Juan Pérez",
  "email": "juan@ejemplo.com",
  "password": "password123",
  "telefono": "+56912345678",
  "direccion": "Av. Ejemplo 123"
}
```

**Ejemplo POST `/auth/login`:**
```json
{
  "email": "juan@ejemplo.com",
  "password": "password123"
}
```

---

### 📦 Productos (`/api/v1/productos`)

| Método | Endpoint | Descripción | Requiere Auth |
|--------|----------|-------------|---------------|
| GET | `/` | Listar productos activos | ❌ |
| GET | `/?categoria={code}` | Filtrar por categoría | ❌ |
| GET | `/?search={keyword}` | Buscar productos | ❌ |
| GET | `/{id}` | Obtener producto por ID | ❌ |
| GET | `/codigo/{code}` | Obtener por código (ej: JM001) | ❌ |
| GET | `/categoria/{categoriaCode}` | Filtrar por categoría (AC, CG, CO, JM, MP, MS, PP, SG) | ❌ |

---

### 🗂️ Categorías (`/api/v1/categorias`)

| Método | Endpoint | Descripción | Requiere Auth |
|--------|----------|-------------|---------------|
| GET | `/` | Listar todas las categorías | ❌ |
| GET | `/{id}` | Obtener categoría por ID | ❌ |

---

### 🛒 Carrito (`/api/v1/carrito`)

| Método | Endpoint | Descripción | Requiere Auth |
|--------|----------|-------------|---------------|
| GET | `/` | Obtener carrito activo | ✅ |
| POST | `/items` | Agregar producto al carrito | ✅ |
| PUT | `/items/{itemId}?cantidad={n}` | Actualizar cantidad | ✅ |
| DELETE | `/items/{itemId}` | Eliminar item del carrito | ✅ |
| DELETE | `/` | Vaciar carrito | ✅ |

**Ejemplo POST `/carrito/items`:**
```json
{
  "productoId": 1,
  "cantidad": 2
}
```

---

### 📋 Pedidos (`/api/v1/pedidos`)

| Método | Endpoint | Descripción | Requiere Auth |
|--------|----------|-------------|---------------|
| GET | `/` | Listar pedidos del usuario | ✅ |
| GET | `/{id}` | Obtener detalle de pedido | ✅ |
| POST | `/` | Crear pedido desde carrito | ✅ |

**Ejemplo POST `/pedidos`:**
```json
{
  "direccionEnvio": "Av. Ejemplo 123, Santiago"
}
```

---

### 💳 Pagos (`/api/v1/pagos`)

| Método | Endpoint | Descripción | Requiere Auth |
|--------|----------|-------------|---------------|
| POST | `/iniciar` | Iniciar transacción Transbank | ✅ |
| POST/GET | `/confirmar?token_ws={token}` | Callback de Transbank (automático) | ❌ |
| GET | `/estado/{token}` | Consultar estado de transacción | ✅ |

**Ejemplo POST `/pagos/iniciar`:**
```json
{
  "pedidoId": 1
}
```

**Respuesta:**
```json
{
  "token": "01ab57abbd6106de2bd64ca88eb35e1dee85f03f...",
  "url": "https://webpay3gint.transbank.cl/webpayserver/initTransaction"
}
```

---

## 🔑 Autenticación

Los endpoints marcados con ✅ requieren JWT Bearer token en el header:

```
Authorization: Bearer {tu_token_jwt}
```

El token se obtiene al hacer login o registro exitoso.

---

## ⚙️ Configuración

### Variables de Entorno

| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `SPRING_DATASOURCE_URL` | URL de base de datos | `jdbc:mariadb://localhost:3306/levelup_shop` |
| `SPRING_DATASOURCE_USERNAME` | Usuario DB | `root` |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña DB | - |
| `JWT_SECRET` | Secret para JWT | - |
| `JWT_EXPIRATION` | Expiración token (ms) | `86400000` |
| `TRANSBANK_ENVIRONMENT` | Ambiente Transbank | `INTEGRACION` |
| `TRANSBANK_COMMERCE_CODE` | Código de comercio | - |
| `TRANSBANK_API_KEY` | API Key Transbank | - |
| `TRANSBANK_RETURN_URL` | URL de retorno post-pago | `http://ec2-44-200-28-175.compute-1.amazonaws.com:8080/api/v1/pagos/confirmar` |

---

## 📚 Documentación Swagger

Accede a la documentación interactiva en:

```
http://localhost:8080/swagger-ui.html
```
