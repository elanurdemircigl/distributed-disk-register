//deneme
package com.example.family;

import com.hatokuse.grpc.MemberServiceGrpc;
import com.hatokuse.grpc.MessageRequest;
import com.hatokuse.grpc.MessageResponse;
import io.grpc.stub.StreamObserver;

import com.hatokuse.grpc.RetrieveRequest;
import com.hatokuse.grpc.RetrieveResponse;

import com.hatokuse.grpc.CountRequest;
import com.hatokuse.grpc.CountResponse;


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

    @Override
    public void retrieveMessage(RetrieveRequest request,
                                StreamObserver<RetrieveResponse> responseObserver) {
        try {
            int id = request.getId();
            String content = diskStorage.read(id);

            if (content == null) {
                responseObserver.onNext(RetrieveResponse.newBuilder()
                        .setFound(false)
                        .setContent("")
                        .setMessage("NOT_FOUND")
                        .build());
            } else {
                responseObserver.onNext(RetrieveResponse.newBuilder()
                        .setFound(true)
                        .setContent(content)
                        .setMessage("OK")
                        .build());
            }
        } catch (Exception e) {
            responseObserver.onNext(RetrieveResponse.newBuilder()
                    .setFound(false)
                    .setContent("")
                    .setMessage("ERROR: " + e.getMessage())
                    .build());
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getLocalCount(CountRequest request,
                              StreamObserver<CountResponse> responseObserver) {
        int c = diskStorage.countMessages();
        responseObserver.onNext(CountResponse.newBuilder().setCount(c).build());
        responseObserver.onCompleted();
    }

}
