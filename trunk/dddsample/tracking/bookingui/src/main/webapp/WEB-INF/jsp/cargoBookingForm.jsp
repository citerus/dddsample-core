<html>
<head>
  <title>Book new cargo</title>
</head>
<body>
<div id="container">
  <form action="<c:url value="/admin/bookCargo.html"/>" method="post">
  <table>
    <tbody>
      <tr>
        <td>Origin</td>
        <td>
          <select name="originUnlocode">
            <c:forEach items="${locations}" var="u">
            <option value="${u.unLocode}">${u.unLocode} (${u.name})</option>
            </c:forEach>
          </select>
        </td>
      </tr>
      <tr>
        <td>Destination</td>
        <td>
          <select name="destinationUnlocode">
            <c:forEach items="${locations}" var="u">
            <option value="${u.unLocode}">${u.unLocode} (${u.name})</option>
            </c:forEach>
          </select>
        </td>
      </tr>
        <tr>
          <td>Arrival deadline:</td>
          <td>
            <input name="arrivalDeadline" type="text" size="10" id="arrivalDeadline" value="${param.arrivalDeadline}"/>
          </td>
        </tr>
        <tr>
            <td></td>
            <td>
                <div id="datepicker"></div>
            </td>
        </tr>
    </tbody>
    <tfoot>
      <tr>
        <td>
          <input type="submit" value="Book"/>
        </td>
        <td></td>
      </tr>
    </tfoot>
  </table>
  </form>
</div>
<script type="text/javascript">
  $(function() {
    $("#arrivalDeadline").datepicker();
  });
</script>
</body>
</html>