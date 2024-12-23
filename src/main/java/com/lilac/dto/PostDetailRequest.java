package com.lilac.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailRequest {
    private Integer id;
    private String title;
    private String content;
    private Integer sectionId;
    private String sectionTitle;//所属板块
    private Integer authorId;// 用户ID
    private String author; // 用户名
    private String avatar; //头像
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastReplyTime;
}
