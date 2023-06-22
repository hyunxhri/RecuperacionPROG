package calculanota

import logs.Logger
import java.math.BigDecimal
import java.math.RoundingMode

class CalculaCE (private var filasNotas: MutableList<MutableList<String>>, private var notasInstrumentos : MutableMap<String, MutableList<Double>>) : Calculadora {

    companion object{
        private val log = Logger()
    }
    override fun calculaPorcentajes(): MutableMap<String, MutableList<Double>> {
        val notasInstrumentosActualizadas = mutableMapOf<String, MutableList<Double>>()
        var contFilas = 0
        notasInstrumentos.forEach{ (clave, valor) ->
            //Cogemos el porcentaje de cada fila utilizando un contador para que vaya a la par que los RA.
            val porcentaje = filasNotas[contFilas][4].trim('"').substringBefore(",").toInt()
            val notasInstrumentos = valor.map { nota -> BigDecimal(nota * porcentaje / 100.0).setScale(3, RoundingMode.HALF_UP).toDouble() }.toMutableList()
            notasInstrumentosActualizadas[clave] = notasInstrumentos
            contFilas++
        }
        log.info("calculanota.CalculaCE.calculaPorcentaje", "Se han almacenado las notas de los CE.")
        return notasInstrumentosActualizadas
    }

}
