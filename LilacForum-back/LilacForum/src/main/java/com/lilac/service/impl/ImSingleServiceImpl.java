package com.lilac.service.impl;

import cn.hutool.core.lang.Dict;

import com.lilac.mapper.ImSingleMapper;
import com.lilac.pojo.ImSingle;
import com.lilac.service.ImService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ImSingleServiceImpl implements ImService {

    @Resource
    private ImSingleMapper imSingleDao;

    // 插入单条消息记录
    public ImSingle add(ImSingle imSingle) {
        imSingleDao.insertSelective(imSingle);
        return imSingle;
    }

    // 根据用户名查找单聊消息记录
    public List<ImSingle> findByUsername(String fromUser, String toUser) {
        List<ImSingle> list = imSingleDao.findByUsername(fromUser, toUser);
        list.forEach(x -> {
            // 如果当前消息未读且是从对方发来的，则更新为已读
            if (x.getToUser().equals(fromUser) && x.getFromUser().equals(toUser)) {
                x.setIsRead(1);
                imSingleDao.updateByPrimaryKey(x);
            }
        });
        return list;
    }

    // 获取未读消息的数量
    public Dict findUnreadNums(String toUser) {
        List<ImSingle> list = imSingleDao.findByToUsername(toUser);
        Map<String, List<ImSingle>> collect = list.stream().collect(Collectors.groupingBy(ImSingle::getFromUser));
        Dict dict = Dict.create();
        collect.forEach((k, v) -> dict.set(k, v.size())); // 获取每个用户未读消息的数量
        return dict;
    }
}
