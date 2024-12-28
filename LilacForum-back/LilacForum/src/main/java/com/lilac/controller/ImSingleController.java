package com.lilac.controller;

import com.lilac.dto.ImSingleRequest;
import com.lilac.pojo.ImSingle;
import com.lilac.pojo.Result;
import com.lilac.service.impl.ImSingleServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping( "/imSingle")
public class ImSingleController {
    @Resource
    private ImSingleServiceImpl imSingleServiceImpl;

    /**
     * 查询所有消息
     */
    @GetMapping
    public Result findByFromUserId(@RequestParam Integer fromUserId, @RequestParam Integer toUserId) {
        List<ImSingleRequest> all = imSingleServiceImpl.findMessagesByUserId(fromUserId, toUserId);
        return Result.success(all);
    }

    /**
     * 查询未读消息数量
     *
     * @return 未读消息数量
     */
    @GetMapping("/unReadNums")
    public Result findUnReadNums(@RequestParam Integer toUserId) {
        Map<Integer, Integer> unReadNums = imSingleServiceImpl.findUnreadNums(toUserId);

        if (unReadNums == null || unReadNums.isEmpty()) {
            return Result.error("No unread messages found");  // 如果没有未读消息，返回错误信息
        }

        return Result.success(unReadNums);  // 返回包含未读消息数的 Map
    }



    //发送消息
    @GetMapping("/send")
    public Result send(@RequestBody ImSingle imSingle) {
        imSingleServiceImpl.add(imSingle);
        return Result.success();
    }
}