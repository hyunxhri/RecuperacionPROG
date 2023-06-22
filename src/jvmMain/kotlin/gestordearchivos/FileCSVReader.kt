package gestordearchivos

import logs.Logger
import java.io.File

class FileCSVReader(private val rutaDirectorio: String) {
    companion object {
        private val log = Logger()
        private const val EXTENSION = "csv"
    }


    fun listaArchivos(): MutableList<File> {
        val listaDeArchivos = mutableListOf<File>()
        val directorio = File(rutaDirectorio)
        val contenidoDirectorio = directorio.listFiles()
        val directorioCorrecto = compruebaDirectorio(contenidoDirectorio)
        log.info("gestordearchivos.FileCSVReader.listaArchivos", "Obteniendo los archivos CSV de $rutaDirectorio")
        if (directorioCorrecto) {
                for (contenido in contenidoDirectorio) {
                    if (contenido.isFile && contenido.extension == EXTENSION) {
                         listaDeArchivos.add(contenido)
                    }
                }
        }
        log.info("gestordearchivos.FileCSVReader.listaArchivos", "Se han obtenido ${listaDeArchivos.size} archivos CSV correctamente de $rutaDirectorio")
        return listaDeArchivos
    }

    private fun compruebaDirectorio(directorio: Array<File>?): Boolean {
        if (directorio == null) {
            log.warning("gestordearchivos.FileCSVReader.compruebaDirectorio", "El directorio con ruta: $rutaDirectorio no existe.")
            throw Exception("ERROR: El directorio no existe.")
        } else if (directorio.isEmpty()) {
            log.warning("gestordearchivos.FileCSVReader.compruebaDirectorio", "El directorio con ruta: $rutaDirectorio está vacío.")
            throw Exception("ERROR: El directorio está vacío.")
        }
        return true
    }

    private fun leeUnArchivo(archivo: File): List<String> {
        log.info("gestordearchivos.FileCSVReader.leeUnArchivo", "Leyendo el archivo: ${archivo.name}")
        return listOf(archivo.readText())
    }

    fun leeArchivos(listaDeArchivos: MutableList<File>): MutableList<List<String>> {
        val contenidoArchivosCSV = mutableListOf<List<String>>()
        for (archivo in listaDeArchivos) {
            val contenidoArchivoParaAgregar = leeUnArchivo(archivo)
            contenidoArchivosCSV.add(contenidoArchivoParaAgregar)
        }
        log.info(
            "gestordearchivos.FileCSVReader.leeArchivos",
            "Se han leído y obtenido el contenido de ${listaDeArchivos.size} archivos CSV."
        )
        return contenidoArchivosCSV
    }

}

