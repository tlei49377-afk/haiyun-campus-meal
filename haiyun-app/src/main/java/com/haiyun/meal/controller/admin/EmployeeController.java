package com.haiyun.meal.controller.admin;

import com.haiyun.meal.annotation.RequirePermission;
import com.haiyun.meal.constant.JwtClaimsConstant;
import com.haiyun.meal.dto.EmployeeDTO;
import com.haiyun.meal.dto.EmployeeLoginDTO;
import com.haiyun.meal.dto.EmployeePageQueryDTO;
import com.haiyun.meal.dto.EmployeeRoleDTO;
import com.haiyun.meal.entity.Employee;
import com.haiyun.meal.properties.JwtProperties;
import com.haiyun.meal.result.PageResult;
import com.haiyun.meal.result.Result;
import com.haiyun.meal.service.EmployeeService;
import com.haiyun.meal.service.PermissionService;
import com.haiyun.meal.service.RoleService;
import com.haiyun.meal.utils.JwtUtil;
import com.haiyun.meal.vo.EmployeeLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/api/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private RoleService roleService;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO.getUsername());

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .permissions(permissionService.listCodesByEmployeeId(employee.getId()))
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     * @param employeeDTO
     * @return
     */
    @RequirePermission("employee:add")
    @PostMapping
    public Result save(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工，员工数据：{}", employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }

    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @RequirePermission("employee:query")
    @GetMapping("/page")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("分页查询，参数为：{}", employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启用禁用员工账号
     * @param status
     * @param id
     */
    @RequirePermission("employee:status")
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status, Long id){
        log.info("员工状态：{}，员工id：{}, 启用or禁用员工账号", status, id);
        employeeService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @RequirePermission("employee:query")
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息，员工id为{}", id);
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    /**
     * 编辑员工信息
     * @param employeeDTO
     * @return
     */
    @RequirePermission("employee:update")
    @PutMapping
    public Result update(@RequestBody EmployeeDTO employeeDTO){
        log.info("编辑员工信息：{}", employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }

    /**
     * 查询员工已分配的角色
     * @param id
     * @return
     */
    @RequirePermission("role:query")
    @GetMapping("/{id}/roles")
    public Result<List<Long>> listRoles(@PathVariable Long id) {
        return Result.success(roleService.listRoleIdsByEmployeeId(id));
    }

    /**
     * 给员工分配角色
     * @param id
     * @param employeeRoleDTO
     * @return
     */
    @RequirePermission("role:assign")
    @PutMapping("/{id}/roles")
    public Result<String> assignRoles(@PathVariable Long id, @RequestBody EmployeeRoleDTO employeeRoleDTO) {
        log.info("员工分配角色：employeeId={}, roleIds={}", id, employeeRoleDTO.getRoleIds());
        roleService.assignRoles(id, employeeRoleDTO.getRoleIds());
        return Result.success();
    }
}
