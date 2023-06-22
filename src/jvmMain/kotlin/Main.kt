import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import app.app
import calculanota.CalculaCE
import calculanota.CalculaInstrumento
import calculanota.CalculaRA
import gestordearchivos.FileCSVParser
import gestordearchivos.FileCSVReader
import gestordearchivos.FileCSVWriter
import jdbc.dao.alumnos.AlumnosDAOImpl
import jdbc.dao.calificaciones.CalificacionesDAOImpl
import jdbc.dao.calificaciones.CalificacionesDAOService
import jdbc.dao.notas.NotasDAOImpl
import jdbc.pooldeconexiones.ConnectionPool
import java.io.File

/**
 * Función que calcula las notas de los Instrumentos de cada archivo y los añade a una lista.
 * @param tareasContenido Es el contenido parseado de los instrumentos.
 * @return Una lista que contiene un diccionario por cada unidad, el cual tiene como clave la
 *         letra que identifica el criterio de la tarea y como valor una lista con todas las notas de ese criterio.
 */
fun calcularNotasInstrumentos(tareasContenido: MutableList<MutableList<MutableList<String>>>): List<MutableMap<String, MutableList<Double>>> {
    val notasCE: MutableList<MutableMap<String, MutableList<Double>>> = mutableListOf()
    for (tarea in tareasContenido) {
        val calculadoraCE = CalculaInstrumento(tarea)
        val notas = calculadoraCE.calculaPorcentajes()
        notasCE.add(notas)
    }
    return notasCE
}

/**
 * Función que calcula las notas de los criterios de evaluación de cada archivo y los añade a una lista.
 * @param filasNotas Filas de cada criterio de evaluación.
 * @param notasCE Una lista que contiene un diccionario por cada unidad, el cual tiene como clave la
 *        letra que identifica el criterio de la tarea y como valor una lista con todas las notas de ese criterio.
 * @return Una lista que contiene un diccionario por cada unidad, el cual tiene como clave la
 *         letra que identifica el criterio de la tarea y como valor una lista con todas las notas de ese criterio con el porcentaje aplicado.
 */
fun calcularTodosCE(filasNotas:  MutableList<MutableList<MutableList<String>>>, notasCE: List<MutableMap<String, MutableList<Double>>>): List<MutableMap<String, MutableList<Double>>> {
    val todosCECalculados = mutableListOf<MutableMap<String, MutableList<Double>>>()
    for ((indice, unidad) in filasNotas.withIndex()) {
        val notas = notasCE.getOrNull(indice)
        if (notas != null) {
            val calculadoraCE = CalculaCE(unidad, notas)
            val calculadosCE = calculadoraCE.calculaPorcentajes()
            todosCECalculados.add(calculadosCE)
        } else {
            throw Exception("No hay notas asignadas a la unidad.")
        }
    }
    return todosCECalculados
}

/**
 * Función que calcula las notas de los resultados de aprendizaje de cada archivo y los añade a una lista.
 * @param contenidoArchivos Contenido del archivo parseado.
 * @param notasCE Una lista que contiene un diccionario por cada unidad, el cual tiene como clave la
 *        letra que identifica el criterio de la tarea y como valor una lista con todas las notas de ese criterio con el porcentaje aplicado.
 * @return Una lista que contiene un diccionario por cada unidad, el cual tiene como clave la
 *         letra que identifica el criterio de la tarea y como valor una lista con todas las notas de ese criterio con el porcentaje aplicado.
 */
fun calcularRA(contenidoArchivos: MutableList<List<String>>, notasCE : List<MutableMap<String, MutableList<Double>>>) :  MutableList<MutableMap<String, MutableList<Double>>>{
    val todosRACalculados = mutableListOf<MutableMap<String, MutableList<Double>>>()
    for ((indice, unidad) in contenidoArchivos.withIndex()) {
        val notas = notasCE.getOrNull(indice)
        if (notas != null) {
            val calculadoraRA = CalculaRA(unidad, notas)
            val calculadosRA = calculadoraRA.calculaPorcentajes()
            todosRACalculados.add(calculadosRA)
        } else {
            throw Exception("No hay notas asignadas a la unidad.")
        }
    }
    return todosRACalculados
}

/**
 * Función que calcula las notas de los criterios de evaluación de cada archivo y los añade a una lista.
 * @param todosCECalculados Notas de los CE.
 * @param todosRACalculados Notas de los RA.
 * @param filasNotas Filas de criterios de evaluación.
 * @param contenidoArchivos Contenido del archivo parseado.
 * @return
 */
fun escribirCECalculados(
    todosCECalculados: List<MutableMap<String, MutableList<Double>>>,
    todosRACalculados: MutableList<MutableMap<String, MutableList<Double>>>,
    filasNotas:  MutableList<MutableList<MutableList<String>>>,
    contenidoArchivos: MutableList<List<String>>, listaDeArchivos: MutableList<File>) {
    for ((indice, contenidoCriterios) in filasNotas.withIndex()) {
        val writer = FileCSVWriter(todosCECalculados[indice], todosRACalculados[indice], contenidoCriterios, contenidoArchivos[indice])
        val notasFinales = writer.modificaContenido()
        writer.sobreescribirArchivos(notasFinales, listaDeArchivos[indice])
    }
}

/**
 * Función main en la que se instancian todos los objetos de las clases.
 */
fun main() = application {

    // Lectura de archivos.
    val archivos = FileCSVReader("./examples")
    val listaDeArchivos = archivos.listaArchivos()
    val archivosParaParsear = archivos.leeArchivos(listaDeArchivos)

    // Parseo de archivos.
    val parser = FileCSVParser(archivosParaParsear)
    val archivosParseados = parser.parsearContenidoArchivos()
    val tareasContenido = parser.obtenerTareasCEArchivos(archivosParseados)
    val filasNotas = parser.obtenerFilasCEArchivos(archivosParseados)
    val alumnos = parser.obtenerNombreseIniciales(archivosParseados)
    //val porcentajes = parser.obtenerPorcentajes(archivosParseados)

    //Llamadas funciones.
    val notasInstrumentos = calcularNotasInstrumentos(tareasContenido)
    val todosCECalculados = calcularTodosCE(filasNotas, notasInstrumentos)
    val todosRACalculados = calcularRA(archivosParseados, todosCECalculados)
    escribirCECalculados(todosCECalculados, todosRACalculados, filasNotas, archivosParaParsear, listaDeArchivos)

    //Base de datos.
    val connectionPool = ConnectionPool()
    val dataSource = connectionPool.obtieneDB()

    AlumnosDAOImpl(dataSource).nuevoAlumno(alumnos, "PRO", todosRACalculados)
    NotasDAOImpl(dataSource).nuevaNota(alumnos, "PRO", todosRACalculados, todosCECalculados)
    val calificaciones = CalificacionesDAOImpl(dataSource)
    val calificacionesService = CalificacionesDAOService(calificaciones)
    val datosAlumnos = calificaciones.obtenerDatosAlumnos()
    val datosNotas = calificaciones.obtenerDatosNotas()


    //Compose.
    Window(title= "Calificaciones", onCloseRequest = ::exitApplication, state= rememberWindowState(width= 700.dp, height= 710.dp)) {
        app(datosAlumnos, datosNotas)
    }
}