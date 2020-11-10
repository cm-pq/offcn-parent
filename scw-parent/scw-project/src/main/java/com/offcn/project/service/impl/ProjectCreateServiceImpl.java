package com.offcn.project.service.impl;

import com.alibaba.fastjson.JSON;
import com.offcn.dycommon.enums.ProjectStatusEnume;
import com.offcn.project.contants.ProjectConstant;
import com.offcn.project.enums.ProjectImageTypeEnume;
import com.offcn.project.mapper.*;
import com.offcn.project.po.*;
import com.offcn.project.service.ProjectCreateService;
import com.offcn.project.vo.req.ProjectRedisStorageVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.json.Json;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectCreateServiceImpl implements ProjectCreateService {
    @Autowired
    private StringRedisTemplate stingRredisTemplate;
    @Autowired
    private TProjectMapper projectMapper;

    @Autowired
    private TProjectImagesMapper projectImagesMapper;

    @Autowired
    private TProjectTagMapper projectTagMapper;

    @Autowired
    private TProjectTypeMapper projectTypeMapper;

    @Autowired
    private TReturnMapper tReturnMapper;
    @Override
    public String initCreateProject(Integer memberId) {
//       临时token
        String token = UUID.randomUUID().toString().replace("-","");
        ProjectRedisStorageVo initVo = new ProjectRedisStorageVo();
        initVo.setMemberid(memberId);
        //initVo转字符串
        String jsonString = JSON.toJSONString(initVo);
        //加入redis
        stingRredisTemplate.opsForValue().set(ProjectConstant.TEMP_PROJECT_PREFIX+token,jsonString);

        return token;
    }

    @Override
    public void saveProjectInfo(ProjectStatusEnume auth, ProjectRedisStorageVo project) {
     //1.保存项目的基本信息,获取数据库的id
        TProject projectBase = new TProject();
        BeanUtils.copyProperties(project,projectBase);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        projectBase.setCreatedate(df.format(new Date()));
         //1.1基本信息插入,获取刚才添加项目的id
        projectMapper.insertSelective(projectBase);
        //1.2 获取刚才的id
        Integer projectBaseId = projectBase.getId();
        String headerImage = project.getHeaderImage();
        TProjectImages images = new TProjectImages(null,projectBaseId,headerImage, ProjectImageTypeEnume.HEADER.getCode());
        //2.将项目保存的图片保存起来
        //2.1 保存头图
        projectImagesMapper.insertSelective(images);
        List<String> detailsImage = project.getDetailsImage();
        //2.2 保存详情图
        if (detailsImage!=null&&detailsImage.size()>0){
        for (String string : detailsImage) {
            TProjectImages ima = new TProjectImages(null,projectBaseId,string,ProjectImageTypeEnume.DETAILS.getCode());
            projectImagesMapper.insertSelective(ima);

        }
        }
        //3.保存项目的标签信息
        List<Integer> tagids = project.getTagids();
        for (Integer tagid : tagids) {
            TProjectTag tProjectTag = new TProjectTag(null,projectBaseId,tagid);
            projectTagMapper.insertSelective(tProjectTag);
        }
        //4.保存项目分类信息
        List<Integer> typeids = project.getTypeids();
        for (Integer typeid : typeids) {
            TProjectType tProjectType = new TProjectType(null,projectBaseId,typeid);
            projectTypeMapper.insertSelective(tProjectType);
        }
        //5.保存会报信息
        List<TReturn> projectReturns = project.getProjectReturns();
        for (TReturn projectReturn : projectReturns) {
            projectReturn.setProjectid(projectBaseId);
            tReturnMapper.insertSelective(projectReturn);
        }
//6.删除临时数据
        stingRredisTemplate.delete(ProjectConstant.TEMP_PROJECT_PREFIX+project.getProjectToken());

    }
}
