package ru.unus.sonus.service;

import com.thamuz.gprc.coordinator.CoordinatorServiceGrpc;
import com.thamuz.gprc.coordinator.NavigateRequest;
import com.thamuz.gprc.coordinator.NavigateResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.unus.sonus.model.FileInfo;

import java.util.HashMap;
import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class CoordinatorService extends CoordinatorServiceGrpc.CoordinatorServiceImplBase {

    private final ClusterManagerService clusterManagerService;
    private final HashMap<String, FileInfo> fileInfoMap = new HashMap<>();

    @Override
    public void defineReadAddress(NavigateRequest request, StreamObserver<NavigateResponse> responseObserver) {
        // TODO: Exception
        if (!fileInfoMap.containsKey(request.getFilePath())) {
            throw new RuntimeException();
        }
        FileInfo fileInfo = fileInfoMap.get(request.getFilePath());
        responseObserver.onNext(NavigateResponse.newBuilder()
                .setDatanode(fileInfo.dataNodeAddress())
                .setUuid(fileInfo.uuid().toString())
                .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void defineWriteAddress(NavigateRequest request, StreamObserver<NavigateResponse> responseObserver) {
        // TODO: Exception
        if (fileInfoMap.containsKey(request.getFilePath())) {
            throw new RuntimeException();
        }
        String dataNodeAddress = clusterManagerService.getLeastLoadedDataNodeAddress();
        UUID uuid = UUID.randomUUID();
        fileInfoMap.put(request.getFilePath(), new FileInfo(dataNodeAddress, uuid));
        responseObserver.onNext(NavigateResponse.newBuilder()
                .setDatanode(dataNodeAddress)
                .setUuid(uuid.toString())
                .build()
        );
        responseObserver.onCompleted();
    }
}
