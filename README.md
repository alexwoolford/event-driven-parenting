# event driven parenting

This project consists of two Spring micro-services:
1. _ic-grades-scraper_: scrapes grades from Infinite Campus into Kafka.
2. _playstation-killswitch_: streams grades from Kafka and adds a firewall rule preventing the PS4 from accessing the internet if there's a grade that's not an A or B.

![event-driven-parenting diagram](event-driven-parenting.png)

Here's a video walk-though:

[![Event-driven parenting: bad grades? No PS4](https://img.youtube.com/vi/AXjudOChbgo/0.jpg)](https://www.youtube.com/watch?v=AXjudOChbgo)
