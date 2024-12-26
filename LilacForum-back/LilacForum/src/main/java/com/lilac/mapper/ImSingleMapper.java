package com.lilac.mapper;

import com.lilac.pojo.ImSingle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ImSingleMapper {

    // 插入一条消息记录
    void insertSelective(ImSingle imSingle);

    // 根据用户获取聊天记录
    List<ImSingle> findByUsername(@Param("fromUser") String fromUser, @Param("toUser") String toUser);

    // 更新消息为已读
    void updateByPrimaryKey(ImSingle imSingle);

    // 查找未读消息
    List<ImSingle> findByToUsername(@Param("toUser") String toUser);
}
