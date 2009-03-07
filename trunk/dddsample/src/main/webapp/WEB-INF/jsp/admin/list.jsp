<html>
<head>
  <title>Cargo Administration</title>
</head>
<body>
  <table border="1" width="600">
    <caption>All cargos</caption>
    <thead>
      <tr>
        <td>Tracking ID</td>
        <td>Origin</td>
        <td>Destination</td>
        <td>Routed</td>
      </tr>
    </thead>
    <tbody>
      <c:forEach items="${cargoList}" var="cargo">
      <tr>
        <td>
          <c:url value="/admin/show.html" var="showUrl">
            <c:param name="trackingId" value="${cargo.trackingId}"/>
          </c:url>
          <a href="${showUrl}">${cargo.trackingId}</a>
        </td>
        <td>${cargo.origin}</td>
        <td>${cargo.finalDestination}</td>
        <td>${cargo.misrouted ? "Misrouted" : (cargo.routed ? "Yes" : "No")}</td>
      </tr>  
      </c:forEach>
    </tbody>
  </table>
</body>
</html>