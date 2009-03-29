package se.citerus.dddsample

class GroovyPeterTest extends GroovyTestCase {

  void testName() {
    def peter = new GroovyPeter(name:"Peter")

    assert peter.name == "Peter"

    try {
      peter.name = "Petter"
      fail "Should not be possible to reassign read-only field name"
    } catch (expected) {}
  }

}