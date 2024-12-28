package com.lilac.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImSingle {
    private Integer id;
    private String content;
    private Integer fromUserId;
    private LocalDateTime time;
    private String type;
    private Integer toUserId;
    private Integer isRead;
}
