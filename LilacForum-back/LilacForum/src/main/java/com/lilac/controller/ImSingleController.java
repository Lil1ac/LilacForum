package com.lilac.controller;

import com.github.pagehelper.PageInfo;
import com.lilac.dto.ImSingleRequest;
import com.lilac.pojo.ImSingle;
import com.lilac.pojo.Result;
import com.lilac.service.impl.ImSingleServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping( "/imSingle")
public class ImSingleController {
    @Resource
    private ImSingleServiceImpl imSingleServiceImpl;

    /**
     * 查询所有消息
     */
    @GetMapping
    public Result findMessagesByCursor(
            @RequestParam Integer fromUserId,
            @RequestParam Integer toUserId,
            @RequestParam(required = false) Long cursor,  // 游标，第一次请求时为 null 或 0
            @RequestParam Integer pageSize) { // 每页显示的条数
        // 调用服务层的游标分页查询方法
        List<ImSingleRequest> messages = imSingleServiceImpl.findMessagesByCursor(fromUserId, toUserId, cursor, pageSize);

        return Result.success(messages);
    }


    /**
     * 查询未读消息数量
     *
     * @return 未读消息数量
     */
    @GetMapping("/unReadNums")
    public Result findUnReadNums(@RequestParam Integer toUserId) {
        Map<Integer, Integer> unReadNums = imSingleServiceImpl.findUnreadNums(toUserId);
        if (unReadNums == null) {
            return Result.error("No unread messages found");  // 如果没有未读消息，返回错误信息
        }
        log.info("unReadNums: {}", unReadNums.size());
        return Result.success(unReadNums);  // 返回包含未读消息数的 Map
    }



    //发送消息
    @GetMapping("/send")
    public Result send(@RequestBody ImSingle imSingle) {
        imSingleServiceImpl.add(imSingle);
        return Result.success();
    }
}