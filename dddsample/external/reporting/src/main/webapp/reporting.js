function search() {
  $('#result').empty();
  if (!$("#query").val()) {
    return;
  }
  
  $.ajax({
      url: 'http://localhost:8080/reporting/rest/cargo/' + $("#query").val() + '.json',
      type: 'GET',
      contentType: 'application/json',
      success: function(response) {
        var json = eval('(' + response + ')');
        var cargo = json.cargoReport.cargo;
        var pdfUrl = 'http://localhost:8080/reporting/rest/cargo/' + $("#query").val() + '.pdf';
        $('#result').append('<h2>Cargo: ' + cargo.trackingId + '</h2>').append('<table>');

        if (cargo.currentLocation) {
          $('#result').append('<tr><td>Status</td><td>' + cargo.currentStatus + ' ' + cargo.currentLocation + '</td></tr>');
        } else if (cargo.currentVoyage) {
          $('#result').append('<tr><td>Status</td><td>' + cargo.currentStatus + ' ' + cargo.currentVoyage + '</td></tr>');
        }

        $('#result').
          append('<tr><td>Destination</td><td>' + cargo.finalDestination + ' by ' + cargo.arrivalDeadline + '</td></tr>').
          append('<tr><td>ETA</td><td>' + cargo.eta + '</td></tr>');


        $('#result').
          append('<tr><td>Updated on</td><td>' + cargo.lastUpdatedOn + '</td></tr>').
          append('</table>');

        $('#result').append(json.cargoReport.misdirected);

        $('#result').
          append('<h2>Handling history</h2>');
        for (i in json.cargoReport.handlings) {
          var handling = json.cargoReport.handlings[i];
          $('#result').
            append('<tr>').
            append('<td>' + handling.completedOn + '</td>').
            append('<td>' + handling.type + '</td>').
            append('<td>' + handling.voyage + '</td>').
            append('<td>' + handling.location + '</td>').
            append('</tr>');
        }
        $('#result').
          append('</table>').
          append('<p><a href="' + pdfUrl + '">Download as PDF</a></p>');
      },
      error: function(response) {
        if (response.status == 404) {
          $('#result').append('<p>No cargo found with tracking ID ' + $("#query").val() + '</p>');
        }
      }
  });
}
