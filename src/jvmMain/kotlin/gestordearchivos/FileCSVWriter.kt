package gestordearchivos

import logs.Logger
import java.io.File

class FileCSVWriter(
    private val notasUnidad: MutableMap<String, MutableList<Double>>,
    contenidoRA: MutableMap<String, MutableList<Double>>,
    contenidoCriterios: MutableList<MutableList<String>>,
    private val contenidoArchivos: List<String>
) {

    private val contenido = contenidoCriterios.toMutableList()
    private val notasRA = contenidoRA
    companion object {
        private val log = Logger()
        private const val PUNTO = '.'
        private const val UDLONGITUD = 4
        private const val PREFIJOUNIDAD = "UD"
    }

    fun modificaContenido(): MutableList<String> {
        val listaNotasParaAgregar = mutableListOf<String>()
        val listaNotasActualizada = mutableListOf<MutableList<String>>()
        contenido.forEach { fila ->
            val unidadLetra = fila[0].last().toString()
            val notasParaAgregar = notasUnidad[unidadLetra]
            notasParaAgregar?.forEach { nota ->
                var notaString = nota.toString().replace(".", ",")
                notaString = "\"$notaString\""
                listaNotasParaAgregar.add(notaString)
            }
            val nuevaFila = mutableListOf("") + fila.subList(0, 6) + listaNotasParaAgregar
            listaNotasActualizada.add(nuevaFila as MutableList<String>)
            listaNotasParaAgregar.clear()
        }

        return reemplazarPorContenidoNuevo(listaNotasActualizada)
    }

    private fun reemplazarPorContenidoNuevo(
        listaNotas: MutableList<MutableList<String>>,
    ): MutableList<String> {
        val listaContenidoActualizado = mutableListOf<String>()
        val listaContenidoPorLineas = mutableListOf<List<String>>()
        var contListaNotas = 0
        val lineas = contenidoArchivos[0].split("\n", "\r")
        listaContenidoPorLineas.add(lineas)
        listaContenidoPorLineas.forEach { unidad ->
            unidad.forEach { linea ->
                val lineaSeparada = linea.split(",")
                if (lineaSeparada.size >= 2 && lineaSeparada[1].startsWith(PREFIJOUNIDAD) && lineaSeparada[1].length >= UDLONGITUD && lineaSeparada[1][3] == PUNTO) {
                    if (contListaNotas < listaNotas.size) {
                        val lineaNueva = listaNotas[contListaNotas].joinToString(",")
                        contListaNotas++
                        listaContenidoActualizado.add(lineaNueva)
                    } else {
                        listaContenidoActualizado.add(linea)
                    }
                } else if (lineaSeparada[0].startsWith(PREFIJOUNIDAD) && lineaSeparada[0].length >= 3) {
                    val notasParaAgregar = cambiaFormatoRA()
                    val posicion = consiguePosicionPorcentaje(lineaSeparada)
                    val lineaNueva = lineaSeparada.subList(0, posicion).joinToString(",") + "," + notasParaAgregar.joinToString(",")
                    listaContenidoActualizado.add(lineaNueva)
                } else {
                    listaContenidoActualizado.add(linea)
                }
            }
        }
        return listaContenidoActualizado
    }

    private fun consiguePosicionPorcentaje(linea:List<String>) : Int{
        var contadorPosicion = 0
        linea.forEach{elemento ->
            if(elemento.contains("%")){
                contadorPosicion ++
                return contadorPosicion
            }
            else{
                contadorPosicion++
            }
        }
        return contadorPosicion
    }

    private fun cambiaFormatoRA() : MutableList<String>{
        val notasRAFinales = mutableListOf<String>()
        for(notasRA in notasRA.values){
            for(nota in notasRA){
                var notaFinal = nota.toString().replace(".", ",")
                notaFinal = "\"$notaFinal\""
                notasRAFinales.add(notaFinal)
            }
        }
        return notasRAFinales
    }

    fun sobreescribirArchivos(notasFinales: MutableList<String>, archivo: File) {
        val archivoCSV = File(archivo.toString())
        archivoCSV.bufferedWriter().use { writer ->
            notasFinales.forEach { fila ->
                writer.write(fila)
                writer.newLine()
            }
        }
    }


}



