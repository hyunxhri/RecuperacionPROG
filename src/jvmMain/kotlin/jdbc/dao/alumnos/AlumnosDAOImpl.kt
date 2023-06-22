package jdbc.dao.alumnos

import jdbc.dao.alumnos.entity.Alumnos
import jdbc.dao.calificaciones.CalificacionesDAOImpl
import java.math.BigDecimal
import java.math.RoundingMode
import javax.sql.DataSource

/**
 * Clase que gestiona la tabla alumnos.
 * @param dataSource
 */
class AlumnosDAOImpl(private val dataSource : DataSource): CalificacionesDAOImpl(dataSource){

    /**
     * Función que genera la fila de datos que se insertará o actualizará en la base de datos.
     * @param alumnos lista de pares de nombres e iniciales.
     * @param modulo
     * @param notasRA todas las notas de RA.
     */
    fun nuevoAlumno(alumnos: MutableList<Pair<String, String>>, modulo: String, notasRA: MutableList<MutableMap<String, MutableList<Double>>>) {
        var id = 0
        for ((counter, alumno) in alumnos.withIndex()) {
            val nombre = alumno.first
            val iniciales = alumno.second
            var nota = 0.0

            for (diccionario in notasRA) {
                for (clave in diccionario.keys) {
                val listaValores = diccionario[clave] // Obtener la lista de valores para el módulo actual

                if (listaValores != null) {
                    if (counter < listaValores.size) {
                        val valor = listaValores[counter] // Obtener el valor correspondiente al alumno actual
                        nota += valor
                    }
                }
                }
            }
            val notaRedondeada = BigDecimal(nota).setScale(3, RoundingMode.HALF_UP).toDouble()
            if (compruebaExistencia("ALUMNOS", id)) {
                actualizaAlumnos(Alumnos(id, iniciales, nombre, modulo, notaRedondeada.toString()))
            } else {
                creaAlumnos(Alumnos(id, iniciales, nombre, modulo, notaRedondeada.toString()))
            }
            id++
        }
    }

    /**
     * Función que actualiza una fila.
     * @param alumnos contenido para actualizar.
     */
    private fun actualizaAlumnos(alumnos: Alumnos) {
        val sql = "UPDATE ALUMNOS SET inicialesm = ?, nombrem = ?, modulom = ?, notam = ? WHERE id = ?"
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                with(stmt) {
                    setString(1, alumnos.inicialesm)
                    setString(2, alumnos.nombrem)
                    setString(3, alumnos.modulom)
                    setString(4, alumnos.notam)
                    setString(5, alumnos.id.toString())
                    executeUpdate()
                }
            }
        }
    }

    /**
     * Función que inserta una nueva fila.
     * @param alumnos fila a insertar.
     */
    private fun creaAlumnos(alumnos: Alumnos) {
        val sql = "INSERT INTO ALUMNOS (id, inicialesm, nombrem, modulom, notam) VALUES (?, ?, ?, ?, ?)"
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                with(stmt) {
                    setInt(1, alumnos.id)
                    setString(2, alumnos.inicialesm)
                    setString(3, alumnos.nombrem)
                    setString(4, alumnos.modulom)
                    setString(5, alumnos.notam)
                    executeUpdate()
                }
            }
        }
    }

}
