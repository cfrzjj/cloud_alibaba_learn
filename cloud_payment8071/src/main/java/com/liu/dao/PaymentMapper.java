package com.liu.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liu.entitis.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {
    //int create(com.liu.entitis.Payment payment);

   // Payment getPaymentById(@Param("id") Long id);

}