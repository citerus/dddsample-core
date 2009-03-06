<html>
<head>
  <title>Cargo Administration</title>
  <script type="text/javascript" charset="UTF-8" src="<c:url value="/js/calendar.js"/>"></script>
  <script type="text/javascript" charset="UTF-8" src="<c:url value="/js/YAHOO.js"/>"></script>
  <script type="text/javascript" charset="UTF-8" src="<c:url value="/js/event.js"/>"></script>
  <script type="text/javascript" charset="UTF-8" src="<c:url value="/js/dom.js"/>"></script>
  <style type="text/css" title="style" media="screen">
    @import "<c:url value="/calendar.css"/>";
  </style>
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

  <c:forEach items="${routeCandidates}" var="it" varStatus="itStatus">
      <form action="${postUrl}" method="post">
        <input type="hidden" name="trackingId" value="${cargo.trackingId}"/>
        <table>
          <caption>Route ${itStatus.index + 1}</caption>
          <thead>
            <tr>
              <td>Voyage</td>
              <td>From</td>
              <td>To</td>
            </tr>
          </thead>
          <tbody>
            <c:forEach items="${it.legs}" var="leg" varStatus="legStatus">
              <input type="hidden" name="legs[${legStatus.index}].voyageNumber" value="${leg.voyageNumber}"/>
              <input type="hidden" name="legs[${legStatus.index}].fromUnLocode" value="${leg.from}"/>
              <input type="hidden" name="legs[${legStatus.index}].toUnLocode" value="${leg.to}"/>
              <tr>
                <td>${leg.voyageNumber}</td>
                <td>${leg.from}</td>
                <td>${leg.to}</td>
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