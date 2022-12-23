package com.example.running.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.example.running.annotations.Pet;
import com.example.running.bean.Cat;
import com.example.running.bean.Dog;
import com.example.running.bean.Zhouzhou;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.ConditionalConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.accept.ParameterContentNegotiationStrategy;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;
import org.yaml.snakeyaml.LoaderOptions;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hongyuan
 * @since 2022/7/25 18:20
 * 1.Spring 配置类 == 配置文件
 * 2.配置(注入)Bean 对象到容器中，实例为单实例对象
 * 3.配置类也是组件 @Component
 * proxyBeanMethods：代理bean的方法
 *      Full(proxyBeanMethods = true)、【保证每个@Bean方法被调用多少次返回的组件都是单实例的】 每次调用都会去检查容器中的对象是否存在，运行较慢
 *      Lite(proxyBeanMethods = false)【每个@Bean方法被调用多少次返回的组件都是新创建的】
 *      组件依赖必须使用Full模式默认。其他默认是否Lite模式
 * 4.@Import 指示要导入的一个或多个组件类——通常是@Configuration类。
 *          给容器中自动创建 LoaderOptions 类型的实例，ID 为全类名
 * 5.@ConditionalOnMissingBean 当容器中缺少 dog Bean注入时，本配置才生效
 * 6.@ImportResource 指示一个或多个包含要导入的 bean 定义的资源，可通过此注解导入spring 的配置文件
 * 7.@EnableConfigurationProperties 开启某个Bean 的属性配置功能，并将该Bean 自动注册容器中。 适用于第三方包中的Bean，
 **/
//@Configuration(proxyBeanMethods = true)
public class DruidConfig {

    /**
     * @author Aloha
     * @date 2022/12/13 23:27
     * @description 使用Spring Bean的方式注入 ServletRegistrationBean/FilterRegistrationBean/ServletListenerRegistrationBean
     */
    /*@Bean
    public ServletRegistrationBean servletRegistrationBean(){
        MyServlet myServlet = new MyServlet();
        return new ServletRegistrationBean(myServlet, "/my");
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean(){
        LoginFilter loginFilter = new LoginFilter();
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(loginFilter);
        filterRegistrationBean.setUrlPatterns(Arrays.asList("/my","/my02"));
        return filterRegistrationBean;
    }

    @Bean
    public ServletListenerRegistrationBean servletListenerRegistrationBean(){
        MyServletContextListener myServletContextListener = new MyServletContextListener();
        return new ServletListenerRegistrationBean(myServletContextListener);
    }*/

    /**
     * @author Aloha
     * @date 2022/12/21 16:12
     * @description 将配置属性与对象绑定
     */
    //@ConfigurationProperties("spring.datasource")
    //@Bean
    public DataSource dataSource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        String url = druidDataSource.getUrl();
        String userName = druidDataSource.getUsername();
        //加入防火墙，监控功能
        druidDataSource.setFilters("wall,stat");
        return druidDataSource;
    }

    /**
     * @author Aloha
     * @date 2022/12/19 1:05
     * @description 使用Servlet 注册的方式 配置druid 的监控功能
     */
    //@Bean
    public ServletRegistrationBean starServlet(){
        StatViewServlet statViewServlet = new StatViewServlet();
        ServletRegistrationBean<StatViewServlet> servletRegistrationBean = new ServletRegistrationBean(statViewServlet, "/druid/*");
        //配置登录账号
        servletRegistrationBean.addInitParameter("loginUsername", "druid");
        servletRegistrationBean.addInitParameter("loginPassword", "123456");
        return servletRegistrationBean;
    }

    /**
     * @author Aloha
     * @date 2022/12/19 1:05
     * @description 使用Servlet 注册的方式 配置druid 的监控功能, WebStatFilter 用于采集web-jdbc 关联监控的功能
     */
    //@Bean
    public FilterRegistrationBean webStatFilter(){
        WebStatFilter webStatFilter = new WebStatFilter();
        FilterRegistrationBean<WebStatFilter> filterRegistrationBean = new FilterRegistrationBean(webStatFilter);
        filterRegistrationBean.setUrlPatterns(Arrays.asList("/*"));
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }

}
