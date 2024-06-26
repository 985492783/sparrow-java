package com.sparrow.switcher;

import com.sparrow.common.AppSwitcherItem;
import com.sparrow.common.Request;
import com.sparrow.common.Response;
import com.sparrow.config.SparrowProperties;
import com.sparrow.exception.SparrowException;
import com.sparrow.remote.auto.BiRequestStreamGrpc;
import com.sparrow.remote.auto.Payload;
import com.sparrow.remote.auto.RequestGrpc;
import com.sparrow.switcher.payload.SwitcherRequest;
import com.sparrow.switcher.payload.SwitcherResponse;
import com.sparrow.utils.GrpcUtils;
import com.sparrow.utils.InetUtils;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.sparrow.config.Constants.*;

/**
 * @author 985492783@qq.com
 * @date 2024/6/15 2:38
 */
@Slf4j
public class SparrowSwitcherService extends BiRequestStreamGrpc.BiRequestStreamImplBase implements SwitcherService {

    private final RequestGrpc.RequestBlockingStub blockingStub;

    private final BiRequestStreamGrpc.BiRequestStreamStub streamStub;
    
    private final String clientId = UUID.randomUUID().toString();

    private final SparrowProperties properties;
    
    public SparrowSwitcherService(SparrowProperties properties) throws SparrowException {
        String addr = (String) properties.getOrDefault(SPARROW_GRPC_ADDR, DEFAULT_SPARROW_GRPC_ADDR);
        ManagedChannel channel = ManagedChannelBuilder.forTarget(addr).usePlaintext().build();
        this.properties = properties;
        this.blockingStub = RequestGrpc.newBlockingStub(channel);
        this.streamStub = BiRequestStreamGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);
        io.grpc.stub.StreamObserver<com.sparrow.remote.auto.Payload> observer = this.streamStub.requestBiStream(
                new StreamObserver<Payload>() {
                    @Override
                    public void onNext(Payload value) {
                        System.out.println(value);
                        latch.countDown();
                    }
                    
                    @Override
                    public void onError(Throwable t) {
                        log.error("stream error", t);
                    }
                    
                    @Override
                    public void onCompleted() {
                    }
                });
        observer.onNext(GrpcUtils.convert(new RegistryRequest(clientId)));
        try {
            latch.await(10_000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new SparrowException(300, "registry error");
        }
        log.info("register success");
    }

    @Override
    public SwitcherResponse registry(String namespace, String appName, Map<String, Map<String, AppSwitcherItem>> classMap) throws SparrowException {
        SwitcherRequest switcherRequest = SwitcherRequest.builder().appName(appName).ip(InetUtils.getSelfIp()).kind(SPARROW_SWITCHER_REGISTRY)
                .classMap(classMap).build();
        switcherRequest.setClientId(clientId);
        switcherRequest.setNamespace(namespace);

        Payload payload = this.blockingStub.request(GrpcUtils.convert(switcherRequest));
        Response response = GrpcUtils.Parser(payload);
        if (response.getStatusCode() != 200) {
            throw new SparrowException(301, "registry response error, status code is " + response.getStatusCode());
        }
        if (response.getClass() != SwitcherResponse.class) {
            throw new SparrowException(301, "registry response error, response class is not ");
        }
        return (SwitcherResponse) response;
    }


    private static class RegistryRequest extends Request {
        
        private String clientId;
        
        private String ip;
        
        public RegistryRequest(String clientId) {
            this.clientId = clientId;
            this.ip = InetUtils.getSelfIp();
        }
        
        @Override
        public String getType() {
            return "server.RegistryRequest";
        }
        
        @Override
        public Map<String, String> getHeaders() {
            return new HashMap<>();
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }
    }

    private static class RegistryResponse extends Response {

        @Override
        public String getType() {
            return "server.RegistryResponse";
        }
    }

    
}
