# Frontend MVC

Este módulo implementa el flujo de integración descrito en la guía con Spring MVC y Thymeleaf:

1. El formulario envía el usuario y la contraseña a Spring Security.
2. `CustomAuthenticationProvider` envía las credenciales como formulario al `POST /login` del backend.
3. `TokenStore` conserva el JWT en la sesión HTTP del usuario; no se guarda en una variable compartida ni se expone a la vista.
4. La ruta protegida `/greetings` llama a `GET /api/private/greetings` con `Authorization: Bearer <JWT>`.
5. El usuario puede cerrar sesión para invalidar la sesión del frontend.

## Ejecutar localmente

Se requiere Java 21 y Maven. Desde la raíz del repositorio:

1. Inicia MySQL con `docker compose up -d`.
2. Inicia el backend: `cd backend` y `.\mvnw.cmd spring-boot:run`.
3. En otra terminal, inicia el frontend desde la raíz con `mvn -f frontend\pom.xml spring-boot:run`.
4. Abre `http://localhost:8080`. El backend debe estar disponible en `http://localhost:8081`.

El backend de esta guía crea el usuario local `admin` con contraseña `admin`; cámbialo antes de usar el sistema fuera del entorno de práctica.

Puedes cambiar la URL del backend con la variable de entorno `BACKEND_BASE_URL`.
