# LevelUp Shop - API Backend

API REST para tienda online con Spring Boot, MariaDB y Transbank.

## Requisitos

- Java 17+
- MariaDB 12+
- Gradle 9+

## Configuración

1. Copia el archivo de ejemplo de variables de entorno:
```bash
cp .env.example .env
```

2. Edita `.env` con tus credenciales:
```env
DB_PASSWORD=tu_password
JWT_SECRET=tu_secret_key_muy_larga_y_segura
TRANSBANK_COMMERCE_CODE=tu_codigo
TRANSBANK_API_KEY=tu_api_key
```

3. Crea la base de datos:
```sql
CREATE DATABASE levelup_shop;
```

4. Ejecuta el script SQL de inicialización (si existe)

## Variables de Entorno Requeridas

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `DB_URL` | URL de la base de datos | `jdbc:mariadb://localhost:3306/levelup_shop` |
| `DB_USERNAME` | Usuario de la base de datos | `root` |
| `DB_PASSWORD` | **Contraseña de la base de datos** | - |
| `JWT_SECRET` | **Secreto para firmar JWT tokens** | - |
| `JWT_EXPIRATION` | Tiempo de expiración del token (ms) | `86400000` |
| `TRANSBANK_ENVIRONMENT` | Ambiente Transbank | `INTEGRACION` o `PRODUCCION` |
| `TRANSBANK_COMMERCE_CODE` | **Código de comercio Transbank** | - |
| `TRANSBANK_API_KEY` | **API Key de Transbank** | - |
| `TRANSBANK_RETURN_URL` | URL de retorno después del pago | `http://localhost:8080/api/v1/pagos/confirmar` |

## Ejecutar

```bash
./gradlew bootRun
```

La aplicación estará disponible en: http://localhost:8080

## Documentación API

Swagger UI: http://localhost:8080/swagger-ui.html

## Endpoints Principales

- `POST /api/v1/auth/register` - Registro de usuario
- `POST /api/v1/auth/login` - Login
- `GET /api/v1/productos` - Listar productos
- `POST /api/v1/carrito/items` - Agregar al carrito
- `POST /api/v1/pedidos/desde-carrito` - Crear pedido
- `POST /api/v1/pagos/iniciar` - Iniciar pago Transbank

## Seguridad

⚠️ **NUNCA subas el archivo `.env` a Git**

El archivo `.gitignore` ya está configurado para ignorar:
- `.env`
- `application-local.properties`
- Credenciales sensibles
# levelup_shop
