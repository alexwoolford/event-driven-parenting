# Infinite Campus Grades Scraper

This little Spring app crawls Infinite Campus and writes my son's grades to a Kafka topic. It uses Selenium to automate Chrome, so you'll need to run it on a host that has Chrome and the Chromedriver.

The output messages look something like this:

    {"_id":"123456","courseID":495325,"courseName":"Algebra 1B Honors","sectionID":2408039,"taskID":2851,"taskName":"Final","progressScore":"A","progressPercent":92.27}
    {"_id":"123456","courseID":495468,"courseName":"Gateway to Computing","sectionID":2408309,"taskID":2851,"taskName":"Final","progressScore":"A","progressPercent":91.59}
    ...

It's for an *event-driven parenting* use case.
