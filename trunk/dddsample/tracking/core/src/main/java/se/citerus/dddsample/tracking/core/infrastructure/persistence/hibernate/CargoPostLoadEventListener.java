package se.citerus.dddsample.tracking.core.infrastructure.persistence.hibernate;

import org.hibernate.event.PostLoadEvent;
import org.hibernate.event.def.DefaultPostLoadEventListener;
import se.citerus.dddsample.tracking.core.domain.model.cargo.Cargo;

import java.lang.reflect.Field;

public class CargoPostLoadEventListener extends DefaultPostLoadEventListener {

  private static final Field ITINERARY_FIELD;
  static {
    try {
      ITINERARY_FIELD = Cargo.class.getDeclaredField("itinerary");
      ITINERARY_FIELD.setAccessible(true);
    } catch (NoSuchFieldException e) {
      throw new AssertionError(e);
    }
  }

  @Override
  public void onPostLoad(PostLoadEvent event) {
    if (event.getEntity() instanceof Cargo) {
      /*
       * Itinerary is a column-less component with a collection field,
       * and there's no way (that I know of) to map this behaviour in metadata.
       *
       * Hibernate is all about reflection, so helping the mapping along with
       * another field manipulation is OK. This avoids the need for a public method
       * on Cargo.
       */
      Cargo cargo = (Cargo) event.getEntity();
      if (cargo.itinerary() != null && cargo.itinerary().legs().isEmpty()) {
        try {
          ITINERARY_FIELD.set(cargo, null);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
    super.onPostLoad(event);
  }

}
