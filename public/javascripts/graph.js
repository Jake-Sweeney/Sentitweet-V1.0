function createBarChart(tweets, filteredData, filterTopic) {
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
            return "<span style='color:white'>" + d.value + "</span>";
        });


    /**
     * The chart vairable selects the "body" element and attaches on an svg element with a g subelement
     */
    var chart = d3.select("#barChart").append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    /**
     * chart.call(tip) -> calls the 'tip' var and attaches it onto chart/appends it onto charts svg elements.
     */
    chart.call(tip);

    var sentimentCategories = [];
    for(var i =0; i < filteredData.length; i++) {
        sentimentCategories.push(filteredData[i].sentiment);
    }

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

        //.selectAll("text")
        //    .style("text-anchor", "end")
        //    .attr("x", "2")
        //    .attr("dx", ".71em")
        //    .attr("transform", "rotate(-45)");

    chart.append("text")
        .attr("transform", "translate(" + (width / 2) + " ," + (height + margin.bottom) + ")")
        .style("font-weight", "bold")
        .style("text-anchor", "middle")
        .text("Senitment");


    chart.append("g")
        .attr("class", "y axis")
        .call(yAxis);

    chart.append("text")
        .attr("transform", "rotate(-90)")
        .attr("y", 0 - margin.left)
        .attr("x",0 - (height / 2))
        .attr("dy", "1em")
        .style("text-anchor", "middle")
        .style("font-weight", "bold")
        .text("Amount of Tweets");

    chart.selectAll(".bar")
        .data(filteredData)
        .enter().append("rect")
        .attr("fill", function(d, i) {
            if(d.sentiment == "positive") {
                return "rgb(0,230,77)";
            } else if(d.sentiment == "negative") {
                return "rgb(253,117,103)";
            } else if(d.sentiment == "neutral") {
                return "rgb(105,145,253)";
            }
        })
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
