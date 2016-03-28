function createLineGraph(tweets, senitmentCount, topic) {

    var width = 900;
    var height = 400;
    var margin = {top: 20, right: 10, bottom: 40, left: 40};
    var transitionDuration = 5000;


    var formatDate = d3.time.format("%H:%M:%S %d-%b-%Y");
    var bisectDate = d3.bisector(function(d) { return d.date; }).left

    tweets.forEach(function (d) {
        var dateWithCorrectFormat = formatTweetDate(d.date);
        d.date = formatDate.parse(dateWithCorrectFormat);
        if(d.sentiment == "POSITIVE")
            d.sentiment = 1;
        else if(d.sentiment == "NEGATIVE")
            d.sentiment = -1;
        else
            d.sentiment = 0;
        //console.log(d.date);
        //console.log(d.sentiment);
    });

    tweets.sort(function (a, b) {
        return a.date - b.date;
    });

    var x = d3.time.scale().range([0, width]);

    var y = d3.scale.linear().range([height, 0]);

    var xAxis = d3.svg.axis().scale(x).orient("bottom");

    var yAxis = d3.svg.axis().scale(y).orient("left").ticks(2);

    var lineGraph = d3.select("#lineGraph").append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    x.domain(d3.extent(tweets, function(d) {
        return d.date;
    }));

    y.domain(d3.extent(tweets, function(d) {
        return d.sentiment;
    }));

    lineGraph.append("g")
        .attr("class", "y axis")
        .call(yAxis)

    lineGraph.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0," + height + ")")
        .call(xAxis);

    lineGraph.append("text")
        .attr("transform", "rotate(-90)")
        .attr("y", -43)
        .attr("x", 20 - height)
        .attr("dy", "1em")
        .style("text-anchor", "middle")
        .style("font-weight", "bold")
        .style("fill", "#FD7567")
        .text("Negative");

    lineGraph.append("text")
        .attr("transform", "rotate(-90)")
        .attr("y", -43)
        .attr("x",0 - (height / 2))
        .attr("dy", "1em")
        .style("text-anchor", "middle")
        .style("font-weight", "bold")
        .style("fill", "#6991FD")
        .text("Neutral");

    lineGraph.append("text")
        .attr("transform", "rotate(-90)")
        .attr("y", -43)
        .attr("x", -20)
        .attr("dy", "1em")
        .style("text-anchor", "middle")
        .style("font-weight", "bold")
        .style("fill", "#00E64D")
        .text("Positive");

    lineGraph.append("text")
        .attr("transform", "translate(" + (width / 2) + " ," + (height + margin.bottom) + ")")
        .style("font-weight", "bold")
        .style("text-anchor", "middle")
        .text("Time");

    var dataLinesGroup = lineGraph.append("svg:g");

    var dataLines = dataLinesGroup.selectAll(".data-line").data([tweets]);

    var line = d3.svg.line()
        .x(function(d,i) {
            return x(d.date);
        })
        .y(function(d) { ;
            return y(d.sentiment);
        })
        .interpolate("linear");

    var garea = d3.svg.area()
        .interpolate("linear")
        .x(function(d) {
            return x(d.date);
        })
        .y0(height)
        .y1(function (d) {
            return y(d.sentiment);
        });

    dataLines.enter()
        .append("svg:path")
        .attr("class", "area")
        .style("fill", function() {
            var majorSentiment = getMajorSentiment();
            if(majorSentiment == "positive") {
                return "rgba(0,230,77,0.7)";
            } else if(majorSentiment == "negative") {
                return "rgba(253,117,103,0.7)";
            } else {
                return "rgba(105,145,253,0.7)";
            }
        })
        .attr("d", garea(tweets));

    dataLines.enter().append("path")
        .attr("class", "data-line")
        .style("opacity", 0.3)
        .attr("d", line(tweets));

    var dataCriclesGroup = lineGraph.append("svg:g");

    var circles = dataCriclesGroup.selectAll(".data-point").data(tweets);

    circles.enter()
        .append("svg:circle")
        .attr("class", "data-point")
        .style("opacity", 1e-6)
        .attr("cx", function(d) {
            return x(d.date);
        })
        .attr("cy", function() {
            return y(0)
        })
        .attr("r", 4)
        .on("mouseover", function(d) {
            document.getElementById("statusContainer").style.visibility = "visible";
            if(d.sentiment == 1) {
                document.getElementById("collectionContent").style.backgroundColor = "rgba(0,230,77,0.8)";
            } else if (d.sentiment == -1) {
                document.getElementById("collectionContent").style.backgroundColor = "rgba(253,117,103,0.8)";
            } else {
                document.getElementById("collectionContent").style.backgroundColor = "rgba(105,145,253,0.8)";
            }
            document.getElementById("userProfileForStatusBar").src = d.profileImageURL;
            document.getElementById("statusUsername").innerHTML = "User: <b>" + d.username;
            document.getElementById("statusText").innerHTML = d.text;
        });

    var gradient = lineGraph.append("defs")
        .append("linearGradient")
        .attr("id", "gradient")
        .attr("x2", "0%")
        .attr("y2", "100%");

    gradient.append("stop")
        .attr("offset", "0%")
        .attr("stop-color", "#9CE7F4")
        .attr("stop-opacity", 1);

    gradient.append("stop")
        .attr("offset", "100%")
        .attr("stop-color", "#25C1ED")
        .attr("stop-opacity", 1);

    var curtain = lineGraph.append('rect')
        .attr('x', -1 * width)
        .attr('y', -1 * height)
        .attr('height', height)
        .attr('width', width)
        .attr('class', 'curtain')
        .attr('transform', 'rotate(180)')
        .style("fill", "url(#gradient)");

    var guideline = lineGraph.append('line')
        .attr('stroke', '#333')
        .attr('stroke-width', 0.5)
        .attr('class', 'guide')
        .attr('x1', 1)
        .attr('y1', 1)
        .attr('x2', 1)
        .attr('y2', height)

    var t = lineGraph.transition()
        .delay(1000)
        .duration(transitionDuration)
        .ease('linear')
        .each('end', function() {
            d3.select('line.guide')
                .transition()
                .style('opacity', 0)
                .remove()

            dataLines.transition()
                .attr("d", line)
                .style('opacity', 1)
                .attr("transform", function(d) { return "translate(" + x(d.date) + "," + y(d.sentiment) + ")"; });

            d3.selectAll(".area").transition()
                .attr("d", garea(tweets));

            circles.transition()
                .duration(transitionDuration)
                .style("opacity", 1)
                .attr("cx", function(d) { return x(d.date) })
                .attr("cy", function(d) { return y(d.sentiment) })
                .attr("fill", function(d) {
                    if(d.sentiment == 1) {
                        return "rgb(0,230,77)";
                    } else if(d.sentiment == -1) {
                        return "rgb(253,117,103)";
                    } else if(d.sentiment == 0) {
                        return "rgb(105,145,253)";
                    }
                });
        });

    t.select('rect.curtain')
        .attr('width', 0);
    t.select('line.guide')
        .attr('transform', 'translate(' + width + ', 0)');

    $("svg circle").tipsy({
        gravity: "w",
        html: true,
    });


    function formatTweetDate(tweetDate) {
        var splitDate = tweetDate.split(" ");
        var formattedDate = splitDate[3] + " " + splitDate[2] + "-" + splitDate[1] + "-" + splitDate[5];
        return formattedDate;
    }

    function getMajorSentiment() {
        var max = 0;
        var majorSentiment;
        for(var i = 0; i < senitmentCount.length; i++) {
            if(senitmentCount[i].value > max) {
                max = senitmentCount[i].value;
                majorSentiment = senitmentCount[i].sentiment;
            }
        }
        return majorSentiment;
    }
}