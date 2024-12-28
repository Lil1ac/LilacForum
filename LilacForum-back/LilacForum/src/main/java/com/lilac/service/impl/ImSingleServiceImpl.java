package com.lilac.service.impl;

import com.lilac.dto.ImSingleRequest;
import com.lilac.mapper.ImSingleMapper;
import com.lilac.mapper.UserMapper;
import com.lilac.pojo.ImSingle;
import com.lilac.service.ImService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public List<ImSingleRequest> findMessagesByUserId(Integer fromUserId, Integer toUserId) {
        // 查询所有消息
        List<ImSingle> list = imSingleMapper.findMessagesByUserId(fromUserId, toUserId);

        // 遍历每条消息，填充头像、用户名等信息
        List<ImSingleRequest> result = new ArrayList<>();
        for (ImSingle imSingle : list) {
            // 获取发送者信息
            String avatar = userMapper.getUserById(imSingle.getFromUserId()).getAvatar();
            String username = userMapper.getUserById(imSingle.getFromUserId()).getUsername();
            // 获取接收者信息
            String toAvatar = userMapper.getUserById(imSingle.getToUserId()).getAvatar();
            String toUsername = userMapper.getUserById(imSingle.getFromUserId()).getUsername();

            // 构造 ImSingleRequest DTO
            ImSingleRequest dto = new ImSingleRequest(imSingle, toAvatar, username);

            // 判断当前消息是否未读且是从对方发来的，如果是，则更新为已读
            if (imSingle.getToUserId().equals(fromUserId) && imSingle.getFromUserId().equals(toUserId) && imSingle.getIsRead() == 0) {
                imSingle.setIsRead(1); // 标记为已读
                imSingleMapper.updateByPrimaryKey(imSingle); // 更新消息状态
            }

            // 将构建好的 DTO 添加到结果列表
            result.add(dto);
        }

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
