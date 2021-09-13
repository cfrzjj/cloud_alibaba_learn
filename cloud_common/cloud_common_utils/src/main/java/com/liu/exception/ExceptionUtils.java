package com.liu.exception;

public class ExceptionUtils {


   /* *//**
     * 静态方法
     *      返回值: SentinelClientHttpResponse
     *      参数 : request , byte[] , clientRquestExcetion , blockException
     *//*
    //限流熔断业务逻辑
    public static SentinelClientHttpResponse handleBlock(HttpRequest request, byte[] body, ClientHttpRequestExecution execution, BlockException ex) {
        return new SentinelClientHttpResponse("abc");
    }

    //异常降级业务逻辑
    public static SentinelClientHttpResponse handleFallback(HttpRequest request, byte[] body,
                                                            ClientHttpRequestExecution execution, BlockException ex) {
        return new SentinelClientHttpResponse("def");
    }*/
}
