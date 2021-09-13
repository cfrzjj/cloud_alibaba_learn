/*
 * Copyright (c) 2020 pig4cloud Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liu.controller;

import com.liu.entity.SysLog;
import com.liu.rs.CommonResult;
import com.liu.service.SysLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 日志表 前端控制器
 * </p>
 *
 * @author lengleng
 * @since 2019/2/1
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/log")
@Slf4j
//@Api(value = "log", tags = "日志管理模块")
public class LogController {

	private final SysLogService sysLogService;


	/**
	 * 插入日志
	 * @param sysLog 日志实体
	 * @return success/false
	 */
	@PostMapping
	public CommonResult save(@Valid @RequestBody SysLog sysLog) {

		log.error("保存日志");
		int i = 1/0;
		return CommonResult.ok(sysLogService.save(sysLog));
	}

	@GetMapping("/get")
	public CommonResult get() {

		log.error("获取日志");
		return CommonResult.ok("cheng");
	}

}
