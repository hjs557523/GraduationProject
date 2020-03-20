package com.hjs.system.mapper;

import com.hjs.system.model.ProjectType;
import org.springframework.stereotype.Repository;


@Repository
public interface ProjectTypeMapper {
    int deleteByPrimaryKey(Integer pid);

    int insert(ProjectType record);

    int insertSelective(ProjectType record);

    ProjectType selectByPrimaryKey(Integer pid);

    int updateByPrimaryKeySelective(ProjectType record);

    int updateByPrimaryKey(ProjectType record);
}