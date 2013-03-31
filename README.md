<h1>Smart Monitoring</h1>
<i>The framework is still under development, more documentation will be available soon.</i>
----

SmartMonitoring is an open source Java Monitoring Platform with HTML (GWT) UI.
The project might be extended to include more monitoring abilities (widgets), the basic once are provided with this project.

<h2 name="features">Features</h2>
- GWT easy extendible application
- Uses JMX to connect to running java processes.
- Extensive statistics over the pools performance
- JMX monitoring:
** Monitoring connected servers: memory, CPU, garbage collections.
** Give ability to get java process thread dump with one mouse click.
** Smart Thread Pool monitoring:
*** Configured pools on all connected servers
--- All statistics are aggregated, meaning if the same pool name is configured on tow or more servers, we will see one pool in UI with aggregated statistics. 
- Can aggregate statistics from many servers at the same time.
 
<h2>Members:</h2>
<b>Owner: </b>
- Arman Gal, @armangal, arman@armangal.com

<b>Contributors:</b>


----
MIT License

Copyright (c) 2013 Arman Gal

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
