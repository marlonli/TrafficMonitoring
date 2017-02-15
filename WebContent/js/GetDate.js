var date1, myLat1, myLat2, myLong1, myLong2, wpinput1, wpinput2;
function getDate(){
	geocodeOri3(platform);
}
function geocodeOri3(platform) {
  var geocoder = platform.getGeocodingService(),
    parameters = {
      searchtext: document.getElementById('f3origin').value,
      gen: '8'};

  geocoder.geocode(parameters,
    function (result) {
      //alert(result);
      console.log(result);
      myLat1 = result.Response.View[0].Result[0].Location.DisplayPosition.Latitude;
      myLong1 = result.Response.View[0].Result[0].Location.DisplayPosition.Longitude;
      wpinput1 = [myLat1,myLong1];
      console.log(myLat1);
      console.log(myLong1);
      geocodeDes3(platform);       //get destination
    }, function (error) {
      alert(error);
    });
}

function geocodeDes3(platform) {
  var geocoder = platform.getGeocodingService(),
    parameters = {
      searchtext: document.getElementById('f3destination').value,
      gen: '8'};

  geocoder.geocode(parameters,
    function (result) {
      //alert(result);
      console.log(result);
      myLat2 = result.Response.View[0].Result[0].Location.DisplayPosition.Latitude;
      myLong2 = result.Response.View[0].Result[0].Location.DisplayPosition.Longitude;
      wpinput2 = [myLat2,myLong2];
      date1 = document.getElementById('f3date').value;
      sendParas3();                    //Send parameters to servlet
      console.log(myLat2);
      console.log(myLong2); 
      showRoute();        //show route
    }, function (error) {
      alert(error);
    });
}

	function sendParas3(){
  var xhttp = new XMLHttpRequest();
  var queryString = "servlet/BestDateServlet?f3origin=" + wpinput1 + "&f3destination=" + wpinput2 + "&f3date=" + date1;
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
}
  xhttp.open("POST", queryString, true);
  xhttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded;");
  xhttp.send(null); 
  //xhttp.send("f1origon=asdf&f1destination=asqwe&f1time=09:00AM");
}
