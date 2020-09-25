#Scalable Server

Author: Nathan Kozakis

first run `gradle build`

Server
* Computes hashes of payloads and returns them to clients. Can handle 100+ clients connected and sending payloads at the same time.
  Uses non-blocking IO to perform socket reads/writes with only threads created from ThreadPool.
* `java -cp build/libs/ScalableServer-1.0.jar cs455.scaling.server.Server <port> <threadpool-size> <batch-size> <batch-time>`
         
Client
* sends random payloads of 8KB to server and computes the hash of that payload. Stores hash in linked list and removes them
  once the hash has been received from the server.
* `java -cp build/libs/ScalableServer-1.0.jar cs455.scaling.client.Client <ip> <port> <rate>`

run `./startClients.sh` to start multiple 10 clients on cs120 lab machines

##Files:

* client
    * **client** - Connects to the server and sends 8KB data payloads. Calculates hash and waits for server to return identical hash.
    * **Throughput** - Timer task to display number of sent and received messages per 20 seconds.
* server
    * **Server** - Starts a threadpool to run tasks. 
    * **Throughput** - scheduled Timer Task which calculates throughput and other statistics for the past 20 seconds of messages.
* tasks
    * **Hashing** - returns string hash for an 8KB byte array - hashes payloads for server and client.
    * **ProcessDataTask** - ThreadPool Task to read from socketchannel, compute hash, and write hash to socketchannel.
    * **RegisterTask** - Register socketchannel with server's selector.
* threading
    * **Task** - All tasks extend this base class. Has a run method that must be overridden.
    * **ThreadPool** - Has *n* number of workers (1 thread per worker), a batch-size, and batch-time. Server adds tasks to batch,
                       and once batch reaches batch size or batch-time has passed since adding first task, the batch will be
                       assigned to a worker. Workers perform Tasks in batch in FIFO order. Number of threads, batch-size, and 
                       batch-time can be configured. keeps track of available workers and pushes batches to next available worker.
    * **Worker** - Has a thread which blocks until assigned a batch of tasks. Performs all tasks and notifies ThreadPool it's available.
