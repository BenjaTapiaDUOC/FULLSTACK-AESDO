package com.tuapp.msrepartidores.model;

/**
 * ===========================================================
 * ENUM ESTADO REPARTIDOR
 * ===========================================================
 *
 * Representa los posibles estados en los que puede
 * encontrarse un repartidor dentro del sistema.
 *
 * DISPONIBLE:
 * El repartidor puede recibir una nueva asignación
 * de delivery.
 *
 * EN_RUTA:
 * El repartidor ya tiene un delivery asignado y se
 * encuentra realizando la entrega.
 *
 * INACTIVO:
 * El repartidor no está operando (por ejemplo, fuera
 * de turno) y no puede recibir asignaciones.
 *
 * Este enum es utilizado por el microservicio msdelivery
 * para validar si un repartidor puede ser asignado a un
 * nuevo delivery (regla de negocio: solo se asignan
 * repartidores en estado DISPONIBLE).
 */

public enum EstadoRepartidor {

    DISPONIBLE,
    EN_RUTA,
    INACTIVO

}
