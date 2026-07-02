package com.tuapp.msproductos.service;

import com.tuapp.msproductos.dto.ProductoRequestDTO;
import com.tuapp.msproductos.dto.ProductoResponseDTO;
import com.tuapp.msproductos.exception.BadRequestException;
import com.tuapp.msproductos.exception.ProductoNotFoundException;
import com.tuapp.msproductos.model.Producto;
import com.tuapp.msproductos.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ===========================================================
 * SERVICIO DE PRODUCTOS
 * ===========================================================
 *
 * Aquí se implementa toda la lógica de negocio.
 *
 * El Controller solamente llama a estos métodos.
 *
 * Esta clase se comunica directamente con el Repository.
 */

@Service
public class ProductoService {

    /**
     * Logger del microservicio.
     * Permite registrar eventos importantes durante la ejecución.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(ProductoService.class);

    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    /**
     * ===========================================================
     * CREAR PRODUCTO
     * ===========================================================
     *
     * Este metodo:
     *
     * 1. Verifica que no exista un producto con el mismo nombre.
     * 2. Crea la entidad Producto.
     * 3. Guarda el producto en la base de datos.
     * 4. Devuelve un DTO como respuesta.
     */
    public ProductoResponseDTO crearProducto(ProductoRequestDTO dto) {

        logger.info("Intentando crear producto: {}", dto.getNombre());

        // Validación para evitar productos duplicados
        if (repository.existsByNombreIgnoreCase(dto.getNombre())) {

            logger.warn("Ya existe un producto registrado con el nombre {}", dto.getNombre());

            throw new BadRequestException(
                    "Ya existe un producto registrado con ese nombre."
            );

        }

        Producto producto = new Producto(
                null,
                dto.getNombre(),
                dto.getPrecio(),
                dto.getCategoria()
        );

        Producto guardado = repository.save(producto);

        logger.info("Producto {} creado correctamente.", guardado.getId());

        return mapToDTO(guardado);

    }

    /**
     * ===========================================================
     * LISTAR PRODUCTOS
     * ===========================================================
     *
     * Obtiene todos los productos registrados.
     */
    public List<ProductoResponseDTO> listarProductos() {

        logger.info("Consultando listado completo de productos.");

        return repository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

    }

    /**
     * ===========================================================
     * BUSCAR PRODUCTO POR ID
     * ===========================================================
     *
     * Busca un producto utilizando su identificador.
     */
    public ProductoResponseDTO obtenerPorId(Long id) {

        logger.info("Buscando producto con ID {}", id);

        Producto producto = repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Producto {} no encontrado.", id);
                    return new ProductoNotFoundException("Producto no encontrado.");
                });

        return mapToDTO(producto);

    }

    /**
     * ===========================================================
     * ACTUALIZAR PRODUCTO
     * ===========================================================
     *
     * Actualiza la información de un producto existente.
     */
    public ProductoResponseDTO actualizar(Long id, ProductoRequestDTO dto) {

        logger.info("Actualizando producto {}", id);

        Producto producto = repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Producto {} no encontrado.", id);
                    return new ProductoNotFoundException("Producto no encontrado.");
                });

        // Validar que el nuevo nombre no pertenezca a otro producto
        if (!producto.getNombre().equalsIgnoreCase(dto.getNombre())
                && repository.existsByNombreIgnoreCase(dto.getNombre())) {

            logger.warn("Intento de actualizar con un nombre ya registrado.");

            throw new BadRequestException(
                    "Ya existe otro producto con ese nombre."
            );

        }

        producto.setNombre(dto.getNombre());
        producto.setPrecio(dto.getPrecio());
        producto.setCategoria(dto.getCategoria());

        Producto actualizado = repository.save(producto);

        logger.info("Producto {} actualizado correctamente.", id);

        return mapToDTO(actualizado);

    }

    /**
     * ===========================================================
     * ELIMINAR PRODUCTO
     * ===========================================================
     *
     * Elimina un producto utilizando su ID.
     */
    public void eliminar(Long id) {

        logger.info("Intentando eliminar producto {}", id);

        if (!repository.existsById(id)) {
            logger.warn("No se pudo eliminar el producto {} porque no existe.", id);
            throw new ProductoNotFoundException("Producto no encontrado.");
        }

        repository.deleteById(id);

        logger.info("Producto {} eliminado correctamente.", id);

    }

    /**
     * ===========================================================
     * MAPEO ENTIDAD -> DTO
     * ===========================================================
     */
    private ProductoResponseDTO mapToDTO(Producto producto) {
        return new ProductoResponseDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getCategoria()
        );
    }

}