package app
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Función que genera el desplegable.
 * @param expanded Indica si está expandido o no.
 * @param onItemClick El elemento clickado.
 * @param onDismiss Para abrir o cerrar desplegable al clickar en otro lado.
 * @param options Las opciones del desplegable.
 */
@Composable
fun desplegable(
    expanded: Boolean,
    onItemClick: (List<String>) -> Unit,
    onDismiss: () -> Unit,
    options: List<List<String>>
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        options.forEach { option ->
            DropdownMenuItem(
                onClick = {
                    onItemClick(option)
                    onDismiss()
                }
            ) {
                Text(text = option[2])
            }
        }
    }
}


/**
 * Función que genera las iniciales y que llama a la función que genera el desplegable.
 * @param listaAlumnos Lista que contiene una lista por alumno, incluyendo en ella todos sus datos.
 */
@Composable
fun desplegableNombresConIniciales(listaAlumnos: List<List<String>>): List<String> {
    var taskMenuOpen by remember { mutableStateOf(false) }
    var selectedAlumno by remember { mutableStateOf(emptyList<String>()) }

    Box(
        Modifier
            .width(350.dp)
    ) {
        Row(
            Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                Modifier
                    .border(width = 1.dp, shape = RectangleShape, color = Color.Black)
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .width(50.dp),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    Text(
                        text = if (selectedAlumno.isNotEmpty()) selectedAlumno.getOrNull(1)
                            ?: "Iniciales Alumno" else "",
                        fontSize = 18.sp
                    )
                }
            }
            Spacer(modifier = Modifier.width(5.dp))
            Box(
                Modifier
                    .border(width = 1.dp, shape = RectangleShape, color = Color.Black)
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .width(300.dp)
            ){
                Column {
                    Text(
                        text = if (selectedAlumno.isNotEmpty()) selectedAlumno.getOrNull(2) ?: "Nombre Alumno" else "Nombre Alumno",
                        fontSize = 18.sp
                    )
                }
            }
        }
        IconButton(
            onClick = { taskMenuOpen = true },
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Nombres de Alumnos"
            )
        }
        desplegable(
            expanded = taskMenuOpen,
            onItemClick = { selectedAlumno = it },
            onDismiss = { taskMenuOpen = false },
            options = listaAlumnos
        )
    }
    return selectedAlumno
}

/**
 * Función que genera el modulo y la nota en base al alumno seleccionado.
 * @param alumno Alumno seleccionado.
 */
@Composable
fun moduloYNotas(alumno : List<String>){
    Box(
        Modifier
            .border(width = 1.dp, shape = RectangleShape, color = Color.Black)
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .width(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (alumno.isNotEmpty()) alumno.getOrNull(3)
                    ?: "Modulo" else "",
                fontSize = 18.sp,
            )
        }
    }

    Spacer(modifier = Modifier.width(5.dp))
    Box(
        Modifier
            .border(width = 1.dp, shape = RectangleShape, color = Color.Black)
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .width(200.dp)
    ){
        Column (
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (alumno.isNotEmpty()) alumno.getOrNull(4) ?: "Nota" else "",
                fontSize = 18.sp
            )
        }
    }
}


/**
 * Función que genera el modulo y la nota en base al alumno seleccionado.
 * @param alumno Alumno seleccionado.
 * @param notas Lista que engloba listas con los datos de las notas de cada alumno.
 */
@Composable
fun creaTablaNotas(alumno: List<String>, notas: MutableList<MutableList<String>>) {
    Column{
        for (notaAlumno in notas) {
            val notasCE = notaAlumno[5].split(",")
            if (alumno.isNotEmpty() && alumno[1] == notaAlumno.getOrNull(1)) {
                Row(modifier = Modifier.padding(top = 40.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Box(
                            Modifier
                                .border(width = 1.dp, shape = RectangleShape, color = Color.Black)
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                                .width(200.dp)
                        ) {
                            Text(
                                text = notaAlumno.getOrNull(3) ?: "RA",
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        for (ce in notasCE) {
                            if (ce.contains("(")) {
                                Box(
                                    Modifier
                                        .border(width = 1.dp, shape = RectangleShape, color = Color.Black)
                                        .padding(horizontal = 8.dp, vertical = 8.dp)
                                        .width(200.dp)
                                ) {
                                    Text(
                                        text = ce.replace("(", ""),
                                        fontSize = 18.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(5.dp))
                            }
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Box(
                            Modifier
                                .border(width = 1.dp, shape = RectangleShape, color = Color.Black)
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                                .width(200.dp)
                        ) {
                            Text(
                                text = notaAlumno.getOrNull(4) ?: "notasRA",
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        for (ce in notasCE) {
                            if (ce.contains(")")) {
                                Box(
                                    Modifier
                                        .border(width = 1.dp, shape = RectangleShape, color = Color.Black)
                                        .padding(horizontal = 8.dp, vertical = 8.dp)
                                        .width(200.dp)
                                ) {
                                    Text(
                                        text = ce.replace(")", ""),
                                        fontSize = 18.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(5.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Función que genera el modulo y la nota en base al alumno seleccionado.
 * @param listaAlumnos Lista que contiene una lista por alumno, incluyendo en ella todos sus datos.
 * @param notas Lista que engloba listas con los datos de las notas de cada alumno.
 */
@Composable
fun interfazNotas(listaAlumnos: List<List<String>>, notas: MutableList<MutableList<String>>) {
    val alumno = desplegableNombresConIniciales(listaAlumnos)

    Box(
        modifier = Modifier
            .width(480.dp)
            .height(450.dp)
            .offset(y = 40.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .border(width = 1.dp, shape = RectangleShape, color = Color.Black)
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
            ) {
                Row {
                    Spacer(modifier = Modifier.width(5.dp))
                    moduloYNotas(alumno)
                }
                Row{
                    Spacer(modifier = Modifier.width(5.dp))
                    creaTablaNotas(alumno, notas)
                }
            }
        }
    }
}


/**
 * Función que genera la app.
 * @param listaAlumnos Lista que contiene una lista por alumno, incluyendo en ella todos sus datos.
 * @param notas Lista que engloba listas con los datos de las notas de cada alumno.
 */
@Preview
@Composable
fun app(listaAlumnos: MutableList<MutableList<String>>, notas:MutableList<MutableList<String>>) {
    interfazNotas(listaAlumnos, notas)
}
