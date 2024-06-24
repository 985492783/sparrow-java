package com.sparrow.utils;

import com.alibaba.fastjson2.JSON;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.UnsafeByteOperations;
import com.sparrow.common.ErrorCodeEnums;
import com.sparrow.common.Request;
import com.sparrow.common.Response;
import com.sparrow.config.Constants;
import com.sparrow.exception.SparrowException;
import com.sparrow.remote.auto.Metadata;
import com.sparrow.remote.auto.Payload;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author 985492783@qq.com
 * @date 2024/6/15 5:20
 */
public class GrpcUtils {
    private static final Map<String, Class<? extends com.sparrow.common.Payload>> parserMap = new HashMap<>();
    static {
        ServiceLoader<com.sparrow.common.Payload> loader = ServiceLoader.load(com.sparrow.common.Payload.class);
        loader.forEach(k -> {
            parserMap.put(k.getType(), k.getClass());
        });
    }
    public static Payload convert(Request request) {
        Metadata newMeta = Metadata.newBuilder().setType(request.getType())
                .setClientIp("").putAllHeaders(request.getHeaders()).build();
    
        byte[] jsonBytes = JSON.toJSONBytes(request);
    
        Payload.Builder builder = Payload.newBuilder();
    
        return builder.setBody(Any.newBuilder().setValue(UnsafeByteOperations.unsafeWrap(jsonBytes)))
                .setMetadata(newMeta).build();
    }
    
    public static Response Parser(Payload payload) throws SparrowException {
        Metadata metadata = payload.getMetadata();
        String type = metadata.getType();
        Class<?> aClass = parserMap.get(type);
        if (aClass == null || !Response.class.isAssignableFrom(aClass)) {
            throw new SparrowException(ErrorCodeEnums.SYSTEM_ERROR.getCode(), "parser type is illegal");
        }
        ByteString byteString = payload.getBody().getValue();
        byte[] bytes = byteString.toByteArray();
        Response response = (Response) JSON.parseObject(bytes, aClass);
        int statusCode = Integer.parseInt(payload.getMetadata().getHeadersOrDefault(Constants.STATUS_CODE, "400"));
        response.setStatusCode(statusCode);
        return response;
    }
    
}
