<%@ include file="/WEB-INF/jspf/include.jspf" %>

<html>
<head>
  <meta http-equiv="content-type" content="text/html; charset=iso-8859-1"/>
  <script type="text/javascript"></script>
  <style type="text/css" title="style" media="screen">
    @import "${rc.contextPath}/style.css";
  </style>
</head>
<body>
<div id="container">
  <h1>Search for Cargo</h1>
  <div id="search">
  <form:form method="post" commandName="trackCommand">
    <table cellspacing="0" cellpadding="4">
      <tr>
        <td align="right">
          Enter tracking id:
        </td>
        <td>
          <form:input path="trackingId"/>
        </td>
        <td>
          <form:errors path="trackingId" cssClass="error"/>
        </td>
        <td>
          <input type="submit" value="Track!">
        </td>
      </tr>
    </table>
  </form:form>
  </div>
  <div id="result">
<c:if test="${cargo ne null}">
Your cargo is currently at: ${cargo.currentLocation}
    <table cellspacing="4">
      <thead>
        <tr>
          <td>Event</td>
          <td>Location</td>
          <td>Time</td>
        </tr>
      </thead>
      <tbody>
        <c:forEach var="event" items="${cargo.deliveryHistory.events}">
          <tr>
            <td>${event.type}</td>
            <td>${event.location}</td>
            <td>${event.time}</td>
          </tr>
        </c:forEach>
      </tbody>
    </table>
</c:if>
  </div>
</div>
</body>
</html>