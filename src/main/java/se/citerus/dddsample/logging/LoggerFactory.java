package se.citerus.dddsample.logging;

import com.tersesystems.echopraxia.DefaultLoggerMethods;
import com.tersesystems.echopraxia.api.CoreLogger;
import com.tersesystems.echopraxia.api.CoreLoggerFactory;
import org.jetbrains.annotations.NotNull;

public final class LoggerFactory {

    private static final FieldBuilder myFieldBuilder = FieldBuilder.instance();

    // the class containing the error/warn/info/debug/trace methods
    private static final String FQCN = DefaultLoggerMethods.class.getName();

    public static Logger getLogger(Class<?> clazz) {
        return getLogger(CoreLoggerFactory.getLogger(FQCN, clazz.getName()));
    }

    public static Logger getLogger(String name) {
        return getLogger(CoreLoggerFactory.getLogger(FQCN, name));
    }

    public static Logger getLogger(@NotNull CoreLogger core) {
        return new Logger(core, myFieldBuilder, Logger.class);
    }
}