package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

/**
 * @author wang
 * @version 1.0
 */
public interface UserService {
	/**
	 * 微信登录
	 * @param userLoginDTO
	 * @return
	 */
	User wxlogin(UserLoginDTO userLoginDTO);
}
