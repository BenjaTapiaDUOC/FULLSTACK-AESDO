package com.tuapp.mspromociones.service;

import com.tuapp.mspromociones.dto.PromocionRequestDTO;
import com.tuapp.mspromociones.dto.PromocionResponseDTO;
import com.tuapp.mspromociones.exception.BadRequestException;
import com.tuapp.mspromociones.exception.PromocionNotFoundException;
import com.tuapp.mspromociones.model.Promocion;
import com.tuapp.mspromociones.repository.PromocionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ===========================================================
 * SERVICIO DE PROMOCIONES
 * ===========================================================
 *
 * Aquí se implementa toda la lógica de negocio.
 *
 * El Controller solamente llama a estos métodos.
 *
 * Esta clase se comunica directamente con el Repository.
 *
 * Este microservicio es consumido por mspedidos para
 * aplicar el descuento correspondiente al calcular el
 * total de un pedido.
 */

@Service
public class PromocionService {

    /**
     * Logger del microservicio.
     * Permite registrar eventos importantes durante la ejecución.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(PromocionService.class);

    private final PromocionRepository repository;

    public PromocionService(PromocionRepository repository) {
        this.repository = repository;
    }

    /**
     * ===========================================================
     * CREAR PROMOCION
     * ===========================================================
     *
     * Este metodo:
     *
     * 1. Verifica que el código no exista.
     * 2. Valida que las fechas de vigencia sean coherentes.
     * 3. Crea la entidad Promocion.
     * 4. Guarda la promoción en la base de datos.
     * 5. Devuelve un DTO como respuesta.
     */
    public PromocionResponseDTO crearPromocion(PromocionRequestDTO dto) {

        logger.info("Intentando crear promoción con código: {}", dto.getCodigo());

        // Validación para evitar códigos duplicados
        if (repository.existsByCodigo(dto.getCodigo())) {

            logger.warn("Ya existe una promoción registrada con el código {}", dto.getCodigo());

            throw new BadRequestException(
                    "Ya existe una promoción registrada con ese código."
            );

        }

        // Validación de coherencia entre fechas
        validarFechas(dto.getFechaInicio(), dto.getFechaFin());

        Boolean activo = dto.getActivo() == null ? Boolean.TRUE : dto.getActivo();

        Promocion promocion = new Promocion(
                null,
                dto.getCodigo(),
                dto.getPorcentajeDescuento(),
                dto.getFechaInicio(),
                dto.getFechaFin(),
                activo
        );

        Promocion guardada = repository.save(promocion);

        logger.info("Promoción {} creada correctamente.", guardada.getId());

        return mapearAResponseDTO(guardada);

    }

    /**
     * ===========================================================
     * LISTAR PROMOCIONES
     * ===========================================================
     *
     * Obtiene todas las promociones registradas.
     */
    public List<PromocionResponseDTO> listarPromociones() {

        logger.info("Consultando listado completo de promociones.");

        return repository.findAll()

                .stream()

                .map(this::mapearAResponseDTO)

                .collect(Collectors.toList());

    }

    /**
     * ===========================================================
     * BUSCAR PROMOCION POR ID
     * ===========================================================
     *
     * Busca una promoción utilizando su identificador.
     */
    public PromocionResponseDTO obtenerPorId(Long id) {

        logger.info("Buscando promoción con ID {}", id);

        Promocion promocion = repository.findById(id)

                .orElseThrow(() -> {

                    logger.warn("Promoción {} no encontrada.", id);

                    return new PromocionNotFoundException(
                            "Promoción no encontrada."
                    );

                });

        return mapearAResponseDTO(promocion);

    }

    /**
     * ===========================================================
     * ACTUALIZAR PROMOCION
     * ===========================================================
     *
     * Actualiza la información de una promoción existente.
     */
    public PromocionResponseDTO actualizar(Long id, PromocionRequestDTO dto) {

        logger.info("Actualizando promoción {}", id);

        Promocion promocion = repository.findById(id)

                .orElseThrow(() -> {

                    logger.warn("Promoción {} no encontrada.", id);

                    return new PromocionNotFoundException(
                            "Promoción no encontrada."
                    );

                });

        // Validar que el nuevo código no pertenezca a otra promoción
        if (!promocion.getCodigo().equals(dto.getCodigo())
                && repository.existsByCodigo(dto.getCodigo())) {

            logger.warn("Intento de actualizar con un código ya registrado.");

            throw new BadRequestException(
                    "El código ya pertenece a otra promoción."
            );

        }

        // Validación de coherencia entre fechas
        validarFechas(dto.getFechaInicio(), dto.getFechaFin());

        promocion.setCodigo(dto.getCodigo());

        promocion.setPorcentajeDescuento(dto.getPorcentajeDescuento());

        promocion.setFechaInicio(dto.getFechaInicio());

        promocion.setFechaFin(dto.getFechaFin());

        promocion.setActivo(dto.getActivo() == null ? promocion.getActivo() : dto.getActivo());

        Promocion actualizada = repository.save(promocion);

        logger.info("Promoción {} actualizada correctamente.", id);

        return mapearAResponseDTO(actualizada);

    }

    /**
     * ===========================================================
     * ELIMINAR PROMOCION
     * ===========================================================
     *
     * Elimina una promoción utilizando su ID.
     */
    public void eliminar(Long id) {

        logger.info("Intentando eliminar promoción {}", id);

        if (!repository.existsById(id)) {

            logger.warn("No se pudo eliminar la promoción {} porque no existe.", id);

            throw new PromocionNotFoundException(
                    "Promoción no encontrada."
            );

        }

        repository.deleteById(id);

        logger.info("Promoción {} eliminada correctamente.", id);

    }

    /**
     * ===========================================================
     * VALIDAR CUPÓN
     * ===========================================================
     *
     * Este metodo es utilizado por mspedidos (u otro
     * microservicio) para verificar si un cupón puede ser
     * aplicado a un pedido.
     *
     * Reglas de negocio:
     *
     * 1. Si el código no existe -> HTTP 404.
     * 2. Si el cupón está inactivo o ya fue usado -> HTTP 400.
     * 3. Si el cupón está vencido -> HTTP 400.
     * 4. Si el cupón aún no comienza su vigencia -> HTTP 400.
     */
    public PromocionResponseDTO validarCupon(String codigo) {

        logger.info("Validando cupón con código {}", codigo);

        Promocion promocion = buscarPorCodigoOFallar(codigo);

        verificarCuponUtilizable(promocion);

        logger.info("Cupón {} validado correctamente.", codigo);

        return mapearAResponseDTO(promocion);

    }

    /**
     * ===========================================================
     * APLICAR CUPÓN
     * ===========================================================
     *
     * Aplica un cupón válido y lo marca como utilizado,
     * dejándolo inactivo para que no pueda volver a
     * emplearse en otro pedido.
     *
     * Reglas de negocio: las mismas que validarCupon().
     */
    public PromocionResponseDTO aplicarCupon(String codigo) {

        logger.info("Intentando aplicar cupón con código {}", codigo);

        Promocion promocion = buscarPorCodigoOFallar(codigo);

        verificarCuponUtilizable(promocion);

        promocion.setActivo(false);

        Promocion actualizada = repository.save(promocion);

        logger.info("Cupón {} aplicado y marcado como utilizado.", codigo);

        return mapearAResponseDTO(actualizada);

    }

    /**
     * ===========================================================
     * MÉTODOS AUXILIARES
     * ===========================================================
     */

    /**
     * Busca una promoción por código o lanza
     * PromocionNotFoundException si no existe.
     */
    private Promocion buscarPorCodigoOFallar(String codigo) {

        return repository.findByCodigo(codigo)

                .orElseThrow(() -> {

                    logger.warn("Cupón {} inexistente.", codigo);

                    return new PromocionNotFoundException(
                            "El cupón ingresado no existe."
                    );

                });

    }

    /**
     * Verifica que un cupón esté activo y dentro
     * de su rango de vigencia.
     */
    private void verificarCuponUtilizable(Promocion promocion) {

        LocalDate hoy = LocalDate.now();

        if (!Boolean.TRUE.equals(promocion.getActivo())) {

            logger.warn("Cupón {} ya fue utilizado o está inactivo.", promocion.getCodigo());

            throw new BadRequestException(
                    "El cupón ya fue utilizado o se encuentra inactivo."
            );

        }

        if (hoy.isAfter(promocion.getFechaFin())) {

            logger.warn("Cupón {} se encuentra vencido.", promocion.getCodigo());

            throw new BadRequestException(
                    "El cupón se encuentra vencido."
            );

        }

        if (hoy.isBefore(promocion.getFechaInicio())) {

            logger.warn("Cupón {} aún no está vigente.", promocion.getCodigo());

            throw new BadRequestException(
                    "El cupón aún no se encuentra vigente."
            );

        }

    }

    /**
     * Valida que la fecha de fin no sea anterior
     * a la fecha de inicio.
     */
    private void validarFechas(LocalDate fechaInicio, LocalDate fechaFin) {

        if (fechaFin.isBefore(fechaInicio)) {

            logger.warn("Fechas inválidas: fechaFin es anterior a fechaInicio.");

            throw new BadRequestException(
                    "La fecha de fin no puede ser anterior a la fecha de inicio."
            );

        }

    }

    /**
     * Convierte una entidad Promocion en su DTO de respuesta.
     */
    private PromocionResponseDTO mapearAResponseDTO(Promocion promocion) {

        return new PromocionResponseDTO(

                promocion.getId(),

                promocion.getCodigo(),

                promocion.getPorcentajeDescuento(),

                promocion.getFechaInicio(),

                promocion.getFechaFin(),

                promocion.getActivo()

        );

    }

}
