<%@ page import="se.citerus.dddsample.domain.model.cargo.Cargo" %>
<%@ page import="se.citerus.dddsample.domain.model.cargo.DeliveryHistory" %>
<%@ page import="se.citerus.dddsample.domain.model.handling.HandlingEvent" %>
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

  <% final Cargo cargo = (Cargo) request.getAttribute("cargo"); %>

  <% if (cargo != null) { %>
    <% final DeliveryHistory dh = cargo.deliveryHistory(); %>
    <div id="result">
    <h2>
      <c:set var="statusMessageCode"><%="cargo.status." + dh.status()%></c:set>
      Status: <spring:message code="${statusMessageCode}"/>
      &nbsp;
      <%= dh.currentLocation() != null ?
          dh.currentLocation().name() : "" %>
      &nbsp;
      <%= dh.currentCarrierMovement() != null ?
          dh.currentCarrierMovement().carrierMovementId().idString() : "" %>
    </h2>
    <% if (cargo.isMisdirected()) { %>
      <p class="notify"><img src="${rc.contextPath}/images/error.png" alt="" />Cargo is misdirected</p>
    <% } %>
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
        <% for (HandlingEvent event : dh.eventsOrderedByCompletionTime()) { %>
          <tr class="event-type-<%=event.type()%>">
            <td><%=event.type()%></td>
            <td><%=event.location().name()%></td>
            <td><%=event.completionTime()%></td>
            <td><img src="${rc.contextPath}/images/<%=cargo.itinerary().isExpected(event) ? "tick" : "cross"%>.png" alt=""/></td>
          </tr>
        <% } %>
      </tbody>
    </table>
  </div>
  <% } %>

</div>
<script type="text/javascript" charset="UTF-8">
  try {
    document.getElementById('idInput').focus()
  } catch (e) {}
</script>
</body>
</html>