package com.lilac.dto;

import com.lilac.pojo.ImSingle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImSingleRequest extends ImSingle {
    private String toAvatar;
    private String toUsername;
    private String fromAvatar;
    private String fromUsername;


    //根据ImSingle构造ImSingleRequest
    public ImSingleRequest(ImSingle imSingle, String toAvatar, String toUsername, String fromAvatar, String fromUsername) {
        super(imSingle.getId(), imSingle.getContent(), imSingle.getFromUserId(), imSingle.getTime(), imSingle.getType(), imSingle.getToUserId(), imSingle.getIsRead());
        this.toAvatar = toAvatar;
        this.toUsername = toUsername;
        this.fromAvatar = fromAvatar;
        this.fromUsername = fromUsername;
    }
}
