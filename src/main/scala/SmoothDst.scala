import
  org.joda.time.{DateTimeZone,
                 LocalDate,
                 LocalTime},
  scala.math.{Pi,
              round},
  scala.sys.env,
  smoothdst._,
  twitter4j.TwitterFactory,
  twitter4j.conf.ConfigurationBuilder

object SmoothDst {
  val configuration = new ConfigurationBuilder()
    .setOAuthAccessToken(env("TWITTER_ACCESS_TOKEN"))
    .setOAuthAccessTokenSecret(env("TWITTER_ACCESS_TOKEN_SECRET"))
    .setOAuthConsumerKey(env("TWITTER_CONSUMER_KEY"))
    .setOAuthConsumerSecret(env("TWITTER_CONSUMER_SECRET"))
    .build

  def deltaMinutes(a: Double, b: Double) = round((a - b) / 60000)

  def message(date: LocalDate, timeZone: DateTimeZone) = {
    val noon            = new LocalTime(12, 0)
    val todayNoon       = date.toDateTime(noon, timeZone)
    val todayInstant    = todayNoon.getMillis
    val tomorrowNoon    = date.plusDays(1).toDateTime(noon, timeZone)
    val tomorrowInstant = tomorrowNoon.getMillis

    for {
      periodToday    <- Period(todayNoon)
      easeToday      <- periodToday.ease(todayInstant)
      periodTomorrow <- Period(tomorrowNoon)
      easeTomorrow   <- periodTomorrow.ease(tomorrowInstant)
      adjustToday    = deltaMinutes(periodToday.offset, easeToday(todayInstant))
      adjustTomorrow = deltaMinutes(periodTomorrow.offset, easeTomorrow(tomorrowInstant))

      adjustWords =
        if (adjustTomorrow == 0)
          "on time"
        else {
          val difference = adjustTomorrow.abs
          val direction  = if (adjustTomorrow >= 0) "late" else "early"
          val plural     = if (difference == 1) "minute" else "minutes"

          s"$difference $plural $direction"
        }

      message <-
        if (periodToday.offset != periodTomorrow.offset) {
          val delta      = deltaMinutes(periodToday.offset, periodTomorrow.offset)
          val difference = delta.abs
          val direction  = if (delta >= 0) "back" else "forward"

          Some(s"Clocks are moving $direction $difference minutes tonight. Ease through the time change and wake up $adjustWords tomorrow.")
        }
        else if (adjustToday != adjustTomorrow) {
          val ease = easeTomorrow match {
            case _: EaseIn  => "Ease into the next time change."
            case _: EaseOut => "Ease out of the last time change."
          }

          Some(s"$ease Start waking up $adjustWords.")
        }
        else
          None
    } yield message
  }

  val twitter = new TwitterFactory(configuration).getInstance

  def main(args: Array[String]) {
    val timeZone = DateTimeZone.getDefault
    val date     = new LocalDate(timeZone)

    for (message <- message(date, timeZone))
      twitter.updateStatus(message)
  }
}
