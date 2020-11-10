package com.offcn.user.service.impl;

import com.offcn.user.enums.UserExceptionEnum;
import com.offcn.user.exception.UserException;
import com.offcn.user.mapper.TMemberAddressMapper;
import com.offcn.user.mapper.TMemberMapper;
import com.offcn.user.po.TMember;
import com.offcn.user.po.TMemberAddress;
import com.offcn.user.po.TMemberAddressExample;
import com.offcn.user.po.TMemberExample;
import com.offcn.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserServiceImpl implements UserService {
  @Autowired
  private TMemberMapper memberMapper;
  @Autowired
  private TMemberAddressMapper memberAddressMapper;

    @Override
    public void registerUser(TMember member) {
        TMemberExample example = new TMemberExample();
        TMemberExample.Criteria criteria = example.createCriteria();
        //查看数据库是否有相同的手机号
        TMemberExample.Criteria criteria1 = criteria.andLoginacctEqualTo(member.getLoginacct());
       long l = memberMapper.countByExample(example);
         if (l>0){
             throw  new UserException(UserExceptionEnum.LOGINACCT_EXIST);
         }
//         2.手机号未被注册
//        设置密码采用BCrypt加密
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode(member.getUserpswd());
        member.setUserpswd(encode);
        //这里是因为数据库的注册手机号就是用户名,所以要设置
        member.setUsername(member.getLoginacct());
        member.setEmail(member.getEmail());
        //实名认证状态 0 - 未实名认证， 1 - 实名认证申请中， 2 - 已实名认证
        member.setAuthstatus("0");
        //用户类型: 0 - 个人， 1 - 企业
        member.setUsertype("0");
        //账户类型: 0 - 企业， 1 - 个体， 2 - 个人， 3 - 政府
        member.setAccttype("2");
        System.out.println("插入数据:"+member.getLoginacct());
        memberMapper.insertSelective(member);
    }

    @Override
    public TMember login(String username, String password) {
       BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
       TMemberExample example = new TMemberExample();
       example.createCriteria().andLoginacctEqualTo(username);
        List<TMember> list = memberMapper.selectByExample(example);
        if (list != null && list.size()==1){
            TMember member = list.get(0);
            boolean matches = encoder.matches(password, member.getUserpswd());
            return matches?member:null;
        }


        return null;
    }

    @Override
    public TMember findTmemberById(Integer id) {

        return memberMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<TMemberAddress> addressList(Integer memberId) {

        TMemberAddressExample example = new TMemberAddressExample();
        TMemberAddressExample.Criteria criteria = example.createCriteria();
        criteria.andMemberidEqualTo(memberId);
        return memberAddressMapper.selectByExample(example);
    }
}
