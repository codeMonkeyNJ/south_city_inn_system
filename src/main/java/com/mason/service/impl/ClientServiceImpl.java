package com.mason.service.impl;

import com.mason.domain.po.Client;
import com.mason.domain.po.ClientCoupon;
import com.mason.mapper.ClientMapper;
import com.mason.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {
    @Autowired
    private ClientMapper clientMapper;
    @Override
    public Integer selectUserCouponNumByCId(Integer clientId, Integer couponId) {
        Integer num = clientMapper.selectUserCouponNumByCId(clientId, couponId);
        if (num == null){return 0;}
        return num;
    }

    @Override
    public List<ClientCoupon> selectUserCouponByCId(Integer clientId) {
        return clientMapper.selectUserCouponByCId(clientId);
    }


    @Override
    public Client selectClientByCId(Integer clientId) {
        return clientMapper.selectClientByCId(clientId);
    }

    @Override
    public boolean checkparticipated(Integer activityId, Integer loginUserId) {
        return clientMapper.countClientActivity(activityId, loginUserId) != 0;
    }
}
