package com.offcn.user.mapper;

import com.offcn.user.po.TMemberCert;
import com.offcn.user.po.TMemberCertExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface TMemberCertMapper {
    int countByExample(TMemberCertExample example);

    int deleteByExample(TMemberCertExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(TMemberCert record);

    int insertSelective(TMemberCert record);

    List<TMemberCert> selectByExample(TMemberCertExample example);

    TMemberCert selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") TMemberCert record, @Param("example") TMemberCertExample example);

    int updateByExample(@Param("record") TMemberCert record, @Param("example") TMemberCertExample example);

    int updateByPrimaryKeySelective(TMemberCert record);

    int updateByPrimaryKey(TMemberCert record);
}