var cargoResult = 0;
var voyageResult= 0;
var cargoJson = '';
var voyageJson = '';

function search() {
  if (!$("#query").val()) {
    $('#result').empty();
    return;
  }

  cargoResult = 0;
  voyageResult= 0;
  cargoJson = '';
  voyageJson = '';

  $.ajax({
      url: 'http://localhost:8080/reporting/rest/cargo/' + $("#query").val() + '.json',
      type: 'GET',
      contentType: 'application/json',
      success: function(response) { cargoResult = 1; cargoJson = eval('(' + response + ')'); present(response); },
      error: function(response) { cargoResult = 2; present(response); }
  });

  $.ajax({
      url: 'http://localhost:8080/reporting/rest/voyage/' + $("#query").val() + '.json',
      type: 'GET',
      contentType: 'application/json',
      success: function(response) { voyageResult = 1; voyageJson = eval('(' + response + ')'); present(response); },
      error: function(response) { voyageResult = 2; present(response); }
  });

}

function present(response) {
  if (cargoResult == 1 && voyageResult > 0) {
    presentCargo();
  } else if (cargoResult == 2 && voyageResult == 1) {
    presentVoyage();
  } else if (cargoResult == 2 && voyageResult == 2) {
    err(response);
  } else {
    // alert(cargoResult + ':' + voyageResult);
  }
}

function presentCargo(response) {
  var cargo = cargoJson.cargoReport.cargo;
  var pdfUrl = 'http://localhost:8080/reporting/rest/cargo/' + $("#query").val() + '.pdf';
  $('#result').empty();
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

  $('#result').append(cargoJson.cargoReport.misdirected);

  $('#result').
    append('<h2>Handling history</h2><table>');
  for (i in cargoJson.cargoReport.handlings) {
    var handling = cargoJson.cargoReport.handlings[i];
    $('#result').
      append('<tr>').
      append('<td>' + handling.completedOn + '</td>').
      append('<td>' + handling.type + '</td>').
      append('<td>' + (handling.voyage ? handling.voyage : '') + '</td>').
      append('<td>' + handling.location + '</td>').
      append('</tr>');
  }
  $('#result').
    append('</table>').
    append('<p><a href="' + pdfUrl + '">Download as PDF</a></p>');
}

function presentVoyage(response) {
  var voyage = voyageJson.voyageReport.voyage;
  var pdfUrl = 'http://localhost:8080/reporting/rest/voyage/' + $("#query").val() + '.pdf';

  $('#result').empty();
  $('#result').
    append('<h2>Voyage: ' + voyage.voyageNumber + '</h2>').append('<table>').
    append('<tr><td>Status</td><td>' + voyage.currentStatus + '</td></tr>').
    append('<tr><td>Next stop</td><td>' + voyage.nextStop + ' at ' + voyage.etaNextStop + '</td></tr>').
    append('<tr><td>Delay</td><td>' + voyage.delayedByMinutes + ' min</td></tr>').
    append('<tr><td>Updated on</td><td>' + voyage.lastUpdatedOn + '</td></tr>').
    append('</table>').
    append('<h2>Onboard cargos</h2><table>');
  for (i in voyageJson.voyageReport.onboardCargos) {
    var cargo = voyageJson.voyageReport.onboardCargos[i];
    $('#result').append('<tr><td>' + cargo.trackingId + '</td><td>' + cargo.finalDestination + '</td></tr>');
  }
  $('#result').
    append('</table>').
    append('<p><a href="' + pdfUrl + '">Download as PDF</a></p>');
}

function err(response) {
  if (response.status == 404) {
    if (cargoResult == 2 && voyageResult == 2) {
      $('#result').empty();
      $('#result').append('<p>No cargo or voyage found</p>');
    }
  }
}