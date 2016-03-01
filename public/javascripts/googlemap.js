var mapCenter = new google.maps.LatLng(52.317209,-9.536724);
var map;
var numberOfResults = 0;
var jsonTweets = [];

function initializeData(jsonTweetsSet) {
    jsonTweets = jsonTweetsSet;
}

function initialize() {
    console.log("initialise called");
    var mapProperties = {
        center:mapCenter,
        zoom:5,
        mapTypeId:google.maps.MapTypeId.ROADMAP
    };
    map = new google.maps.Map(document.getElementById("googleMap"), mapProperties);
    map.setOptions({ minZoom: 1, maxZoom: 20 });
    createMarkers();
}
google.maps.event.addDomListener(window, 'load', initialize);

function createMarkers() {
    var redCounter = 0;
    var blueCounter = 0;
    var greenCounter = 0;
    for(var i = 0; i< jsonTweets.length; i++) {
        numberOfResults++;

        var tweet = jsonTweets[i];
        if(tweet.sentiment == "POSITIVE") {
            markerIcon = new google.maps.MarkerImage("http://maps.google.com/mapfiles/ms/icons/green-dot.png");
            greenCounter++;
        } else if(tweet.sentiment == "NEGATIVE") {
            markerIcon = new google.maps.MarkerImage("http://maps.google.com/mapfiles/ms/icons/red-dot.png");
            redCounter++;
        } else if(tweet.sentiment == "NEUTRAL") {
            markerIcon = new google.maps.MarkerImage("http://maps.google.com/mapfiles/ms/icons/blue-dot.png");
            blueCounter++;
        }

        //console.log("Tweet #" + i);
        var lat = getRandomGeolocation(-180, 180, 3);
        //console.log("lat = " + lat);
        var lng = getRandomGeolocation(-180, 180, 3);
        //console.log("long = " + lng);

        var marker = new google.maps.Marker({
            position: new google.maps.LatLng(lat, lng),
            map: map,
            icon:markerIcon
        })

        var content = "<p>" + tweet.username + "<br />" + tweet.text + "<br />"
            + tweet.sentiment + "</p>";

        var infoWindow = new google.maps.InfoWindow();

        google.maps.event.addListener(marker, 'click',(function(marker,content,infoWindow) {
            return function() {
                infoWindow.setContent(content);
                infoWindow.open(map, marker);
            };
        })(marker,content,infoWindow));
    }
    console.log("Number of red markers: " + redCounter);
    console.log("Number of green markers: " + greenCounter);
    console.log("Number of blue markers: " + blueCounter);
    //var resultsCounter = document.getElementById("resultsCounter");
    //resultsCounter.innerHTML += "Number of Geolocated Tweets = " + numberOfResults;
}

function getRandomGeolocation(from, to, numberOfDecimalPlaces) {
    return (Math.random() * (to - from) + from).toFixed(numberOfDecimalPlaces) * 1;
}
