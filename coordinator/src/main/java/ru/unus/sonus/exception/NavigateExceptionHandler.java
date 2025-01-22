package ru.unus.sonus.exception;

import com.google.protobuf.Any;
import com.google.protobuf.Timestamp;
import com.google.rpc.Code;
import com.google.rpc.Status;
import com.thamuz.gprc.coordinator.NavigateExceptionResponse;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

import java.time.Instant;

@GrpcAdvice
public class NavigateExceptionHandler {

    @GrpcExceptionHandler(NavigateException.class)
    public StatusRuntimeException handleNavigateException(NavigateException exception) {
        Instant time = Instant.now();
        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(time.getEpochSecond())
                .setNanos(time.getNano())
                .build();

        NavigateExceptionResponse exceptionResponse = NavigateExceptionResponse.newBuilder()
                .setErrorCode(exception.getErrorCode())
                .setMessage(exception.getMessage())
                .setTimestamp(timestamp)
                .build();

        Status.Builder statusBuilder = Status.newBuilder();

        switch (exception.getErrorCode()) {
            case FILE_NOT_FOUND -> statusBuilder.setCode(Code.NOT_FOUND_VALUE);
            case FILE_ALREADY_EXISTS -> statusBuilder.setCode(Code.ALREADY_EXISTS_VALUE);
            case FILE_PATH_IS_NULL -> statusBuilder.setCode(Code.INVALID_ARGUMENT_VALUE);
            case DATANODE_OFFLINE -> statusBuilder.setCode(Code.INTERNAL_VALUE);
        }

        return StatusProto.toStatusRuntimeException(statusBuilder.setMessage(exception.getMessage())
                .addDetails(Any.pack(exceptionResponse))
                .build()
        );
    }
}
