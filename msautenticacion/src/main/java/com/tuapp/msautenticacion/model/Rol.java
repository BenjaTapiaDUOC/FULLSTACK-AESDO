package com.tuapp.msautenticacion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ===========================================================
 * ENTIDAD ROL
 * ===========================================================
 *
 * Esta clase representa la tabla "roles" en la base de datos.
 *
 * Los roles disponibles en el sistema son:
 *
 * - ADMIN
 * - CLIENTE
 * - REPARTIDOR
 *
 * Estos roles se crean automáticamente al iniciar el
 * microservicio (ver config/RolInitializer).
 *
 * Un Usuario se relaciona con un único Rol (@ManyToOne),
 * ya que un usuario del sistema solo puede cumplir un rol
 * a la vez dentro de la plataforma.
 */

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rol {

    /**
     * Identificador único del rol.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del rol.
     *
     * Valores permitidos: ADMIN, CLIENTE, REPARTIDOR.
     */
    @Column(nullable = false, unique = true)
    private String nombre;

}
