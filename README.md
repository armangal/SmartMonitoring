## Smart Monitoring
<i>The framework is still under development, more documentation will be available soon.</i>
<hr>

SmartMonitoring is an open source Java Monitoring Platform with HTML (GWT) UI that monitors Java processes via JMX connection.
The project might be extended to include more monitoring abilities (widgets), the basic once are provided with in a scope of a current projec

## Communication

- Twitter: [@armangal](http://twitter.com/armangal)
- [GitHub Issues](https://github.com/armangal/SmartMonitoring/issues)


## Features
- Easy extendible GWT client side UI application, allowing you to add more widget to the UI.
- Uses JMX to connect to running java processes.
- Extensive statistics over the pools performance.
- JMX monitoring:
** Monitoring connected servers: memory, CPU, garbage collections.
** Give ability to get java process thread dump with one mouse click.
** Smart Thread Pool monitoring:
*** Configured pools on all connected servers
--- All statistics are aggregated, meaning if the same pool name is configured on tow or more servers, we will see one pool in UI with aggregated statistics. 
- Can aggregate statistics from many servers at the same time.
 
<h2>Members:</h2>
<b>Owner: </b>
- Arman Gal, <a href='https://twitter.com/armangal'> @armangal</a>, arman@armangal.com

<b>Contributors:</b>
- Erdoan Veliev, erdoan.veliev@gmail.com

<hr>
<h1>Example, Customized Monitoring</h1>
<img src="https://raw.github.com/armangal/SmartMonitoring/master/docs/example_pok.png">
<p></p>
<img src="https://raw.github.com/armangal/SmartMonitoring/master/docs/example_server_details_extended.png">
<hr>

 Copyright (C) 2013 Arman Gal

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

