package smoothdst

import org.joda.time.{DateTime,
                      DateTimeZone,
                      ReadableInstant}

case class Period(start:    Long,
                  end:      Long,
                  timeZone: DateTimeZone) {
  val midpoint = (start + end) / 2
  val offset   = timeZone.getOffset(start)

  lazy val next     = Period(end,       timeZone)
  lazy val previous = Period(start - 1, timeZone)

  def ease(instant: Long) =
    if (instant < midpoint) previous.map { previous =>
      Ease.out(
        fromInstant = previous.midpoint,
        fromOffset  = previous.offset,
        midInstant  = start,
        toInstant   = midpoint,
        toOffset    = offset)
    }
    else next.map { next =>
      Ease.in(
        fromInstant = midpoint,
        fromOffset  = offset,
        midInstant  = end,
        toInstant   = next.midpoint,
        toOffset    = next.offset)
    }
}

object Period {
  def apply(dateTime: DateTime): Option[Period] =
    apply(dateTime.getMillis, dateTime.getChronology.getZone)

  def apply(instant: Long, timeZone: DateTimeZone) = {
    val previous = timeZone.previousTransition(instant)
    val next     = timeZone.nextTransition(instant)

    val start    = if (previous == instant) None
                   else Some(timeZone.nextTransition(previous))
    val end      = if (next == instant) None
                   else Some(next)

    for (s <- start; e <- end) yield new Period(s, e, timeZone)
  }
}
