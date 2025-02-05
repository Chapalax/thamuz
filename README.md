# Thamuz 📦

Thamuz is a distributed file storage system based on [Java](https://www.oracle.com/java/) and [gRPC](https://grpc.io/). It was designed to efficiently manage file storage and retrieval in a cluster environment. The system consists of data nodes, which are responsible for storing files, and a coordinator node, which manages data node interactions and handles client requests.

## Features 🔥

- **Distributed Architecture**: Data is stored across multiple nodes for reliability and scalability.
- **Coordinator and Data Nodes**: Clear separation of responsibilities between the coordinator and data nodes.
- **Heartbeat Mechanism**: Ensures live monitoring of data nodes.
- **File Management**: Allows file upload and retrieval.
- **Extensible Protocol Buffers**: Uses [gRPC](https://grpc.io/) for communication, ensuring high performance and language interoperability.

---

## Prerequisites 📌

- [`Java 17+`](https://www.oracle.com/java/technologies/downloads/#java17)
- [`Maven`](https://maven.apache.org/) 

> Ensure maven is using the correct java version: `$ mvn -version` has to print out `Java version: 17...`

- [`Makefile`](https://www.gnu.org/software/make/) to remove tedious chain of commands when building & running the project
- [`Docker`](https://www.docker.com/) (In the future)

---

## Getting Started 🛠

### Clone the Repository

```bash
$ git clone https://github.com/Chapalax/thamuz.git
```

### Build the Project

```bash
thamuz$ make dev-build
```

### Run the Services

> You should do it in separate shells open so that logs of different services don't interleave 
#### 1. Start the Coordinator Node

```bash
thamuz$ make dev-run-coordinator
```

#### 2. Run Data Nodes

*Each* data node should run on a different port. **Non-provided `GRPC_PORT` variable will result to `9091`**.

```bash
thamuz$ make dev-run-datanode GRPC_PORT=<your_value>
```

#### 3. Run the CLI client

```bash
thamuz$ make dev-run-client
```
---

## Usage ⚡️

At present, two functions are available: uploading and downloading. These allow users to save files on the server and to retrieve them, respectively.

### Uploading

1. Send the `upload` command.

2. Provide the absolute path of an existing file on your computer.

3. Specify the absolute path where the file should be uploaded on the server, including the file extension.

4. Enjoy! 👀

### Downloading

1. Send the `download` command.

2. Provide the absolute path of the file located on the server, including the file extension.

3. Receive a message indicating the location of the downloaded file.

4. Enjoy! 👀


### Examples 🐘 

```
This is my first time using gRPC. Don't be too hard on me ^_^
List of available commands:
1) upload;
2) download.

Please enter the command:
upload
Enter the absolute path to the file you want to upload:
pom.xml 
Enter the absolute path to the file you would like to create:
/tmp
/tmp
2025-01-24T04:29:56.471+03:00  INFO 40364 --- [grpc-client] [           main] r.u.sonus.service.FileProcessingService  : SUCCESS File successfully uploaded.
```

---

## Heartbeat Mechanism 💗

The data nodes periodically send heartbeat messages to the coordinator to indicate they are active. If a node fails to send a heartbeat within a predefined interval, the coordinator marks the node as inactive.

---

## Authors ❤️

- [Chapalax](https://github.com/Chapalax)
- [chrxn1c](https://github.com/chrxn1c)

For questions, suggestions, or issues, please create an issue on GitHub or contact the author directly.

