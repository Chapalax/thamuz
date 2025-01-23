package ru.unus.sonus.schedule;

import com.google.protobuf.Timestamp;
import com.thamuz.gprc.coordinator.ClusterManagerServiceGrpc;
import com.thamuz.gprc.coordinator.HeartbeatRequest;
import com.thamuz.gprc.coordinator.HeartbeatResponse;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;

@Slf4j
@Component
@EnableScheduling
public class HeartbeatScheduler {

    @GrpcClient("coordinator")
    private ClusterManagerServiceGrpc.ClusterManagerServiceStub coordinatorStub;

    @Value("${grpc.server.port}")
    private String serverPort;

    private StreamObserver<HeartbeatRequest> requestStreamObserver;

    public static int workload = 0;

    @PostConstruct
    private void postConstruct() {
        requestStreamObserver = coordinatorStub.nodeHeartbeat(new StreamObserver<>() {

            @Override
            public void onNext(HeartbeatResponse heartbeatResponse) {
                log.info("Heartbeat acknowledged: {}", heartbeatResponse.getMessage());
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("Heartbeat failed: {}", throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                log.info("Heartbeat stream closed.");
            }
        });
    }

    @Scheduled(fixedDelayString = "${scheduler.interval}")
    public void sendHeartbeat() {
        log.info("Start scheduling heartbeat...");
        requestStreamObserver.onNext(buildHeartbeatRequest());

    }

    private HeartbeatRequest buildHeartbeatRequest() {
        Instant time = Instant.now();
        return HeartbeatRequest.newBuilder()
                .setNodeAddress("localhost:" + serverPort)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(time.getEpochSecond())
                        .setNanos(time.getNano())
                        .build())
                .setWorkload(workload)
                .build();
    }
}
