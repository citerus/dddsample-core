package se.citerus.dddsample


import org.scalatest.junit.JUnit3Suite
import org.scalatest.prop.Checkers
import testing.SUnit._


class ScalaPeterTest extends JUnit3Suite with Checkers {

  def testName() {
    val peter = new ScalaPeter("Peter")
    assert(peter.theName() == "Peter")
  }
  
}