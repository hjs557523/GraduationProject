package com.hjs.system.controller.student;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.hjs.system.base.utils.JSONUtil;
import com.hjs.system.base.utils.StringUtil;
import com.hjs.system.model.Class;
import com.hjs.system.model.ClassMember;
import com.hjs.system.model.Student;
import com.hjs.system.service.ClassMemberService;
import com.hjs.system.service.ClassService;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 黄继升 16041321
 * @Description:
 * @date Created in 2020/4/2 20:18
 * @Modified By:
 */

@Controller("StudentClassManagement")
public class ClassManagementController {

    private static final Logger logger = LoggerFactory.getLogger(ClassManagementController.class);

    @Autowired
    private HttpServletRequest request;

    @Autowired
    ClassMemberService classMemberServiceImpl;

    @Autowired
    ClassService classServiceImpl;

    @RequestMapping(value = "/student/class/join", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String joinClass(Integer cid) {
        Student current_user = (Student) SecurityUtils.getSubject().getPrincipal();
        if (StringUtil.isEmpty(cid.toString()))
            return JSONUtil.returnFailResult("请选择加入的班级!");
        ClassMember classMember = new ClassMember();
        classMember.setClassId(cid);
        classMember.setStudentId(current_user.getClassId());
        classMember.setAccept(false);//等待老师确认
        try {
            if (classMemberServiceImpl.insertClassMember(classMember) > 0) {
                logger.info("班级成员申请加入成功, 生成的班级成员编号 = {}, 该学生的学号 = {}", classMember.getClassMemberId(), classMember.getStudentId());
                return JSONUtil.returnSuccessResult("申请加入班级成功! 请等待老师确认");
            } else {
                logger.info("申请加入失败", classMember.getClassMemberId(), classMember.getStudentId());
                return JSONUtil.returnSuccessResult("申请加入班级失败! 请稍后重试, 或联系老师解决");
            }
        } catch (Exception e) {
            logger.info("申请加入失败, 数据库执行异常: " + e.getMessage());
            return JSONUtil.returnFailResult("申请加入失败, 请稍后重试。或联系老师解决");
        }
    }


    @RequestMapping(value = "/student/class/findAll", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String findAllClassesByPage(@RequestParam("page")Integer pageNum, @RequestParam("limit")Integer pageSize) {
        if (pageNum == null)
            pageNum = 1;
        else if (pageSize == null)
            pageSize = 12;
        logger.info("分页查询第{}页，每页{}条", pageNum, pageSize);

        //获取当前用户
        Student current_user = (Student) SecurityUtils.getSubject().getPrincipal();
        Page<Class> classes = classServiceImpl.findClassByPage(pageNum, pageSize);
        PageInfo<Class> pageInfo = new PageInfo<>(classes);
        Integer count = (int) pageInfo.getTotal();
//        if (count == 0)
//            return JSONUtil.returnEntityResult(count, "目前没有");



        return null;
    }


}