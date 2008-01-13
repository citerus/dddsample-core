<html>
<head>
  <title>Cargo search</title>
</head>
<body>
<div id="container">
  <div id="search">
  <h1>Search for Your Cargo</h1>
  <form:form method="post" commandName="trackCommand">
    <table cellspacing="0" cellpadding="4">
      <tr>
        <td align="right">
          Enter tracking id:
        </td>
        <td>
          <form:input path="trackingId" id="idInput"/>
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

  <c:if test="${cargo ne null}">
    <div id="result">	
	<h2>Your cargo is currently at: ${cargo.currentLocation}</h2>
	<h3>Tracking History</h3>
    <table cellspacing="4">
      <thead>
        <tr>
          <td>Event</td>
          <td>Location</td>
          <td>Time</td>
        </tr>
      </thead>
      <tbody>
        <c:forEach var="event" items="${cargo.events}">
          <tr class="event-type-${event.type}">
            <td>${event.type}</td>
            <td>${event.location}</td>
            <td>${event.time}</td>
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