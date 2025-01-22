package ru.unus.sonus.service;

import com.thamuz.gprc.coordinator.CoordinatorServiceGrpc;
import com.thamuz.gprc.coordinator.NavigateErrorCode;
import com.thamuz.gprc.coordinator.NavigateRequest;
import com.thamuz.gprc.coordinator.NavigateResponse;
import com.thamuz.gprc.datanode.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.unus.sonus.exception.NavigateException;
import ru.unus.sonus.model.FileInfo;

import java.util.HashMap;
import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class CoordinatorService extends CoordinatorServiceGrpc.CoordinatorServiceImplBase {

    private final ClusterManagerService clusterManagerService;

    private final HashMap<String, FileInfo> fileInfoMap = new HashMap<>();

    private final HashMap<String, ManagedChannel> channelPool = new HashMap<>();

    @Override
    public void defineReadAddress(NavigateRequest request, StreamObserver<NavigateResponse> responseObserver) {
        if (!fileInfoMap.containsKey(request.getFilePath())) {
            throw new NavigateException(NavigateErrorCode.FILE_NOT_FOUND, "File not found.");
        }

        FileInfo fileInfo = fileInfoMap.get(request.getFilePath());
        if (!clusterManagerService.isAvailable(fileInfo.dataNodeAddress())) {
            throw new NavigateException(NavigateErrorCode.DATANODE_OFFLINE, "DataNode is currently unavailable.");
        }

        responseObserver.onNext(NavigateResponse.newBuilder()
                .setDatanode(fileInfo.dataNodeAddress())
                .setUuid(fileInfo.uuid().toString())
                .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void defineWriteAddress(NavigateRequest request, StreamObserver<NavigateResponse> responseObserver) {
        if (fileInfoMap.containsKey(request.getFilePath())) {
            throw new NavigateException(NavigateErrorCode.FILE_ALREADY_EXISTS, "File already exists.");
        }

        String dataNodeAddress = clusterManagerService.getLeastLoadedDataNodeAddress();
        if (dataNodeAddress.isEmpty()) {
            throw new NavigateException(NavigateErrorCode.DATANODE_OFFLINE, "DataNode is currently unavailable.");
        }

        UUID uuid = UUID.randomUUID();

        ManagedChannel targetChannel = channelPool.computeIfAbsent(dataNodeAddress,
                addr -> ManagedChannelBuilder.forTarget(addr)
                        .usePlaintext()
                        .build()
        );

        UuidServiceGrpc.UuidServiceBlockingStub targetStub = UuidServiceGrpc.newBlockingStub(targetChannel);
        FileIOResponse response = targetStub.registerUuid(UuidRequest.newBuilder()
                .setUuid(uuid.toString())
                .build()
        );

        if (response.getStatus().equals(FileIOStatusCode.INTERNAL_SERVER_ERROR)) {
            throw new NavigateException(NavigateErrorCode.DATANODE_OFFLINE, "DataNode is currently unavailable.");
        }

        fileInfoMap.put(request.getFilePath(), new FileInfo(dataNodeAddress, uuid));
        responseObserver.onNext(NavigateResponse.newBuilder()
                .setDatanode(dataNodeAddress)
                .setUuid(uuid.toString())
                .build()
        );
        responseObserver.onCompleted();
    }
}
