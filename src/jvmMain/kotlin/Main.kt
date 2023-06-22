
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

fun calcularNotasInstrumentos(tareasContenido: MutableList<MutableList<MutableList<String>>>): List<MutableMap<String, MutableList<Double>>> {
    val notasCE: MutableList<MutableMap<String, MutableList<Double>>> = mutableListOf()
    for (tarea in tareasContenido) {
        val calculadoraCE = CalculaInstrumento(tarea)
        val notas = calculadoraCE.calculaPorcentajes()
        notasCE.add(notas)
    }
    return notasCE
}

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

fun calcularRA(filasNotas: MutableList<List<String>>, notasCE : List<MutableMap<String, MutableList<Double>>>) :  MutableList<MutableMap<String, MutableList<Double>>>{
    val todosRACalculados = mutableListOf<MutableMap<String, MutableList<Double>>>()
    for ((indice, unidad) in filasNotas.withIndex()) {
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

fun escribirCECalculados(
    todosCECalculados: List<MutableMap<String, MutableList<Double>>>,
    todosRACalculados: MutableList<MutableMap<String, MutableList<Double>>>,
    filasNotas:  MutableList<MutableList<MutableList<String>>>,
    contenidoArchivos: MutableList<List<String>>, listaDeArchivos: MutableList<File>) {

    for ((indice, contenidoCriterios) in filasNotas.withIndex()) {
        val writer = FileCSVWriter(todosCECalculados[indice], todosRACalculados[indice], contenidoCriterios, contenidoArchivos[indice])
        val notasFinales = writer.modificaContenido()
        //writer.sobreescribirArchivos(notasFinales, listaDeArchivos[indice])
    }
}
fun main(args: Array<String>) = application {

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
    val porcentajes = parser.obtenerPorcentajes(archivosParseados)

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
    val datosAlumnos = calificaciones.obtenerDatosModulos()
    val datosNotas = calificaciones.obtenerDatosNotas()



    //Compose.
    Window(title= "Calificaciones", onCloseRequest = ::exitApplication, state= rememberWindowState(width= 700.dp, height= 710.dp)) {
        app(datosAlumnos, datosNotas, porcentajes)
    }

}