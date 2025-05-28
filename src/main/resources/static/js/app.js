// Configuración de la API
const API_BASE_URL = 'http://localhost:8081/api';

// Estado global de la aplicación
let vehiculosData = [];
let pedidosData = [];
let vehiculoSeleccionado = null;
let vehiculoEnEdicion = null;
let pedidoEnEdicion = null;

// Inicialización cuando se carga la página
document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
});

function initializeApp() {
    setupEventListeners();
    // Verificar conexión con el servidor antes de cargar datos
    verificarConexionServidor().then(conectado => {
        if (conectado) {
            cargarVehiculos();
        } else {
            // Mostrar estado inicial sin datos
            renderVehiculosEmpty();
            renderPedidosEmpty();
            showToast('Servidor no disponible - Mostrando vista offline', 'info');
        }
    });
}

// Configurar event listeners
function setupEventListeners() {
    // Tabs navigation
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const tabId = this.getAttribute('data-tab');
            switchTab(tabId);
        });
    });

    // Filtro de vehículos
    document.getElementById('filtro-tipo').addEventListener('change', function() {
        const tipo = this.value;
        filtrarVehiculos(tipo);
    });

    // Filtro de pedidos por estado
    document.getElementById('filtro-estado').addEventListener('change', function() {
        const estado = this.value;
        filtrarPedidos(estado);
    });

    // Botones de refresh
    document.getElementById('btn-refresh-vehiculos').addEventListener('click', () => {
        showToast('Actualizando vehículos...', 'info');
        cargarVehiculos();
    });
    document.getElementById('btn-refresh-pedidos').addEventListener('click', () => {
        showToast('Actualizando pedidos...', 'info');
        cargarPedidos();
    });

    // Formulario nuevo vehículo
    document.getElementById('form-nuevo-vehiculo').addEventListener('submit', function(e) {
        e.preventDefault();
        crearVehiculo();
    });

    // Formulario nuevo pedido
    document.getElementById('form-nuevo-pedido').addEventListener('submit', function(e) {
        e.preventDefault();
        crearPedido();
    });

    // Formulario editar vehículo
    document.getElementById('form-editar-vehiculo').addEventListener('submit', function(e) {
        e.preventDefault();
        editarVehiculo();
    });

    // Formulario editar pedido
    document.getElementById('form-editar-pedido').addEventListener('submit', function(e) {
        e.preventDefault();
        editarPedido();
    });



    // Modal controls
    document.querySelectorAll('.modal-close').forEach(btn => {
        btn.addEventListener('click', cerrarModal);
    });

    // Cerrar modal al hacer click fuera
    document.getElementById('modal-pedido').addEventListener('click', function(e) {
        if (e.target === this) {
            cerrarModal();
        }
    });

    // Cerrar modal de editar vehículo al hacer click fuera
    document.getElementById('modal-editar-vehiculo').addEventListener('click', function(e) {
        if (e.target === this) {
            cerrarModalEditarVehiculo();
        }
    });

    // Cerrar modal de editar pedido al hacer click fuera
    document.getElementById('modal-editar-pedido').addEventListener('click', function(e) {
        if (e.target === this) {
            cerrarModalEditarPedido();
        }
    });
}

// Verificar conexión con el servidor
async function verificarConexionServidor() {
    try {
        const response = await fetch(`${API_BASE_URL}/test`, {
            method: 'GET',
            signal: AbortSignal.timeout(5000) // Timeout de 5 segundos
        });
        return response.ok;
    } catch (error) {
        console.warn('Servidor no disponible:', error.message);
        return false;
    }
}

// Navegación entre tabs
function switchTab(tabId) {
    // Actualizar botones
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    document.querySelector(`[data-tab="${tabId}"]`).classList.add('active');

    // Mostrar contenido
    document.querySelectorAll('.tab-content').forEach(content => {
        content.classList.remove('active');
    });
    document.getElementById(tabId).classList.add('active');

    // Cargar datos si es necesario y hay conexión
    if (tabId === 'vehiculos' && vehiculosData.length === 0) {
        cargarVehiculos();
    } else if (tabId === 'pedidos' && pedidosData.length === 0) {
        cargarPedidos();
    }
}

// Funciones de carga de datos
async function cargarVehiculos() {
    try {
        showLoading(true);
        const response = await fetch(`${API_BASE_URL}/vehiculos`);
        
        if (!response.ok) {
            // Si hay error del servidor, mostrar lista vacía sin notificación de error
            console.warn('No se pudieron cargar vehículos:', response.status, response.statusText);
            vehiculosData = [];
            renderVehiculosEmpty();
            return;
        }
        
        const data = await response.json();
        vehiculosData = data.vehiculos || [];
        renderVehiculos(vehiculosData);
        
        // Solo mostrar mensaje de éxito si realmente hay vehículos o es una recarga manual
        if (vehiculosData.length > 0) {
            showToast('Vehículos cargados exitosamente', 'success');
        }
    } catch (error) {
        console.warn('Error conectando con el servidor:', error);
        // En lugar de mostrar error, mostrar estado vacío
        vehiculosData = [];
        renderVehiculosEmpty();
    } finally {
        showLoading(false);
    }
}

async function cargarPedidos() {
    try {
        showLoading(true);
        const response = await fetch(`${API_BASE_URL}/pedidos`);
        
        if (!response.ok) {
            // Si hay error del servidor, mostrar lista vacía sin notificación de error
            console.warn('No se pudieron cargar pedidos:', response.status, response.statusText);
            pedidosData = [];
            renderPedidosEmpty();
            return;
        }
        
        const data = await response.json();
        pedidosData = data.pedidos || [];
        renderPedidos(pedidosData);
        
        // Solo mostrar mensaje de éxito si realmente hay pedidos o es una recarga manual
        if (pedidosData.length > 0) {
            showToast('Pedidos cargados exitosamente', 'success');
        }
    } catch (error) {
        console.warn('Error conectando con el servidor:', error);
        // En lugar de mostrar error, mostrar estado vacío
        pedidosData = [];
        renderPedidosEmpty();
    } finally {
        showLoading(false);
    }
}

// Renderizado de vehículos
function renderVehiculos(vehiculos) {
    const container = document.getElementById('vehiculos-list');
    
    if (vehiculos.length === 0) {
        renderVehiculosEmpty();
        return;
    }
    
    container.innerHTML = vehiculos.map(vehiculo => `
        <div class="vehicle-card">
            <div class="vehicle-header">
                <span class="vehicle-type">${vehiculo.tipo}</span>
                <span class="vehicle-price">$${formatNumber(vehiculo.precio)}</span>
            </div>
            <div class="vehicle-info">
                <h3>${vehiculo.marca} ${vehiculo.modelo}</h3>
                <div class="vehicle-details">
                    ${vehiculo.año ? `<p><strong>Año:</strong> ${vehiculo.año}</p>` : ''}
                    ${vehiculo.color ? `<p><strong>Color:</strong> ${vehiculo.color}</p>` : ''}
                    <p><strong>Disponible:</strong> ${vehiculo.disponible ? 'Sí' : 'No'}</p>
                </div>
            </div>
            <div class="vehicle-actions">
                ${vehiculo.disponible 
                    ? `<button class="btn btn-success" onclick="abrirModalPedido(${vehiculo.id})">
                        <i class="fas fa-shopping-cart"></i> Hacer Pedido
                       </button>`
                    : `<button class="btn btn-secondary" disabled title="Vehículo no disponible">
                        <i class="fas fa-ban"></i> No Disponible
                       </button>`
                }
                <button class="btn btn-primary" onclick="abrirModalEditarVehiculo(${vehiculo.id})">
                    <i class="fas fa-edit"></i> Editar
                </button>
                <button class="btn btn-secondary" onclick="verDetallesVehiculo(${vehiculo.id})">
                    <i class="fas fa-info-circle"></i> Detalles
                </button>
                <button class="btn btn-danger" onclick="eliminarVehiculo(${vehiculo.id})" 
                        title="Eliminar vehículo">
                    <i class="fas fa-trash"></i>
                </button>
            </div>
        </div>
    `).join('');
}

function renderVehiculosEmpty() {
    const container = document.getElementById('vehiculos-list');
    container.innerHTML = `
        <div class="empty-state">
            <i class="fas fa-car"></i>
            <h3>No hay vehículos disponibles</h3>
            <p>No se encontraron vehículos en el catálogo</p>
        </div>
    `;
}

// Renderizado de pedidos
function renderPedidos(pedidos) {
    const container = document.getElementById('pedidos-list');
    
    if (pedidos.length === 0) {
        renderPedidosEmpty();
        return;
    }
    
    container.innerHTML = pedidos.map(pedido => `
        <div class="order-card">
            <div class="order-header">
                <span class="order-id">Pedido #${pedido.id}</span>
                <span class="order-status ${pedido.estado.toLowerCase()}">${pedido.estado}</span>
            </div>
            <div class="order-info">
                <div class="order-client">
                    <strong>${pedido.cliente.nombre}</strong><br>
                    <small>${pedido.cliente.email}</small>
                </div>
                <div class="order-vehicle">
                    ${pedido.vehiculo.marca} ${pedido.vehiculo.modelo}
                </div>
                <div class="order-date">
                    <small>Fecha: ${pedido.fecha}</small>
                </div>
            </div>
            <div class="order-actions">
                <div class="order-total">
                    Total: $${formatNumber(pedido.total)}
                </div>
                <div class="action-buttons">
                    ${getEstadoButtons(pedido)}
                    <button class="btn btn-secondary btn-small" onclick="abrirModalEditarPedido(${pedido.id})" 
                            title="Editar pedido">
                        <i class="fas fa-edit"></i> Editar
                    </button>
                    <button class="btn btn-danger btn-small" onclick="cancelarPedido(${pedido.id})" 
                            title="Cancelar pedido">
                        <i class="fas fa-times"></i> Cancelar
                    </button>
                </div>
            </div>
        </div>
    `).join('');
}

function renderPedidosEmpty() {
    const container = document.getElementById('pedidos-list');
    container.innerHTML = `
        <div class="empty-state">
            <i class="fas fa-shopping-cart"></i>
            <h3>No hay pedidos registrados</h3>
            <p>No se encontraron pedidos en el sistema</p>
        </div>
    `;
}

// Filtrado de vehículos
function filtrarVehiculos(tipo) {
    if (!tipo) {
        renderVehiculos(vehiculosData);
        return;
    }
    
    const vehiculosFiltrados = vehiculosData.filter(vehiculo => 
        vehiculo.tipo.toUpperCase() === tipo.toUpperCase()
    );
    renderVehiculos(vehiculosFiltrados);
}

// Filtrado de pedidos por estado
function filtrarPedidos(estado) {
    if (!estado) {
        renderPedidos(pedidosData);
        return;
    }
    
    const pedidosFiltrados = pedidosData.filter(pedido => 
        pedido.estado.toUpperCase() === estado.toUpperCase()
    );
    renderPedidos(pedidosFiltrados);
}

// Crear nuevo vehículo
async function crearVehiculo() {
    try {
        showLoading(true);
        
        const formData = {
            tipo: document.getElementById('vehiculo-tipo').value,
            marca: document.getElementById('vehiculo-marca').value,
            modelo: document.getElementById('vehiculo-modelo').value,
            año: parseInt(document.getElementById('vehiculo-año').value),
            color: document.getElementById('vehiculo-color').value,
            precio: parseFloat(document.getElementById('vehiculo-precio').value)
        };

        const response = await fetch(`${API_BASE_URL}/vehiculos`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData)
        });

        if (!response.ok) {
            throw new Error(`Error ${response.status}: ${response.statusText}`);
        }

        const result = await response.json();
        
        showToast('Vehículo creado exitosamente', 'success');
        document.getElementById('form-nuevo-vehiculo').reset();
        
        // Recargar vehículos y cambiar a tab de vehículos
        await cargarVehiculos();
        switchTab('vehiculos');
        
    } catch (error) {
        console.error('Error creando vehículo:', error);
        if (error.message.includes('Failed to fetch') || error.message.includes('NetworkError')) {
            showToast('Error de conexión - Verifique que el servidor esté funcionando', 'error');
        } else {
            showToast('Error al crear vehículo: ' + error.message, 'error');
        }
    } finally {
        showLoading(false);
    }
}

// Modal para crear pedido
function abrirModalPedido(vehiculoId) {
    vehiculoSeleccionado = vehiculosData.find(v => v.id === vehiculoId);
    
    if (!vehiculoSeleccionado) {
        showToast('Vehículo no encontrado', 'error');
        return;
    }
    
    // Llenar información del vehículo en el modal
    document.getElementById('vehiculo-seleccionado').innerHTML = `
        <h4>Vehículo Seleccionado</h4>
        <p><strong>Tipo:</strong> ${vehiculoSeleccionado.tipo}</p>
        <p><strong>Marca:</strong> ${vehiculoSeleccionado.marca}</p>
        <p><strong>Modelo:</strong> ${vehiculoSeleccionado.modelo}</p>
        ${vehiculoSeleccionado.año ? `<p><strong>Año:</strong> ${vehiculoSeleccionado.año}</p>` : ''}
        <p class="precio"><strong>Precio:</strong> $${formatNumber(vehiculoSeleccionado.precio)}</p>
    `;
    
    // Limpiar formulario
    document.getElementById('form-nuevo-pedido').reset();
    
    // Mostrar modal
    document.getElementById('modal-pedido').classList.add('show');
}

function cerrarModal() {
    document.getElementById('modal-pedido').classList.remove('show');
    document.getElementById('modal-editar-vehiculo').classList.remove('show');
    document.getElementById('modal-editar-pedido').classList.remove('show');
    vehiculoSeleccionado = null;
    vehiculoEnEdicion = null;
    pedidoEnEdicion = null;
    
    // Reset forms
    const editForm = document.getElementById('form-editar-vehiculo');
    if (editForm) editForm.reset();
    
    const editPedidoForm = document.getElementById('form-editar-pedido');
    if (editPedidoForm) editPedidoForm.reset();
}

// Crear nuevo pedido
async function crearPedido() {
    if (!vehiculoSeleccionado) {
        showToast('No hay vehículo seleccionado', 'error');
        return;
    }
    
    try {
        showLoading(true);
        
        const formData = {
            nombreCliente: document.getElementById('cliente-nombre').value,
            emailCliente: document.getElementById('cliente-email').value,
            tipo: vehiculoSeleccionado.tipo,
            marca: vehiculoSeleccionado.marca,
            modelo: vehiculoSeleccionado.modelo,
            precio: vehiculoSeleccionado.precio
        };

        const response = await fetch(`${API_BASE_URL}/pedidos`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData)
        });

        if (!response.ok) {
            throw new Error(`Error ${response.status}: ${response.statusText}`);
        }

        const result = await response.json();
        
        showToast('Pedido creado exitosamente', 'success');
        cerrarModal();
        
        // Recargar pedidos y cambiar a tab de pedidos
        await cargarPedidos();
        switchTab('pedidos');
        
    } catch (error) {
        console.error('Error creando pedido:', error);
        if (error.message.includes('Failed to fetch') || error.message.includes('NetworkError')) {
            showToast('Error de conexión - Verifique que el servidor esté funcionando', 'error');
        } else {
            showToast('Error al crear pedido: ' + error.message, 'error');
        }
    } finally {
        showLoading(false);
    }
}

// Ver detalles de vehículo
function verDetallesVehiculo(vehiculoId) {
    const vehiculo = vehiculosData.find(v => v.id === vehiculoId);
    if (!vehiculo) {
        showToast('Vehículo no encontrado', 'error');
        return;
    }
    
    const detalles = `
        Tipo: ${vehiculo.tipo}
        Marca: ${vehiculo.marca}
        Modelo: ${vehiculo.modelo}
        ${vehiculo.año ? `Año: ${vehiculo.año}\n` : ''}
        ${vehiculo.color ? `Color: ${vehiculo.color}\n` : ''}
        Precio: $${formatNumber(vehiculo.precio)}
        Disponible: ${vehiculo.disponible ? 'Sí' : 'No'}
    `;
    
    alert(detalles);
}

// Generar botones según el estado del pedido
function getEstadoButtons(pedido) {
    const estado = pedido.estado;
    let buttons = '';
    
    switch(estado) {
        case 'VENTAS':
            buttons = `
                <button class="btn btn-primary btn-small" onclick="cambiarEstadoPedido(${pedido.id}, 'COBRANZA')">
                    → Cobranza
                </button>
            `;
            break;
        case 'COBRANZA':
            buttons = `
                <button class="btn btn-primary btn-small" onclick="cambiarEstadoPedido(${pedido.id}, 'LOGISTICA')">
                    → Logística
                </button>
            `;
            break;
        case 'LOGISTICA':
            buttons = `
                <button class="btn btn-success btn-small" onclick="cambiarEstadoPedido(${pedido.id}, 'COMPLETADO')">
                    ✓ Completar
                </button>
            `;
            break;
        case 'COMPLETADO':
            buttons = `
                <span class="completed-badge">
                    <i class="fas fa-check-circle"></i> Finalizado
                </span>
            `;
            break;
        default:
            buttons = '';
    }
    
    return buttons;
}

// Eliminar vehículo
async function eliminarVehiculo(vehiculoId) {
    if (!confirm('¿Estás seguro de que deseas eliminar este vehículo?')) {
        return;
    }
    
    try {
        showLoading(true);
        
        const response = await fetch(`${API_BASE_URL}/vehiculos/${vehiculoId}`, {
            method: 'DELETE'
        });
        
        if (!response.ok) {
            throw new Error(`Error ${response.status}: ${response.statusText}`);
        }
        
        const result = await response.json();
        
        if (result.success) {
            showToast('Vehículo eliminado exitosamente', 'success');
            await cargarVehiculos();
        } else {
            showToast('Error: ' + result.error, 'error');
        }        } catch (error) {
            console.error('Error eliminando vehículo:', error);
            if (error.message.includes('Failed to fetch') || error.message.includes('NetworkError')) {
                showToast('Error de conexión - Verifique que el servidor esté funcionando', 'error');
            } else {
                showToast('Error al eliminar vehículo: ' + error.message, 'error');
            }
        } finally {
            showLoading(false);
        }
}

// Cancelar pedido
async function cancelarPedido(pedidoId) {
    if (!confirm('¿Estás seguro de que deseas cancelar este pedido?')) {
        return;
    }
    
    try {
        showLoading(true);
        
        const response = await fetch(`${API_BASE_URL}/pedidos/${pedidoId}`, {
            method: 'DELETE'
        });
        
        if (!response.ok) {
            throw new Error(`Error ${response.status}: ${response.statusText}`);
        }
        
        const result = await response.json();
        
        if (result.success) {
            showToast('Pedido cancelado exitosamente', 'success');
            await cargarPedidos();
        } else {
            showToast('Error: ' + result.error, 'error');
        }        } catch (error) {
            console.error('Error cancelando pedido:', error);
            if (error.message.includes('Failed to fetch') || error.message.includes('NetworkError')) {
                showToast('Error de conexión - Verifique que el servidor esté funcionando', 'error');
            } else {
                showToast('Error al cancelar pedido: ' + error.message, 'error');
            }
        } finally {
            showLoading(false);
        }
}

// Cambiar estado de pedido
async function cambiarEstadoPedido(pedidoId, nuevoEstado) {
    try {
        showLoading(true);
        
        const response = await fetch(`${API_BASE_URL}/pedidos/${pedidoId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ estado: nuevoEstado })
        });
        
        if (!response.ok) {
            throw new Error(`Error ${response.status}: ${response.statusText}`);
        }
        
        const result = await response.json();
        
        if (result.success) {
            showToast(`Pedido movido a ${nuevoEstado}`, 'success');
            await cargarPedidos();
        } else {
            showToast('Error: ' + result.error, 'error');
        }
        
    } catch (error) {
        console.error('Error cambiando estado del pedido:', error);
        if (error.message.includes('Failed to fetch') || error.message.includes('NetworkError')) {
            showToast('Error de conexión - Verifique que el servidor esté funcionando', 'error');
        } else {
            showToast('Error al cambiar estado del pedido: ' + error.message, 'error');
        }
    } finally {
        showLoading(false);
    }
}

// Funciones de edición de vehículos
function abrirModalEditarVehiculo(vehiculoId) {
    const vehiculo = vehiculosData.find(v => v.id === vehiculoId);
    
    if (!vehiculo) {
        showToast('Vehículo no encontrado', 'error');
        return;
    }

    vehiculoEnEdicion = vehiculo;
    
    // Llenar el formulario con los datos actuales
    document.getElementById('edit-vehiculo-tipo').value = vehiculo.tipo;
    document.getElementById('edit-vehiculo-marca').value = vehiculo.marca;
    document.getElementById('edit-vehiculo-modelo').value = vehiculo.modelo;
    document.getElementById('edit-vehiculo-año').value = vehiculo.año;
    document.getElementById('edit-vehiculo-color').value = vehiculo.color || '';
    document.getElementById('edit-vehiculo-precio').value = vehiculo.precio;
    document.getElementById('edit-vehiculo-disponible').value = vehiculo.disponible.toString();
    
    // Mostrar modal
    document.getElementById('modal-editar-vehiculo').classList.add('show');
}

function cerrarModalEditarVehiculo() {
    document.getElementById('modal-editar-vehiculo').classList.remove('show');
    vehiculoEnEdicion = null;
    document.getElementById('form-editar-vehiculo').reset();
}

function cerrarModalEditarPedido() {
    document.getElementById('modal-editar-pedido').classList.remove('show');
    pedidoEnEdicion = null;
    document.getElementById('form-editar-pedido').reset();
}

async function editarVehiculo() {
    if (!vehiculoEnEdicion) {
        showToast('No hay vehículo seleccionado para editar', 'error');
        return;
    }
    
    try {
        showLoading(true);
        
        const formData = {
            tipo: document.getElementById('edit-vehiculo-tipo').value,
            marca: document.getElementById('edit-vehiculo-marca').value,
            modelo: document.getElementById('edit-vehiculo-modelo').value,
            año: parseInt(document.getElementById('edit-vehiculo-año').value),
            color: document.getElementById('edit-vehiculo-color').value,
            precio: parseFloat(document.getElementById('edit-vehiculo-precio').value),
            disponible: document.getElementById('edit-vehiculo-disponible').value === 'true'
        };

        const response = await fetch(`${API_BASE_URL}/vehiculos/${vehiculoEnEdicion.id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData)
        });

        if (!response.ok) {
            throw new Error(`Error ${response.status}: ${response.statusText}`);
        }

        const result = await response.json();
        
        if (result.success) {
            showToast('Vehículo actualizado exitosamente', 'success');
            cerrarModalEditarVehiculo();
            await cargarVehiculos();
        } else {
            showToast('Error: ' + result.error, 'error');
        }
        
    } catch (error) {
        console.error('Error editando vehículo:', error);
        if (error.message.includes('Failed to fetch') || error.message.includes('NetworkError')) {
            showToast('Error de conexión - Verifique que el servidor esté funcionando', 'error');
        } else {
            showToast('Error al editar vehículo: ' + error.message, 'error');
        }
    } finally {
        showLoading(false);
    }
}

// Funciones de edición de pedidos
function abrirModalEditarPedido(pedidoId) {
    const pedido = pedidosData.find(p => p.id === pedidoId);
    
    if (!pedido) {
        showToast('Pedido no encontrado', 'error');
        return;
    }

    pedidoEnEdicion = pedido;
    
    // Llenar el formulario con los datos actuales del cliente
    document.getElementById('edit-pedido-cliente-nombre').value = pedido.cliente.nombre;
    document.getElementById('edit-pedido-cliente-email').value = pedido.cliente.email;
    
    // Mostrar información del vehículo (solo lectura)
    document.getElementById('edit-pedido-vehiculo-info').innerHTML = `
        <h4>Vehículo del Pedido</h4>
        <p><strong>Tipo:</strong> ${pedido.vehiculo.tipo || 'N/A'}</p>
        <p><strong>Marca:</strong> ${pedido.vehiculo.marca}</p>
        <p><strong>Modelo:</strong> ${pedido.vehiculo.modelo}</p>
        <p><strong>Estado:</strong> ${pedido.estado}</p>
        <p class="precio"><strong>Total:</strong> $${formatNumber(pedido.total)}</p>
    `;
    
    // Mostrar modal
    document.getElementById('modal-editar-pedido').classList.add('show');
}

async function editarPedido() {
    if (!pedidoEnEdicion) {
        showToast('No hay pedido seleccionado para editar', 'error');
        return;
    }
    
    try {
        showLoading(true);
        
        const formData = {
            nombreCliente: document.getElementById('edit-pedido-cliente-nombre').value,
            emailCliente: document.getElementById('edit-pedido-cliente-email').value
        };

        const response = await fetch(`${API_BASE_URL}/pedidos/${pedidoEnEdicion.id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData)
        });

        if (!response.ok) {
            throw new Error(`Error ${response.status}: ${response.statusText}`);
        }

        const result = await response.json();
        
        if (result.success) {
            showToast('Datos del cliente actualizados exitosamente', 'success');
            cerrarModalEditarPedido();
            await cargarPedidos();
        } else {
            showToast('Error: ' + result.error, 'error');
        }
        
    } catch (error) {
        console.error('Error editando pedido:', error);
        if (error.message.includes('Failed to fetch') || error.message.includes('NetworkError')) {
            showToast('Error de conexión - Verifique que el servidor esté funcionando', 'error');
        } else {
            showToast('Error al editar pedido: ' + error.message, 'error');
        }
    } finally {
        showLoading(false);
    }
}

// Utilidades
function formatNumber(number) {
    return new Intl.NumberFormat('es-AR', {
        minimumFractionDigits: 0,
        maximumFractionDigits: 2
    }).format(number);
}

function showLoading(show) {
    const loading = document.getElementById('loading');
    if (show) {
        loading.classList.add('show');
    } else {
        loading.classList.remove('show');
    }
}

function showToast(message, type = 'info') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    
    toast.className = `toast ${type}`;
    toast.innerHTML = `
        <div class="toast-title">${getToastTitle(type)}</div>
        <div class="toast-message">${message}</div>
    `;
    
    container.appendChild(toast);
    
    // Auto-remove after 5 seconds
    setTimeout(() => {
        if (toast.parentNode) {
            toast.parentNode.removeChild(toast);
        }
    }, 5000);
}

function getToastTitle(type) {
    switch(type) {
        case 'success': return 'Éxito';
        case 'error': return 'Error';
        case 'info': return 'Información';
        default: return 'Notificación';
    }
}

// Funciones adicionales para extender funcionalidad
function buscarVehiculo(query) {
    if (!query) {
        renderVehiculos(vehiculosData);
        return;
    }
    
    const resultados = vehiculosData.filter(vehiculo => 
        vehiculo.marca.toLowerCase().includes(query.toLowerCase()) ||
        vehiculo.modelo.toLowerCase().includes(query.toLowerCase()) ||
        vehiculo.tipo.toLowerCase().includes(query.toLowerCase())
    );
    
    renderVehiculos(resultados);
}

// Manejo de errores de red
window.addEventListener('online', function() {
    showToast('Conexión restaurada', 'success');
});

window.addEventListener('offline', function() {
    showToast('Sin conexión a internet', 'error');
});

// Manejo de errores globales
window.addEventListener('error', function(e) {
    console.error('Error global:', e.error);
    showToast('Ha ocurrido un error inesperado', 'error');
});

// Export para uso en otros archivos si es necesario
window.ConcesionariaApp = {
    cargarVehiculos,
    cargarPedidos,
    crearVehiculo,
    crearPedido,
    abrirModalPedido,
    verDetallesVehiculo,
    showToast
};
