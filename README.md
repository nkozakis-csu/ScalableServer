#Scalable Server
Author: Nathan Kozakis

##Files:

* client
    * **client** - Connects to the server and sends 8KB data payloads. Calculates hash and waits for server to return identical hash
    * **Throughput** - Timer task to display number of sent and received messages per 20 seconds
* server
    * **Server** - Starts a threadpool to run tasks. 
    * **Throughput**
* tasks
    * **Hashing**
    * **ProcessDataTask**
    * **RegisterTask**
* threading
    * **Task**
    * **ThreadPool**
    * **Worker**