package com.liu.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.dao.PaymentMapper;
import com.liu.entitis.Payment;
import com.liu.service.PaymentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("paymentService")
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements PaymentService {

}