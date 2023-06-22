package logs
import java.util.logging.Logger

class Logger {

    private val log = Logger.getLogger("Logs")

    internal fun info(tag:String, informacion:String){
        log.info("$tag --> $informacion")
    }

    internal fun warning(tag:String, advertencia:String){
        log.warning("$tag --> $advertencia")
    }
}