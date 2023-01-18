package com.example.running.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.running.annotations.User;
import com.example.running.exception.UserTooManyException;
import com.example.running.service.UserService;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author Aloha
 * @date 2022/12/6 0:19
 * @description View 视图解析
 */
@Controller
public class ViewController {


    /**
     * @author Aloha
     * @date 2022/12/6 0:33
     * @description view视图解析
     */
    @GetMapping("/index-thymeleaf")
    public String goIndexThymeleaf(Model model){
        //model中的数据会被放在请求域中 request.setAttribute()
        model.addAttribute("title", "使用thymeleaf模板");
        model.addAttribute("linkTitle", "小破站");
        model.addAttribute("link", "http://www.bilibili.com");
        //跳转到页面
        return "index-thymeleaf";
    }

    @GetMapping("/index")
    public String index(){
        //跳转到页面
        return "index";
    }

    /**
     * @author Aloha
     * @date 2022/12/6 11:31
     * @description login页面跳转，支持2种路径访问
     */
    @GetMapping(value = {"/", "/login"})
    public String login(){

        //跳转到页面
        return "login";
    }

    /**
     * @author Aloha
     * @date 2022/12/6 11:32
     * @description 验证用户登录
     * RedirectAttributes 也可使用重定向数据
     */
    @PostMapping("/login")
    public String login(User user, HttpSession session, Model model){
        if (StringUtils.hasLength(user.getUserName()) && user.getPassword().equals("123456")) {
            //将登录成功的用户保存起来
            session.setAttribute("loginUser", user);
            //登录成功重定向到 main.html， 重定向是防止表单重复提交的最好方式
            return "redirect:/main.html";
        } else {
            //往请求域中添加 提示信息
            model.addAttribute("msg", "账号密码错误");
            return "login";
        }
    }

    /**
     * @author Aloha
     * @date 2022/12/6 11:52
     * @description 重定向到main 页面，避免刷新等操作 重复提交 /login请求
     */
    @GetMapping("/main.html")
    public String mainPage(HttpSession session, Model model){
        //判断是否有登录用户缓存   拦截器/过滤器
        Object loginUser = session.getAttribute("loginUser");
        if(loginUser!=null){
            //登录成功重定向到 main.html
            return "main";
        } else {
            //往请求域中添加 提示信息
            model.addAttribute("msg","请重新登录");
            return "login";
        }
    }

    @GetMapping("/basic_table")
    public String basicTable(){
        //登录成功重定向到 main.html
        return "table/basic_table";
    }

    @Autowired
    UserService userService;

    @GetMapping("/dynamic_table")
    public String dynamicTable(@RequestParam(value = "pn", defaultValue = "1")Integer pageNum, Model model){
        /*List userList = Arrays.asList(new User("user1", "111"),
                new User("user2", "222"),
                new User("user3", "333"),
                new User("user4", "444"));
        model.addAttribute("users", userList);

        if(userList.size()>3){
            throw new UserTooManyException("炸了");
        }*/

        //从数据库中查询数据返回
        //List<com.example.running.bean.User> userList = userService.list();
        //model.addAttribute("users", userList);

        //分页查询数据 需配置 MybatisPlusInterceptor 插件
        Page<com.example.running.bean.User> page = new Page<>(pageNum, 2);
        //数据返回
        Page<com.example.running.bean.User> returnPage = userService.page(page);
        List<com.example.running.bean.User> userList = returnPage.getRecords();

        //model.addAttribute("users", userList);

        long current = returnPage.getCurrent();
        long pages = returnPage.getPages();
        long total = returnPage.getTotal();

        model.addAttribute("users", returnPage);

        return "table/dynamic_table";
    }

    @GetMapping("/user/delete/{id}")
    public String dynamicDeleteUser(@PathVariable Long id, @RequestParam(value = "pn", defaultValue = "1") Integer pageNum, RedirectAttributes redirectAttributes){
        //使用mybatisplus
        com.example.running.bean.User user = userService.getById(id);
        boolean result = userService.removeById(user);

        //携带重定向 当前页数据
        redirectAttributes.addAttribute("pn", pageNum);
        //删除完成重定向回原列表
        return "redirect:/dynamic_table";
    }

    @GetMapping("/editable_table")
    public String editableTable(){
        //登录成功重定向到 main.html
        return "table/editable_table";
    }

    @GetMapping("/pricing_table")
    public String pricingTable(){
        //登录成功重定向到 main.html
        return "table/pricing_table";
    }

    @GetMapping("/responsive_table")
    public String responsiveTable(){
        //登录成功重定向到 main.html
        return "table/responsive_table";
    }

    @GetMapping("/form_layouts")
    public String formLayouts(){
        //登录成功重定向到 main.html
        return "form/form_layouts";
    }

}
