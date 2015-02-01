<html>
<head>
  <title>Cargo Administration</title>
</head>
<body>
<div id="container">
  <table>
    <caption>Details for cargo ${cargo.trackingId}</caption>
    <tbody>
      <tr>
        <td>Origin</td>
        <td>${cargo.origin}</td>
      </tr>
      <tr>
        <td>Destination</td>
        <td>
            ${cargo.finalDestination}
        </td>
      </tr>
      <tr>
          <td></td>
          <td>
           <c:url value="/admin/pickNewDestination.html" var="cdUrl">
               <c:param name="trackingId" value="${cargo.trackingId}"/>
           </c:url>
            <a href="${cdUrl}">Change destination</a>    
          </td>
      </tr>
      <tr>
        <td>Arrival deadline</td>
        <td><fmt:formatDate value="${cargo.arrivalDeadline}" pattern="yyyy-MM-dd hh:mm"/></td>
      </tr>
    </tbody>
  </table>
  <p></p>
  <c:choose>
    <c:when test="${cargo.routed}">
      <c:if test="${cargo.misrouted}">
          <c:url value="/admin/selectItinerary.html" var="selectUrl">
            <c:param name="trackingId" value="${cargo.trackingId}"/>
          </c:url>
        <p><em>Cargo is misrouted - <a href="${selectUrl}">reroute this cargo</a></em></p>    
      </c:if>
      <table border="1">
        <caption>Itinerary</caption>
        <thead>
          <tr>
            <td>Voyage number</td>
            <td colspan="2">Load</td>
            <td colspan="2">Unload</td>
          </tr>
        </thead>
        <tbody>
          <c:forEach items="${cargo.legs}" var="leg">
            <tr>
              <td>${leg.voyageNumber}</td>
              <td>${leg.from}</td>
              <td>(<fmt:formatDate value="${leg.loadTime}" pattern="yyyy-MM-dd hh:mm"/>)</td>
              <td>${leg.to}</td>
              <td>(<fmt:formatDate value="${leg.unloadTime}" pattern="yyyy-MM-dd hh:mm"/>)</td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </c:when>
    <c:otherwise>
      <p>
        <c:url value="/admin/selectItinerary.html" var="selectUrl">
          <c:param name="trackingId" value="${cargo.trackingId}"/>
        </c:url>
        <strong>Not routed</strong> - <a href="${selectUrl}">Route this cargo</a>
      </p>
    </c:otherwise>
  </c:choose>
</div>
</body>
</html>