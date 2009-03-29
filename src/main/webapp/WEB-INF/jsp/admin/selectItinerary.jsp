<html>
<head>
  <title>Cargo Administration</title>
</head>
<body>
<div id="container">
  <table>
    <caption>Select route</caption>
    <tr>
      <td>
          Cargo ${cargo.trackingId} is going from ${cargo.origin} to ${cargo.finalDestination}
      </td>
    </tr>
  </table>
  <c:url value="/admin/assignItinerary.html" var="postUrl"/>
  <c:if test="${empty routeCandidates}">
      <p>No routes found that satisfy the route specification.
          Try setting an arrival deadline futher into the future (a few weeks at least).
      </p>
  </c:if>

  <c:forEach items="${routeCandidates}" var="it" varStatus="itStatus">
      <form action="${postUrl}" method="post">
        <input type="hidden" name="trackingId" value="${cargo.trackingId}"/>
        <table>
          <caption>Route candidate ${itStatus.index + 1}</caption>
          <thead>
            <tr>
              <td>Voyage</td>
              <td>From</td>
              <td></td>
              <td>To</td>
              <td></td>
            </tr>
          </thead>
          <tbody>
            <c:forEach items="${it.legs}" var="leg" varStatus="legStatus">
              <input type="hidden" name="legs[${legStatus.index}].voyageNumber" value="${leg.voyageNumber}"/>
              <input type="hidden" name="legs[${legStatus.index}].fromUnLocode" value="${leg.from}"/>
              <input type="hidden" name="legs[${legStatus.index}].toUnLocode" value="${leg.to}"/>
              <input type="hidden" name="legs[${legStatus.index}].fromDate" value="<fmt:formatDate value="${leg.loadTime}" pattern="yyyy-MM-dd hh:mm"/>"/>
              <input type="hidden" name="legs[${legStatus.index}].toDate" value="<fmt:formatDate value="${leg.unloadTime}" pattern="yyyy-MM-dd hh:mm"/>"/>
              <tr>
                <td>${leg.voyageNumber}</td>
                <td>${leg.from}</td>
                <td><fmt:formatDate value="${leg.loadTime}" pattern="yyyy-MM-dd hh:mm"/></td>
                <td>${leg.to}</td>
                <td><fmt:formatDate value="${leg.unloadTime}" pattern="yyyy-MM-dd hh:mm"/></td>
              </tr>
            </c:forEach>
          </tbody>
          <tfoot>
            <tr>
              <td colspan="3">
                <p>
                  <input type="submit" value="Assign cargo to this route"/>
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