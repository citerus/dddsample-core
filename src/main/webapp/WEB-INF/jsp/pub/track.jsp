<html>
<head>
  <title>Tracking cargo</title>
</head>
<body>
<div id="container">
  <div id="search">
  <form:form method="post" commandName="trackCommand">
    <table>
      <tr>
        <td>
          Enter your tracking id:
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
  <c:if test="${cargo == null}">
    <p><em>Hint: try tracking "ABC123" or "JKL567".</em></p>
  </c:if>

  <c:if test="${cargo != null}">
    <div id="result">
    <h2>Cargo ${cargo.trackingId} is now: ${cargo.statusText}</h2>
    <p>Estimated time of arrival in ${cargo.destination}: ${cargo.eta}</p>
    <p>${cargo.nextExpectedActivity}</p>
    <c:if test="${cargo.misdirected}">
      <p class="notify"><img src="${rc.contextPath}/images/error.png" alt="" />Cargo is misdirected</p>
    </c:if>
    <c:if test="${not empty cargo.events}">
      <h3>Handling History</h3>
        <ul style="list-style-type: none;">
            <c:forEach items="${cargo.events}" var="leg">
            <li>
                <p><img style="vertical-align: top;" src="${rc.contextPath}/images/${leg.expected ? "tick" : "cross"}.png" alt=""/>
                &nbsp;${leg.description}</p>
            </li>
            </c:forEach>
        </ul>
    </c:if>
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