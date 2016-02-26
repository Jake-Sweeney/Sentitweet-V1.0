var mapCenter = new google.maps.LatLng(52.317209,-9.536724);
var map;
var numberOfResults = 0;
var jsonTweets;

function createChart(tweets, filteredData, filterTopic) {
    /**
     * This margin variable is used for scaling the overall chart.
     */
    jsonTweets = tweets;

    var margin = {top: 40, right: 50, bottom: 65, left: 60},
        width = 300 - margin.left - margin.right,
        height = 300 - margin.top - margin.bottom;

    var formatPercent = d3.format("0");

    var x = d3.scale.ordinal()
        .rangeRoundBands([0, width], .3);

    var y = d3.scale.linear()
        .range([height, 0]);

    var xAxis = d3.svg.axis()
        .scale(x)
        .orient("bottom");

    var yAxis = d3.svg.axis()
        .scale(y)
        .orient("left")
        .tickFormat(formatPercent);

    var tip = d3.tip()
        .attr("class", "d3-tip")
        .offset([-10, 0]) /* x & y position of tooltip */
        .html(function (d) {
            return "<span style='color:pink'>" + d.value + "</span>";
        });


    /**
     * The chart vairable selects the "body" element and attaches on an svg element with a g subelement
     */
    var chart = d3.select("body").append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    /**
     * chart.call(tip) -> calls the 'tip' var and attaches it onto chart/appends it onto charts svg elements.
     */
    chart.call(tip);

    var sentimentCategories = ["positive", "negative", "neutral"];

    x.domain(filteredData.map(function (d) {
        return d.sentiment;
    }));
    y.domain([0, d3.max(filteredData, function (d) {
        return d.value;
    })]);

    chart.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0," + height + ")")
        .call(xAxis)
        .selectAll("text")
            .style("text-anchor", "end")
            .attr("x", "2")
            .attr("dx", ".71em")
            .attr("transform", "rotate(-45)");

    chart.append("g")
        .attr("class", "y axis")
        .call(yAxis)
        .append("text")
        .attr("transform", "rotate(-90)")
        .attr("y", 4)
        .attr("dy", "1em")
        .style("text-anchor", "end")
        .text(filterTopic);


    chart.selectAll(".bar")
        .data(sentimentCount)
        .enter().append("rect")
        .attr("class", "bar")
        .attr("x", function (d) {
            return x(d.sentiment);
        })
        //.attr("transform", "rotate(-45)")
        .attr("width", x.rangeBand())
        .attr("y", function (d) {
            return y(d.value);
        })
        .attr("height", function (d, i) {
            return height - y(d.value);
        })
        .on("mouseover", tip.show)
        .on("mouseout", tip.hide);
}

function initialize() {
    console.log("initialise called");
    var mapProperties = {
        center:mapCenter,
        zoom:5,
        mapTypeId:google.maps.MapTypeId.SATELLITE
    };
    map = new google.maps.Map(document.getElementById("googleMap"), mapProperties);
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
    var resultsCounter = document.getElementById("resultsCounter");
    resultsCounter.innerHTML += "Number of Geolocated Tweets = " + numberOfResults;
}

function getRandomGeolocation(from, to, numberOfDecimalPlaces) {
    return (Math.random() * (to - from) +  from).toFixed(numberOfDecimalPlaces) * 1;
}

function fillTable() {

}