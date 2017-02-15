var wpinput1,wpinput2,myLat1,myLong1,myLat2,myLong2,time1;
function setgeocode(){
  geocodeOri(platform);
  //geocodeDes(platform);
  //setTimeout(showRoute(),10000);
  //showRoute();
  //sendParas();
}

 function geocodeOri(platform) {
  var geocoder = platform.getGeocodingService(),
    parameters = {
      searchtext: document.getElementById('origin1').value,
      gen: '8'};
      time1 = document.getElementById('time1').value;

  geocoder.geocode(parameters,
    function (result) {
      //alert(result);
      console.log(result);
      myLat1 = result.Response.View[0].Result[0].Location.DisplayPosition.Latitude;
      myLong1 = result.Response.View[0].Result[0].Location.DisplayPosition.Longitude;
      wpinput1 = [myLat1,myLong1];
      console.log(myLat1);
      console.log(myLong1);
      geocodeDes(platform);       //get destination
    }, function (error) {
      alert(error);
    });
}

function geocodeDes(platform) {
  var geocoder = platform.getGeocodingService(),
    parameters = {
      searchtext: document.getElementById('destination1').value,
      gen: '8'};

  geocoder.geocode(parameters,
    function (result) {
      //alert(result);
      console.log(result);
      myLat2 = result.Response.View[0].Result[0].Location.DisplayPosition.Latitude;
      myLong2 = result.Response.View[0].Result[0].Location.DisplayPosition.Longitude;
      wpinput2 = [myLat2,myLong2];
      sendParas();                    //Send parameters to servlet
      console.log(myLat2);
      console.log(myLong2);
      showRoute();                    //show route
    }, function (error) {
      alert(error);
    });
}

function showRoute(){
  var routingParameters = {
    'mode': 'fastest;car',
    'waypoint0': wpinput1,
   'waypoint1': wpinput2,
   'representation': 'display'
  };
  console.log(routingParameters);
  console.log(wpinput2);
//show route
  var onResult = function(resultofroute) {
    var route,
      routeShape,
      startPoint,
      endPoint,
      strip;
      console.log(resultofroute);
  if(resultofroute.response.route) {
    route = resultofroute.response.route[0];
    routeShape = route.shape;
    strip = new H.geo.Strip();
    routeShape.forEach(function(point) {
      var parts = point.split(',');
      strip.pushLatLngAlt(parts[0], parts[1]);
    });
    startPoint = route.waypoint[0].mappedPosition;
    endPoint = route.waypoint[1].mappedPosition;
    routeLine = new H.map.Polyline(strip, {
      style: { strokeColor: 'blue', lineWidth: 4 }
    });
    
    startMarker = new H.map.Marker({
      lat: startPoint.latitude,
      lng: startPoint.longitude
    });
    endMarker = new H.map.Marker({
      lat: endPoint.latitude,
      lng: endPoint.longitude
    });
    var group = new H.map.Group();

    map.addObjects([routeLine, startMarker, endMarker]);
    map.setViewBounds(routeLine.getBounds());
  }
};
var router = platform.getRoutingService();
router.calculateRoute(routingParameters, onResult,
    function(error) {
        alert(error.message);
});
map.removeObject(routeLine);
map.removeObject(startMarker);
map.removeObject(endMarker);
map.removeObject(group);
}

map.removeObject(routeLine);

function sendParas(){
  var xhttp = new XMLHttpRequest();
  var queryString = "servlet/BestTimeServlet?f1origin=" + wpinput1 + "&f1destination=" + wpinput2 + "&f1time=" + time1;
  console.log(wpinput1);
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      //myFunction(this);
      //this.responseText;
    	
        var responseDiv = document.getElementById("Display");
        if(responseDiv.hasChildNodes()) {  
            responseDiv.removeChild(responseDiv.childNodes[0]);  
        }
        var responseText  = document.createTextNode(this.responseText);  
        responseDiv.appendChild(responseText);   
    }
  };
  
  xhttp.open("POST", queryString, true);
  xhttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded;");
  xhttp.send(null); 
  //xhttp.send("f1origon=asdf&f1destination=asqwe&f1time=09:00AM");
}



/*function myFunction(xml) {
  var i;
  var xmlDoc = xml.responseXML;
  var table="<tr><th>Artist</th><th>Title</th></tr>";
  var x = xmlDoc.getElementsByTagName("CD");
  for (i = 0; i <x.length; i++) {
    table += "<tr><td>" +
    x[i].getElementsByTagName("ARTIST")[0].childNodes[0].nodeValue +
    "</td><td>" +
    x[i].getElementsByTagName("TITLE")[0].childNodes[0].nodeValue +
    "</td></tr>";
  }
  document.getElementById("demo").innerHTML = table;
}*/

