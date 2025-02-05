dev-build:
	cd proto-common && mvn clean install compile && cd .. ; \
    mvn clean install package spring-boot:repackage -pl !proto-common


dev-run-coordinator:
	cd coordinator && java -jar target/coordinator-1.0-SNAPSHOT.jar &

GRPC_PORT = 9091
dev-run-datanode:
	cd datanode && java -jar target/datanode-1.0-SNAPSHOT.jar --grpc.server.port=$(GRPC_PORT) &

dev-run-client:
	cd client && java -jar target/client-1.0-SNAPSHOT.jar