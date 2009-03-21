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
  <style type="text/css">
    td {
      align: left;
    }
  </style>
</head>
<body>
<div id="container">
  <form action="<c:url value="/admin/changeDestination.html"/>" method="post">
  <input type="hidden" name="trackingId" value="${cargo.trackingId}"/>
  <table>
    <caption>Change destination for cargo ${cargo.trackingId}</caption>
    <tbody>
      <tr>
        <td>Current destination</td>
        <td>
            ${cargo.finalDestination}
        </td>
      </tr>
      <tr>
        <td>New destination</td>
        <td>
          <select name="unlocode">
            <c:forEach items="${locations}" var="location">
            <option value="${location.unLocode}">${location.unLocode}</option>
            </c:forEach>
          </select>
        </td>
      </tr>
    </tbody>
    <tfoot>
      <tr>
        <td> </td>
        <td>
          <input type="submit" value="Change destination"/>
        </td>
      </tr>
    </tfoot>
  </table>
  </form>
</div>
</body>
</html>