package jdbc.dao.calificaciones

import java.sql.Connection

interface CalificacionesDAO {

    fun creaBD()
    fun compruebaExistencia(table: String, value: Int): Boolean
    fun borraDatos()

}