package com.liu.entitis;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor  // 有参构造函数
@NoArgsConstructor //无参函数
public class Payment implements Serializable { //为对象提供标准的序列化与反序列化操作
    private Long id;

    /**
     * 订单号
     */
    private String serial;

}