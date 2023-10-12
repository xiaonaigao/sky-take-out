package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * @author wang
 * @version 1.0
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
	public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private WeChatProperties weChatProperties;


	/**
	 * 获取微信用户的openid
	 * @param code
	 * @return
	 */
	private String getOpenid(String code){
		// 请求参数封装
		HashMap map = new HashMap();
		map.put("appid",weChatProperties.getAppid());
		map.put("secret",weChatProperties.getSecret());
		map.put("js_code",code);
		map.put("grant_type","authorization_code");

		// 调用工具类，微信接口服务发送
		String json = HttpClientUtil.doGet(WX_LOGIN, map);
		log.info("微信登录的返回结果{}",json);
		String openid = JSON.parseObject(json).getString("openid");
		return openid;
	}



	/**
	 * 微信登录
	 * @param userLoginDTO
	 * @return
	 */
	@Override
	public User wxlogin(UserLoginDTO userLoginDTO) {
		// 授权码
		String openid = getOpenid(userLoginDTO.getCode());
		if (openid == null){
			throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
		}
		// 根据openid查询用户信息
		User user = userMapper.getByOpenid(openid);
		// 判断用户是否存在
		if (user == null){
			user = new User();
			user.setOpenid(openid);
			user.setCreateTime(LocalDateTime.now());
			userMapper.insert(user);
		}
		return user;
	}
}
