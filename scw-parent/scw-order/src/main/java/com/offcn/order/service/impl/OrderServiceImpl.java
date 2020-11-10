package com.offcn.order.service.impl;

import com.offcn.OrderStartApplication;
import com.offcn.dycommon.enums.OrderStatusEnumes;
import com.offcn.dycommon.response.AppResponse;
import com.offcn.order.mapper.TOrderMapper;
import com.offcn.order.po.TOrder;
import com.offcn.order.service.OrderService;
import com.offcn.order.service.ProjectServiceFeign;
import com.offcn.order.vo.req.OrderInfoSubmitVo;
import com.offcn.order.vo.resp.TReturn;
import com.offcn.utils.AppDateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private TOrderMapper orderMapper;
    @Autowired
    private ProjectServiceFeign projectServiceFeign;


    @Override
    public TOrder saveOrder(OrderInfoSubmitVo vo) {
       TOrder order = new TOrder();
       //获取令牌
        String accessToken = vo.getAccessToken();
        //从缓存中获取会员id
        String memberId = redisTemplate.opsForValue().get(accessToken);
        order.setMemberid(Integer.parseInt(memberId));
        //项目id
        order.setProjectid(vo.getProjectid());
        //回报id
        order.setReturnid(vo.getReturnid());
        //生成订单编号
        String orderNum = UUID.randomUUID().toString().replace("-", "");
        order.setOrdernum(orderNum);
        //订单创建时间
        order.setCreatedate(AppDateUtils.getFormatTime());
        //从模块中获取回报列表
        AppResponse<List<TReturn>> listAppResponse = projectServiceFeign.returnInfo(vo.getProjectid());
        List<TReturn> tReturnList = listAppResponse.getData();
        TReturn tReturn = tReturnList.get(0);
        //计算回报金额 支付的数量*支付金额+运费
       Integer totalMoney = vo.getRtncount()*tReturn.getSupportmoney()+tReturn.getFreight();
       order.setMoney(totalMoney);
       //回报数量
        order.setRtncount(vo.getRtncount());
        //支付状态 未支付
        order.setStatus(OrderStatusEnumes.UNPAY.getCode()+"");
        //收货地址
        order.setAddress(vo.getAddress());
        //发票名头
        order.setInvoictitle(vo.getInvoictitle());
        //备主
        order.setRemark(vo.getRemark());
        orderMapper.insertSelective(order);
        return null;
    }
}
