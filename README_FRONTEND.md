# Sistema de Concesionaria - Frontend

## 🚗 Descripción

He creado un frontend completo y moderno para tu sistema de concesionaria que permite:

- ✅ **Ver catálogo de vehículos** con filtros por tipo
- ✅ **Crear nuevos vehículos** en el sistema
- ✅ **Gestionar pedidos** de clientes
- ✅ **Interfaz responsive** que funciona en desktop y móvil
- ✅ **Diseño moderno** con gradientes y animaciones

## 🌐 Cómo acceder

1. **URL del sistema:** http://localhost:8081
2. La aplicación Spring Boot está ejecutándose en el puerto 8090
3. El frontend está servido automáticamente desde `/static`

## 📱 Funcionalidades del Frontend

### 1. Catálogo de Vehículos
- Ver todos los vehículos disponibles
- Filtrar por tipo (Auto, Camioneta, Moto, Camión)
- Botón "Hacer Pedido" para vehículos disponibles
- Botón "Ver Detalles" para información completa

### 2. Gestión de Pedidos
- Ver lista de pedidos existentes
- Estados de pedidos con colores diferenciados
- Información de cliente y vehículo

### 3. Agregar Nuevos Vehículos
- Formulario completo para crear vehículos
- Validación de campos
- Tipos: Auto, Camioneta, Moto, Camión

### 4. Crear Pedidos
- Modal para crear pedidos desde el catálogo
- Campos para información del cliente
- Confirmación automática con el vehículo seleccionado

## 🎨 Características de Diseño

- **Colores modernos:** Gradientes azul/púrpura
- **Iconos:** Font Awesome para una mejor UX
- **Animaciones:** Transiciones suaves y hover effects
- **Responsive:** Se adapta a cualquier tamaño de pantalla
- **Notificaciones:** Toast messages para feedback del usuario

## 🔧 Tecnologías Utilizadas

- **HTML5** para la estructura
- **CSS3** con Flexbox/Grid para el layout
- **JavaScript (ES6+)** para la funcionalidad
- **Fetch API** para comunicación con el backend
- **Font Awesome** para iconografía

## 📊 Endpoints Integrados

El frontend se comunica con estos endpoints del backend:

- `GET /api/vehiculos` - Obtener todos los vehículos
- `POST /api/vehiculos` - Crear nuevo vehículo
- `GET /api/pedidos` - Obtener todos los pedidos
- `POST /api/pedidos` - Crear nuevo pedido

## 🚀 Próximos Pasos

Puedes extender el sistema agregando:

1. **Edición de vehículos** existentes
2. **Búsqueda avanzada** por marca/modelo
3. **Estados de pedidos** más detallados
4. **Dashboard** con estadísticas
5. **Autenticación** de usuarios
6. **Reportes** en PDF
7. **Notificaciones en tiempo real**

## 📝 Archivos Creados

```
src/main/resources/static/
├── index.html          # Página principal
├── css/
│   └── styles.css      # Estilos CSS
└── js/
    └── app.js          # Lógica JavaScript
```

¡El sistema está listo para usar! 🎉
