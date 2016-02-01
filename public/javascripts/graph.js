function createChart(tweets, filterTopic) {
    /**
     * This margin variable is used for scaling the overall chart.
     */
    var margin = {top: 40, right: 50, bottom: 65, left: 40},
        width = 1200 - margin.left - margin.right,
        height = 550 - margin.top - margin.bottom;

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
            return "<span style='color:pink'>" + d.retweetCount + "</span>";
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

    x.domain(tweets.map(function (d) {
        return d.username;
    }));
    y.domain([0, d3.max(tweets, function (d) {
        return d.retweetCount;
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
        .attr("dy", ".71em")
        .style("text-anchor", "end")
        .text(filterTopic);


    chart.selectAll(".bar")
        .data(tweets)
        .enter().append("rect")
        .attr("class", "bar")
        .attr("x", function (d) {
            return x(d.username);
        })
        //.attr("transform", "rotate(-45)")
        .attr("width", x.rangeBand())
        .attr("y", function (d) {
            return y(d.retweetCount);
        })
        .attr("height", function (d, i) {
            return height - y(d.retweetCount);
        })
        .on("mouseover", tip.show)
        .on("mouseout", tip.hide);
}