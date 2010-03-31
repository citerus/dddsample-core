<html>
<head>
  <title>Report delay</title>
  <script type="text/javascript">
    var arrivals = ${arrivals};
    var departures = ${departures};

    function fill_locations() {
      var t = $("#type_select").val();
      var v = $("#voyage_select").val();
      var locs;
      if (v && (t == "ARR")) {
        locs = arrivals[v];
      } else if (v && (t == "DEPT")) {
        locs = departures[v];
      } else {
        locs = [];
      }

      var sel = $("#location_select");
      sel.empty();
      $.each(locs, function(i, val) {
        var option = "<option value=\"" + val + "\">" + val + "</option>";
        sel.append(option);
      });
    }
  </script>
</head>
<body>
<div id="container">
  <form action="<c:url value="/admin/voyageDelayed.html"/>" method="post">
    <p>
      Voyage
      <select name="voyageNumber" id="voyage_select" onchange="fill_locations()">
        <option value="">-- Select --</option>
        <c:forEach items="${voyages}" var="v">
        <option value="${v.voyageNumber}">${v.voyageNumber}</option>
        </c:forEach>
      </select>
      : 
      <select name="type" id="type_select">
        <option value="ARR">arrival to</option>
        <option value="DEPT">departure from</option>
      </select>
      <select name="unLocode" id="location_select">
      </select>
      is delayed by
      <input name="hours" type="text" size="4" maxlength="4">
      hours
    </p>
    <p>
      <input type="submit"/>
    </p>
  </form>
</div>
</body>
</html>