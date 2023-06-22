package jdbc.dao.notas

import jdbc.dao.calificaciones.CalificacionesDAOImpl
import jdbc.dao.notas.entity.Notas
import java.math.BigDecimal
import java.math.RoundingMode
import javax.sql.DataSource

class NotasDAOImpl(private val dataSource: DataSource) : CalificacionesDAOImpl(dataSource) {
    fun nuevaNota(alumnos: MutableList<Pair<String, String>>, modulo: String, notasRA: MutableList<MutableMap<String, MutableList<Double>>>, notasCE : List<MutableMap<String, MutableList<Double>>>
    ){
        var id = 0
        var indice = 0
        var counter = 0

        alumnos.forEach { alumno ->
            val iniciales = alumno.second
            val notaCEAlumno = mutableListOf<Pair<String, Double>>()
            var notaRA = ""
            notasRA.forEach { ra ->
                val idRA = ra.keys.toString()
                ra.mapValues { (_, lista) ->
                    val nota = BigDecimal(lista[counter]).setScale(3, RoundingMode.HALF_UP).toString()
                    notaRA = nota
                }
                for (clave in notasCE[indice].keys) {
                    val criterio = "CE.$clave"
                    val listaValores = notasCE[indice][clave] // Obtener la lista de valores para el módulo actual
                    if (listaValores != null) {
                        if (counter < listaValores.size) {
                            val notaRedondeada = BigDecimal(listaValores[counter]).setScale(3, RoundingMode.HALF_UP).toDouble()
                            val notaPorCriterio = Pair(criterio, notaRedondeada)
                            notaCEAlumno.add(notaPorCriterio) // Obtener el valor correspondiente al alumno actual
                        }
                    }
                }
                if (compruebaExistencia("NOTAS", id)) {
                    actualizaNota(Notas(id, iniciales, modulo, idRA, notaRA, notaCEAlumno.joinToString(",")))
                } else {
                    creaNota(Notas(id, iniciales, modulo, idRA, notaRA, notaCEAlumno.joinToString(",")))
                }
                notaCEAlumno.clear()
                id++
                indice++
            }
            counter++
            indice = 0
        }
    }

    private fun actualizaNota(notas: Notas) {
        val sql = "UPDATE NOTAS SET inicialesn = ?, modulon = ?, idRAn = ?, notaRAn = ?, notasCEn = ? WHERE id = ?"
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                with(stmt) {
                    setString(1, notas.inicialesn)
                    setString(2, notas.modulon)
                    setString(3, notas.idRAn)
                    setString(4, notas.notaRAn)
                    setString(5, notas.notasCEn)
                    setString(6, notas.id.toString())
                    executeUpdate()
                }
            }
        }
    }

    private fun creaNota(notas: Notas) {
        val sql = "INSERT INTO NOTAS (id, inicialesn, modulon, idRAn, notaRAn, notasCEn) VALUES (?, ?, ?, ?, ?, ?)"
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                with(stmt) {
                    setInt(1, notas.id)
                    setString(2, notas.inicialesn)
                    setString(3, notas.modulon)
                    setString(4, notas.idRAn)
                    setString(5, notas.notaRAn)
                    setString(6, notas.notasCEn)
                    executeUpdate()
                }
            }
        }
    }

}