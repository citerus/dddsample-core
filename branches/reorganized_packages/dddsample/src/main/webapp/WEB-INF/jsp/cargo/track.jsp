<html>
<head>
  <title>Cargo search</title>
</head>
<body>
<div id="container">
  <h1>Track Your Cargo</h1>
  <div id="search">
  <form:form method="post" commandName="trackCommand">
    <table>
      <tr>
        <td>
          Enter tracking id:
        </td>
        <td>
          <form:input path="trackingId" id="idInput"/>
        </td>
        <td>
          <input type="submit" value="Track!">
        </td>
      </tr>
      <tr>
        <td></td>
        <td>
          <form:errors path="trackingId" cssClass="error"/>
        </td>
        <td></td>
      </tr>
    </table>
  </form:form>
  </div>

  <c:if test="${cargo ne null}">
    <div id="result">	
    <h2>Status: <spring:message code="cargo.status.${cargo.statusCode}"/>&nbsp;${cargo.currentLocationId}&nbsp;${cargo.carrierMovementId}</h2>
    <c:if test="${cargo.misdirected}">
      <p class="notify"><img src="${rc.contextPath}/images/error.png" alt="" />Cargo is misdirected</p>
    </c:if>  
    <h3>Tracking History</h3>
    <table cellspacing="4">
      <thead>
        <tr>
          <td>Event</td>
          <td>Location</td>
          <td>Time</td>
          <td></td>
        </tr>
      </thead>
      <tbody>
        <c:forEach var="event" items="${cargo.events}">
          <tr class="event-type-${event.type}">
            <td>${event.type}</td>
            <td>${event.location}</td>
            <td>${event.time}</td>
            <td><img src="${rc.contextPath}/images/${event.expected ? "tick" : "cross"}.png" alt=""/></td>
          </tr>
        </c:forEach>
      </tbody>
    </table>
  </div>
  </c:if>
</div>
<script type="text/javascript" charset="UTF-8">
  try {
    document.getElementById('idInput').focus()
  } catch (e) {}
</script>
</body>
</html>