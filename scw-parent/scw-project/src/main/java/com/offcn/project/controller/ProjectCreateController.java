package com.offcn.project.controller;

import com.alibaba.fastjson.JSON;
import com.offcn.dycommon.enums.ProjectStatusEnume;
import com.offcn.dycommon.response.AppResponse;
import com.offcn.project.contants.ProjectConstant;
import com.offcn.project.po.TReturn;
import com.offcn.project.service.ProjectCreateService;
import com.offcn.project.vo.req.ProjectBaseInfoVo;
import com.offcn.project.vo.req.ProjectRedisStorageVo;
import com.offcn.project.vo.req.ProjectReturnVo;
import com.offcn.vo.BaseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "项目基本功能模块（创建、保存、项目信息获取、文件上传等）")
@Slf4j
@RequestMapping("/project")
@RestController
public class ProjectCreateController {
  @Autowired
    private StringRedisTemplate stringRedisTemplate;
  @Autowired
    private ProjectCreateService projectCreateService;

  @ApiOperation("项目发起第1步-阅读同意协议")
    @GetMapping("/init")
    public AppResponse<String> init(BaseVo vo) {
        String accessToken = vo.getAccessToken();
        //通过登录令牌获取用户id
        String memberId = stringRedisTemplate.opsForValue().get(accessToken);
        if (StringUtils.isEmpty(memberId)){
            return AppResponse.fail("无此权限,请先登录");
        }
        int id = Integer.parseInt(memberId);
        //保存临时项目信息的redis
        String projectToke = projectCreateService.initCreateProject(id);
        return AppResponse.ok(projectToke);
    }
    @ApiOperation("项目发起第2步-保存项目的基本信息")
    @PostMapping("/savebaseInfo")
    public AppResponse<String> savebaseInfo(ProjectBaseInfoVo vo) {
        //1.获取之前redis存储的json信息
        String orignal = stringRedisTemplate.opsForValue().get(ProjectConstant.TEMP_PROJECT_PREFIX + vo.getProjectToken());
        //2.转换redis为对象
        ProjectRedisStorageVo projectRedisStorageVo = JSON.parseObject(orignal, ProjectRedisStorageVo.class);
        //3、将页面收集来的数据，复制到和redis映射的vo中
        BeanUtils.copyProperties(vo,projectRedisStorageVo);
        //4、将这个Vo对象再转换为json字符串
        String jsonString = JSON.toJSONString(projectRedisStorageVo);
        //5.更新到redis
        stringRedisTemplate.opsForValue().set(ProjectConstant.TEMP_PROJECT_PREFIX+vo.getProjectToken(),jsonString);
        return AppResponse.ok("ok");
    }
    @ApiOperation("项目发起第3步-项目保存项目回报信息")
    @PostMapping("/savereturn")
    public AppResponse<Object> saveReturnInfo(@RequestBody List<ProjectReturnVo> pro) {
        ProjectReturnVo projectReturnVo = pro.get(0);
        String projectToken = projectReturnVo.getProjectToken();
        //1.取到之前redis的json信息
        String procectContext = stringRedisTemplate.opsForValue().get(ProjectConstant.TEMP_PROJECT_PREFIX + projectToken);
        //2.进行转换
        ProjectRedisStorageVo projectRedisStorageVo = JSON.parseObject(procectContext, ProjectRedisStorageVo.class);
        //页面数剧
        List<TReturn> returns = new ArrayList<>();
        //前端变后端
        for (ProjectReturnVo projectreturnVo : pro) {
         TReturn tReturn = new TReturn();
            BeanUtils.copyProperties(projectreturnVo,tReturn);
            returns.add(tReturn);
        }
        //4.更新到return集合
        projectRedisStorageVo.setProjectReturns(returns);
        String jsonString = JSON.toJSONString(projectRedisStorageVo);
        //5.更新到redis
        stringRedisTemplate.opsForValue().set(ProjectConstant.TEMP_PROJECT_PREFIX+projectToken,jsonString);
        return AppResponse.ok("ok");

    }
    @ApiOperation("项目发起第4步-项目保存项目回报信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "accessToken",value = "用户令牌",required = true),
            @ApiImplicitParam(name = "projectToken",value="项目标识",required = true),
            @ApiImplicitParam(name="ops",value="用户操作类型 0-保存草稿 1-提交审核",required = true)})
    @PostMapping("/submit")
    public AppResponse<Object> submit(String accessToken,String projectToken,String ops) {
    //1.前置效验 检查 accessTock
        String memberId = stringRedisTemplate.opsForValue().get(accessToken);
        if (StringUtils.isEmpty(memberId)){
            return AppResponse.ok("权限不足,请先登录");
        }
        //2.根据项目token,获取项目redis信息
        String projectJsonStr = stringRedisTemplate.opsForValue().get(ProjectConstant.TEMP_PROJECT_PREFIX + projectToken);
        //还原对象
        ProjectRedisStorageVo projectRedisStorageVo = JSON.parseObject(projectJsonStr, ProjectRedisStorageVo.class);
        //判断操作类型不为控,进行处理
        if (!StringUtils.isEmpty(ops)){
        //判断操作类型是1,提交审核
            if (ops.equals("1")){
                ProjectStatusEnume submitAuth =ProjectStatusEnume.SUBMIT_AUTH;
               projectCreateService.saveProjectInfo(submitAuth,projectRedisStorageVo);
               return AppResponse.ok(null);

            }else if (ops.equals("0")){
                ProjectStatusEnume projectStatusEnume = ProjectStatusEnume.DRAFT;
                projectCreateService.saveProjectInfo(projectStatusEnume,projectRedisStorageVo);
                return AppResponse.ok(null);
            }else {
                return AppResponse.fail("不支持此操作");
            }
        }

           return  AppResponse.fail(null);
    }

    }
