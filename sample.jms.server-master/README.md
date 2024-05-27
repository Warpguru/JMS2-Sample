# sample.jms.server

Liberty JMS sample

This sample was clones from the [JMS 1.1 sample](https://github.com/WASdev/sample.jms.server) on Github.

This sample project contains a simple JMS Servlet application called JMSSample. JMSSample listens for HTTP requests sent to `localhost:9124/jms2-JMSSample`, and responds with various actions.
There are 3 main Servlets that are contained:

1. Help: If nothing else is specified, the Help servlet displays the API documentation for the P2P and PubSub Servlets.
2. P2P : Point-to-Point Messaging uses JMS Queue to send and receive messages. It also contains MDB message send and response.
3. PubSub : Publish-and-Subscribe Messaging uses JMS topic to send and receive messages.

Note: The Server and Client versions of the sample are currently the same except for the server.xml (and texts to allow
differentiation between them).
The code for the Server is in the folder `sample.jms.server-master` where as the Client is in the folder
`sample.jms.client-master`. 

Both versions will run on separate Liberty server instances, however the Server is the instance that runs the JMS server
built-in into Liberty whereas the Client instance runs as a JMS Client requiring a JMS server (running at the Server instance).

## Running with Maven

This project can be build with [Apache Maven](http://maven.apache.org/). The project uses [Liberty Maven Plug-in][] to automatically download and install Liberty.  Liberty Maven Plug-in is also used to create, configure, and run the application on the Liberty server.

1. To start the Liberty server, run dev mode.

    mvn liberty:dev

Press `Ctrl+C` to exit dev mode and stop the server. Alternatively use `mvn liberty:run` to run the server normally.

### JMS documentation

Please see [A Complete JMS 2.0 Tutorial](https://jstobigdata.com/jms/a-complete-jms-2-0-tutorial/) for the concepts of using JMS.

### Application Details

Run the application [http://localhost:9124/jms2-JMSSample](http://localhost:9124/jms2-JMSSample)
for further instructions about the provided APIs or invoke the `P2P` or `PubSub` Servlets directly:

Use the following steps to run the Queue application actions with Maven:

[http://localhost:9124/jms2-JMSSample/JMSSampleP2P?ACTION=listAction](http://localhost:9124/jms2-JMSSample/JMSSampleP2P?ACTION=listAction)

the Topic application actions will be available under :

[http://localhost:9124/jms2-JMSSample/JMSSamplePubSub?ACTION=listAction](http://localhost:9124/jms2-JMSSample/JMSSamplePubSub?ACTION=listAction)

## JMS processing

The Server version runs the JMS server provided by a Liberty server, thus the message and topic queues used by the
`P2P` and `PubSub` Servlets are defined on this Liberty instances.

The Client version references the remote message and topic queues, used by the `P2P` and `PubSub` Servlets, that are
defined on the Server version. The Client version thus won't work without the Server version running!

### Server side

In order to provide the JMS server, the Server version defines a `wasJmsEndpoint` on port `9126` and a
`messagingEngine` with the queues the Client version can reference remotely.

### MDB considerations

As said, all message queues are defined by the JMS server running the Server version Liberty instance. That includes the
queue whose activation specification triggers the processing of the message by the MDB `SampleMBD` and the queue the 
MDB writes the reply into.

By default, both the Server version and the Client version have the MDB `SampleMBD` configured, so by default both MDBs
will process incoming messages in a round robin (or kind of load balancing) schedule. 
It does not matter if the message has been written into the queue by the Server or Client version.

Note! Simultaneously writing messages in the MDB's queue (regardless if both the Server and the Client version or just one
has a MDB activated) will cause error messages to be shown. This is no technical limitation, but that this sample requires a
match of the message sent to the MDB and the reply. During simultaneous processing of messages this simple validation logic may
get confused.

However, if only one MDB should process messages, just comment out the `@MessageDriven` annotation at the other `SampleMBD`.

### Contributor License Agreement

In order for us to accept pull requests, you must declare that you wrote the code or, at least, have the right to contribute it to the repo under the open source license of the project in the repo. It's very easy...

1. Read this (from [developercertificate.org](http://developercertificate.org/)):

  ```
  Developer Certificate of Origin
Version 1.1

Copyright (C) 2004, 2006 The Linux Foundation and its contributors.
660 York Street, Suite 102,
San Francisco, CA 94110 USA

Everyone is permitted to copy and distribute verbatim copies of this
license document, but changing it is not allowed.


Developer's Certificate of Origin 1.1

By making a contribution to this project, I certify that:

(a) The contribution was created in whole or in part by me and I
    have the right to submit it under the open source license
    indicated in the file; or

(b) The contribution is based upon previous work that, to the best
    of my knowledge, is covered under an appropriate open source
    license and I have the right under that license to submit that
    work with modifications, whether created in whole or in part
    by me, under the same open source license (unless I am
    permitted to submit under a different license), as indicated
    in the file; or

(c) The contribution was provided directly to me by some other
    person who certified (a), (b) or (c) and I have not modified
    it.

(d) I understand and agree that this project and the contribution
    are public and that a record of the contribution (including all
    personal information I submit with it, including my sign-off) is
    maintained indefinitely and may be redistributed consistent with
    this project or the open source license(s) involved.
  ```

2. If you can certify that it is true, sign off your `git commit` with a message like this:
  ```
  Signed-off-by: Scott Kurz <skurz@us.ibm.com>
  ```
  You must use your real name (no pseudonyms or anonymous contributions, sorry).
  
  Instead of typing that in every git commit message, your Git tools might let you automatically add the details for you. If you configure them to do that, when you issue the `git commit` command, just add the `-s` option.

If you are an IBM employee, please contact us directly as the contribution process is slightly different.

## Notice

© Copyright IBM Corporation 2015, 2021.

## License

```text
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
````

[Liberty Maven Plug-in]: https://github.com/OpenLiberty/ci.maven
