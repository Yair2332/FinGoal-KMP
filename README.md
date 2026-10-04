# FinGoal - Aplicación Multiplataforma de Finanzas Personales 📱🎯

**FinGoal** es una aplicación de finanzas personales desarrollada con **Kotlin Multiplatform y Compose Multiplatform**, diseñada para ayudar a los usuarios a administrar sus ingresos y gastos, desarrollar hábitos financieros y alcanzar objetivos de ahorro.

El proyecto utiliza una arquitectura moderna basada en **Clean Architecture**, separando la lógica de negocio, acceso a datos y presentación para facilitar el mantenimiento, las pruebas y la evolución de la aplicación.

FinGoal cuenta con soporte para **Android e iOS**, compartiendo la mayor parte de la lógica de negocio, modelos, casos de uso, persistencia y componentes de la aplicación entre ambas plataformas.

---

# 🏛️ Arquitectura

El proyecto está organizado siguiendo los principios de **Clean Architecture**, separando las responsabilidades en diferentes capas:

```text
┌─────────────────────────────────────┐
│             Presentation            │
│                                     │
│  Compose Multiplatform              │
│  Screens                            │
│  Components                         │
│  ViewModels                         │
└─────────────────┬───────────────────┘
                  │
                  ▼
┌─────────────────────────────────────┐
│               Domain                │
│                                     │
│  Models                             │
│  UseCases                           │
│  Repository Interfaces              │
│  Business Logic                     │
└─────────────────┬───────────────────┘
                  │
                  ▼
┌─────────────────────────────────────┐
│                Data                 │
│                                     │
│  Repositories                       │
│  Room Database                      │
│  DAOs                               │
│  Entities                           │
│  Retrofit / API                     │
│  DTOs                               │
│  Mappers                            │
└─────────────────────────────────────┘
```

La aplicación utiliza un enfoque **reactivo y offline-first**, donde la información almacenada localmente puede alimentar la interfaz mediante `Flow`, mientras que la sincronización con el servidor mantiene los datos actualizados.

---

# 📱 Plataformas

FinGoal utiliza **Kotlin Multiplatform (KMP)** para compartir la lógica entre plataformas.

### Android

Aplicación Android desarrollada utilizando:

- Kotlin
- Jetpack Compose / Compose Multiplatform
- Android SDK
- Material 3
- Room
- Retrofit
- Kotlin Coroutines
- Flow
- Hilt
- Clean Architecture
- MVVM

### iOS

La aplicación también cuenta con un target de **iOS**, compartiendo la lógica desarrollada en el módulo multiplataforma.

Targets actuales del proyecto:

```text
Android
iOS Arm64
iOS Simulator Arm64
```

Esto permite mantener una única base de código para gran parte de la lógica de la aplicación y reducir la duplicación entre plataformas.

---

# 🎨 UI y Compose Multiplatform

La interfaz está desarrollada utilizando **Compose Multiplatform**, utilizando componentes declarativos y reactivos.

Entre los elementos implementados se encuentran:

- Pantalla de autenticación.
- Dashboard financiero.
- Gestión de transacciones.
- Gestión de hábitos.
- Gestión de metas de ahorro.
- Formularios para crear y editar información.
- Componentes reutilizables.
- Bottom Sheets.
- Cards.
- Componentes personalizados.
- Estados de carga y error.
- Soporte para Light/Dark Theme mediante Material 3.

La interfaz utiliza componentes reutilizables para mantener una estructura consistente entre las diferentes pantallas.

---

# 🧠 ViewModels

Cada sección principal de la aplicación cuenta con su propio `ViewModel`, encargado de manejar el estado de la interfaz y coordinar los casos de uso.

Actualmente se utilizan ViewModels para:

- Autenticación.
- Transacciones.
- Hábitos.
- Metas.
- Dashboard.

Los ViewModels utilizan `StateFlow` y `MutableStateFlow` para representar el estado de la interfaz de forma reactiva.

Ejemplo conceptual:

```text
UseCase
   ↓
ViewModel
   ↓
StateFlow
   ↓
Compose UI
```

Cuando los datos cambian, el estado se actualiza y Compose recompone automáticamente los componentes correspondientes.

---

# 💰 Gestión de Transacciones

FinGoal permite administrar los movimientos financieros del usuario.

Cada transacción puede contener:

- Título.
- Descripción.
- Monto.
- Categoría.
- Fecha.
- Tipo de movimiento.
  - Ingreso.
  - Gasto.
- Identificador local.
- Identificador remoto.

Entre las funcionalidades implementadas se encuentran:

- Crear ingresos.
- Crear gastos.
- Editar transacciones.
- Eliminar transacciones.
- Consultar transacciones.
- Obtener una transacción específica.
- Obtener ingresos totales.
- Obtener gastos totales.
- Consultar gastos del mes.
- Consultar movimientos del mes.
- Ordenar transacciones por fecha.
- Reemplazar los datos locales durante la sincronización.

---

# 🎯 Metas de Ahorro

La aplicación permite crear y administrar objetivos financieros.

Cada meta contiene información como:

- Título.
- Descripción.
- Monto objetivo.
- Monto acumulado.
- Prioridad.
- Estado.
- Fecha de creación.
- Identificador remoto.
- Imagen asociada.

Funcionalidades implementadas:

- Crear metas.
- Editar metas.
- Eliminar metas.
- Agregar dinero a una meta.
- Retirar dinero de una meta.
- Sincronizar metas.
- Calcular progreso.
- Identificar metas completadas.
- Identificar metas pendientes.
- Obtener la meta más avanzada.
- Obtener la meta más cercana a completarse.
- Obtener la meta prioritaria.
- Calcular dinero acumulado.
- Calcular dinero restante.
- Calcular objetivos totales.

El progreso de una meta se calcula en función del monto actual respecto del monto objetivo.

---

# 🔁 Hábitos Financieros

FinGoal también incorpora un sistema de hábitos orientado a mejorar la constancia financiera.

Los hábitos contienen:

- Nombre.
- Descripción.
- Frecuencia.
- Estado de cumplimiento diario.
- Racha actual.
- Identificador remoto.

Se pueden:

- Crear hábitos.
- Editar hábitos.
- Eliminar hábitos.
- Marcar hábitos como completados.
- Consultar hábitos pendientes.
- Consultar hábitos completados.
- Mantener rachas.
- Sincronizar hábitos.
- Obtener los hábitos recientes.

La aplicación también utiliza la información disponible para generar estadísticas sobre el progreso diario.

---

# 🤖 Asistente Financiero

Una de las funcionalidades personalizadas de FinGoal es un **asistente financiero integrado en la interfaz**.

El asistente utiliza diferentes personajes visuales:

- **Grenny** 🟦
- **Rosy** 🟩
- **Sun** 🟧
- **Gooli** 🟪

Cada personaje posee su propia identidad visual y color.

El asistente se presenta mediante un botón flotante que:

- Puede desplazarse por la pantalla.
- Se mantiene dentro de los límites de la interfaz.
- Se ajusta automáticamente al borde más cercano al soltarlo.
- Abre un `ModalBottomSheet`.

Dentro del asistente se pueden seleccionar preguntas relacionadas con la información disponible en cada sección.

---

## 💬 Preguntas inteligentes

El asistente no se limita a mostrar estadísticas básicas.

Dependiendo de la pantalla puede responder preguntas como:

### Transacciones

```text
¿Cuánto dinero ingresé?
¿Cuánto gasté?
¿Cuánto dinero me queda este mes?
¿Cuánto puedo gastar por día?
¿Cuánto gasto por día en promedio?
¿Cuánto puedo gastar todavía?
¿Estoy gastando demasiado?
¿Cuál fue mi mayor gasto?
```

### Hábitos

```text
¿Cuántos hábitos tengo?
¿Cuántos hábitos completé hoy?
¿Cuántos hábitos me faltan hoy?
¿Qué porcentaje de mis hábitos completé hoy?
¿Cómo voy con mis hábitos hoy?
¿Cuál es mi mejor racha?
¿Qué hábito tiene mi mejor racha?
¿Qué hábito debería completar hoy?
¿Estoy siendo constante con mis hábitos?
```

### Metas

```text
¿Cuántas metas tengo?
¿Cuántas metas completé?
¿Cuántas metas tengo pendientes?
¿Cuánto dinero tengo ahorrado en mis metas?
¿Cuánto me falta para alcanzar mis metas?
¿Qué porcentaje de mis metas completé?
¿Cómo voy con mis metas?
¿Cuál es mi meta más avanzada?
¿Qué meta estoy más cerca de completar?
¿Cuál es mi meta más grande?
¿En qué meta tengo más dinero?
¿Cuál es mi meta prioritaria?
¿En qué meta debería concentrarme?
```

Las respuestas son generadas utilizando los datos actuales de la aplicación, evitando mostrar información que no esté disponible en el modelo de datos.

---

# 🗄️ Persistencia local con Room

FinGoal utiliza **Room Database** para almacenar información localmente.

La implementación fue adaptada para funcionar dentro del proyecto **Kotlin Multiplatform**, utilizando el driver correspondiente para SQLite.

Actualmente la base de datos contiene entidades para:

```text
TransactionEntity
GoalEntity
HabitEntity
```

Y sus respectivos DAOs:

```text
TransactionDao
GoalDao
HabitDao
```

La base de datos central se encuentra definida mediante:

```text
AppDatabase
```

Los datos se exponen mediante `Flow`, permitiendo que la interfaz reaccione automáticamente ante cambios.

Ejemplo:

```kotlin
@Query("SELECT * FROM transactions ORDER BY date DESC")
fun getAllTransactions(): Flow<List<TransactionEntity>>
```

Esto permite mantener el flujo:

```text
Room
 ↓
Flow
 ↓
Repository
 ↓
UseCase
 ↓
ViewModel
 ↓
StateFlow
 ↓
Compose
```

---

# 🌐 Comunicación con el Backend

FinGoal cuenta con un backend independiente encargado de manejar los datos remotos.

### Backend

```text
Node.js
Express
Firebase Cloud Firestore
```

Repositorio:

`https://github.com/Yair2332/fingoal-api`

La aplicación móvil se comunica con el backend mediante una API REST.

La comunicación utiliza:

- Retrofit.
- HTTP.
- DTOs.
- Mappers.
- Repositories.
- UseCases.

---

# 🔄 Sincronización de datos

La aplicación utiliza un enfoque **Offline-First**.

Los datos locales se utilizan como fuente inmediata para la interfaz, mientras que la aplicación puede sincronizar información con el servidor.

## Flujo de lectura

```text
UI
 ↓
ViewModel
 ↓
Get...UseCase
 ↓
Repository
 ↓
Room
 ↓
Flow
 ↓
UI
```

En paralelo, los casos de uso de sincronización pueden obtener información actualizada desde la API:

```text
Sync...UseCase
 ↓
Repository
 ↓
Retrofit
 ↓
Backend
 ↓
Firestore
```

Una vez obtenidos los datos remotos:

```text
Backend
 ↓
Repository
 ↓
Mapper
 ↓
Entity
 ↓
Room
 ↓
Flow
 ↓
Compose
```

Esto permite que la interfaz se actualice automáticamente cuando cambia la información local.

---

# ✍️ Flujo de escritura

Cuando el usuario crea información nueva, el flujo general es:

```text
Compose UI
 ↓
ViewModel
 ↓
Add...UseCase
 ↓
Repository
 ↓
Retrofit
 ↓
Backend
 ↓
Respuesta
 ↓
Room
 ↓
Flow
 ↓
UI
```

De esta forma, la información remota y local se mantienen sincronizadas.

---

# 🧩 Repositories

Los repositories funcionan como intermediarios entre la capa de dominio y las fuentes de datos.

Permiten abstraer:

- Room.
- Retrofit.
- API remota.
- Datos locales.
- Sincronización.

La capa `domain` no necesita conocer directamente cómo se almacenan o recuperan los datos.

Esto permite mantener la separación de responsabilidades definida por Clean Architecture.

---

# ⚙️ UseCases

Las operaciones principales de la aplicación están encapsuladas mediante casos de uso.

Entre ellos se encuentran operaciones relacionadas con:

```text
Authentication
Transactions
Goals
Habits
Dashboard
Synchronization
```

Los UseCases contienen la lógica necesaria para ejecutar las acciones de negocio y evitan concentrar toda la lógica dentro de los ViewModels.

Ejemplo conceptual:

```text
UI
 ↓
ViewModel
 ↓
AddTransactionUseCase
 ↓
TransactionRepository
```

---

# 🧪 Testing

El proyecto incorpora pruebas automatizadas para las principales capas de la aplicación.

La estrategia de testing se organiza principalmente en tres áreas:

```text
ViewModels
UseCases
Room / DAOs
```

## ViewModels

Se desarrollaron pruebas para los ViewModels principales:

```text
AuthViewModelTest
TransactionViewModelTest
HabitViewModelTest
GoalViewModelTest
DashboardViewModelTest
```

Estas pruebas permiten verificar estados, acciones y comportamiento de la capa de presentación.

---

## UseCases

También se implementaron pruebas para los principales casos de uso:

```text
AuthTest
TransactionsTest
HabitsTest
GoalsTest
DashboardTest
```

Estas pruebas validan la lógica de negocio de la aplicación de forma aislada.

---

## Room / DAOs

Se implementaron pruebas específicas para la persistencia local.

Actualmente se cubren:

```text
TransactionDaoTest
GoalDaoTest
HabitDaoTest
```

Se prueban operaciones como:

- Inserción.
- Inserción múltiple.
- Actualización.
- Eliminación.
- Eliminación por ID.
- Eliminación por ID remoto.
- Limpieza de tablas.
- Reemplazo de información.
- Búsqueda por identificador.
- Consultas mediante `Flow`.
- Ordenamiento.
- Cálculos agregados.
- Consultas relacionadas con el mes actual.
- Consultas específicas de metas y hábitos.

Las pruebas utilizan una base de datos Room aislada para evitar afectar datos reales.

---

# 🧪 Ejecución de Tests

Debido a que el proyecto utiliza **Kotlin Multiplatform** y no un target JVM independiente, los tests compartidos se ejecutan mediante la tarea multiplataforma:

```bash
./gradlew :sharedLogic:allTests
```

En Windows:

```powershell
.\gradlew :sharedLogic:allTests
```

El proyecto cuenta con targets de testing para las plataformas soportadas.

---

# 📦 Estructura del proyecto

La estructura principal se organiza aproximadamente de la siguiente manera:

```text
FinGoal/
│
├── androidApp/
│   └── Aplicación Android
│
├── sharedLogic/
│   │
│   ├── src/
│   │   ├── commonMain/
│   │   │
│   │   │   ├── data/
│   │   │   │   ├── local/
│   │   │   │   │   ├── database/
│   │   │   │   │   ├── dao/
│   │   │   │   │   └── entities/
│   │   │   │   │
│   │   │   │   ├── remote/
│   │   │   │   │   ├── dto/
│   │   │   │   │   └── api/
│   │   │   │   │
│   │   │   │   ├── mapper/
│   │   │   │   └── repository/
│   │   │   │
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   ├── repository/
│   │   │   │   └── usecase/
│   │   │   │
│   │   │   └── ui/
│   │   │       ├── components/
│   │   │       └── screens/
│   │   │           ├── auth/
│   │   │           ├── dashboard/
│   │   │           ├── transactions/
│   │   │           ├── habits/
│   │   │           └── goals/
│   │   │
│   │   ├── androidMain/
│   │   ├── iosMain/
│   │   └── commonTest/
│   │
│   └── build.gradle.kts
│
└── backend/
    └── fingoal-api
```

---

# 🛠️ Tecnologías utilizadas

## Mobile

| Tecnología | Uso |
|---|---|
| Kotlin | Lenguaje principal |
| Kotlin Multiplatform | Código compartido Android/iOS |
| Compose Multiplatform | Interfaz multiplataforma |
| Material 3 | Sistema visual |
| Room | Persistencia local |
| SQLite | Base de datos local |
| Retrofit | Comunicación HTTP |
| Kotlin Coroutines | Programación asíncrona |
| Flow / StateFlow | Reactividad |
| Hilt | Inyección de dependencias |
| MVVM | Arquitectura de presentación |
| Clean Architecture | Organización del proyecto |

## Backend

| Tecnología | Uso |
|---|---|
| Node.js | Runtime |
| Express | API REST |
| Firebase | Servicios backend |
| Cloud Firestore | Persistencia remota |

---

# 🔐 Usuario de demostración

Para probar la aplicación sin configurar un usuario desde cero se puede utilizar la cuenta de demostración:

```text
Correo:
fingoaldemo@gmail.com

Contraseña:
1234567demo
```

> Estas credenciales son únicamente para demostración del proyecto.

---

# 🌐 Configuración del entorno

## Requisitos

Para trabajar con el proyecto se recomienda contar con:

- Android Studio.
- Kotlin.
- Android SDK.
- JDK compatible con la versión utilizada por el proyecto.
- Node.js para ejecutar el backend.
- Xcode para compilar el target iOS en macOS.

El proyecto utiliza Kotlin Multiplatform, por lo que el entorno necesario depende de la plataforma que se quiera ejecutar.

---

# 🔗 Configuración de la API

La aplicación puede utilizar diferentes entornos para comunicarse con el backend.

### Producción

```text
https://fingoal-api-production.up.railway.app/
```

### Desarrollo local en Android Emulator

```text
http://10.0.2.2:3000/
```

`10.0.2.2` permite que el emulador Android acceda al servidor que está ejecutándose en la máquina host.

---

# 🎨 Identidad visual

FinGoal utiliza una identidad visual propia orientada a representar el concepto de organización financiera de una manera más amigable.

La aplicación incorpora personajes como asistentes visuales:

```text
Grenny
Rosy
Sun
Gooli
```

Estos personajes se utilizan principalmente dentro del asistente financiero y forman parte de la identidad de la aplicación.

También se configuraron recursos e iconografía específicos para las aplicaciones móviles.

---

# 🤖 Desarrollo asistido por Inteligencia Artificial

Durante el desarrollo se utilizaron herramientas de Inteligencia Artificial como apoyo para determinadas tareas de programación y resolución de problemas.

La IA fue utilizada como herramienta de asistencia para:

- Diseño y refactorización de código.
- Resolución de errores.
- Generación de estructuras iniciales.
- Implementación de componentes.
- Desarrollo de tests.
- Análisis de errores de Gradle.
- Migración y adaptación de componentes.
- Mejora de arquitectura.
- Documentación.

La implementación, integración, modificación y validación del código forman parte del proceso de desarrollo del proyecto.

---

# 🚀 Objetivos del proyecto

FinGoal busca combinar educación financiera, organización personal y tecnología móvil en una única aplicación.

Los principales objetivos son:

- Facilitar el registro y seguimiento de ingresos y gastos.
- Ayudar al usuario a controlar sus hábitos financieros.
- Permitir establecer y seguir objetivos de ahorro.
- Mantener la información disponible incluso sin conexión.
- Sincronizar información con un backend remoto.
- Compartir lógica entre Android e iOS.
- Mantener una arquitectura escalable y mantenible.
- Incorporar asistentes inteligentes para facilitar la interpretación de los datos financieros.

---

# 📈 Estado actual

Actualmente FinGoal cuenta con una arquitectura multiplataforma basada en:

```text
Kotlin Multiplatform
        │
        ├── Android
        │
        └── iOS
             │
             ▼
    Compose Multiplatform
             │
             ▼
      Clean Architecture
             │
      ┌──────┴──────┐
      ▼             ▼
    Room          Retrofit
      │             │
      ▼             ▼
  SQLite         REST API
                    │
                    ▼
              Node + Express
                    │
                    ▼
             Firebase Firestore
```

Además, cuenta con:

- Gestión de autenticación.
- Transacciones.
- Hábitos.
- Metas de ahorro.
- Dashboard financiero.
- Persistencia local.
- Sincronización remota.
- Arquitectura reactiva.
- ViewModels.
- UseCases.
- Repositories.
- Room DAOs.
- Tests automatizados.
- Asistente financiero.
- Personajes personalizados.
- Soporte Android/iOS.
- Tema claro y oscuro.
- Componentes Compose reutilizables.

---

# 📌 Próximas mejoras

Algunas posibles líneas de evolución del proyecto son:

- Mejorar la sincronización offline/online.
- Incorporar más estadísticas financieras.
- Ampliar las capacidades del asistente.
- Incorporar gráficos financieros más avanzados.
- Mejorar la experiencia multiplataforma.
- Agregar más automatizaciones relacionadas con hábitos y metas.
- Incorporar nuevas herramientas de planificación financiera.

---

## 👨‍💻 Autor

**Yair Lezcano**

Desarrollador Mobile especializado en Kotlin, Android, Compose y tecnologías multiplataforma.

---

> **FinGoal** — Una forma simple de organizar tus finanzas, desarrollar mejores hábitos y alcanzar tus objetivos. 🎯

Desarrollado con Kotlin Multiplatform, Compose Multiplatform y ❤️.
