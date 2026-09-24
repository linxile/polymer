package com.polymer.system.service;

import com.polymer.framework.common.pojo.PageResult;
import com.polymer.system.query.SysLogLoginQuery;
import com.polymer.system.vo.SysLogLoginVO;

/**
 * 登录日志
 *
 * @author polymer
 */
public interface SysLogLoginService {

    /**
     * 根据登录日志查询获取分页登录日志列表
     *
     * @param query 登录日志查询
     * @return PageResult<SysLogLoginVO>
     */
    PageResult<SysLogLoginVO> page(SysLogLoginQuery query);

    /**
     * 保存登录日志
     *
     * @param username  用户名
     * @param status    登录状态
     * @param operation 操作信息
     * @return 保存数量
     */
    int saveLogLogin(String username, Integer status, Integer operation);

    /**
     * 导出登录日志
     *
     * @return byte[]
     */
    byte[] export(SysLogLoginQuery query);
}