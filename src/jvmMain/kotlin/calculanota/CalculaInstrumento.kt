package calculanota

import logs.Logger

/**
 * Clase encargada de realizar los calculos de los Instrumentos.
 * @param filasTareasCE Filas donde se encuentran las tareas (instrumentos).
 */
class CalculaInstrumento(private var filasTareasCE: MutableList<MutableList<String>>) : Calculadora {

    companion object{
        private val log = Logger()
        val regexCE = Regex("^[a-z]+$")
        val regexNota = Regex("^\"[0-9],\\d+\"")
        val regexNotaInt = Regex("^[0-9]|10$")
    }

    /**
     * Función que calcula las notas de los instrumentos.
     * @return Mapa cuyas claves son los criterios y los valores las notas de todos los alumnos para ese criterio.
     */
    override fun calculaPorcentajes(): MutableMap<String, MutableList<Double>> {
        val notasPorCriterio = mutableMapOf<String, MutableList<Double>>()
        filasTareasCE.forEach { fila ->
            val notas = obtenerNotas(fila)
            for (criterio in fila[0]) {
                val criterioString = criterio.toString()
                if (criterioString.matches(regexCE)) {
                    if (notasPorCriterio.containsKey(criterioString)) {
                        val notasAnteriores = notasPorCriterio[criterioString]
                        val notasSumadas = sumarNotas(notas, notasAnteriores!!)
                        notasPorCriterio[criterioString] = notasSumadas
                    } else {
                        notasPorCriterio[criterioString] = notas.toMutableList()
                    }
                }
            }
        }

        log.info("calculanota.CalculaInstrumento.calculaPorcentaje", "Se han almacenado las notas de los Instrumentos.")
        return notasPorCriterio.toSortedMap()
    }

    /**
     * Función que obtiene las notas de los instrumentos de esa fila y aplica el porcentaje.
     * @param fila Fila de Instrumentos.
     * @return Lista de notas de esa fila.
     */
    private fun obtenerNotas(fila: MutableList<String>): MutableList<Double> {
        val notas: MutableList<Double> = mutableListOf()
        var porcentaje = 0
        for ((index, elemento) in fila.withIndex()) {
            if (index == 1) {
                continue  // Ignorar el elemento de índice 2, para que no se interprete como nota.
            }
            if (elemento.endsWith("%")) {
                porcentaje = elemento.slice(0 until elemento.length - 1).toInt()
            } else if (regexNotaInt.matches(elemento)) {
                val nota = elemento.toDouble() * porcentaje / 100
                notas.add(nota)
            } else if (regexNota.matches(elemento)) {
                val elementoDouble = elemento.trim('"').replace(",", ".").toDouble()
                val nota = elementoDouble * porcentaje / 100
                notas.add(nota)
            }
        }
        log.info("calculanota.CalculaInstrumento.obtenerNotas", "Se han obtenido las notas correctamente.")
        return notas
    }


    /**
     * Función que suma las notas.
     * @param notas
     * @param notasAnteriores
     * @return Lista de notas actualizada.
     */
    private fun sumarNotas(notas: List<Double>, notasAnteriores: List<Double>): MutableList<Double> {
        log.info("calculanota.CalculaInstrumento.sumarNotas", "Se han sumado las notas correctamente.")
        return notas.zip(notasAnteriores) { a, b -> a + b }.toMutableList()
    }

}