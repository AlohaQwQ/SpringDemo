package com.example.running;

import com.example.running.bean.Cat;
import com.example.running.bean.Dog;
import com.example.running.bean.Zhouzhou;
import com.example.running.config.RunningConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Conditional;
import org.yaml.snakeyaml.LoaderOptions;

/**
 * Spring 的能力—
 * 1.微服务(Microservices)
 * 2.响应式开发
 * 3.分布式开发(Spring Cloud)
 * 4.Web开发(Spring MVC)
 * 5.Serverless (无服务开发/函数式服务)
 * 6.事件驱动，结合分布式系统
 *
 * 为什么用SpringBoot—
 * 能快速创建出生产级别的Spring应用
 *
 * 1.Create stand-alone Spring applications
 * 创建独立Spring应用
 * 2.Embed Tomcat, Jetty or Undertow directly (no need to deploy WAR files)
 * 内嵌web服务器
 * 3.Provide opinionated ‘starter’ dependencies to simplify your build configuration
 * 自动starter依赖，简化构建配置
 * 4.Automatically configure Spring and 3rd party libraries whenever possible
 * 自动配置Spring以及第三方功能
 * 5.Provide production-ready features such as metrics, health checks, and externalized configuration
 * 提供生产级别的监控、健康检查及外部化配置
 * 6.Absolutely no code generation and no requirement for XML configuration
 * 无代码生成、无需编写XML
 * 7.SpringBoot是整合Spring技术栈的一站式框架
 * 8.SpringBoot是简化Spring技术栈的快速开发脚手架
 *
 */
/**
 * 引导加载自动配置类
 * @SpringBootApplication
 *    ——@SpringBootConfiguration 配置类
 *      @ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
 *                @Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })  包扫描注解，自定义扫描器
 *      @EnableAutoConfiguration 开启自动配置
 *       ——@AutoConfigurationPackage 自动配置包
 *          ——@Import(AutoConfigurationPackages.Registrar.class) Registrar给容器中 某个包下的组件批量注册（MainApplication）
 *         @Import(AutoConfigurationImportSelector.class)
 *          1、利用getAutoConfigurationEntry(annotationMetadata);给容器中批量导入一些组件
 *          2、调用List<String> configurations = AutoConfigurationImportSelector.getCandidateConfigurations(annotationMetadata, attributes)获取到所有需要导入到容器中的配置类
 *          3、利用工厂加载 Map<String, List<String>> SpringFactoriesLoader.loadSpringFactories(@Nullable ClassLoader classLoader)；得到所有的组件
 *          4、从META-INF/spring.factories位置来加载一个文件。
 *         	默认扫描我们当前系统里面所有META-INF/spring.factories位置的文件
 *          spring-boot-autoconfigure-2.3.4.RELEASE.jar包里面也有META-INF/spring.factories
 *          文件里面配置了spring-boot一启动就要给容器中加载的所有配置类
 *          xxxxAutoConfiguration(BatchAutoConfiguration、CacheAutoConfiguration)  按照条件装配规则@Conditional，最终会按需配置。
 *          (@ConditionalOnClass(LocalContainerEntityManagerFactoryBean.class，@ConditionalOnBean(AbstractEntityManagerFactoryBean.class)
 *
 */

/**
 * @author Aloha
 * @date 2022/7/24 17:11
 * @description 这是一个SpringBoot主程序入口类
 */
//scanBasePackages-修改包扫描路径
@SpringBootApplication()
//@SpringBootConfiguration(proxyBeanMethods = false)
public class MainApplication {

    public static void main(String[] args) {
        //1.返回IOC 容器
        ConfigurableApplicationContext context =  SpringApplication.run(MainApplication.class, args);

        //2.查看容器中的组件
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            //System.out.println("bean:" + beanDefinitionName);
        }

        //com.example.running.config.RunningConfig$$EnhancerBySpringCGLIB$$f728ec07@7544ac86  代理类
        RunningConfig config = context.getBean(RunningConfig.class);
        System.out.println("config:" + config);

        //3.从容器中获取组件
//        Zhouzhou zhouzhou = context.getBean(Zhouzhou.class);
////        Zhouzhou bendan = (Zhouzhou) context.getBean("bendan");
//        Zhouzhou zhouzhou = config.zhouzhou();
//        Zhouzhou bendan = config.zhouzhou();
//
//        //4.Config 配置为代理类，则容器中对象为单实例，每次获取都是唯一
//        System.out.println("bean:" + zhouzhou);
//        System.out.println("bean:" + bendan);
//        System.out.println("bean equals:" + zhouzhou.equals(bendan));
//
//        Cat cat = zhouzhou.getCat();
//        Cat cat1 = context.getBean(Cat.class);
//        System.out.println("cat:" + cat);
//        System.out.println("cat:" + cat1);
//
//        //5.获取以Bean 类的所有组件
//        String[] beanNames = context.getBeanNamesForType(Zhouzhou.class);
//        for (String s : beanNames) {
//            System.out.println("beanNames:" + s);
//        }
//        String[] beanNames1 = context.getBeanNamesForType(LoaderOptions.class);
//        for (String s : beanNames1) {
//            System.out.println("beanNames1:" + s);
//        }

        //6.条件装配
        boolean dog = context.containsBean("dog");
        System.out.println("dog:" + dog);

        boolean bendan = context.containsBean("xiaozhu");
//        Zhouzhou zhouzhou = context.getBean(Zhouzhou.class);
        System.out.println("zhouzhou:" + bendan);

        //7.通过 @ImportResource 的方式导入组件
//        Cat cat22 = (Cat) context.getBean("cat22");
//        Dog dog22 = (Dog) context.getBean("dog22");
//        System.out.println("cat22:" + cat22);
//        System.out.println("dog22:" + dog22);



    }

}
