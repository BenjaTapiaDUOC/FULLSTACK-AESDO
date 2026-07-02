package com.tuapp.msusuarios.service;

import com.tuapp.msusuarios.dto.UsuarioRequestDTO;
import com.tuapp.msusuarios.dto.UsuarioResponseDTO;
import com.tuapp.msusuarios.exception.BadRequestException;
import com.tuapp.msusuarios.exception.UsuarioNotFoundException;
import com.tuapp.msusuarios.model.Usuario;
import com.tuapp.msusuarios.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ===========================================================
 * SERVICIO DE USUARIOS
 * ===========================================================
 *
 * Aquí se implementa toda la lógica de negocio.
 *
 * El Controller solamente llama a estos métodos.
 *
 * Esta clase se comunica directamente con el Repository.
 */

@Service
public class UsuarioService {

    /**
     * Logger del microservicio.
     * Permite registrar eventos importantes durante la ejecución.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    /**
     * ===========================================================
     * CREAR USUARIO
     * ===========================================================
     *
     * Este metodo:
     *
     * 1. Verifica que el correo no exista.
     * 2. Crea la entidad Usuario.
     * 3. Guarda el usuario en la base de datos.
     * 4. Devuelve un DTO como respuesta.
     */
    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO dto) {

        logger.info("Intentando crear usuario con correo: {}", dto.getEmail());

        // Validación para evitar correos duplicados
        if (repository.existsByEmail(dto.getEmail())) {

            logger.warn("Ya existe un usuario registrado con el correo {}", dto.getEmail());

            throw new BadRequestException(
                    "Ya existe un usuario registrado con ese correo."
            );

        }

        Usuario usuario = new Usuario(
                null,
                dto.getNombre(),
                dto.getEmail(),
                dto.getPassword()
        );

        Usuario guardado = repository.save(usuario);

        logger.info("Usuario {} creado correctamente.", guardado.getId());

        return new UsuarioResponseDTO(
                guardado.getId(),
                guardado.getNombre(),
                guardado.getEmail(),
                guardado.getPassword()
        );

    }

    /**
     * ===========================================================
     * LISTAR USUARIOS
     * ===========================================================
     *
     * Obtiene todos los usuarios registrados.
     */
    public List<UsuarioResponseDTO> listarUsuarios() {

        logger.info("Consultando listado completo de usuarios.");

        return repository.findAll()

                .stream()

                .map(usuario -> new UsuarioResponseDTO(

                        usuario.getId(),

                        usuario.getNombre(),

                        usuario.getEmail(),

                        usuario.getPassword()

                ))

                .collect(Collectors.toList());

    }

    /**
     * ===========================================================
     * BUSCAR USUARIO POR ID
     * ===========================================================
     *
     * Busca un usuario utilizando su identificador.
     */
    public UsuarioResponseDTO obtenerPorId(Long id) {

        logger.info("Buscando usuario con ID {}", id);

        Usuario usuario = repository.findById(id)

                .orElseThrow(() -> {

                    logger.warn("Usuario {} no encontrado.", id);

                    return new UsuarioNotFoundException(
                            "Usuario no encontrado."
                    );

                });

        return new UsuarioResponseDTO(

                usuario.getId(),

                usuario.getNombre(),

                usuario.getEmail(),

                usuario.getPassword()

        );

    }

    /**
     * ===========================================================
     * ACTUALIZAR USUARIO
     * ===========================================================
     *
     * Actualiza la información de un usuario existente.
     */
    public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO dto) {

        logger.info("Actualizando usuario {}", id);

        Usuario usuario = repository.findById(id)

                .orElseThrow(() -> {

                    logger.warn("Usuario {} no encontrado.", id);

                    return new UsuarioNotFoundException(
                            "Usuario no encontrado."
                    );

                });

        // Validar que el nuevo correo no pertenezca a otro usuario
        if (!usuario.getEmail().equals(dto.getEmail())
                && repository.existsByEmail(dto.getEmail())) {

            logger.warn("Intento de actualizar con un correo ya registrado.");

            throw new BadRequestException(
                    "El correo ya pertenece a otro usuario."
            );

        }

        usuario.setNombre(dto.getNombre());

        usuario.setEmail(dto.getEmail());

        usuario.setPassword(dto.getPassword());

        Usuario actualizado = repository.save(usuario);

        logger.info("Usuario {} actualizado correctamente.", id);

        return new UsuarioResponseDTO(

                actualizado.getId(),

                actualizado.getNombre(),

                actualizado.getEmail(),

                actualizado.getPassword()

        );

    }

    /**
     * ===========================================================
     * ELIMINAR USUARIO
     * ===========================================================
     *
     * Elimina un usuario utilizando su ID.
     */
    public void eliminar(Long id) {

        logger.info("Intentando eliminar usuario {}", id);

        if (!repository.existsById(id)) {

            logger.warn("No se pudo eliminar el usuario {} porque no existe.", id);

            throw new UsuarioNotFoundException(
                    "Usuario no encontrado."
            );

        }

        repository.deleteById(id);

        logger.info("Usuario {} eliminado correctamente.", id);

    }

}