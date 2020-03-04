package com.hjs.system.mapper;

import com.hjs.system.base.BaseDAO;
import com.hjs.system.model.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentMapper extends BaseDAO<Student> {

    /**
     * 通过学号查询学生信息
     * @param studentId
     * @return
     */
    Student findStudentByStudentId(String studentId);


//    void save(Student student);
//
//    void update(Student student);
//
//    void delete(Integer id);
//
//    Student find(Integer id);


//    void batchSave(List<Student> list);
//
//    void batchDelete(List<Integer> idlist);
}