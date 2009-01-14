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
  <form action="<c:url value="/admin/register.html"/>" method="post">
  <table>
    <caption>Book new cargo</caption>
    <tbody>
      <tr>
        <td>Origin</td>
        <td>
          <select name="originUnlocode">
            <c:forEach items="${unlocodes}" var="u">
            <option value="${u}">${u}</option>
            </c:forEach>
          </select>
        </td>
      </tr>
      <tr>
        <td>Destination</td>
        <td>
          <select name="destinationUnlocode">
            <c:forEach items="${unlocodes}" var="u">
            <option value="${u}">${u}</option>
            </c:forEach>
          </select>
        </td>
      </tr>
        <tr>
          <td>Arrival deadline:</td>
          <td>
            <input name="spec" type="text" size="10" id="cal1" value="${param.spec}"/>&nbsp;
            <img alt="" src="<c:url value="/images/calendarTrigger.gif"/>" class="calendarTrigger" onclick="calendar.toggle( event, this, 'cal1')"/>
          </td>
        </tr>
    </tbody>
    <tfoot>
      <tr>
        <td> </td>
        <td>
          <input type="submit" value="Book"/>
        </td>
      </tr>
    </tfoot>
  </table>
  </form>
</div>
</body>
</html>