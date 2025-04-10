package com.pm.billingservice.grpc;

import billing.BillingResponse;
import billing.BillingServiceGrpc.BillingServiceImplBase ;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class BillingGrpcService extends BillingServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(BillingGrpcService.class);

    @Override
    public void createBillingAccount(billing.BillingRequest billingRequest,
                                     StreamObserver<BillingResponse> responseObserver) {

        log.info("Creating Billing Account request received {}: " , billingRequest.toString());

        // Business logic -e.g save to database etc

        BillingResponse response = BillingResponse.newBuilder()
                .setAccountId("1234")
                .setStatus("SUCCESS")
                .build();
        responseObserver.onNext(response); // sends response from grpc server
        responseObserver.onCompleted(); // that response is completed and ready to end cycle

    }
}
