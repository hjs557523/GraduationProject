package com.hjs.system.mapper;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.hjs.system.model.Subject;
import org.springframework.stereotype.Repository;


@Repository
public interface SubjectMapper {

    Page<Subject> findAllSubject();

    Page<Subject> findSubjectByTid(Integer tid);

    Page<Subject> findSubjectByTname(String name);

    int deleteSubjectBySubjectId(Integer subjectId);

    int insertSubject(Subject record);

    Subject findSubjectBySubjectId(Integer subjectId);

    int updateSubject(Subject record);

}