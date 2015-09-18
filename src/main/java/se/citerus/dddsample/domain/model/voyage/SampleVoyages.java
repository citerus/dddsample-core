package se.citerus.dddsample.domain.model.voyage;

import static se.citerus.dddsample.application.util.DateTestUtil.toDate;

import se.citerus.dddsample.domain.model.location.Location;

import static se.citerus.dddsample.domain.model.location.SampleLocations.*;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Sample carrier movements, for test purposes.
 */
public class SampleVoyages {

    public static final Voyage CM001 = createVoyage("CM001", STOCKHOLM, HAMBURG);
    public static final Voyage CM002 = createVoyage("CM002", HAMBURG, HONGKONG);
    public static final Voyage CM003 = createVoyage("CM003", HONGKONG, NEWYORK);
    public static final Voyage CM004 = createVoyage("CM004", NEWYORK, CHICAGO);
    public static final Voyage CM005 = createVoyage("CM005", CHICAGO, HAMBURG);
    public static final Voyage CM006 = createVoyage("CM006", HAMBURG, HANGZOU);

    private static Voyage createVoyage(String id, Location from, Location to) {
        return new Voyage(new VoyageNumber(id), new Schedule(Collections.singletonList(
                new CarrierMovement(from, to, new Date(), new Date())
        )));
    }

    // TODO CM00[1-6] and createVoyage are deprecated. Remove and refactor tests.

    public final static Voyage v100 = new Voyage.Builder(new VoyageNumber("V100"), HONGKONG).
            addMovement(TOKYO, toDate("2009-03-03"), toDate("2009-03-05")).
            addMovement(NEWYORK, toDate("2009-03-06"), toDate("2009-03-09")).
            build();
    public final static Voyage v200 = new Voyage.Builder(new VoyageNumber("V200"), TOKYO).
            addMovement(NEWYORK, toDate("2009-03-06"), toDate("2009-03-08")).
            addMovement(CHICAGO, toDate("2009-03-10"), toDate("2009-03-14")).
            addMovement(STOCKHOLM, toDate("2009-03-14"), toDate("2009-03-16")).
            build();
    public final static Voyage v300 = new Voyage.Builder(new VoyageNumber("V300"), TOKYO).
            addMovement(ROTTERDAM, toDate("2009-03-08"), toDate("2009-03-11")).
            addMovement(HAMBURG, toDate("2009-03-11"), toDate("2009-03-12")).
            addMovement(MELBOURNE, toDate("2009-03-14"), toDate("2009-03-18")).
            addMovement(TOKYO, toDate("2009-03-19"), toDate("2009-03-21")).
            build();
    public final static Voyage v400 = new Voyage.Builder(new VoyageNumber("V400"), HAMBURG).
            addMovement(STOCKHOLM, toDate("2009-03-14"), toDate("2009-03-15")).
            addMovement(HELSINKI, toDate("2009-03-15"), toDate("2009-03-16")).
            addMovement(HAMBURG, toDate("2009-03-20"), toDate("2009-03-22")).
            build();

    /**
     * Voyage number 0100S (by ship)
     * <p>
     * Hongkong - Hangzou - Tokyo - Melbourne - New York
     */
    public static final Voyage HONGKONG_TO_NEW_YORK =
            new Voyage.Builder(new VoyageNumber("0100S"), HONGKONG).
                    addMovement(HANGZOU, toDate("2008-10-01", "12:00"), toDate("2008-10-03", "14:30")).
                    addMovement(TOKYO, toDate("2008-10-03", "21:00"), toDate("2008-10-06", "06:15")).
                    addMovement(MELBOURNE, toDate("2008-10-06", "11:00"), toDate("2008-10-12", "11:30")).
                    addMovement(NEWYORK, toDate("2008-10-14", "12:00"), toDate("2008-10-23", "23:10")).
                    build();


    /**
     * Voyage number 0200T (by train)
     * <p>
     * New York - Chicago - Dallas
     */
    public static final Voyage NEW_YORK_TO_DALLAS =
            new Voyage.Builder(new VoyageNumber("0200T"), NEWYORK).
                    addMovement(CHICAGO, toDate("2008-10-24", "07:00"), toDate("2008-10-24", "17:45")).
                    addMovement(DALLAS, toDate("2008-10-24", "21:25"), toDate("2008-10-25", "19:30")).
                    build();

    /**
     * Voyage number 0300A (by airplane)
     * <p>
     * Dallas - Hamburg - Stockholm - Helsinki
     */
    public static final Voyage DALLAS_TO_HELSINKI =
            new Voyage.Builder(new VoyageNumber("0300A"), DALLAS).
                    addMovement(HAMBURG, toDate("2008-10-29", "03:30"), toDate("2008-10-31", "14:00")).
                    addMovement(STOCKHOLM, toDate("2008-11-01", "15:20"), toDate("2008-11-01", "18:40")).
                    addMovement(HELSINKI, toDate("2008-11-02", "09:00"), toDate("2008-11-02", "11:15")).
                    build();

    /**
     * Voyage number 0301S (by ship)
     * <p>
     * Dallas - Hamburg - Stockholm - Helsinki, alternate route
     */
    public static final Voyage DALLAS_TO_HELSINKI_ALT =
            new Voyage.Builder(new VoyageNumber("0301S"), DALLAS).
                    addMovement(HELSINKI, toDate("2008-10-29", "03:30"), toDate("2008-11-05", "15:45")).
                    build();

    /**
     * Voyage number 0400S (by ship)
     * <p>
     * Helsinki - Rotterdam - Shanghai - Hongkong
     */
    public static final Voyage HELSINKI_TO_HONGKONG =
            new Voyage.Builder(new VoyageNumber("0400S"), HELSINKI).
                    addMovement(ROTTERDAM, toDate("2008-11-04", "05:50"), toDate("2008-11-06", "14:10")).
                    addMovement(SHANGHAI, toDate("2008-11-10", "21:45"), toDate("2008-11-22", "16:40")).
                    addMovement(HONGKONG, toDate("2008-11-24", "07:00"), toDate("2008-11-28", "13:37")).
                    build();

    public static final Map<VoyageNumber, Voyage> ALL = new HashMap<>();

    static {
        for (Field field : SampleVoyages.class.getDeclaredFields()) {
            if (field.getType().equals(Voyage.class)) {
                try {
                    Voyage voyage = (Voyage) field.get(null);
                    ALL.put(voyage.voyageNumber(), voyage);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static List<Voyage> getAll() {
        return new ArrayList<>(ALL.values());
    }

    public static Voyage lookup(VoyageNumber voyageNumber) {
        return ALL.get(voyageNumber);
    }

}
