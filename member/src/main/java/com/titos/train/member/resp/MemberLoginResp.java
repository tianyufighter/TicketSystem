package com.titos.train.member.resp;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MemberLoginResp {
    private Long id;
    private String mobile;
}
