package com.tyut.hardwaremall.service.impl;

import com.tyut.hardwaremall.common.Constants;
import com.tyut.hardwaremall.common.ServiceResultEnum;
import com.tyut.hardwaremall.controller.vo.HardwareMallUserVO;
import com.tyut.hardwaremall.dao.MallUserMapper;
import com.tyut.hardwaremall.entity.MallUser;
import com.tyut.hardwaremall.service.HardwareMallUserService;
import com.tyut.hardwaremall.util.BeanUtil;
import com.tyut.hardwaremall.util.MD5Util;
import com.tyut.hardwaremall.util.PageQueryUtil;
import com.tyut.hardwaremall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class HardwareMallUserServiceImpl implements HardwareMallUserService {

    @Autowired
    private MallUserMapper mallUserMapper;

    @Override
    public PageResult getHardwareMallUsersPage(PageQueryUtil pageUtil) {
        List<MallUser> mallUsers = mallUserMapper.findMallUserList(pageUtil);
        int total = mallUserMapper.getTotalMallUsers(pageUtil);
        PageResult pageResult = new PageResult(mallUsers, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String register(String loginName, String password) {
        if (mallUserMapper.selectByLoginName(loginName) != null) {
            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
        }
        MallUser registerUser = new MallUser();
        registerUser.setLoginName(loginName);
        registerUser.setNickName(loginName);
        String passwordMD5 = MD5Util.MD5Encode(password, "UTF-8");
        registerUser.setPasswordMd5(passwordMD5);
        if (mallUserMapper.insertSelective(registerUser) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String login(String loginName, String passwordMD5, HttpSession httpSession) {
        MallUser user = mallUserMapper.selectByLoginNameAndPasswd(loginName, passwordMD5);
        if (user != null && httpSession != null) {
            if (user.getLockedFlag() == 1) {
                return ServiceResultEnum.LOGIN_USER_LOCKED.getResult();
            }
            //昵称太长 影响页面展示
            if (user.getNickName() != null && user.getNickName().length() > 7) {
                String tempNickName = user.getNickName().substring(0, 7) + "..";
                user.setNickName(tempNickName);
            }
            HardwareMallUserVO hardwareMallUserVO = new HardwareMallUserVO();
            BeanUtil.copyProperties(user, hardwareMallUserVO);
            //设置购物车中的数量
            httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, hardwareMallUserVO);
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.LOGIN_ERROR.getResult();
    }

    @Override
    public HardwareMallUserVO updateUserInfo(MallUser mallUser, HttpSession httpSession) {
        MallUser user = mallUserMapper.selectByPrimaryKey(mallUser.getUserId());
        if (user != null) {
            user.setNickName(mallUser.getNickName());
            user.setAddress(mallUser.getAddress());
            user.setIntroduceSign(mallUser.getIntroduceSign());
            if (mallUserMapper.updateByPrimaryKeySelective(user) > 0) {
                HardwareMallUserVO hardwareMallUserVO = new HardwareMallUserVO();
                user = mallUserMapper.selectByPrimaryKey(mallUser.getUserId());
                BeanUtil.copyProperties(user, hardwareMallUserVO);
                httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, hardwareMallUserVO);
                return hardwareMallUserVO;
            }
        }
        return null;
    }

    @Override
    public Boolean lockUsers(Integer[] ids, int lockStatus) {
        if (ids.length < 1) {
            return false;
        }
        return mallUserMapper.lockUserBatch(ids, lockStatus) > 0;
    }
}
