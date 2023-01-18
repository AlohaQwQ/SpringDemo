package com.example.running.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.running.bean.User;
import com.example.running.service.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
 * @author Aloha
 * @date 2022/12/26 17:15
 * @description User 接口实现, 继承 mybatisplus 数据库查询通用Service 实现类
 */
@Service("UserService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


}
