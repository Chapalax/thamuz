package ru.unus.sonus.service;

import com.google.protobuf.Timestamp;
import com.thamuz.gprc.coordinator.ClusterManagerServiceGrpc;
import com.thamuz.gprc.coordinator.HeartbeatRequest;
import com.thamuz.gprc.coordinator.HeartbeatResponse;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.unus.sonus.model.DataNodeInfo;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@GrpcService
public class ClusterManagerService extends ClusterManagerServiceGrpc.ClusterManagerServiceImplBase {

    private final HashMap<String, DataNodeInfo> dataNodes = new HashMap<>();

    @Override
    public StreamObserver<HeartbeatRequest> nodeHeartbeat(StreamObserver<HeartbeatResponse> responseObserver) {
        return new StreamObserver<>() {

            @Override
            public void onNext(HeartbeatRequest heartbeatRequest) {
                if (!dataNodes.containsKey(heartbeatRequest.getNodeAddress())) {
                    log.info("DataNode {} was successfully registered!", heartbeatRequest.getNodeAddress());
                }
                // TODO: validate data
                DataNodeInfo info = new DataNodeInfo(heartbeatRequest.getWorkload(), heartbeatRequest.getTimestamp());
                dataNodes.put(heartbeatRequest.getNodeAddress(), info);
                responseObserver.onNext(HeartbeatResponse.newBuilder()
                        .setStatus(true)
                        .setMessage("Heartbeat processed successfully")
                        .build()
                );
            }

            @Override
            public void onError(Throwable throwable) {
                log.error(throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    public String getLeastLoadedDataNodeAddress() {
        String leastLoadedDataNodeAddress = "";
        int leastLoadedDataNodeSize = Integer.MAX_VALUE;
        for (Map.Entry<String, DataNodeInfo> info : dataNodes.entrySet()) {
            if (info.getValue().workload() < leastLoadedDataNodeSize) {
                leastLoadedDataNodeAddress = info.getKey();
                leastLoadedDataNodeSize = info.getValue().workload();
            }
        }
        return leastLoadedDataNodeAddress;
    }

    public boolean isAvailable(String dataNodeAddress) {
        return dataNodes.containsKey(dataNodeAddress);
    }

    public void deleteDeathDataNodes(int boundary) {
        Instant time = Instant.now();
        List<String> deathDataNodes = new ArrayList<>();
        for (Map.Entry<String, DataNodeInfo> info : dataNodes.entrySet()) {
            Timestamp currentTimestamp = info.getValue().timestamp();
            Instant currentInstant = Instant.ofEpochSecond(currentTimestamp.getSeconds(), currentTimestamp.getNanos());
            if (time.minus(boundary, ChronoUnit.MINUTES).isAfter(currentInstant)) {
                deathDataNodes.add(info.getKey());
            }
        }

        for (String dataNodeAddress : deathDataNodes) {
            log.info("DataNode {} was removed.", dataNodeAddress);
            dataNodes.remove(dataNodeAddress);
        }
    }
}
