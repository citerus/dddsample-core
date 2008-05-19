<html>
<head>
  <title>Cargo Administration</title>
</head>
<body>
<div id="container">
  <h1>Select itinerary</h1>
  <c:url value="/admin/assignItinerary.html" var="postUrl"/>

  <c:forEach items="${itineraryCandidates}" var="it" varStatus="itStatus">
      <form action="${postUrl}" method="post">
        <input type="hidden" name="trackingId" value="${trackingId}"/>
        <table>
          <caption>Itinerary ${status.index + 1}</caption>
          <thead>
            <tr>
              <td>Carrier</td>
              <td>From</td>
              <td>To</td>
            </tr>
          </thead>
          <tbody>
            <c:forEach items="${it.legs}" var="leg" varStatus="legStatus">
              <input type="hidden" name="legs.carrierMovementId" value="${leg.carrierMovementId}"/>
              <input type="hidden" name="legs.fromUnlocode" value="${leg.fromUnlocode}"/>
              <input type="hidden" name="legs.toUnlocode" value="${leg.toUnlocode}"/>
              <tr>
                <td>${leg.carrierMovementId}</td>
                <td>${leg.fromUnlocode}</td>
                <td>${leg.toUnlocode}</td>
              </tr>
            </c:forEach>
          </tbody>
          <tfoot>
            <tr>
              <td colspan="3">
                <p>
                  <input type="submit" value="Select"/>
                </p>
              </td>
            </tr>
          </tfoot>
        </table>
      </form>
  </c:forEach>

</div>
</body>
</html>