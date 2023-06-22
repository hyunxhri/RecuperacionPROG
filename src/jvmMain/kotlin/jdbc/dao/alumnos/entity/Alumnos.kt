package jdbc.dao.alumnos.entity

/**
 * Clase para almacenar información.
 * @param id identificador del alumno.
 * @param inicialesm iniciales del alumno.
 * @param nombrem nombre del alumno.
 * @param modulom nombre del modulo.
 * @param notam nota del alumno.
 */
data class Alumnos(val id : Int, val inicialesm : String, val nombrem : String, val modulom : String, val notam : String)
