var mapCenter = new google.maps.LatLng(52.317209,-9.536724);
var map;
var numberOfResults = 0;
var jsonTweets = [];
var numberOfGeolocatedTweets = 0;
var markers = [];

function initializeMapData(jsonTweetsSet, selectedFilter) {
    if(selectedFilter == "none") {
        jsonTweets = jsonTweetsSet;
    } else {
        for(var i = 0; i < jsonTweetsSet.length; i++) {
            if(jsonTweetsSet[i].sentiment.toUpperCase() == selectedFilter.toUpperCase()) {
                jsonTweets.push(jsonTweetsSet[i]);
            }
        }
    }

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

        var lng =  tweet.tweetGeolocation.lng;
        var lat = tweet.tweetGeolocation.lat;

        if(lng != 0 && lat != 0) {
            numberOfGeolocatedTweets++;
            var marker = new google.maps.Marker({
                position: new google.maps.LatLng(lat, lng),
                map: map,
                icon: markerIcon
            });
            markers.push(marker);

            var content = "<p>" + tweet.username + "<br />" + tweet.text + "<br />" + tweet.sentiment + "</p>";
            var infoWindow = new google.maps.InfoWindow();

            google.maps.event.addListener(marker, 'click',(function(marker,content,infoWindow) {
                return function() {
                    infoWindow.setContent(content);
                    infoWindow.open(map, marker);
                };
            })(marker,content,infoWindow));

        }
    }
    console.log("Number of red markers: " + redCounter);
    console.log("Number of green markers: " + greenCounter);
    console.log("Number of blue markers: " + blueCounter);
    console.log("Number of Geolocated Tweets: " + numberOfGeolocatedTweets);
    console.log("Markers.length " + markers.length);
}

//function clearMarkers() {
//    jsonTweets = null;
//    console.log("clear markers called");
//    if(markers.length > 0) {
//        for(var i=0; i < markers.length; i++) {
//            console.log(markers[i]);
//            markers[i].setMap(null);
//            markers[i] = null;
//        }
//    }
//}