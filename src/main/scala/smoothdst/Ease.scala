package smoothdst

import scala.math.Pi

sealed trait Ease {
  def fromInstant: Long
  def fromOffset:  Long
  def toInstant:   Long
  def toOffset:    Long

  protected def curve: Double => Double
  def apply(instant: Long): Double = curve(instant)

  protected def rise = Pi * (toInstant - fromInstant) * (toOffset - fromOffset)
}

object Ease {
  def in(fromInstant: Long, fromOffset: Long, midInstant: Long, toInstant: Long, toOffset: Long): Ease =
    EaseIn(fromInstant, fromOffset, midInstant, toInstant, toOffset)

  def out(fromInstant: Long, fromOffset: Long, midInstant: Long, toInstant: Long, toOffset: Long): Ease =
    EaseOut(fromInstant, fromOffset, midInstant, toInstant, toOffset)
}

case class EaseIn(fromInstant: Long,
                  fromOffset:  Long,
                  midInstant:  Long,
                  toInstant:   Long,
                  toOffset:    Long) extends Ease {
  val curve = hermite(
    x0 = fromInstant,
    y0 = fromOffset,
    m0 = 0,
    x1 = midInstant,
    y1 = (fromOffset + toOffset) / 2,
    m1 = rise / (8 * (toInstant - midInstant)))
}

case class EaseOut(fromInstant: Long,
                   fromOffset:  Long,
                   midInstant:  Long,
                   toInstant:   Long,
                   toOffset:    Long) extends Ease {
  val curve = hermite(
    x0 = midInstant,
    y0 = (fromOffset + toOffset) / 2,
    m0 = rise / (8 * (midInstant - fromInstant)),
    x1 = toInstant,
    y1 = toOffset,
    m1 = 0)
}
