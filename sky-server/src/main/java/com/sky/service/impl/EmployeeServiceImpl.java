package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeMapper employeeMapper;

	/**
	 * 员工登录
	 *
	 * @param employeeLoginDTO
	 * @return
	 */
	@Override
	public Employee login(EmployeeLoginDTO employeeLoginDTO) {
		String username = employeeLoginDTO.getUsername();
		String password = employeeLoginDTO.getPassword();

		//1、根据用户名查询数据库中的数据
		Employee employee = employeeMapper.getByUsername(username);

		//2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
		if (employee == null) {
			//账号不存在
			throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
		}

		//密码比对
		password = DigestUtils.md5DigestAsHex(password.getBytes());
		if (!password.equals(employee.getPassword())) {
			//密码错误
			throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
		}

		if (employee.getStatus() == StatusConstant.DISABLE) {
			//账号被锁定
			throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
		}

		//3、返回实体对象
		return employee;
	}

	/**
	 * 新增员工
	 *
	 * @param employeeDTO
	 */
	@Override
	public void save(EmployeeDTO employeeDTO) {
		//1.封装参数
		Employee employee = new Employee();
		// 对象属性拷贝，前提字段属性一一对应
		BeanUtils.copyProperties(employeeDTO, employee);
		// 设置密码
		employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
		// 账号状态
		employee.setStatus(StatusConstant.ENABLE);
		// 时间
		employee.setCreateTime(LocalDateTime.now());
		employee.setUpdateTime(LocalDateTime.now());
		// 创建者
		// 拦截器解析token存empid
		employee.setCreateUser(BaseContext.getCurrentId());
		employee.setUpdateUser(BaseContext.getCurrentId());

		//2.调用mapper
		employeeMapper.insert(employee);
	}

	/**
	 * 分页查询
	 *
	 * @param employeePageQueryDTO
	 */
	@Override
	public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
		//开始分页查询
		PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
		Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
		// 转换page成pageResult
		long total = page.getTotal();
		List<Employee> records = page.getResult();
		return new PageResult(total, records);
	}

	/**
	 * 启用，禁用
	 *
	 * @param status
	 * @param id
	 */
	@Override
	public void startOrStop(Integer status, Long id) {
		// 封装参数
		Employee employee = new Employee();
		employee.setStatus(status);
		employee.setId(id);
		// 调用持久层
		employeeMapper.update(employee);
	}

	/**
	 * 根据id查询员工信息
	 *
	 * @param id
	 * @return
	 */
	@Override
	public Employee getById(Long id) {
		//调用mapper
		Employee employee = employeeMapper.getById(id);
		employee.setPassword("********");
		return employee;
	}
	/**
	 * 修改员工信息
	 * @param employeeDTO
	 */
	@Override
	public void update(EmployeeDTO employeeDTO) {
		// 封装参数
		Employee employee = new Employee();
		BeanUtils.copyProperties(employeeDTO,employee);
		employee.setUpdateTime(LocalDateTime.now());
		employee.setUpdateUser(BaseContext.getCurrentId());
		//调用mapper
		employeeMapper.update(employee);
	}

}
