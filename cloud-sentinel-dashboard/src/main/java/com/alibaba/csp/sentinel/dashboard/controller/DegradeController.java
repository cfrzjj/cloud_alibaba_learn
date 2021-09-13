/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.dashboard.controller;

import com.alibaba.csp.sentinel.dashboard.auth.AuthService;
import com.alibaba.csp.sentinel.dashboard.auth.AuthService.AuthUser;
import com.alibaba.csp.sentinel.dashboard.auth.AuthService.PrivilegeType;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.DegradeRuleEntity;
import com.alibaba.csp.sentinel.dashboard.domain.Result;
import com.alibaba.csp.sentinel.dashboard.repository.rule.InMemDegradeRuleStore;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @author leyou
 */
@Controller
@RequestMapping(value = "/degrade", produces = MediaType.APPLICATION_JSON_VALUE)
public class DegradeController {

    private final Logger logger = LoggerFactory.getLogger(DegradeController.class);

    @Autowired
    private InMemDegradeRuleStore repository;

    @Autowired
    @Qualifier("degradeRuleNacosProvider")
    private DynamicRuleProvider<List<DegradeRuleEntity>> ruleProvider;
    @Autowired
    @Qualifier("degradeRuleNacosPublisher")
    private DynamicRulePublisher<List<DegradeRuleEntity>> rulePublisher;

    @Autowired
    private AuthService<HttpServletRequest> authService;

    @ResponseBody
    @RequestMapping("/rules.json")
    public Result<List<DegradeRuleEntity>> queryMachineRules(HttpServletRequest request, String app, String ip, Integer port) {
        AuthUser authUser = authService.getAuthUser(request);
        authUser.authTarget(app, PrivilegeType.READ_RULE);

        if (StringUtil.isEmpty(app)) {
            return Result.ofFail(-1, "app can't be null or empty");
        }
        if (StringUtil.isEmpty(ip)) {
            return Result.ofFail(-1, "ip can't be null or empty");
        }
        if (port == null) {
            return Result.ofFail(-1, "port can't be null");
        }
        try {
            List<DegradeRuleEntity> rules = ruleProvider.getRules(app);
            rules = repository.saveAll(rules);
            return Result.ofSuccess(rules);
        } catch (Throwable throwable) {
            logger.error("queryApps error:", throwable);
            return Result.ofThrowable(-1, throwable);
        }
    }

    @ResponseBody
    @RequestMapping("/rule")
    public Result<DegradeRuleEntity> add(HttpServletRequest request,@RequestBody DegradeRuleEntity entity
                                         ) {
        AuthUser authUser = authService.getAuthUser(request);
        authUser.authTarget(entity.getApp(), PrivilegeType.WRITE_RULE);

        if (StringUtil.isBlank(entity.getApp())) {
            return Result.ofFail(-1, "app can't be null or empty");
        }
        if (StringUtil.isBlank(entity.getIp())) {
            return Result.ofFail(-1, "ip can't be null or empty");
        }
        if (entity.getPort() == null) {
            return Result.ofFail(-1, "port can't be null");
        }
        if (StringUtil.isBlank(entity.getLimitApp())) {
            return Result.ofFail(-1, "limitApp can't be null or empty");
        }
        if (StringUtil.isBlank(entity.getResource())) {
            return Result.ofFail(-1, "resource can't be null or empty");
        }
        if (entity.getCount() == null) {
            return Result.ofFail(-1, "count can't be null");
        }
        if (entity.getTimeWindow() == null) {
            return Result.ofFail(-1, "timeWindow can't be null");
        }
        if (entity.getGrade() == null) {
            return Result.ofFail(-1, "grade can't be null");
        }
        if (entity.getGrade() < RuleConstant.DEGRADE_GRADE_RT || entity.getGrade() > RuleConstant.DEGRADE_GRADE_EXCEPTION_COUNT) {
            return Result.ofFail(-1, "Invalid grade: " + entity.getGrade());
        }
       /* DegradeRuleEntity entity = new DegradeRuleEntity();
        entity.setApp(app.trim());
        entity.setIp(ip.trim());
        entity.setPort(port);
        entity.setLimitApp(limitApp.trim());
        entity.setResource(resource.trim());
        entity.setCount(count);
        entity.setTimeWindow(timeWindow);
        entity.setGrade(grade);*/
        Date date = new Date();
        entity.setGmtCreate(date);
        entity.setGmtModified(date);
        try {
            entity = repository.save(entity);
            publishRules(entity.getApp());
        } catch (Throwable throwable) {
            logger.error("add error:", throwable);
            return Result.ofThrowable(-1, throwable);
        }
        return Result.ofSuccess(entity);
    }

    @ResponseBody
    @RequestMapping("/save.json")
    public Result<DegradeRuleEntity> updateIfNotNull(HttpServletRequest request,
                                                     Long id, String app, String limitApp, String resource,
                                                     Double count, Integer timeWindow, Integer grade) {
        AuthUser authUser = authService.getAuthUser(request);
        if (id == null) {
            return Result.ofFail(-1, "id can't be null");
        }
        if (grade != null) {
            if (grade < RuleConstant.DEGRADE_GRADE_RT || grade > RuleConstant.DEGRADE_GRADE_EXCEPTION_COUNT) {
                return Result.ofFail(-1, "Invalid grade: " + grade);
            }
        }
        DegradeRuleEntity entity = repository.findById(id);
        if (entity == null) {
            return Result.ofFail(-1, "id " + id + " dose not exist");
        }
        authUser.authTarget(entity.getApp(), PrivilegeType.WRITE_RULE);
        if (StringUtil.isNotBlank(app)) {
            entity.setApp(app.trim());
        }

        if (StringUtil.isNotBlank(limitApp)) {
            entity.setLimitApp(limitApp.trim());
        }
        if (StringUtil.isNotBlank(resource)) {
            entity.setResource(resource.trim());
        }
        if (count != null) {
            entity.setCount(count);
        }
        if (timeWindow != null) {
            entity.setTimeWindow(timeWindow);
        }
        if (grade != null) {
            entity.setGrade(grade);
        }
        Date date = new Date();
        entity.setGmtModified(date);
        try {
            entity = repository.save(entity);
            publishRules(entity.getApp());
        } catch (Throwable throwable) {
            logger.error("save error:", throwable);
            return Result.ofThrowable(-1, throwable);
        }
        return Result.ofSuccess(entity);
    }

    @PutMapping("/rule/{id}")
    //@AuthAction(PrivilegeType.WRITE_RULE)

    public Result<DegradeRuleEntity> apiUpdateDegradeRule(HttpServletRequest request,@PathVariable("id") Long id, @RequestBody DegradeRuleEntity entity) {
        AuthUser authUser = authService.getAuthUser(request);
        authUser.authTarget(entity.getApp(), PrivilegeType.WRITE_RULE);
        if (id == null || id <= 0) {
            return Result.ofFail(-1, "Invalid id");
        }
        DegradeRuleEntity oldEntity = repository.findById(id);
        if (oldEntity == null) {
            return Result.ofFail(-1, "id " + id + " does not exist");
        }
        if (entity == null) {
            return Result.ofFail(-1, "invalid body");
        }

        entity.setApp(oldEntity.getApp());
        entity.setIp(oldEntity.getIp());
        entity.setPort(oldEntity.getPort());
        Result<DegradeRuleEntity> checkResult = checkEntityInternal(entity);
        if (checkResult != null) {
            return checkResult;
        }

        entity.setId(id);
        Date date = new Date();
        entity.setGmtCreate(oldEntity.getGmtCreate());
        entity.setGmtModified(date);
        try {
            entity = repository.save(entity);
            if (entity == null) {
                return Result.ofFail(-1, "save entity fail");
            }
            publishRules(entity.getApp());
        }
        catch (Throwable throwable) {
            logger.error("Failed to update flow rule", throwable);
            return Result.ofThrowable(-1, throwable);
        }
        return Result.ofSuccess(entity);
    }
    @ResponseBody
    @RequestMapping("/delete.json")
    public Result<Long> delete(HttpServletRequest request, Long id) {
        AuthUser authUser = authService.getAuthUser(request);
        if (id == null) {
            return Result.ofFail(-1, "id can't be null");
        }

        DegradeRuleEntity oldEntity = repository.findById(id);
        if (oldEntity == null) {
            return Result.ofSuccess(null);
        }
        authUser.authTarget(oldEntity.getApp(), PrivilegeType.DELETE_RULE);
        try {
            repository.delete(id);
            publishRules(oldEntity.getApp());
        } catch (Throwable throwable) {
            logger.error("delete error:", throwable);
            return Result.ofThrowable(-1, throwable);
        }
        return Result.ofSuccess(id);
    }

    private void publishRules(String app) throws Exception {
        List<DegradeRuleEntity> rules = repository.findAllByApp(app);
        rulePublisher.publish(app, rules);
    }

    private <R> Result<R> checkEntityInternal(DegradeRuleEntity entity) {
        if (StringUtil.isBlank(entity.getApp())) {
            return Result.ofFail(-1, "app can't be null or empty");
        }
        if (StringUtil.isBlank(entity.getIp())) {
            return Result.ofFail(-1, "ip can't be null or empty");
        }
        if (entity.getPort() == null) {
            return Result.ofFail(-1, "port can't be null");
        }
        if (StringUtil.isBlank(entity.getLimitApp())) {
            return Result.ofFail(-1, "limitApp can't be null or empty");
        }
        if (StringUtil.isBlank(entity.getResource())) {
            return Result.ofFail(-1, "resource can't be null or empty");
        }
        if (entity.getCount() == null) {
            return Result.ofFail(-1, "count can't be null");
        }
        if (entity.getTimeWindow() == null) {
            return Result.ofFail(-1, "timeWindow can't be null");
        }
        if (entity.getGrade() == null) {
            return Result.ofFail(-1, "grade can't be null");
        }
        if (entity.getGrade() < RuleConstant.DEGRADE_GRADE_RT || entity.getGrade() > RuleConstant.DEGRADE_GRADE_EXCEPTION_COUNT) {
            return Result.ofFail(-1, "Invalid grade: " + entity.getGrade());
        }
        return null;
    }
}
