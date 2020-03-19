package com.hjs.system.mapper;

import com.hjs.system.model.Process;

public interface ProcessMapper {
    int deleteByPrimaryKey(Integer processId);

    int insert(Process record);

    int insertSelective(Process record);

    Process selectByPrimaryKey(Integer processId);

    int updateByPrimaryKeySelective(Process record);

    int updateByPrimaryKey(Process record);
}