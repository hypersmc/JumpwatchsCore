package JumpWatch.hypercore.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class helplogger {
    private static Logger logger = LogManager.getLogger("hypercore");

    public static void log(Level logLevel, Object object){
        logger.log(logLevel, String.valueOf(object));
    }
    public static void log(Level logLevel, Object object, Throwable throwable)
    {
        logger.log(logLevel, String.valueOf(object), throwable);
    }
    public static void info(Object object)
    {
        log(Level.INFO, object);
    }
    public static void error(Object object){
        log(Level.ERROR, object);
    }
    public static void fatal(Object object) { log(Level.FATAL, object); }
    public static void warn(Object object) { log(Level.WARN, object); }
    public static void all(Object object) { log(Level.ALL, object); }
    public static void debug(Object object) { log(Level.DEBUG, object); }
    public static void trace(Object object) { log(Level.TRACE, object); }
    

}