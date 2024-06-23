package com.sparrow.switcher;

import com.sparrow.common.Request;
import com.sparrow.config.SparrowProperties;
import com.sparrow.remote.auto.BiRequestStreamGrpc;
import com.sparrow.remote.auto.Payload;
import com.sparrow.remote.auto.RequestGrpc;
import com.sparrow.utils.GrpcUtils;
import com.sparrow.utils.InetUtils;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.sparrow.config.Constants.DEFAULT_SPARROW_GRPC_ADDR;
import static com.sparrow.config.Constants.SPARROW_GRPC_ADDR;

/**
 * @author 985492783@qq.com
 * @date 2024/6/15 2:38
 */
public class SparrowSwitcherService extends BiRequestStreamGrpc.BiRequestStreamImplBase implements SwitcherService {
    
    private final RequestGrpc.RequestBlockingStub blockingStub;
    
    private final BiRequestStreamGrpc.BiRequestStreamStub streamStub;
    
    private final String clientId = UUID.randomUUID().toString();
    
    public SparrowSwitcherService(SparrowProperties properties) {
        String addr = (String) properties.getOrDefault(SPARROW_GRPC_ADDR, DEFAULT_SPARROW_GRPC_ADDR);
        ManagedChannel channel = ManagedChannelBuilder.forTarget(addr).usePlaintext().build();
        this.blockingStub = RequestGrpc.newBlockingStub(channel);
        this.streamStub = BiRequestStreamGrpc.newStub(channel);
        io.grpc.stub.StreamObserver<com.sparrow.remote.auto.Payload> observer = this.streamStub.requestBiStream(
                new StreamObserver<Payload>() {
                    @Override
                    public void onNext(Payload value) {
                        System.out.println(value);
                    }
                    
                    @Override
                    public void onError(Throwable t) {
                    }
                    
                    @Override
                    public void onCompleted() {
                    
                    }
                });
        
        observer.onNext(GrpcUtils.convert(new RegistryRequest(clientId)));
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
    
}
