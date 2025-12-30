package com.example.family;

import com.hatokuse.grpc.MemberServiceGrpc;
import com.hatokuse.grpc.MessageRequest;
import com.hatokuse.grpc.MessageResponse;
import io.grpc.stub.StreamObserver;

public class MemberServiceImpl extends MemberServiceGrpc.MemberServiceImplBase {

    private final DiskStorage diskStorage;

    public MemberServiceImpl(DiskStorage diskStorage) {
        this.diskStorage = diskStorage;
    }

    @Override
    public void storeMessage(MessageRequest request,
                             StreamObserver<MessageResponse> responseObserver) {

        try {
            diskStorage.write(request.getId(), request.getContent());

            MessageResponse response = MessageResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("OK")
                    .build();

            responseObserver.onNext(response);
        } catch (Exception e) {
            MessageResponse response = MessageResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("ERROR: " + e.getMessage())
                    .build();

            responseObserver.onNext(response);
        } finally {
            responseObserver.onCompleted();
        }
    }
}
