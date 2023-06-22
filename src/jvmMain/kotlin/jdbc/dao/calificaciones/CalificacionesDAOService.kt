package jdbc.dao.calificaciones

class CalificacionesDAOService(private val calificacionesDAO : CalificacionesDAOImpl) : CalificacionesDAO{

    override fun creaBD() {
        return calificacionesDAO.creaBD()
    }

    override fun compruebaExistencia( table: String, value: Int): Boolean {
        return calificacionesDAO.compruebaExistencia(table, value)
    }

    override fun borraDatos() {
        return calificacionesDAO.borraDatos()
    }
}