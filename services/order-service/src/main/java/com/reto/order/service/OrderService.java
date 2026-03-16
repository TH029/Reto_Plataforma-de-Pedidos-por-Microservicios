package com.reto.order.service;

import com.reto.order.client.CatalogClient;
import com.reto.order.dto.CreateOrderRequest;
import com.reto.order.dto.OrderResponse;
import com.reto.order.dto.StockCheckResponse;
import com.reto.order.entity.OrderEntity;
import com.reto.order.entity.OrderStatus;
import com.reto.order.exception.InsufficientStockException;
import com.reto.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CatalogClient catalogClient;

    public OrderService(OrderRepository orderRepository, CatalogClient catalogClient) {
        this.orderRepository = orderRepository;
        this.catalogClient = catalogClient;
    }

    public OrderResponse crearPedido(CreateOrderRequest request, String correlationId) {
        StockCheckResponse stockResponse = catalogClient.checkStock(
                request.getProductId(),
                request.getCantidad(),
                correlationId
        );

        if (stockResponse == null || !Boolean.TRUE.equals(stockResponse.getAvailable())) {
            throw new InsufficientStockException(
                    "No hay stock suficiente para el producto " + request.getProductId()
            );
        }

        OrderEntity order = new OrderEntity();
        order.setProductId(request.getProductId());
        order.setCantidad(request.getCantidad());
        order.setEstado(OrderStatus.CREATED);

        OrderEntity savedOrder = orderRepository.save(order);

        return new OrderResponse(
                savedOrder.getId(),
                savedOrder.getProductId(),
                savedOrder.getCantidad(),
                savedOrder.getEstado(),
                savedOrder.getFechaCreacion()
        );
    }
}
import com.reto.order.entity.*;
import com.reto.order.repository.OrderItemRepository;
import com.reto.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CatalogServiceClient catalogServiceClient;

    public OrderService(OrderRepository orderRepository,
                       OrderItemRepository orderItemRepository,
                       CatalogServiceClient catalogServiceClient) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.catalogServiceClient = catalogServiceClient;
    }

    /**
     * Crea un nuevo pedido validando productos en Catalog Service
     * @param orderRequest DTO con los datos del pedido
     * @return OrderResponse con los datos del pedido creado
     */
    public OrderResponse crearPedido(OrderRequest orderRequest) {
        // Validaciones básicas
        if (orderRequest.getItems() == null || orderRequest.getItems().isEmpty()) {
            throw new RuntimeException("El pedido debe contener al menos un item");
        }

        if (orderRequest.getUsuarioId() == null || orderRequest.getUsuarioId() <= 0) {
            throw new RuntimeException("Usuario ID inválido");
        }

        // Crear la orden
        OrderEntity orden = new OrderEntity();
        orden.setUsuarioId(orderRequest.getUsuarioId());
        orden.setEstado("PENDING");
        orden.setTotal(BigDecimal.ZERO);

        // Procesar cada item
        BigDecimal totalPedido = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : orderRequest.getItems()) {
            // Validar cantidad
            if (itemRequest.getCantidad() == null || itemRequest.getCantidad() <= 0) {
                throw new RuntimeException("Cantidad inválida para producto: " + itemRequest.getProductoId());
            }

            // Consultar producto del Catalog Service
            CatalogProductDTO producto = catalogServiceClient.obtenerProducto(itemRequest.getProductoId());

            // Validar stock
            catalogServiceClient.validarStock(itemRequest.getProductoId(), itemRequest.getCantidad());

            // Crear item de orden con snapshot del producto
            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setOrden(orden);
            orderItem.setProductoId(producto.getId());
            orderItem.setProductoNombre(producto.getTitulo());
            orderItem.setCantidad(itemRequest.getCantidad());
            orderItem.setPrecioUnitario(producto.getPrecio());
            orderItem.setSubtotal(producto.getPrecio().multiply(BigDecimal.valueOf(itemRequest.getCantidad())));

            orden.getItems().add(orderItem);
            totalPedido = totalPedido.add(orderItem.getSubtotal());
        }

        // Establecer total y guardar
        orden.setTotal(totalPedido);
        OrderEntity ordenGuardada = orderRepository.save(orden);

        return convertirAResponse(ordenGuardada);
    }

    /**
     * Obtiene un pedido por su ID
     * @param ordenId ID del pedido
     * @return OrderResponse con los datos del pedido
     */
    public OrderResponse obtenerPedido(Long ordenId) {
        OrderEntity orden = orderRepository.findById(ordenId)
                .orElseThrow(() -> new PedidoNoEncontradoException("Pedido no encontrado: " + ordenId));

        return convertirAResponse(orden);
    }

    /**
     * Lista todos los pedidos de un usuario
     * @param usuarioId ID del usuario
     * @return Lista de OrderResponse con los pedidos del usuario
     */
    public List<OrderResponse> listarPedidosPorUsuario(Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new RuntimeException("Usuario ID inválido");
        }

        List<OrderEntity> ordenes = orderRepository.findByUsuarioId(usuarioId);
        return ordenes.stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza el estado de un pedido
     * @param ordenId ID del pedido
     * @param nuevoEstado nuevo estado (PENDING, CONFIRMED, DELIVERED, CANCELLED)
     * @return OrderResponse con los datos actualizados
     */
    public OrderResponse actualizarEstado(Long ordenId, String nuevoEstado) {
        OrderEntity orden = orderRepository.findById(ordenId)
                .orElseThrow(() -> new PedidoNoEncontradoException("Pedido no encontrado: " + ordenId));

        // Validar transiciones de estado
        validarTransicionEstado(orden.getEstado(), nuevoEstado);

        orden.setEstado(nuevoEstado);
        OrderEntity ordenActualizada = orderRepository.save(orden);

        return convertirAResponse(ordenActualizada);
    }

    /**
     * Convierte una OrderEntity a OrderResponse (DTO)
     */
    private OrderResponse convertirAResponse(OrderEntity orden) {
        List<OrderItemResponse> itemsResponse = orden.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProductoId(),
                        item.getProductoNombre(),
                        item.getCantidad(),
                        item.getPrecioUnitario(),
                        item.getSubtotal()
                ))
                .collect(Collectors.toList());

        return new OrderResponse(
                orden.getId(),
                orden.getUsuarioId(),
                orden.getEstado(),
                orden.getTotal(),
                orden.getFechaCreacion(),
                orden.getFechaActualizacion(),
                itemsResponse
        );
    }

    /**
     * Valida las transiciones de estado permitidas
     */
    private void validarTransicionEstado(String estadoActual, String nuevoEstado) {
        // Transiciones válidas: PENDING -> CONFIRMED, CONFIRMED -> DELIVERED, etc.
        if (estadoActual.equals("DELIVERED")) {
            throw new RuntimeException("No se puede cambiar un pedido entregado");
        }
        if (estadoActual.equals("CANCELLED")) {
            throw new RuntimeException("No se puede cambiar un pedido cancelado");
        }
        if (estadoActual.equals(nuevoEstado)) {
            throw new RuntimeException("El pedido ya está en estado: " + nuevoEstado);
        }
    }
}
