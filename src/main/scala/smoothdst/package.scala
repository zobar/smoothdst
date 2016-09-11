package object smoothdst {
  def hermite(x0: Double, y0: Double, m0: Double, x1: Double, y1: Double, m1: Double) = {
    val a  = x1 - x0
    val a3 = a * a * a

    (x: Double) => {
      val b   = x - x0
      val b2  = b * b
      val c   = x1 - x
      val c2  = c * c
      val h00 = y0 * (a + 2 * b) * c2
      val h10 = m0 * b * c2
      val h01 = y1 * b2 * (3 * a - 2 * b)
      val h11 = m1 * b2 * -c

      (h00 + h10 + h01 + h11) / a3
    }
  }
}
