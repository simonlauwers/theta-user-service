import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Sl4jLogger{
    companion object{
        val <reified T> T.log: Logger
            inline get() = LoggerFactory.getLogger(T::class.java)
    }
}