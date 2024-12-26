package com.lilac.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImSingle {
    private Integer id;
    private String content;
    private String fromUser;
    private String fromAvatar;
    private Date time;
    private String type;
    private String toUser;
    private String toAvatar;
    private Integer isRead;
}
