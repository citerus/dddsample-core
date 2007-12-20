<%@ include file="/WEB-INF/jspf/include.jspf" %>

<html>
<body>
<c:choose>
  <c:when test="${location ne null}">
    <p>Your cargo is currently at: <b>${location}</b></p>
  </c:when>
  <c:otherwise>
    <p>Unknown cargo id</p>
  </c:otherwise>
</c:choose>

<br/>
<a href="start.html">Start page</a>

</body>
</html>