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
- [`Docker`](https://www.docker.com/) (In the future)

---

## Getting Started 🛠

### Clone the Repository

```bash
git clone https://github.com/Chapalax/thamuz.git
```

### Build the Project

#### 1. Build the `proto-common` module

(In /thamuz/proto-common directory)
```bash
mvn clean install compile
```

#### 2. Build other modules

(In /thamuz directory)
```
mvn clean install package spring-boot:repackage -pl !proto-common
```

### Run the Services

#### 1. Start the Coordinator Node

(In /thamuz/coordinator directory)
```bash
java -jar target/coordinator-1.0-SNAPSHOT.jar
```

#### 2. Start Data Nodes

Each data node should run on a different port (default 9091):

(In /thamuz/datanode directory)
```bash
java -jar target/datanode-1.0-SNAPSHOT.jar --grpc.server.port={your port}
```

#### 3. Start the Client

(In /thamuz/client directory)
```bash
java -jar target/client-1.0-SNAPSHOT.jar
```
There will be a Docker option here in the future :)

---

## Usage ⚡️

At present, two functions are available: upload and download. These allow users to save files to the server and retrieve them, respectively.

### Upload

Step 1: Send the "upload" command.

Step 2: Provide the absolute path of an existing file on your computer.

Step 3: Specify the absolute path where the file should be uploaded on the server, including the file extension.

Step 4: Enjoy! 👀

### Download

Step 1: Send the "download" command.

Step 2: Provide the absolute path of the file located on the server, including the file extension.

Step 3: Receive a message indicating the location of the downloaded file.

Step 4: Enjoy! 👀

---

## Heartbeat Mechanism 💗

The data nodes periodically send heartbeat messages to the coordinator to indicate they are active. If a node fails to send a heartbeat within a predefined interval, the coordinator marks the node as inactive.

---

## Authors ❤️

- [Chapalax](https://github.com/Chapalax)

For questions, suggestions, or issues, please create an issue on GitHub or contact the author directly.

