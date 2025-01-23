package ru.unus.sonus.service;

import com.google.protobuf.ByteString;
import com.thamuz.gprc.coordinator.CoordinatorServiceGrpc;
import com.thamuz.gprc.coordinator.NavigateRequest;
import com.thamuz.gprc.coordinator.NavigateResponse;
import com.thamuz.gprc.datanode.FileIOResponse;
import com.thamuz.gprc.datanode.FileIOServiceGrpc;
import com.thamuz.gprc.datanode.UploadFileRequest;
import com.thamuz.gprc.datanode.UuidRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FileProcessingService {

    @GrpcClient("coordinator")
    private CoordinatorServiceGrpc.CoordinatorServiceBlockingStub coordinatorStub;

    public void uploadFile(String filePath, byte[] fileContent) {
        NavigateRequest request = NavigateRequest.newBuilder()
                .setFilePath(filePath)
                .build();

        NavigateResponse navigateResponse = coordinatorStub.defineWriteAddress(request);

        ManagedChannel targetChannel = ManagedChannelBuilder.forTarget(navigateResponse.getDatanode())
                        .usePlaintext()
                        .build();
        FileIOServiceGrpc.FileIOServiceBlockingStub dataNodeStub = FileIOServiceGrpc.newBlockingStub(targetChannel);

        UploadFileRequest uploadFileRequest = UploadFileRequest.newBuilder()
                .setUuid(navigateResponse.getUuid())
                .setFile(ByteString.copyFrom(fileContent))
                .build();

        FileIOResponse fileIOResponse = dataNodeStub.writeFile(uploadFileRequest);
        log.info("{} {}", fileIOResponse.getStatus(), fileIOResponse.getMessage());
        targetChannel.shutdown();
    }

    public byte[] readFile(String filePath) {
        NavigateRequest request = NavigateRequest.newBuilder()
                .setFilePath(filePath)
                .build();

        NavigateResponse navigateResponse = coordinatorStub.defineReadAddress(request);

        ManagedChannel targetChannel = ManagedChannelBuilder.forTarget(navigateResponse.getDatanode())
                .usePlaintext()
                .build();
        FileIOServiceGrpc.FileIOServiceBlockingStub dataNodeStub = FileIOServiceGrpc.newBlockingStub(targetChannel);

        UuidRequest uuidRequest = UuidRequest.newBuilder()
                .setUuid(navigateResponse.getUuid())
                .build();

        targetChannel.shutdown();

        return dataNodeStub.readFile(uuidRequest).getFile().toByteArray();
    }
}
