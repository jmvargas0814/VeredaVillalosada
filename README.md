# 🏛 Sistema Administrativo – Vereda Villalosada

Sistema web administrativo desarrollado con **Spring Boot** para la gestión de socios y control de pagos mensuales en la Vereda Villalosada.

---

# 📌 Descripción General

La aplicación permite:

- ✅ Gestión de usuarios (socios)
- ✅ Generación automática de código de usuario
- ✅ Creación automática de credenciales
- ✅ Control de estado (Activo / Inactivo)
- ✅ Registro y control de pagos mensuales
- ✅ Exportación de reportes en Excel
- ✅ Seguridad basada en roles (ADMIN / USER)

---

# 🏗 Arquitectura

La aplicación sigue una:

## ✅ Arquitectura en Capas (Layered Architecture)


Y utiliza el patrón:

## ✅ MVC (Modelo – Vista – Controlador)

- **Modelo** → Entidades JPA
- **Vista** → Thymeleaf (HTML server-side rendering)
- **Controlador** → Clases Spring MVC

---

# 🧱 Estructura del Proyecto


---

# 🔐 Seguridad

Implementada con **Spring Security**:

- Login personalizado
- Encriptación de contraseñas con BCrypt
- Roles:
    - ROLE_ADMIN
    - ROLE_USER
- Restricción por rutas
- Control de sesión única
- Logout seguro

---

# 🗄 Base de Datos

## Modelo Relacional

### 1️⃣ Tabla: roles

CREATE TABLE roles (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
nombre VARCHAR(50) NOT NULL UNIQUE,
descripcion VARCHAR(255)
);

---

### 2️⃣ Tabla: usuarios

CREATE TABLE usuarios (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
codigo_usuario BIGINT,
numero_identificacion VARCHAR(20) NOT NULL UNIQUE,
nombres VARCHAR(100) NOT NULL,
apellidos VARCHAR(100) NOT NULL,
correo VARCHAR(150) NOT NULL UNIQUE,
telefono VARCHAR(20),
fecha_nacimiento DATE,
fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
estado BOOLEAN DEFAULT TRUE,
rol_id BIGINT NOT NULL,

    CONSTRAINT fk_usuario_rol
        FOREIGN KEY (rol_id)
        REFERENCES roles(id)
);

---

### 3️⃣ Tabla: credenciales

CREATE TABLE credenciales (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
usuario_id BIGINT NOT NULL UNIQUE,
username VARCHAR(50) NOT NULL UNIQUE,
password_hash VARCHAR(255) NOT NULL,
fecha_actualizacion_password DATETIME,
CONSTRAINT fk_credencial_usuario
FOREIGN KEY (usuario_id)
REFERENCES usuarios(id)
ON DELETE CASCADE
);

---

### 4️⃣ Tabla: pagos_mensuales
CREATE TABLE pagos_mensuales (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
anio INT NOT NULL,
mes INT NOT NULL,
pagado BOOLEAN DEFAULT FALSE,
descripcion VARCHAR(255),
usuario_id BIGINT,
CONSTRAINT fk_pago_usuario
FOREIGN KEY (usuario_id)
REFERENCES usuarios(id)
ON DELETE CASCADE
);

---
INSERT INTO roles (nombre, descripcion) VALUES ('ADMIN', 'Administrador del sistema');
INSERT INTO roles (nombre, descripcion) VALUES ('USER', 'Usuario de la aplicacion');


INSERT INTO usuarios (
codigo_usuario,
numero_identificacion,
nombres,
apellidos,
correo,
telefono,
fecha_nacimiento,
estado,
rol_id
) VALUES (
'000001',
'1081407288',
'Juan Manuel',
'Vargas Liz',
'juanvargas9010@gmail.com',
'3219055677',
'1990-10-01',
true,
1
);

INSERT INTO credenciales (
usuario_id,
username,
password_hash,
fecha_actualizacion_password
) VALUES (
1,
'admin',
'$2a$10$HsrXFzwX.JFJDAqCVfNLxeB4C4GSATNFXPc1p/IryCi7ZYZNCuq76',//123456
NOW()
);


# 🔄 Flujo de Registro de Usuario

1. Administrador crea usuario.
2. Se asigna automáticamente:
    - Código incremental.
    - Rol USER.
    - Estado Activo.
3. Se generan credenciales:
    - Username: nombre.apellido
    - Password: número de documento (encriptado)
4. Se crean automáticamente registros de pagos desde el mes de registro hasta diciembre.

---

# 💰 Gestión de Pagos

- Visualización por modal.
- Toggle manual (ADMIN).
- Pago formal por selección de mes.
- Estado visual:
    - 🟢 PAGADO
    - 🔴 DEBE

---

# 📊 Reportes Excel

## Reporte Individual
- Información del usuario
- Estado mensual del año actual

## Reporte General
Incluye:
- Código
- Nombre completo
- Número de cédula
- Estado
- Meses transcurridos hasta la fecha actual
- Estado de pago por mes

Los meses se generan dinámicamente según:
- Año actual
- Fecha de registro del usuario

---

# ⚙ Tecnologías Utilizadas

- Java 17+
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- Thymeleaf
- Apache POI (Excel)
- Gradle
- Base de datos relacional (MySQL / H2)

---

# 🚀 Ejecución del Proyecto

Desde la raíz del proyecto:

