package ru.unus.sonus.service;

import com.google.protobuf.Timestamp;
import com.thamuz.gprc.datanode.FileIOResponse;
import com.thamuz.gprc.datanode.FileIOStatusCode;
import com.thamuz.gprc.datanode.UuidRequest;
import com.thamuz.gprc.datanode.UuidServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.Instant;
import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class UuidService extends UuidServiceGrpc.UuidServiceImplBase {

    private final FileIOService fileIOService;

    @Override
    public void registerUuid(UuidRequest request, StreamObserver<FileIOResponse> responseObserver) {
        fileIOService.addUuid(UUID.fromString(request.getUuid()));

        Instant time = Instant.now();
        responseObserver.onNext(FileIOResponse.newBuilder()
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(time.getEpochSecond())
                        .setNanos(time.getNano())
                        .build())
                .setStatus(FileIOStatusCode.SUCCESS)
                .setMessage("UUID successfully registered.")
                .build()
        );
        responseObserver.onCompleted();
    }
}
