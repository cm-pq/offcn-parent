package com.offcn.user.service;

import com.offcn.user.po.TMember;
import com.offcn.user.po.TMemberAddress;
import io.swagger.models.auth.In;

import java.util.List;

public interface UserService {
//注册
    public void registerUser(TMember member);
//    登录
    public TMember login(String username,String password);
    //findOne
    public TMember findTmemberById(Integer id);
    /**
     * 获取用户收货地址
     * @param memberId
     * @return
     */
    List<TMemberAddress> addressList(Integer memberId);
}
