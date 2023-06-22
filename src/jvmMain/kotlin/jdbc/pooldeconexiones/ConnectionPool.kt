package jdbc.pooldeconexiones
import com.zaxxer.hikari.HikariDataSource
import logs.Logger
import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource
import com.zaxxer.hikari.HikariConfig



class ConnectionPool {

    companion object {
        private val log = Logger()
    }

    private val dataSource: HikariDataSource

    init {
        val config = HikariConfig().apply {
            driverClassName = "org.h2.Driver"
            jdbcUrl = "jdbc:h2:./default"
            username = "usuario"
            password = "usuario"
            maximumPoolSize = 10
            isAutoCommit = true
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }

        dataSource = HikariDataSource(config)
    }

    fun obtieneDB(): DataSource {
        var connection: Connection? = null

        try {
            connection = dataSource.connection
            log.info("pooldeconexiones.ConnectionPool.obtieneDB", "Conexión correcta.")
            return dataSource
        } catch (error: SQLException) {
            log.warning("pooldeconexiones.ConnectionPool.obtieneDB","Ha ocurrido un error inesperado: $error.")
            throw Exception("Ha ocurrido un error: $error.")
        } finally {
            connection?.close()
            log.info("pooldeconexiones.ConnectionPool.obtieneDB","Conexión cerrada.")
        }
    }

}
