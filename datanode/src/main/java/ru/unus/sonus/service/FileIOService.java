package ru.unus.sonus.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import com.thamuz.gprc.datanode.*;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.unus.sonus.schedule.HeartbeatScheduler;

import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

@Slf4j
@GrpcService
public class FileIOService extends FileIOServiceGrpc.FileIOServiceImplBase {

    private final HashMap<UUID, ByteString> storage = new HashMap<>();

    @Override
    public void writeFile(UploadFileRequest request, StreamObserver<FileIOResponse> responseObserver) {
        UUID currentUuid = UUID.fromString(request.getUuid());
        Instant time = Instant.now();
        FileIOResponse.Builder builder = FileIOResponse.newBuilder()
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(time.getEpochSecond())
                        .setNanos(time.getNano())
                        .build());

        if (!storage.containsKey(currentUuid)) {
            log.info("Invalid UUID {}", currentUuid);
            builder.setStatus(FileIOStatusCode.INVALID_UUID)
                    .setMessage("Invalid UUID.");
        } else {
            log.info("Uploaded file {}", currentUuid);
            storage.put(currentUuid, request.getFile());
            HeartbeatScheduler.workload += request.getFile().size();
            builder.setStatus(FileIOStatusCode.SUCCESS)
                    .setMessage("File successfully uploaded.");
        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void readFile(UuidRequest request, StreamObserver<FileIOResponse> responseObserver) {
        UUID currentUuid = UUID.fromString(request.getUuid());
        Instant time = Instant.now();
        FileIOResponse.Builder builder = FileIOResponse.newBuilder()
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(time.getEpochSecond())
                        .setNanos(time.getNano())
                        .build());

        if (!storage.containsKey(currentUuid)) {
            log.info("Invalid UUID {}", currentUuid);
            builder.setStatus(FileIOStatusCode.INVALID_UUID)
                    .setMessage("Invalid UUID.");
        } else {
            log.info("Downloaded file {}", currentUuid);
            builder.setStatus(FileIOStatusCode.SUCCESS)
                    .setMessage("File successfully downloaded.")
                    .setFile(storage.get(currentUuid));
        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    public void addUuid(UUID uuid) {
        log.info("UUID {} successfully registered.", uuid);
        storage.put(uuid, null);
    }
}
