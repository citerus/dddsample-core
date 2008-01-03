<%@ include file="/WEB-INF/jspf/include.jspf" %>

<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=iso-8859-1" />
	<script type="text/javascript"></script>
	<style type="text/css" title="style" media="screen">
		@import "/dddsample/style.css";
	</style>
</head>
<body>
<div id="form">
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
<c:choose>
  <c:when test="${cargo ne null}">
  	
    <p>Your cargo is currently at: <span id="currentLocation">${cargo.currentLocation}</span></p>
    
    <table>
      <c:forEach var="event" items="${cargo.deliveryHistory.events}">
        <tr><td><c:out value="${event.type}"/> &nbsp; on &nbsp;</td><td><c:out value="${event.location}"/>&nbsp; at &nbsp;</td><td><c:out value="${event.time}"/></td></tr>
      </c:forEach>
    </table>
    
  </c:when>
</c:choose>
</div>
</body>
</html>