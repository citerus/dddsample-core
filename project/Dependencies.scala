import sbt._

object Versions {
  val spring = "4.2.1.RELEASE"
  val cxf = "3.0.3"
  val thymeleaf = "2.1.4.RELEASE"
  val hibernate = "4.3.5.Final"
  val slf4j = "1.7.21"
}

object Libraries {
  val junit = "com.novocode" % "junit-interface" % "0.11" % "test" // JUnit runner for SBT, includes JUnit dependency

  val springWebmvc = "org.springframework" % "spring-webmvc" % Versions.spring
  val springTx = "org.springframework" % "spring-tx" % Versions.spring
  val springJdbc = "org.springframework" % "spring-jdbc" % Versions.spring
  val springJms = "org.springframework" % "spring-jms" % Versions.spring
  val springOrm = "org.springframework" % "spring-orm" % Versions.spring
  val springWeb = "org.springframework" % "spring-web" % Versions.spring
  val springTest = "org.springframework" % "spring-test" % Versions.spring % "test"

  val thymeleaf = "org.thymeleaf" % "thymeleaf-spring4" % Versions.thymeleaf

  val hibernate = "org.hibernate" % "hibernate-core" % Versions.hibernate exclude("org.slf4j", "slf4j-api")

  val commonsCollections = "commons-collections" % "commons-collections" % "3.2.1"
  val commonsLang = "commons-lang" % "commons-lang" % "2.3"
  val commonsIo = "commons-io" % "commons-io" % "1.3.1"
  val commonsDbcp = "commons-dbcp" % "commons-dbcp" % "1.2.2"

  val slf4jlog4j = "org.slf4j" % "slf4j-log4j12" % Versions.slf4j
  val slf4jjcl = "org.slf4j" % "jcl-over-slf4j" % Versions.slf4j
  val slf4japi = "org.slf4j" % "slf4j-api" % Versions.slf4j

  val javassist = "javassist" % "javassist" % "3.8.0.GA"

  val hsqldb = "org.hsqldb" % "hsqldb" % "2.3.3"

  val servletApi = "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided"

  val taglibs = "taglibs" % "standard" % "1.1.2"

  val jstl = "javax.servlet" % "jstl" % "1.1.2"

  val sitemesh = "opensymphony" % "sitemesh" % "2.3" % "runtime"

  val activeMQ = "org.apache.activemq" % "activemq-core" % "5.2.0" exclude("commons-logging", "commons-logging-api")

  val xbeanSpring = "org.apache.xbean" % "xbean-spring" % "3.4.3"

  val easyMock = "org.easymock" % "easymock" % "2.3" % "test"

  val cxfFrontend = "org.apache.cxf" % "cxf-rt-frontend-jaxws" % Versions.cxf
  val cxfHttp = "org.apache.cxf" % "cxf-rt-transports-http" % Versions.cxf

}