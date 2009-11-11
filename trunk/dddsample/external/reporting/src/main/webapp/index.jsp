<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
  <script type="text/javascript" src="jquery-1.3.2.js"></script>
  <title>Reporting</title>
</head>
<body>
  <h1 id="apa">Reporting</h1>
  <div>
    <form action="" method="get" onsubmit="search(); return false;">
      Search:
      <input id="query" name="query" type="text"/>
      <input id="submit" name="submit" type="submit" value="Search"/>
    </form>
  </div>
  <div id="result"></div>
  <script type="text/javascript">
    $('#query').focus();

    function search() {
      $.ajax({
          url: "http://localhost:8080/reporting/rest/cargo/" + $("#query").val(),
          type: 'GET',
          contentType: 'application/json',
          success: function(response) {
            var json = eval('(' + response + ')');
            var cargo = json.cargoReport.cargo;
            $('#result').empty();
            $('#result').
              append('<h2>Cargo ' + cargo.trackingId + '</h2>').
              append('<p>Destination: ' + cargo.finalDestination + ' (at ' + cargo.arrivalDeadline + ')</p>')

            if (cargo.currentLocation) {
              $('#result').append('<p>Status: ' + cargo.currentStatus + ' ' + cargo.currentLocation + '</p>');
            } else if (cargo.currentVoyage) {
              $('#result').append('<p>Status: ' + cargo.currentStatus + ' ' + cargo.currentVoyage + '</p>');
            }

            $('#result').append('<p>Updated on: ' + cargo.lastUpdatedOn + '</p>');
          },
          error: function(response) {
            if (response.status == 404) {
              $('#result').text('No cargo with tracking id ' + $("#query").val());
            }
          }
      });
    }
  </script>
</body>
</html>

