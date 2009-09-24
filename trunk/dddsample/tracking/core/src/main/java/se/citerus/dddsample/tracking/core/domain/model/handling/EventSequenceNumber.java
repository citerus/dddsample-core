package se.citerus.dddsample.tracking.core.domain.model.handling;

import se.citerus.dddsample.tracking.core.domain.shared.ValueObject;

import java.util.concurrent.atomic.AtomicLong;

public class EventSequenceNumber implements ValueObject<EventSequenceNumber> {

  private long value;
  private static final AtomicLong SEQUENCE = new AtomicLong(System.currentTimeMillis());

  private EventSequenceNumber(final long value) {
    this.value = value;
  }

  public static EventSequenceNumber next() {
    return new EventSequenceNumber(SEQUENCE.getAndIncrement());
  }

  public long longValue() {
    return value;
  }

  @Override
  public boolean sameValueAs(final EventSequenceNumber other) {
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    return sameValueAs((EventSequenceNumber) o);
  }

  @Override
  public int hashCode() {
    return Long.valueOf(value).hashCode();
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  EventSequenceNumber() {
    // Needed by Hibernate
  }

}
