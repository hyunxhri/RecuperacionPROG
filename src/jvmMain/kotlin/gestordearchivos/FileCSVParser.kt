package gestordearchivos

import calculanota.CalculaRA
import logs.Logger

class FileCSVParser(private val textoArchivosCSV: MutableList<List<String>>) {

    companion object {
        private val log = Logger()
        private const val PUNTO = '.'
        private const val UDLONGITUD= 4
        private const val PREFIJOUNIDAD = "UD"
    }

    private fun parseaContenidoDeUnArchivo(contenidoArchivo: List<String>): List<String> {
        val contenidoCSV = contenidoArchivo[0].split(",", "\n", "\r")
        val camposParseados = mutableListOf<String>()
        var campoAgrupado = ""
        var creaCampoAgrupado = false
        log.info("gestordearchivos.FileCSVParser.parseaContenidoDeUnArchivo", "Parseando el archivo...")
        contenidoCSV.forEach { campo ->
            if (campo.endsWith('"')) {
                creaCampoAgrupado = false
                campoAgrupado += campo
                camposParseados.add(campoAgrupado)
                campoAgrupado = ""
            } else if (campo.startsWith('"') && !campo.endsWith('"')) {
                campoAgrupado += "$campo,"
                creaCampoAgrupado = true
            } else if (creaCampoAgrupado) {
                campoAgrupado += "$campo,"
            } else {
                camposParseados.add(campo)
            }
        }
        log.info("gestordearchivos.FileCSVParser.parseaContenidoDeUnArchivo", "Se ha parseado el contenido del archivo.")
        return camposParseados.toList()
    }

    fun parsearContenidoArchivos(): MutableList<List<String>> {
        val contenidoArchivosCSVParseados = mutableListOf<List<String>>()
        for (csvSinParseo in textoArchivosCSV) {
            val csvParseado = parseaContenidoDeUnArchivo(csvSinParseo)
            contenidoArchivosCSVParseados.add(csvParseado)
        }
        log.info("gestordearchivos.FileCSVParser.parseaContenidoDeUnArchivo", "Todos los archivos han sido parseados correctamente.")
        return contenidoArchivosCSVParseados
    }

    private fun obtenerFilasCE(contenidoParseado: List<String>): MutableList<MutableList<String>> {
        val filasCETodosLosArchivos = mutableListOf<MutableList<String>>()
        var creandoFila = false
        contenidoParseado.forEach { elemento ->
            if (elemento.startsWith(PREFIJOUNIDAD) && elemento.length >= UDLONGITUD && elemento[3] == PUNTO) {
                val filaCE = mutableListOf<String>()
                filaCE.add(elemento)
                filasCETodosLosArchivos.add(filaCE)
                creandoFila = true
            } else if (elemento.startsWith("RA") && creandoFila) {
                creandoFila = false
            } else if (creandoFila) {
                filasCETodosLosArchivos.last().add(elemento)
            }
        }
        log.info("gestordearchivos.FileCSVParser.obtenerFilasCE", "Se han obtenido las filas correspondientes a los CE del archivo.")
        return filasCETodosLosArchivos
    }

    fun obtenerFilasCEArchivos(listaDeContenidoParseado: MutableList<List<String>>): MutableList<MutableList<MutableList<String>>> {
        val filasCETodosLosArchivos = mutableListOf<MutableList<MutableList<String>>>()
        for (contenidoParseado in listaDeContenidoParseado){
            val filasCE = obtenerFilasCE(contenidoParseado)
            filasCETodosLosArchivos.add(filasCE)
        }
        log.info("gestordearchivos.FileCSVParser.obtenerFilasCEArchivos", "Se han obtenido las filas correspondientes a los CE de todos los archivos.")
        return filasCETodosLosArchivos
    }


    private fun obtenerTareasCE(contenidoParseado: List<String>) : MutableList<MutableList<String>>{
        val filasTareasTodosLosArchivos = mutableListOf<MutableList<String>>()
        val regexTareas = Regex("^\"[a-z](,[a-z])*\"$")
        var creandoFila = false
        contenidoParseado.forEach{ elemento ->
            if (regexTareas.matches(elemento)){
                val filaTareas = mutableListOf<String>()
                filaTareas.add(elemento)
                filasTareasTodosLosArchivos.add(filaTareas)
                creandoFila = true
            } else if(creandoFila){
                filasTareasTodosLosArchivos.last().add(elemento)
            }
        }
        log.info("gestordearchivos.FileCSVParser.obtenerTareasCE", "Se han obtenido las tareas correspondientes a los CE del archivo.")
        return filasTareasTodosLosArchivos
    }

    fun obtenerTareasCEArchivos(listaDeContenidoParseado: MutableList<List<String>>) : MutableList<MutableList<MutableList<String>>>{
        val tareasCETodosLosArchivos = mutableListOf<MutableList<MutableList<String>>>()
        for (contenidoParseado in listaDeContenidoParseado){
            val tareasCE = obtenerTareasCE(contenidoParseado)
            tareasCETodosLosArchivos.add(tareasCE)
        }
        log.info("gestordearchivos.FileCSVParser.obtenerTareasCEArchivos", "Se han obtenido las tareas correspondientes a los CE de todos los archivos.")
        return tareasCETodosLosArchivos
    }

    fun obtenerNombreseIniciales(contenidoParseado: MutableList<List<String>>): MutableList<Pair<String, String>> {
        val nombreRegex = Regex("^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ]+(\\s[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ]+)+\$")
        val paresDeNombres = mutableListOf<Pair<String, String>>()
        contenidoParseado[0].forEach { elemento ->
            if (elemento.matches(nombreRegex)) {
                val nombre = elemento.split(" ")
                val iniciales = buildString {
                    append(nombre[0].first())
                    append(nombre[nombre.size - 2].trim().first())
                    append(nombre.last().trim().first())
                }
                val nombreConIniciales = Pair(elemento, iniciales)
                paresDeNombres.add(nombreConIniciales)
            }
        }
        return paresDeNombres
    }

    fun obtenerPorcentajes(contenidoArchivo : MutableList<List<String>>): MutableList<Double> {
        val listaPorcentajes = mutableListOf<Double>()
        var porcentajeAlmacenado = false
        for(archivo in contenidoArchivo) {
            archivo.forEach { elemento ->
                if(!porcentajeAlmacenado){
                    if (elemento.matches(CalculaRA.porcentajeRegex) && !CalculaRA.hayPorcentaje) {
                        val porcentaje = elemento.trim('"').replace(",", ".").replace("%", "").toDouble()
                        listaPorcentajes.add(porcentaje)
                        porcentajeAlmacenado = true
                    }
                }
            }
            porcentajeAlmacenado = false
        }
        return listaPorcentajes
    }
}
