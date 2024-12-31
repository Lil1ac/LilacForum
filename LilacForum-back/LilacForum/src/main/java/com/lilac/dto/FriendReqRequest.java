package com.lilac.dto;

import com.lilac.pojo.FriendReq;
import com.lilac.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendReqRequest extends FriendReq{
    User sender;
}
