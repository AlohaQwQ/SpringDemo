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
 *          Web-Servlet 自动配置类，Spring-MVC 相关配置
 *          @ConditionalOnClass(DispatcherServlet.class)
 *          public class DispatcherServletAutoConfiguration {
 *              容器中是否有该组件(tomcat包中已包含)
 *              @ConditionalOnClass(ServletRegistrion.class)
 * 	            @EnableConfigurationProperties(WebMvcPropeies.class)
 * 	            protected static class DispatcherServletConfiguration {
 *
 *                      @Bean
 * 	             		@ConditionalOnBean(Multipartolver.class)
 * 	             		@ConditionalOnMissingBean(name = DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME) 如果用户没有配置该组件，则将该bean 注册到容器中
 *  	            	public MultipartResolver multipartResolver(MultipartResolver resolver) {
 *  	            		// Detect if the user has created a MultipartResolver but named it incorrectly
 * 	             			return resolver;
 *  	            	}
 * 	            }
 *          }
 *          配置属性前缀为 spring.mvc
 *          @ConfigurationProperties(prefix = "spring.mvc")
 *          public class WebMvcProperties {}
 *
 *          条件装配——SpringBoot 默认组件
 *          @Bean
 * 	        @ConditionalOnMisngBean
 * 	        public CharacterEncodingFilter characterEncodingFilter() {}
 *
 *	        @Bean
 *	        @Lazy
 *	        @ConditionalOnMisngBean
 *	        public RestTemplateBuilder restTemplateBuilder(RestTemplateBuilderConfigurer restTemplateBuilderConfigurer) {}
 *
 */

/**
 * 总结：
 * https://spring.io/projects/spring-boot
 * ● SpringBoot先加载所有的自动配置类  xxxxxAutoConfiguration
 * ● 每个自动配置类按照条件进行生效，默认都会绑定配置文件指定的值。xxxxProperties里面拿。xxxProperties和配置文件进行了绑定
 * ● 生效的配置类就会给容器中装配很多组件
 * ● 只要容器中有这些组件，相当于这些功能就有了
 * ● 定制化配置
 *   ○ 用户直接自己@Bean替换底层的组件
 *   ○ 用户去看这个组件是获取的配置文件什么值就去修改。
 * xxxxxAutoConfiguration ---> 组件  ---> xxxxProperties里面拿值  ----> application.properties 修改对应属性
 */

/**
 * @author Aloha
 * @date 2022/7/24 17:11
 * @description 这是一个SpringBoot主程序入口类
 */

/**
 * @author Aloha
 * 1、SpringMVC自动配置概览
 * Spring Boot provides auto-configuration for Spring MVC that works well with most applications.(大多场景我们都无需自定义配置)
 * The auto-configuration adds the following features on top of Spring’s defaults: (自动配置在 Spring 的默认值之上添加了以下特性：)
 *
 * ● Inclusion of ContentNegotiatingViewResolver and BeanNameViewResolver beans.
 *   ○ 内容协商视图解析器和BeanName视图解析器
 * ● Support for serving static resources, including support for WebJars (covered later in this document)).
 *   ○ 静态资源（包括webjars）
 * ● Automatic registration of Converter, GenericConverter, and Formatter beans.
 *   ○ 自动注册 Converter，GenericConverter，Formatter
 * ● Support for HttpMessageConverters (covered later in this document).
 *   ○ 支持 HttpMessageConverters （后来我们配合内容协商理解原理）
 * ● Automatic registration of MessageCodesResolver (covered later in this document).
 *   ○ 自动注册 MessageCodesResolver （国际化用）
 * ● Static index.html support.
 *   ○ 静态index.html 页支持
 * ● Custom Favicon support (covered later in this document).
 *   ○ 自定义 Favicon
 * ● Automatic use of a ConfigurableWebBindingInitializer bean (covered later in this document).
 *   ○ 自动使用 ConfigurableWebBindingInitializer ，（DataBinder负责将请求数据绑定到JavaBean上）
 *
 * If you want to keep those Spring Boot MVC customizations and make more MVC customizations (interceptors, formatters,
 * view controllers, and other features), you can add your own @Configuration class of type WebMvcConfigurer but without @EnableWebMvc.
 * 不用@EnableWebMvc注解。使用 @Configuration + WebMvcConfigurer 自定义规则
 *
 * If you want to provide custom instances of RequestMappingHandlerMapping, RequestMappingHandlerAdapter, or ExceptionHandlerExceptionResolver,
 * and still keep the Spring Boot MVC customizations, you can declare a bean of type WebMvcRegistrations and use it to provide custom instances of those components.
 * 声明 WebMvcRegistrations 改变默认底层组件
 *
 * If you want to take complete control of Spring MVC, you can add your own @Configuration annotated with @EnableWebMvc,
 * or alternatively add your own @Configuration-annotated DelegatingWebMvcConfiguration as described in the Javadoc of @EnableWebMvc.
 * 使用 @EnableWebMvc+@Configuration+DelegatingWebMvcConfiguration 全面接管SpringMVC
 *
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
