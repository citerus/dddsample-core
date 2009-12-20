package se.citerus.dddsample.tracking.core.domain.patterns;

import org.hibernate.proxy.AbstractLazyInitializer;
import org.hibernate.proxy.HibernateProxy;

import java.lang.reflect.Field;

/**
 * Utils for working with (around) the ORM framework, i.e. Hibernate.
 */
public class OrmUtils {

  private static final String HANDLER_FIELD_NAME = "handler";

  /**
   * @param o an object, possibly wrapped in a lazy proxy
   * @return the wrapped persistent entity (if wrapped), or o
   */
  public static Object unwrapOrmProxy(final Object o) {
    if (o instanceof HibernateProxy) {
      try {
        final Field handlerField = o.getClass().getDeclaredField(HANDLER_FIELD_NAME);
        handlerField.setAccessible(true);
        final AbstractLazyInitializer handler = (AbstractLazyInitializer) handlerField.get(o);
        return handler.getImplementation();
      } catch (IllegalAccessException e) {
        throw new AssertionError(e);
      } catch (NoSuchFieldException e) {
        throw new AssertionError(e);
      }
    } else {
      return o;
    }
  }

}
