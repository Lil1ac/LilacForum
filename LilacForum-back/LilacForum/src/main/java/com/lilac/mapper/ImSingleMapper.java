package com.lilac.mapper;

import com.lilac.pojo.ImSingle;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ImSingleMapper {

    // 插入一条消息记录
    void insertSelective(ImSingle imSingle);

    // 更新消息为已读
    void updateByPrimaryKey(ImSingle imSingle);

    // 根据用户ID获取未读消息数量
    List<ImSingle> findByToUserId(Integer toUserId);

    List<ImSingle> findMessagesByUserId(Integer fromUserId, Integer toUserId);

    List<ImSingle> findMessagesByCursorAndLimit(Integer fromUserId, Integer toUserId, Long cursor, Integer pageSize);

    List<ImSingle> findMessagesByUserIdAndLimit(Integer fromUserId, Integer toUserId, Integer pageSize);

    Integer findLastUnReadMessageId(Integer fromUserId, Integer toUserId);

    void updateMessagesAsRead(Integer fromUserId, Integer toUserId, Integer lastUnreadMessageId);

    ImSingle getLastMessage(Integer fromUserId, Integer toUserId);
}
