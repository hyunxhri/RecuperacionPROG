package calculanota

import logs.Logger
import java.math.BigDecimal
import java.math.RoundingMode

class CalculaRA(private var contenidoArchivo: List<String>, private var notasCE : MutableMap<String, MutableList<Double>> ) : Calculadora {

    companion object{
        private val log = Logger()
        val porcentajeRegex = Regex("^\"\\d+(,\\d+)?%\"$")
        var hayPorcentaje = false
    }

    override fun calculaPorcentajes(): MutableMap<String, MutableList<Double>> {
        val notasRA: MutableMap<String, MutableList<Double>> = mutableMapOf()
        var porcentajeRA = 0.0
        var suma = 0.0
        val listaDeNotasRA = mutableListOf<Double>()
        contenidoArchivo.forEach {elemento ->
            if(elemento.startsWith("RA") && elemento.length >= 3){
                notasRA[elemento] = listaDeNotasRA
            }
            if (elemento.matches(porcentajeRegex) && !hayPorcentaje) {
                porcentajeRA = elemento.trim('"').replace(",", ".").replace("%", "").toDouble()
                hayPorcentaje = true
            }
        }

        for (index in 0 until notasCE.values.first().size) {
            for (lista in notasCE.values) {
                suma += lista[index]
            }
            suma = suma * porcentajeRA / 100
            suma = BigDecimal(suma).setScale(3, RoundingMode.HALF_UP).toDouble()
            listaDeNotasRA.add(suma)
            suma = 0.0
        }

        hayPorcentaje = false
        log.info("calculanota.CalculaRA.calculaPorcentajes", "Se han almacenado las notas de los RA.")
        return notasRA
    }

}