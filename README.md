# FinGoal - Aplicación Multiplataforma de Finanzas Personales 📱🎯

**FinGoal** es una aplicación de finanzas personales desarrollada con **Kotlin Multiplatform (KMP)** y **Compose Multiplatform**, compatible con **Android e iOS**.

Permite administrar ingresos y gastos, establecer metas de ahorro, desarrollar hábitos financieros y consultar estadísticas mediante un asistente financiero integrado.

El proyecto utiliza **Clean Architecture + MVVM**, con persistencia local mediante **Room Multiplatform** y comunicación con un backend mediante **Ktor Client**.

---

## 🔐 Usuario de prueba

Podés utilizar la siguiente cuenta para probar la aplicación:

```text
Correo: fingoaldemo@gmail.com
Contraseña: 1234567demo
```

> Cuenta destinada exclusivamente a demostración.

---

## ✨ Funcionalidades

### 💰 Transacciones

- Registro de ingresos y gastos.
- Edición y eliminación.
- Categorías y fechas.
- Resumen de ingresos y gastos.
- Estadísticas del mes.
- Cálculo de gasto diario disponible.
- Persistencia local y sincronización con el backend.

### 🎯 Metas de ahorro

- Creación y edición de metas.
- Monto objetivo y acumulado.
- Prioridades.
- Agregar y retirar dinero.
- Cálculo de progreso.
- Metas completadas y pendientes.
- Identificación de metas más avanzadas y prioritarias.

### 🔁 Hábitos financieros

- Creación y edición de hábitos.
- Frecuencia diaria, semanal y mensual.
- Cumplimiento diario.
- Sistema de rachas.
- Hábitos pendientes y completados.
- Estadísticas de progreso.

### 🤖 Asistente financiero

FinGoal incorpora un asistente financiero con cuatro personajes:

**Grenny 🟦 · Rosy 🟩 · Sun 🟧 · Gooli 🟪**

El asistente permite consultar información de la aplicación mediante preguntas como:

```text
¿Cuánto gasté?
¿Cuánto dinero me queda este mes?
¿Cuánto puedo gastar por día?
¿Cuál es mi mejor racha?
¿Qué hábito debería completar hoy?
¿Cuánto dinero tengo ahorrado?
¿Cuál es mi meta más avanzada?
¿En qué meta debería concentrarme?
```

Las respuestas utilizan los datos actuales de la aplicación.

---

# 🏛️ Arquitectura

FinGoal utiliza **Clean Architecture** y **MVVM**.

```text
Compose Multiplatform
        ↓
    ViewModels
        ↓
      UseCases
        ↓
    Repositories
        ↓
 ┌──────┴────────┐
 ▼               ▼
Room KMP      Ktor Client
 ▼               ▼
Datos locales   REST API
                    ↓
              Node + Express
                    ↓
            Firebase Firestore
```

### Capas principales

```text
Presentation
├── Compose UI
├── Screens
├── Components
└── ViewModels

Domain
├── Models
├── UseCases
└── Repository Interfaces

Data
├── Room KMP
├── DAOs
├── Entities
├── Ktor Client
├── DTOs
├── Mappers
└── Repositories
```

---

# 📱 Multiplataforma

Targets actuales:

```text
Android
iOS Arm64
iOS Simulator Arm64
```

La mayor parte de la lógica se comparte mediante Kotlin Multiplatform.

Se comparten:

- Modelos.
- UseCases.
- Repositories.
- ViewModels.
- Persistencia.
- Comunicación con API.
- Componentes Compose.
- Lógica de negocio.

---

# 🗄️ Persistencia local

FinGoal utiliza **Room Multiplatform (Room KMP)**.

Entidades principales:

```text
TransactionEntity
GoalEntity
HabitEntity
```

DAOs:

```text
TransactionDao
GoalDao
HabitDao
```

Los datos se exponen mediante `Flow`, permitiendo que la interfaz reaccione automáticamente ante cambios.

---

# 🌐 Backend

La aplicación se comunica con una API REST desarrollada con:

```text
Node.js
Express
Firebase Cloud Firestore
```

La aplicación móvil utiliza **Ktor Client / HttpClient** para comunicarse con la API.

Repositorio del backend:

```text
https://github.com/Yair2332/fingoal-api
```

### API de producción

```text
https://fingoal-api-production.up.railway.app/
```

### API local — Android Emulator

```text
http://10.0.2.2:3000/
```

---

# 🔄 Sincronización

FinGoal utiliza un enfoque orientado a **Offline-First**.

La información local se gestiona mediante Room KMP y la aplicación puede sincronizar los datos con el backend.

```text
UI
 ↓
ViewModel
 ↓
UseCase
 ↓
Repository
 ├── Room KMP
 └── Ktor Client
        ↓
     REST API
```

---

# 🧪 Testing

El proyecto cuenta con pruebas para:

### ViewModels

```text
AuthViewModelTest
TransactionViewModelTest
HabitViewModelTest
GoalViewModelTest
DashboardViewModelTest
```

### UseCases

```text
AuthTest
TransactionsTest
HabitsTest
GoalsTest
DashboardTest
```

### Room / DAOs

```text
TransactionDaoTest
GoalDaoTest
HabitDaoTest
```

Los tests compartidos se encuentran en:

```text
sharedLogic/src/commonTest/
```

Para ejecutarlos:

```powershell
.\gradlew :sharedLogic:allTests
```

---

# 🛠️ Tecnologías

| Tecnología | Uso |
|---|---|
| Kotlin | Lenguaje principal |
| Kotlin Multiplatform | Desarrollo Android/iOS |
| Compose Multiplatform | UI multiplataforma |
| Material 3 | Diseño de interfaz |
| Room Multiplatform | Persistencia local |
| Ktor Client | Comunicación con API |
| Kotlin Coroutines | Programación asíncrona |
| Flow / StateFlow | Estado reactivo |
| Koin | Inyección de dependencias |
| MVVM | Arquitectura |
| Clean Architecture | Organización del proyecto |
| Node.js | Backend |
| Express | API REST |
| Firebase Firestore | Persistencia remota |

---

# 📁 Estructura

```text
FinGoal/
│
├── androidApp/
│
├── sharedLogic/
│   └── src/
│       ├── commonMain/
│       │   ├── data/
│       │   ├── domain/
│       │   └── ui/
│       ├── androidMain/
│       ├── iosMain/
│       └── commonTest/
│
└── backend/
    └── fingoal-api
```

---

# 🎨 Identidad visual

FinGoal cuenta con una identidad visual propia y cuatro personajes utilizados dentro del asistente financiero:

**Grenny · Rosy · Sun · Gooli**

La aplicación también cuenta con:

- Tema claro y oscuro.
- Componentes Compose reutilizables.
- Iconografía propia.
- Interfaz adaptada al concepto de finanzas personales.

---

# 🤖 Desarrollo

Durante el desarrollo se utilizaron herramientas de Inteligencia Artificial como apoyo para:

- Resolución de errores.
- Refactorización.
- Arquitectura.
- Implementación de funcionalidades.
- Tests.
- Migración a Kotlin Multiplatform.
- Documentación.

La integración y validación del código forman parte del proceso de desarrollo del proyecto.

---

# 🚀 Objetivos

FinGoal busca combinar **finanzas personales, hábitos y objetivos de ahorro** en una única aplicación multiplataforma.

El proyecto tiene como objetivos principales:

- Facilitar el control de ingresos y gastos.
- Ayudar a desarrollar hábitos financieros.
- Permitir establecer objetivos de ahorro.
- Mantener información disponible localmente.
- Sincronizar información con un backend.
- Compartir lógica entre Android e iOS.
- Mantener una arquitectura escalable.

---

## 👨‍💻 Autor

**Yair Lezcano**

Desarrollador Mobile especializado en Kotlin, Android, Compose y tecnologías multiplataforma.

---

> **FinGoal** — Organizá tus finanzas, desarrollá mejores hábitos y alcanzá tus objetivos. 🎯

Desarrollado con **Kotlin Multiplatform, Compose Multiplatform, Room KMP, Ktor y Node.js**. ❤️
