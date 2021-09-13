package com.liu.exception;

import com.alibaba.csp.sentinel.slots.block.BlockException;

public class ConstomerBlockHandler {

    public static String blockHandler(BlockException exception) {
        return "被限流了哦！！！by ConstomerBlockHandler";
    }
}
