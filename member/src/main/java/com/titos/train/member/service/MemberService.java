package com.titos.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.titos.train.common.exception.BusinessException;
import com.titos.train.common.exception.BusinessExceptionEnum;
import com.titos.train.common.util.SnowUtil;
import com.titos.train.member.domain.Member;
import com.titos.train.member.domain.MemberExample;
import com.titos.train.member.mapper.MemberMapper;
import com.titos.train.member.req.MemberLoginReq;
import com.titos.train.member.req.MemberRegisterReq;
import com.titos.train.member.req.MemberSendCodeReq;
import com.titos.train.member.resp.MemberLoginResp;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MemberService {

    @Resource
    private MemberMapper memberMapper;

    public int count() {
        return (int) memberMapper.countByExample(null);
    }

    public long register(MemberRegisterReq req) {
        String mobile = req.getMobile();
        Member memberDB = selectByMobile(mobile);
        if (ObjectUtil.isNull(memberDB)) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_EXIST);
        }
        Member member = new Member();
        member.setId(SnowUtil.getSnowflakeNextId());
        member.setMobile(mobile);
        memberMapper.insert(member);
        return member.getId();
    }

    public void sendCode(MemberSendCodeReq  req) {
        String mobile = req.getMobile();
        Member memberDB = selectByMobile(mobile);
        // 如果手机号不存在，则插入一条记录
        if (ObjectUtil.isNull(memberDB)) {
            log.info("手机号不存在，插入一条记录");
            Member member = new Member();
            member.setId(SnowUtil.getSnowflakeNextId());
            member.setMobile(mobile);
            memberMapper.insert(member);
        } else {
            log.info("手机号存在, 不插入记录");
        }

        // 生成验证码
//        String code = RandomUtil.randomString(4);
        String code = "8888";
        log.info("生成短信验证码: {}", code);
        // 保存短信记录表: 手机号，短信验证码，有效期，是否已使用，业务类型，发送时间，使用时间
        log.info("保存短信记录表");
        // 对接短信通道，发送短息
        log.info("对接短信通道");
    }

    public MemberLoginResp login(MemberLoginReq req) {
        String mobile = req.getMobile();
        String code = req.getCode();
        Member memberDB = selectByMobile(mobile);
        // 如果手机号不存在，则插入一条记录
        if (ObjectUtil.isNull(memberDB)) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_NOT_EXIST);
        }
        // 校验短信验证码
        if (!"8888".equals(code)) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_CODE_ERROR);
        }
        return BeanUtil.copyProperties(memberDB, MemberLoginResp.class);
    }

    private Member selectByMobile(String mobile) {
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        List<Member> list = memberMapper.selectByExample(memberExample);
        // 如果手机号不存在，则插入一条记录
        if (CollUtil.isEmpty(list)) {
            return null;
        } else {
            return list.get(0);
        }
    }
}
