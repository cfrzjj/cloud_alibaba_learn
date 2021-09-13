

package com.liu.feign.fallback;

import com.liu.entity.SysLog;
import com.liu.feign.RemoteLogService;
import com.liu.rs.CommonResult;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class RemoteLogServiceFallbackImpl implements RemoteLogService {

	@Setter
	private Throwable cause;

	/**
	 * 保存日志
	 * @param sysLog 日志实体
	 * @param from 内部调用标志
	 * @return succes、false
	 */
	@Override
	public CommonResult saveLog(SysLog sysLog, String from) {

		log.error("feign 插入日志失败，{}", cause.getMessage());
		cause.printStackTrace();
		log.error("feign 插入日志失败，{}", cause.getLocalizedMessage());
		return null;
	}

}
