package jdbc.dao.calificaciones
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import javax.sql.DataSource


open class CalificacionesDAOImpl(private val dataSource : DataSource) : CalificacionesDAO {
    override fun creaBD() {

        val alumnos = """
        CREATE TABLE IF NOT EXISTS MODULOS (
            id INT PRIMARY KEY,
            iniciales VARCHAR(3), 
            nombre VARCHAR(100),
            modulo VARCHAR(3),
            nota VARCHAR(5)
        )"""

        val notas = """
        CREATE TABLE IF NOT EXISTS NOTAS (
            id INT PRIMARY KEY,
            iniciales VARCHAR(3), 
            modulo VARCHAR(100),
            idRA VARCHAR(5),
            notaRA VARCHAR(5),
            notasCE VARCHAR(100)
        )"""

        val statement: Statement = dataSource.connection.createStatement()
        statement.execute(alumnos)
        statement.execute(notas)
        statement.close()
    }

    override fun compruebaExistencia(table: String, value: Int): Boolean {
        var existe = false

        dataSource.connection.use { conn ->
            try {
                val statement: Statement = conn.createStatement()
                val sql = "SELECT * FROM $table WHERE id = '$value'"
                val resultSet = statement.executeQuery(sql)

                if (resultSet?.next() == true) {
                    existe = true
                }
            } catch (error: SQLException) {
                error.printStackTrace()
            }
        }

        return existe
    }

    override fun borraDatos() {
        try {
            val statement = dataSource.connection.prepareStatement("DELETE FROM MODULOS")
            statement.executeUpdate()
            statement.close()
            val statement2 = dataSource.connection.prepareStatement("DELETE FROM NOTAS")
            statement2.executeUpdate()
            statement2.close()
        } catch (error: SQLException) {
            error.printStackTrace()
        }
    }

    fun obtenerDatosModulos(): MutableList<MutableList<String>> {
        val filaCompleta = mutableListOf<MutableList<String>>()
        try {
            val statement: Statement = dataSource.connection.createStatement()
            val datosFila = "SELECT * FROM ALUMNOS"
            val resultSet: ResultSet = statement.executeQuery(datosFila)
            while (resultSet.next()) {
                val fila = mutableListOf<String>()
                val id = resultSet.getString("id")
                val iniciales = resultSet.getString("inicialesm")
                val nombre = resultSet.getString("nombrem")
                val modulo = resultSet.getString("modulom")
                val nota = resultSet.getString("notam")
                with(fila) {
                    add(id)
                    add(iniciales)
                    add(nombre)
                    add(modulo)
                    add(nota)
                }
                filaCompleta.add(fila)
            }
            resultSet.close()
        } catch (error: SQLException) {
            error.printStackTrace()
        }
        return filaCompleta
    }

    fun obtenerDatosNotas(): MutableList<MutableList<String>> {
        val filaCompleta = mutableListOf<MutableList<String>>()
        try {
            val statement: Statement = dataSource.connection.createStatement()
            val datosFila = "SELECT * FROM NOTAS"
            val resultSet: ResultSet = statement.executeQuery(datosFila)
            while (resultSet.next()) {
                val fila = mutableListOf<String>()
                val id = resultSet.getString("id")
                val iniciales = resultSet.getString("inicialesn")
                val nombre = resultSet.getString("modulon")
                val modulo = resultSet.getString("idRAn")
                val notaRA = resultSet.getString("notaRAn")
                val notasCE = resultSet.getString("notasCEn")
                with(fila) {
                    add(id)
                    add(iniciales)
                    add(nombre)
                    add(modulo)
                    add(notaRA)
                    add(notasCE)
                }
                filaCompleta.add(fila)
            }
            resultSet.close()
        } catch (error: SQLException) {
            error.printStackTrace()
        }
        return filaCompleta
    }

}