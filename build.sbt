
import Libraries._

scalaVersion := "2.11.8"

enablePlugins(JettyPlugin)

libraryDependencies ++= Seq(
  junit,

  springWebmvc,
  springTx,
  springJdbc,
  springJms,
  springOrm,
  springWeb,
  springTest,

  thymeleaf,

  hibernate,

  commonsCollections,
  commonsLang,
  commonsIo,
  commonsDbcp,

  slf4jlog4j,
  slf4jjcl,
  slf4japi,

  javassist,

  hsqldb,

  servletApi,

  taglibs,

  jstl,

  sitemesh,

  activeMQ,

  xbeanSpring,

  easyMock,

  cxfFrontend,
  cxfHttp
)