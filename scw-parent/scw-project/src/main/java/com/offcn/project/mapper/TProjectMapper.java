package com.offcn.project.mapper;

import com.offcn.project.po.TProject;
import com.offcn.project.po.TProjectExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface TProjectMapper {
    int countByExample(TProjectExample example);

    int deleteByExample(TProjectExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(TProject record);

    int insertSelective(TProject record);

    List<TProject> selectByExample(TProjectExample example);

    TProject selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") TProject record, @Param("example") TProjectExample example);

    int updateByExample(@Param("record") TProject record, @Param("example") TProjectExample example);

    int updateByPrimaryKeySelective(TProject record);

    int updateByPrimaryKey(TProject record);
}