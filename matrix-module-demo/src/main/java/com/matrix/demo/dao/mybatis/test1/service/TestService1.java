package com.matrix.demo.dao.mybatis.test1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.matrix.demo.dao.mybatis.test1.model.Test1;

/**
 * @author WangCheng
 */
public interface TestService1 extends IService<Test1> {
    Test1 find();
}
