package com.lilac.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lilac.dto.ImSingleRequest;
import com.lilac.mapper.ImSingleMapper;
import com.lilac.mapper.UserMapper;
import com.lilac.pojo.ImSingle;
import com.lilac.service.ImService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ImSingleServiceImpl implements ImService {

    @Resource
    private ImSingleMapper imSingleMapper;

    @Resource
    private UserMapper userMapper;

    // 插入单条消息记录
    public ImSingle add(ImSingle imSingle) {
        imSingleMapper.insertSelective(imSingle);
        return imSingle;
    }

    // 根据用户ID查找单聊消息记录


    public List<ImSingleRequest> findMessagesByCursor(Integer fromUserId, Integer toUserId, Long cursor, Integer pageSize) {
        // 查询消息，假设使用 messageId 作为游标
        List<ImSingle> list;
        if (cursor == null|| cursor == 0) {
            // 如果没有传入游标，查询最早的消息
            list = imSingleMapper.findMessagesByUserIdAndLimit(fromUserId, toUserId, pageSize);
        } else {
            // 查询从游标之后的数据
            list = imSingleMapper.findMessagesByCursorAndLimit(fromUserId, toUserId, cursor, pageSize);
        }


        // 获取最后一个未读消息的ID，用于批量更新已读
        Integer lastUnreadMessageId = imSingleMapper.findLastUnReadMessageId(fromUserId, toUserId);
        if (lastUnreadMessageId != null) {
            // 批量更新该未读消息及之后的所有消息为已读
            imSingleMapper.updateMessagesAsRead(fromUserId, toUserId, lastUnreadMessageId);
        }

        // 处理查询结果，返回 ImSingleRequest 列表
        List<ImSingleRequest> result = new ArrayList<>();
        for (ImSingle imSingle : list) {
            // 填充消息内容及头像等信息
            String fromAvatar = userMapper.getUserById(imSingle.getFromUserId()).getAvatar();
            String fromUsername = userMapper.getUserById(imSingle.getFromUserId()).getUsername();
            String toAvatar = userMapper.getUserById(imSingle.getToUserId()).getAvatar();
            String toUsername = userMapper.getUserById(imSingle.getToUserId()).getUsername();
            // 构造 ImSingleRequest DTO
            ImSingleRequest dto = new ImSingleRequest(imSingle, toAvatar, toUsername, fromAvatar, fromUsername);
            result.add(dto);
        }
        Collections.reverse(result);
        return result;
    }



    // 获取未读消息的数量
    public Map<Integer, Integer> findUnreadNums(Integer toUserId) {
        List<ImSingle> list = imSingleMapper.findByToUserId(toUserId);  // 获取到用户的消息列表
        Map<Integer, List<ImSingle>> collect = list.stream()
                .collect(Collectors.groupingBy(ImSingle::getFromUserId));  // 按发送者用户 ID 分组
        Map<Integer, Integer> unReadCounts = new HashMap<>();  // 使用 HashMap 来存储每个发送者的未读消息数

        collect.forEach((fromUserId, messages) -> {
            unReadCounts.put(fromUserId, messages.size());  // 将每个用户的未读消息数量放入 HashMap
        });

        return unReadCounts;  // 返回包含所有未读消息数的 Map
    }
}
