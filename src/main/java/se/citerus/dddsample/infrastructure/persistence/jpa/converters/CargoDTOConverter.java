package se.citerus.dddsample.infrastructure.persistence.jpa.converters;

import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.CargoDTO;

public class CargoDTOConverter {

    public static CargoDTO toDto(Cargo source) {
        return new CargoDTO();
    }

    public static Cargo fromDto(CargoDTO source) {
        return null;
    }
}
