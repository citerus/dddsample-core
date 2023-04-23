package se.citerus.dddsample.logging;

import com.tersesystems.echopraxia.DefaultLoggerMethods;
import com.tersesystems.echopraxia.api.*;
import org.jetbrains.annotations.*;


public final class Logger extends AbstractLoggerSupport<Logger, FieldBuilder>
        implements DefaultLoggerMethods<FieldBuilder> {
    private static final String FQCN = Logger.class.getName();

    protected Logger(
            @NotNull CoreLogger core, @NotNull FieldBuilder fieldBuilder, Class<?> selfType) {
        super(core, fieldBuilder, selfType);
    }

    @Override
    protected @NotNull Logger newLogger(CoreLogger core) {
        return new Logger(core, fieldBuilder(), Logger.class);
    }

    @Override
    protected @NotNull Logger neverLogger() {
        return new Logger(
                core.withCondition(Condition.never()), fieldBuilder(), Logger.class);
    }
}