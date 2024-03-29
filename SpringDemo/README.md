[TOC]
![img_0.png](assets/img_0.png)
# 1.SpringBoot概览
> https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle
> 
> https://docs.spring.io/spring-boot/docs/current/reference/html/index.html
> 
> https://github.com/spring-projects/spring-boot

## 1.1 Spring 的能力

1. 微服务(Microservices)
2. 响应式开发
3. 分布式开发(Spring Cloud)
4. Web开发(Spring MVC)
5. Serverless (无服务开发/函数式服务)
6. 事件驱动，结合分布式系统

> 为什么用SpringBoot—
> 能快速创建出生产级别的Spring应用

1. Create stand-alone Spring applications
   创建独立Spring应用
2. Embed Tomcat, Jetty or Undertow directly (no need to deploy WAR files)
   内嵌web服务器
3. Provide opinionated ‘starter’ dependencies to simplify your build configuration
   自动starter依赖，简化构建配置
4. Automatically configure Spring and 3rd party libraries whenever possible
   自动配置Spring以及第三方功能
5. Provide production-ready features such as metrics, health checks, and externalized configuration
   提供生产级别的监控、健康检查及外部化配置
6. Absolutely no code generation and no requirement for XML configuration
   无代码生成、无需编写XML
7. SpringBoot是整合Spring技术栈的一站式框架
8. SpringBoot是简化Spring技术栈的快速开发脚手架

## 1.2 SpringBoot应用入口

```
* SpringBoot Web应用入口启动类,引导加载自动配置类
@SpringBootApplication()
public class MainApplication {

    public static void main(String[] args) {

    }
}
```

`@SpringBootApplication 注解阐释`

```
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
@Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {

}

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage
@Import(AutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {

}
```

* @SpringBootApplication 配置类，自动配置如何生效
  - @ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
  - @Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })  包扫描注解，自定义扫描器
  - @EnableAutoConfiguration 开启自动配置
    - @AutoConfigurationPackage 自动配置包
    - @Import(AutoConfigurationPackages.Registrar.class) Registrar给容器中 某个包下的组件批量注册（MainApplication）
    - @Import(AutoConfigurationImportSelector.class)

      1. 利用getAutoConfigurationEntry(annotationMetadata);给容器中批量导入一些组件
      2. 调用List<String> configurations = AutoConfigurationImportSelector.getCandidateConfigurations(annotationMetadata, attributes)获取到所有需要导入到容器中的配置类
      3. 利用工厂加载 Map<String, List<String>> SpringFactoriesLoader.loadFactoryNames() 得到所有的组件
      4. classLoader.getResources(FACTORIES_RESOURCE_LOCATION)  从META-INF/spring.factories位置来加载所有配置文件
         - 默认扫描我们当前系统里面所有META-INF/spring.factories位置的文件
         - spring-boot-autoconfigure-2.3.4.RELEASE.jar包里面也有META-INF/spring.factories

      > org\springframework\boot\spring-boot\2.7.1\spring-boot-2.7.1.jar!\META-INF\spring.factories
      > org\springframework\boot\spring-boot-autoconfigure\2.7.1\spring-boot-autoconfigure-2.7.1.jar!\META-INF\spring.factories
      >

      `spring.factories文件里面配置了spring-boot启动时就要给容器中加载的所有配置类,但是最终会按照条件装配规则按需配置 xxxxAutoConfiguration(如BatchAutoConfiguration、CacheAutoConfiguration) @Conditional  @ConditionalOnClass(LocalContainerEntityManagerFactoryBean.class， @ConditionalOnBean(AbstractEntityManagerFactoryBean.class`

      ```java
      @AutoConfiguration
      //当项目引入了RabbitMq消息队列组件该配置类才会生效
      @ConditionalOnClass({ RabbitTemplate.class, Channel.class }) 
      @EnableConfigurationProperties(RabbitProperties.class)
      @Import({ RabbitAnnotationDrivenConfiguration.class, RabbitStreamConfiguration.class })
      public class RabbitAutoConfiguration {

      }
      ```

## 1.3 自动配置的条件装配规则

> org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration
> Web-Servlet 自动配置类，Spring-MVC 请求分发相关配置`

```java
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE) //装配最高优先级
//指定装配优先级在 ServletWebServerFactoryAutoConfiguration.class之后
@AutoConfiguration(after = ServletWebServerFactoryAutoConfiguration.class) 
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnClass(DispatcherServlet.class)
public class DispatcherServletAutoConfiguration {

    @ConditionalOnClass(ServletRegistrion.class)//当容器中有该组件时装配(tomcat包中已包含)
    @EnableConfigurationProperties(WebMvcPropeies.class)//开启该配置类绑定该配置文件功能 spring.mvc
    protected static class DispatcherServletConfiguration {

        @Bean
        @ConditionalOnBean(Multipartolver.class)
        @ConditionalOnMissingBean(name = DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME) 如果用户没有配置该组件，则将该bean 注册到容器中
        public MultipartResolver multipartResolver(MultipartResolver resolver) {
            // Detect if the user has created a MultipartResolver but named it incorrectly
            return resolver;
        }
    }
}
  
//配置属性前缀为 spring.mvc
@ConfigurationProperties(prefix = "spring.mvc")
public class WebMvcProperties {}
```

> org.springframework.boot.autoconfigure.aop.AopAutoConfiguration
> Aop自动配置类

```java
@AutoConfiguration
//当配置文件中配置了spring.aop.name，并且配置值为true时该装配生效。当未配置时，该值也为true
@ConditionalOnProperty(prefix = "spring.aop", name = "auto", havingValue = "true", matchIfMissing = true)
public class AopAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(Advice.class) //当容器中存在该类时生效
    static class AspectJAutoProxyingConfiguration {

        @Configuration(proxyBeanMethods = false)
        @EnableAspectJAutoProxy(proxyTargetClass = false)
        @ConditionalOnProperty(prefix = "spring.aop", name = "proxy-target-class", havingValue = "false")
        static class JdkDynamicAutoProxyConfiguration { }

        @Configuration(proxyBeanMethods = false)
        @EnableAspectJAutoProxy(proxyTargetClass = true)
        @ConditionalOnProperty(prefix = "spring.aop", name = "proxy-target-class", havingValue = "true",
                matchIfMissing = true)
        static class CglibAutoProxyConfiguration { }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMissingClass("org.aspectj.weaver.Advice") //当容器中缺少Advice.class类时生效
    //当配置文件中配置了spring.aop.proxy-target-class，并且配置值为true时该装配生效。当未配置时，该值也为true
    @ConditionalOnProperty(prefix = "spring.aop", name = "proxy-target-class", havingValue = "true",
            matchIfMissing = true) 
    static class ClassProxyingConfiguration {

        //配置简单的Aop功能
        @Bean
        static BeanFactoryPostProcessor forceAutoProxyCreatorToUseClassProxying() {
            return (beanFactory) -> {
                if (beanFactory instanceof BeanDefinitionRegistry) {
                    BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
                    AopConfigUtils.registerAutoProxyCreatorIfNecessary(registry);
                    AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
                }
            };
        }

    }
}
```

> 总结：https://spring.io/projects/spring-boot      https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle

* SpringBoot先加载所有的自动配置类  xxxxxAutoConfiguration
  + org\springframework\boot\spring-boot\2.7.1\spring-boot-2.7.1.jar!\META-INF\spring.factories
  + org\springframework\boot\spring-boot-autoconfigure\2.7.1\spring-boot-autoconfigure-2.7.1.jar!\META-INF\spring.factories
* 每个自动配置类按照条件装配规则生效，默认都会绑定配置文件指定的值。xxxxProperties里面拿。xxxProperties和配置文件进行了绑定
* 生效的配置类就会给容器中装配很多组件
* 只要容器中有这些组件，相当于这些功能就有了
* 定制化配置
  + 用户直接自己@Bean替换底层的组件
  + 用户去看这个组件是获取的配置文件什么值就去修改。xxxxxAutoConfiguration ---> 组件  ---> xxxxProperties里面拿值  ----> application.properties 修改对应属性

## 1.4 注解的阐释

`注解可理解为释义说明，标记或标签，如类/属性/方法的说明。根据定义的不同，可在运行时或编译时被忽略或一直保存。`

`通常由反射获取该类上的注解信息，并以其作为初始配置信息做相应处理`

```java
@Retention(RetentionPolicy.RUNTIME)
public @interface CatAnnotations {

    int age() default 1;
    String name() default "";
}

@CatAnnotations(name = "cat class")//注解 修饰类
public class Cat {

    private String eatFood;
  
    @CatAnnotations(age = 10,name = "cat")//注解 修饰属性
    private String name;

    @CatAnnotations(name = "cat construtor")//注解 修饰构造器
    public Cat(String eatFood, String name) {
        this.eatFood = eatFood;
        this.name = name;
    }

    public String getEatFood() {
        return eatFood;
    }

    public void setEatFood(String eatFood) {
        this.eatFood = eatFood;
    }

    @CatAnnotations(name = "cat name")//注解 修饰方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

    public static void main(String[] args) throws NoSuchFieldException, NoSuchMethodException {

        //获取Cat class 实例
        Class<Cat> catClass = Cat.class;

        //反射获取 类上的注解
        TestAnnotations classAnnotation = catClass.getAnnotation(CatAnnotations.class);
        System.out.println("annotions:" + classAnnotation.age());
        System.out.println("annotions:" + classAnnotation.name());

        //反射获取 private属性上的注解
        Field name = catClass.getDeclaredField("name");
        CatAnnotations fieldAnnotation = name.getAnnotation(CatAnnotations.class);
        System.out.println("annotions-name:" + fieldAnnotation);
        //反射获取 方法上的注解
        Method me = catClass.getMethod("getName",null);
        CatAnnotations methodAnnotation = me.getAnnotation(CatAnnotations.class);

    }
```

> 总结：注解如何使用
[详见@RequestParam 注解分析](#3.4.2)

* 编译器可以利用注解来探测错误和检查信息，像@override检查是否重写
* 适合工具类型的软件用的，避免繁琐的代码，生成代码配置，比如jpa自动生成sql,日志注解，权限控制
* 程序运行时的处理： 某些注解可以在程序运行的时候，通过反射获取被代码所读取，因此我们可以自定义注解做一些初始配置为代码逻辑做支持

# 2.SpringMVC

## 2.1 SpringMVC概述

> Spring Boot provides auto-configuration for Spring MVC that works well with most applications.(大多场景我们都无需自定义配置)
> The auto-configuration adds the following features on top of Spring’s defaults: (自动配置在 Spring 的默认值之上添加了以下特性：)

* ● Inclusion of ContentNegotiatingViewResolver and BeanNameViewResolver beans.
* ○ 内容协商视图解析器和BeanName视图解析器
* ● Support for serving static resources, including support for WebJars (covered later in this document)).
* ○ 静态资源（包括webjars）
* ● Automatic registration of Converter, GenericConverter, and Formatter beans.
* ○ 自动注册 Converter，GenericConverter，Formatter
* ● Support for HttpMessageConverters (covered later in this document).
* ○ 支持 HttpMessageConverters （后来我们配合内容协商理解原理）
* ● Automatic registration of MessageCodesResolver (covered later in this document).
* ○ 自动注册 MessageCodesResolver （国际化用）
* ● Static index.html support.
* ○ 静态index.html 页支持
* ● Custom Favicon support (covered later in this document).
* ○ 自定义 Favicon
* ● Automatic use of a ConfigurableWebBindingInitializer bean (covered later in this document).
* ○ 自动使用 ConfigurableWebBindingInitializer ，（DataBinder负责将请求数据绑定到JavaBean上）

1. If you want to keep those Spring Boot MVC customizations and make more MVC customizations (interceptors, formatters,view controllers, and other features), you can add your own @Configuration class of type WebMvcConfigurer but without @EnableWebMvc.
   不用@EnableWebMvc注解。使用 @Configuration + WebMvcConfigurer 自定义规则
2. If you want to provide custom instances of RequestMappingHandlerMapping, RequestMappingHandlerAdapter, or ExceptionHandlerExceptionResolver,and still keep the Spring Boot MVC customizations, you can declare a bean of type WebMvcRegistrations and use it to provide custom instances of those components.
   声明 WebMvcRegistrations 改变默认底层组件
3. If you want to take complete control of Spring MVC, you can add your own @Configuration annotated with @EnableWebMvc,or alternatively add your own @Configuration-annotated DelegatingWebMvcConfiguration as described in the Javadoc of @EnableWebMvc.
   使用 @EnableWebMvc+@Configuration+DelegatingWebMvcConfiguration 全面接管SpringMVC

## 2.2.静态资源访问

> [classpath [META-INF/resources/], classpath [resources/], classpath [static/], classpath [public/], ServletContext [/]]
> 2022-08-04 16:07:22.954 DEBUG 25196 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed 200 OK

只要静态资源放在资源路径下： resources/static (or /public or /resources or /META-INF/resources)
访问: 当前项目根路径/ + 静态资源名

原理: 静态映射/**

`spring.mvc.static-path-pattern 属性资源映射调整`

`spring.web.resources.static-locations 属性自定义静态资源位置`

请求进来，DispatcherServlet 分发处理，先去找Controller看能不能处理。不能处理的所有请求又都交给静态资源处理器。静态资源也找不到则响应404页面

欢迎页支持

`静态资源路径下  index.html`

网站图标支持
`'favicon.ico 放在静态资源目录下即可`

## 2.3 静态资源配置原理(WebMvcAutoConfiguration)

* org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
* SpringBoot启动默认加载  xxxAutoConfiguration 类（自动配置类）

```java
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class })
@ConditionalOnMissingBean(WebMvcConfigurationSupport.class) //当容器中缺少 WebMvcConfigurationSupport 组件时生效，该组件用于自定义MVC配置
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
@AutoConfigureAfter({ DispatcherServletAutoConfiguration.class, TaskExecutionAutoConfiguration.class,
ValidationAutoConfiguration.class })
public class WebMvcAutoConfiguration {}
```

* 给容器中配了什么

```java
@Configuration(proxyBeanMethods = false)
@Import(EnableWebMvcConfiguration.class)
@EnableConfigurationProperties({ WebMvcProperties.class, ResourceProperties.class }) //配置属性
@Order(0)
public static class WebMvcAutoConfigurationAdapter implements WebMvcConfigurer {}
```

* 配置文件的相关属性和xxx进行了绑定

`WebMvcProperties==spring.mvc    WebProperties==spring.web`

```java
//配置类只有一个有参构造器，有参构造器所有参数的值都会从容器中确定
//WebProperties webProperties；获取和spring.web绑定的所有的值的对象
//WebMvcProperties mvcProperties 获取和spring.mvc绑定的所有的值的对象
//ListableBeanFactory beanFactory Spring的beanFactory
//HttpMessageConverters 找到所有的HttpMessageConverters  Http请求和响应处理
//ResourceHandlerRegistrationCustomizer 找到 资源处理器的自定义器。=========
//DispatcherServletPath  处理分发器
//ServletRegistrationBean   给应用注册Servlet、Filter....
public WebMvcAutoConfigurationAdapter(WebProperties webProperties, WebMvcProperties mvcProperties,
                  ListableBeanFactory beanFactory, ObjectProvider<HttpMessageConverters> messageConvertersProvider,
                  ObjectProvider<ResourceHandlerRegistrationCustomizer> resourceHandlerRegistrationCustomizerProvider,
                  ObjectProvider<DispatcherServletPath> dispatcherServletPath,
                  ObjectProvider<ServletRegistrationBean<?>> servletRegistrations) {
    this.resourceProperties = resourceProperties;
    this.mvcProperties = mvcProperties;
    this.beanFactory = beanFactory;
    this.messageConvertersProvider = messageConvertersProvider;
    this.resourceHandlerRegistrationCustomizer = resourceHandlerRegistrationCustomizerProvider.getIfAvailable();
    this.dispatcherServletPath = dispatcherServletPath;
    this.servletRegistrations = servletRegistrations;
}
```

```java
//资源处理的默认规则
//CLASSPATH_RESOURCE_LOCATIONS = { "classpath:/META-INF/resources/", "classpath:/resources/", "classpath:/static/", "classpath:/public/" }
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    if (!this.resourceProperties.isAddMappings()) {
        logger.debug("Default resource handling disabled");
        return;
    }
    //webjars的规则
    addResourceHandler(registry, "/webjars/**", "classpath:/META-INF/resources/webjars/");
    addResourceHandler(registry, this.mvcProperties.getStaticPathPattern(), (registration) -> {
        registration.addResourceLocations(this.resourceProperties.getStaticLocations());
        if (this.servletContext != null) {
            ServletContextResource resource = new ServletContextResource(this.servletContext, SERVLET_LOCATION);
            registration.addResourceLocations(resource);
        }
    });
}
```

```java
//HandlerMapping：定义请求和处理响应对象之间的映射处理器。保存了每一个Handler能处理哪些请求。
//WelcomePageHandlerMapping 欢迎页响应处理(/index.hteml)
@Bean
public WelcomePageHandlerMapping welcomePageHandlerMapping(ApplicationContext applicationContext,
                                                           FormattingConversionService mvcConversionService, ResourceUrlProvider mvcResourceUrlProvider) {
    WelcomePageHandlerMapping welcomePageHandlerMapping = new WelcomePageHandlerMapping(
            new TemplateAvailabilityProviders(applicationContext), applicationContext, getWelcomePage(),
            this.mvcProperties.getStaticPathPattern());
    welcomePageHandlerMapping.setInterceptors(getInterceptors(mvcConversionService, mvcResourceUrlProvider));
    welcomePageHandlerMapping.setCorsConfigurations(getCorsConfigurations());
    return welcomePageHandlerMapping;
}

WelcomePageHandlerMapping(TemplateAvailabilityProviders templateAvailabilityProviders,
                          ApplicationContext applicationContext, org.springframework.core.io.Resource welcomePage, String staticPathPattern) {
    if (welcomePage != null && "/**".equals(staticPathPattern)) {
        logger.info("Adding welcome page: " + welcomePage);
        setRootViewName("forward:index.html");
    }
    else if (welcomeTemplateExists(templateAvailabilityProviders, applicationContext)) {
        logger.info("Adding welcome page template: index");
        setRootViewName("index");
    }
}
```

# 3.请求参数处理(Rest)

## 3.1 WebMvcAutoConfiguration-HiddenHttpMethodFilter-隐藏http方法过滤器

> org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration

* @xxxMapping
* Rest风格支持（使用HTTP请求方式动词来表示对资源的操作）
  ○ 以前：/getUser   获取用户     /deleteUser 删除用户    /editUser  修改用户       /saveUser 保存用户
  ○ 现在： /user    GET-获取用户    DELETE-删除用户     PUT-修改用户      POST-保存用户
  ○ 核心Filter HiddenHttpMethodFilter

`手动配置开启-spring.mvc.hiddenmethod.filter.enabled=true`

* 用法: 表单method=put, 隐藏域 _method=put

```java
//WebMvcAutoConfiguration中配置
@Bean
@ConditionalOnMissingBean(HiddenHttpMethodFilter.class)
@ConditionalOnProperty(prefix = "spring.mvc.hiddenmethod.filter", name = "enabled")
public OrderedHiddenHttpMethodFilter hiddenHttpMethodFilter() {
    return new OrderedHiddenHttpMethodFilter();
}

//http方法过滤器
public class HiddenHttpMethodFilter extends OncePerRequestFilter {

    private static final List<String> ALLOWED_METHODS =
            Collections.unmodifiableList(Arrays.asList(HttpMethod.PUT.name(),
                    HttpMethod.DELETE.name(), HttpMethod.PATCH.name()));

    /** Default method parameter: {@code _method}. */
    public static final String DEFAULT_METHOD_PARAM = "_method";

    private String methodParam = DEFAULT_METHOD_PARAM;

    //方法过滤
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        HttpServletRequest requestToUse = request;
        if ("POST".equals(request.getMethod()) && request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE) == null) {
            String paramValue = request.getParameter(this.methodParam);
            if (StringUtils.hasLength(paramValue)) {
                String method = paramValue.toUpperCase(Locale.ENGLISH);
                if (ALLOWED_METHODS.contains(method)) {
                    requestToUse = new HttpMethodRequestWrapper(request, method);
                }
            }
        }
        filterChain.doFilter(requestToUse, response);
    }
}
```

## 3.2 Rest原理（from表单提交要使用REST的时候）(ServletRequestWrapper)

* 主流浏览器from表单请求时，只支持get/post请求，不支持rest风格请求
* 欲支持rest风格请求时，在表单提交中带上带上参数_method，指定rest请求方式(PUT/DELETE)
* 请求过来被HiddenHttpMethodFilter拦截
* 请求是否正常，并且请求方式是POST
* 获取到表单中_method的值
* 兼容以下请求: PUT.DELETE.PATCH
* 原生的使用了post请求的request，通过包装模式requestWrapper重写了getMethod方法，传递_method参数，getMethod方法返回的是新rest请求方式
* 过滤器链放行的时候用wrapper，调用getMethod方法识别当前请求方式则会调用requestWrapper的，返回传入的_method参数
* 从而匹配到对应的rest 请求处理方法

> Rest使用客户端工具/api请求(如安卓请求)，如PostMan直接发送put、delete等方式请求，无需Filter。因为request请求可指定rest请求方式

```java
public class WebMvcAutoConfiguration {
    ...
    //配置表单支持rest提交 _method参数是否隐藏，默认false
    @Bean
    @ConditionalOnMissingBean(HiddenHttpMethodFilter.class)
    @ConditionalOnProperty(prefix = "spring.mvc.hiddenmethod.filter", name = "enabled")
    public OrderedHiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new OrderedHiddenHttpMethodFilter();
    }
}

public class HiddenHttpMethodFilter extends OncePerRequestFilter {

    private static final List<String> ALLOWED_METHODS =
            Collections.unmodifiableList(Arrays.asList(HttpMethod.PUT.name(),
                    HttpMethod.DELETE.name(), HttpMethod.PATCH.name()));

    /** 
     * Default method parameter: {@code _method}. 
     **/
    public static final String DEFAULT_METHOD_PARAM = "_method";

    private String methodParam = DEFAULT_METHOD_PARAM;
  
    //当配置生效时
    ...
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        HttpServletRequest requestToUse = request;
        //请求方式是post 并且没有错误
        if ("POST".equals(request.getMethod()) && request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE) == null) {
            //获取请求参数中是否有 _method
            String paramValue = request.getParameter(this.methodParam);
            if (StringUtils.hasLength(paramValue)) {
                //忽略大小写
                String method = paramValue.toUpperCase(Locale.ENGLISH);
                //参数值在请求范围内
                if (ALLOWED_METHODS.contains(method)) {
                    //通过包装模式requestWrapper重写了getMethod方法返回新的request
                    requestToUse = new HttpMethodRequestWrapper(request, method);
                }
            }
        }
        //过滤器将新request请求发行处理
        filterChain.doFilter(requestToUse, response);
    }

  
    private static class HttpMethodRequestWrapper extends HttpServletRequestWrapper {

        private final String method;

        public HttpMethodRequestWrapper(HttpServletRequest request, String method) {
            super(request);
            this.method = method;
        }

        //重写getMethod方法返回传入的 _method rest新请求方式
        @Override
        public String getMethod() {
            return this.method;
        }
    }
}
```

```html
<form action="/user" method="get">
    <input value="REST-GET提交" type="submit" />
</form>

<form action="/user" method="post">
    <input value="REST-POST提交" type="submit" />
</form>

<form action="/user" method="post">
    <input name="_method" type="hidden" value="DELETE"/>
    <input value="REST-DELETE 提交" type="submit"/>
</form>

<form action="/user" method="post">
    <input name="_method" type="hidden" value="PUT" />
    <input value="REST-PUT提交"type="submit" />
<form>
```

## 3.3请求映射原理(DispatcherServlet)

> SpringMvc中的DispatcherServlet是负责处理所有请求的开始

DispatcherServlet 类继承关系
`DispatcherServlet->FrameworkServlet->HttpServletBean->HttpServlet`

`org.springframework.web.servlet.HttpServletBean`

`javax.servlet.http.HttpServlet`

`关注HttpServlet 核心的 doGet(),doPost()方法`

> Http请求方法分发逻辑

`HttpServlet.doGet()->FrameworkServlet.doGet()->FrameworkServlet.processRequest()->DispatcherServlet.doService()->DispatcherServlet.doDispatch()`

> DispatcherServlet.doDispatch() 方法是SpringMvc 中每个请求都会首先经过的方法，由该方法决定请求分派匹配规则

```java
protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception{
  HttpServletRequest processedRequest=request;
  HandlerExecutionChain mappedHandler=null;
  boolean multipartRequestParsed=false;

  WebAsyncManager asyncManager=WebAsyncUtils.getAsyncManager(request);
  //检测是否是文件请求
  processedRequest = checkMultipart(request);
  multipartRequestParsed = (processedRequest != request);

  // Determine handler for the current request. 决定当前请求由哪个handler(Controller) 处理
  mappedHandler = getHandler(processedRequest);
  // HandlerMapping 处理器映射
  if (mappedHandler == null) {
     noHandlerFound(processedRequest, response);
     return;
  }
  ...
  
  
}
```

> 分发请求路径地址映射匹配逻辑

`DispatcherServlet.doDispatch()->DispatcherServlet.getHandler()-> HandlerMapping.getHandler()->HandlerMapping.getHandlerInternal()->HandlerMapping.lookupHandlerMethod()->MappingRegistry.getMappingsByDirectPath()->MappingRegistry.addMatchingMappings()`

```java
protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
    if (this.handlerMappings != null) {
        for (HandlerMapping mapping : this.handlerMappings) {
            HandlerExecutionChain handler = mapping.getHandler(request);
            if (handler != null) {
                return handler;
            }
        }
    }
    return null;
}

    0 = {RequestMappingHandlerMapping@7139} //保存了所有RequestMapping(GetMapping/PostMapping)和HandlerMapping的映射规则
    1 = {WelcomePageHandlerMapping@7140}
    2 = {BeanNameUrlHandlerMapping@7141}
    3 = {RouterFunctionMapping@7142}
    4 = {SimpleUrlHandlerMapping@7143}

protected HandlerMethod getHandlerInternal(HttpServletRequest request) throws Exception {
    //获取请求访问路径
    String lookupPath = initLookupPath(request);
    //获取映射list读取锁
    this.mappingRegistry.acquireReadLock();
    try {
        //寻找请求路径是否有匹配值
        HandlerMethod handlerMethod = lookupHandlerMethod(lookupPath, request);
        return (handlerMethod != null ? handlerMethod.createWithResolvedBean() : null);
    }
    finally {
        this.mappingRegistry.releaseReadLock();
    }
}

protected HandlerMethod lookupHandlerMethod(String lookupPath, HttpServletRequest request) throws Exception{
    List<Match> matches=new ArrayList<>();
    //匹配当前请求路径的值
    List<T> directPathMatches=this.mappingRegistry.getMappingsByDirectPath(lookupPath);
    //匹配到/user路径请求的所有RequestMapping
    directPathMatches = {ArrayList@7475}  size = 4
        0 = {RequestMappingInfo@7456} "{PUT [/user]}"
        1 = {RequestMappingInfo@7454} "{POST [/user]}"
        2 = {RequestMappingInfo@7462} "{GET [/user]}"
        3 = {RequestMappingInfo@7458} "{DELETE [/user]}"
  
    if(directPathMatches!=null){
        //将所有匹配到的结果加入缓存集合中
        addMatchingMappings(directPathMatches,matches,request);
    }
    if(matches.isEmpty()){
        addMatchingMappings(this.mappingRegistry.getRegistrations().keySet(),matches,request);
    }
    if (!matches.isEmpty()){
        Match bestMatch=matches.get(0);
        //匹配结果有多个，则需要再次匹配
        if(matches.size()>1){
            Comparator<Match> comparator=new MatchComparator(getMappingComparator(request));
            matches.sort(comparator);
        }
        ...
    }
    ...
}
```

![img.png](assets/img.png)
<details>
  <summary>WebMvcConfigurationSupport</summary>

```java
//SpringBoot 容器中自动创建的RequestMappingHandlerMapping，用于解析所有 @RequestMapping 注解的方法
public class WebMvcConfigurationSupport implements ApplicationContextAware, ServletContextAware {
    ...
    /**
     * Return a {@link RequestMappingHandlerMapping} ordered at 0 for mapping
     * requests to annotated controllers.
     * 返回一个顺序为0的RequestMappingHandlerMapping，用于将请求映射到带注释的控制器
     */
    @Bean
    @SuppressWarnings("deprecation")
    public RequestMappingHandlerMapping requestMappingHandlerMapping(
            @Qualifier("mvcContentNegotiationManager") ContentNegotiationManager contentNegotiationManager,
            @Qualifier("mvcConversionService") FormattingConversionService conversionService,
            @Qualifier("mvcResourceUrlProvider") ResourceUrlProvider resourceUrlProvider) {

        RequestMappingHandlerMapping mapping = createRequestMappingHandlerMapping();
        mapping.setOrder(0);
        mapping.setInterceptors(getInterceptors(conversionService, resourceUrlProvider));
        mapping.setContentNegotiationManager(contentNegotiationManager);
        mapping.setCorsConfigurations(getCorsConfigurations());
        ...
    }
    ...
}
```
</details>
HandlerMappings(List)-所有的请求映射都存放在此

* RequestMappingHandlerMapping -保存了所有@RequestMapping 和handler(Controller)的映射规则(MappingRegistry-映射注册中心)
* WelcomePageHandlerMapping -SpringBoot自动配置欢迎页, 访问 /能访问到index.html
* BeanNameUrlHandlerMapping
* RouterFunctionMapping
* SimpleUrlHandlerMapping

1. SpringBoot自动配置欢迎页的 WelcomePageHandlerMapping 。访问/ 能访问到index.html；
2. SpringBoot自动配置了默认的 RequestMappingHandlerMapping
3. 请求进来，挨个尝试所有的HandlerMapping看是否有请求信息
   * 如果有就找到这个请求对应的handler
   * 如果没有就是下一个 HandlerMapping
4. 我们需要一些自定义的映射处理，我们也可以自己给容器中自定义HandlerMapping

## 3.4 SpringMvc常用处理web请求注解

### 3.4.1 web请求传参使用注解

* @PathVariable 获取url路径变量上的参数，也可通过Map<String,String>取值
* @RequestHeader 获取请求头内容，可单独获取某个参数或通过Map<String,String>取值
* @RequestParam 获取请求参数
* @CookieValue 获取某个Cookie的值(_ga)，或通过Cookie类型获取
* @RequestBody 获取post请求的请求体信息
* @RequestAttribute 获取request域中属性，通过用于页面转发时保存数据(注:请求方法上需加上 @ResponseBody注解)
* @MatrixVariable
  * /cars/{path}?xxx=xxx&a=cc  queryString 查询字符串 @RequestParam
  * /cars/sell;low=34;brand=byd,audi,yd
  * /cars/sell;low=34;brand=byd;brand=audi;brand=yd  使用矩阵变量重写url路径
  * 矩阵变量需要在springBoot中手动开启
  * 根据RFC3986的规范，矩阵变量应当绑定在路径变量中
  * 若是有多个矩阵变量，应当使用英文符号;进行分隔
  * 若是一个矩阵变量有多个值，应当使用英文符号,进行分隔，或之命名多个重复的key即可
* @ModelAttribute

<details>
  <summary>代码示例</summary>

```java
    @RequestMapping("/hello")
    public String hello(@RequestParam("username") String name){
        return "hello:" + name;
    }

    @RequestMapping("/hello")
    public String hello(@RequestParam String name){
        return "hello:" + name;
    }

    // car/2/owner/zhangsan @PathVariable 获取url路径变量上的参数，参数也可存放在Map<String,String>中
    @GetMapping("/getCar/{id}/owner/{username}")
    public Map<String,Object> getCar(@PathVariable("id") Integer carId,
                                     @PathVariable("username") String name,
                                     @PathVariable Map<String,String> pv,
                                     @RequestHeader("host") String headerHost,
                                     @RequestHeader("Referer") String headerOrigin,
                                     @RequestHeader Map<String,String> headerMap,
                                     @RequestParam("id") Integer id,
                                     @RequestParam("ints") List<String> paramList,
                                     @RequestParam Map<String,String> paramMap,
                                     @CookieValue("_ga") String _ga,
                                     @CookieValue("_ga") Cookie cookie){
        Map<String,Object> map = new HashMap<>();
        map.put("car1", "新车1-" + carId + "|车主-" + name);
        map.put("car1-copy", "新车1copy-" + pv.get("id") + "|车主-" + pv.get("username"));
        map.put("header", headerMap);
        map.put("param",paramMap);
        map.put("cookie", _ga);
        map.put("cookie2", cookie.getName() + ":" + cookie.getValue());
        return map;
    }

    @PostMapping("/postCar/{id}/owner/{username}")
    public Map<String,Object> postCar(@PathVariable("id") Integer carId,
                                      @PathVariable("username") String name,
                                      @PathVariable Map<String,String> pv,
                                      @RequestParam("userName") Integer userName,
                                      @RequestParam("email") List<String> email,
                                      @RequestBody String requestBody){
        Map<String,Object> map = new HashMap<>();
        map.put("car1", "新车1-" + carId + "|车主-" + name);
        map.put("car1-copy", "新车1copy-" + pv.get("id") + "|车主-" + pv.get("username"));
        map.put("body", requestBody);
        //@RequestAttribute String requestAttribute
        //map.put("attribute", requestAttribute);
        return map;
    }

@Controller
//@RestController
public class GotoController {

    private Enumeration<String> attributeNames;

    @GetMapping("/goto")
    public String gotoCar(HttpServletRequest request){
        //设置请求域request属性
        request.setAttribute("msg","success...");
        request.setAttribute("code","0");
        //转发到 gotoSuccess请求
        return "forward:/gotoSuccess";
    }
    @ResponseBody
    @GetMapping("/gotoSuccess")
    public Map<String,Object> successCar(@RequestAttribute("msg") String msg,
                                         HttpServletRequest request){
        Map<String,Object> map = new HashMap<>();
        //请求属性，获取request域中属性，通过用于页面转发时保存数据
        map.put("msg", msg);
        map.put("code", request.getAttribute("code"));
        return map;
    }
}

    @Bean
    public WebMvcConfigurer webMvcConfigurer(){
        WebMvcConfigurer webMvcConfigurer = new WebMvcConfigurer() {
            @Override
            public void configurePathMatch(PathMatchConfigurer configurer) {
                UrlPathHelper urlPathHelper = new UrlPathHelper();
                //设置不移除 ;后面的内容，使矩阵变量注解可用
                urlPathHelper.setAlwaysUseFullPath(false);
                configurer.setUrlPathHelper(urlPathHelper);
            }
        };
        return webMvcConfigurer;
    }
  
    // /cars/sell;low=34;brand=byd,audi,yd
    //SpringBoot 默认禁用了矩阵变量注解功能
    // 手动开启: SpringBoot mvc配置中对于url请求路径的处理，UrlPathHelper进行解析
    // removeSemicolonContent 配置对于url路径中 ; 分号后内容移除，故矩阵变量注解不生效
    // 矩阵变量必须用url 路径变量才能生效
    @GetMapping("/matrixCar/{path}")
    public Map<String,Object> matrixCar(@MatrixVariable("low") Integer low,
                                        @MatrixVariable("brand") List<String> brands,
                                        @PathVariable("path") String path){
        Map<String,Object> map = new HashMap<>();
        map.put("low", low);
        map.put("brand", brands);
        map.put("path", path);
        return map;
    }
```
</details>

### 3.4.2 请求响应源码分析 <a id="3.4.2"></a>

<details>
  <summary>DispatcherServlet</summary>

```java
public class DispatcherServlet extends FrameworkServlet {
    ...
    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpServletRequest processedRequest = request;
        //处理程序执行链，由处理程序对象和任何处理程序拦截器组成。由HandlerMapping的HandlerMapping返回
        HandlerExecutionChain mappedHandler = null;
        boolean multipartRequestParsed = false;

        WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);

        try {
            ModelAndView mv = null;
            Exception dispatchException = null;

            try {
                //检测是否是文件请求
                processedRequest = checkMultipart(request);
                //判断request 是否被改变，是则为文件上传请求
                multipartRequestParsed = (processedRequest != request);

                // Determine handler for the current request. 决定当前请求由哪个handler(Controller) 处理
                // HandlerExecutionChain 中也包含拦截器
                mappedHandler = getHandler(processedRequest);
                // HandlerMapping 处理器映射
                if (mappedHandler == null) {
                    noHandlerFound(processedRequest, response);
                    return;
                }

                // Determine handler adapter for the current request. 确定当前请求的处理程序适配器。
                HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());

                // Process last-modified header, if supported by the handler. 如果处理程序支持，则处理最后修改的标头。
                String method = request.getMethod();
                boolean isGet = HttpMethod.GET.matches(method);
                if (isGet || HttpMethod.HEAD.matches(method)) {
                    long lastModified = ha.getLastModified(request, mappedHandler.getHandler());
                    if (new ServletWebRequest(request, response).checkNotModified(lastModified) && isGet) {
                        return;
                    }
                }

                //遍历执行 所有已注册拦截器的preHandle方法看是否拦截
                if (!mappedHandler.applyPreHandle(processedRequest, response)) {
                    return;
                }

                // Actually invoke the handler. 真正的handler调用处理程序(RequestMappingHandlerAdapter)
                mv = ha.handle(processedRequest, response, mappedHandler.getHandler());

                if (asyncManager.isConcurrentHandlingStarted()) {
                  return;
                }
                //如果没有view name则添加默认的view name
                applyDefaultViewName(processedRequest, mv);
                //倒序遍历执行已注册拦截器的postHandle()方法。
                mappedHandler.applyPostHandle(processedRequest, response, mv);
            }
            //处理程序选择和处理程序调用的结果，它要么是ModelAndView，要么是要解析为ModelAndView的异常。
            //处理派发请求
            processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);
        }
        catch (Exception ex) {
            //执行异常，触发已注册拦截器的afterCompletion()方法
            triggerAfterCompletion(processedRequest, response, mappedHandler, ex);
        }
        catch (Throwable err) {
            //执行异常，触发已注册拦截器的afterCompletion()方法
            triggerAfterCompletion(processedRequest, response, mappedHandler,
                    new NestedServletException("Handler processing failed", err));
        }
        finally {
            if (asyncManager.isConcurrentHandlingStarted()) {
                // Instead of postHandle and afterCompletion
                if (mappedHandler != null) {
                    //倒序遍历执行已注册拦截器的afterCompletion()方法
                    mappedHandler.applyAfterConcurrentHandlingStarted(processedRequest, response);
                }
            }
            else {
                // Clean up any resources used by a multipart request.
                if (multipartRequestParsed) {
                    cleanupMultipart(processedRequest);
                }
            }
        }
    }

  private void processDispatchResult(HttpServletRequest request, HttpServletResponse response,
                                     @Nullable HandlerExecutionChain mappedHandler, @Nullable ModelAndView mv,
                                     @Nullable Exception exception) throws Exception {
    boolean errorView = false;

    //判断之前解析过程是否有错误
    if (exception != null) {
      if (exception instanceof ModelAndViewDefiningException) {
        logger.debug("ModelAndViewDefiningException encountered", exception);
        mv = ((ModelAndViewDefiningException) exception).getModelAndView();
      }
      else {
        Object handler = (mappedHandler != null ? mappedHandler.getHandler() : null);
        //处理异常，通过注册的HandlerExceptionResolvers确定错误ModelAndView
        mv = processHandlerException(request, response, handler, exception);
        errorView = (mv != null);
      }
    }

    // Did the handler return a view to render? 处理程序是否返回要渲染的视图?
    //img10
    if (mv != null && !mv.wasCleared()) {
      //渲染View。呈现给定的ModelAndView。 这是处理请求的最后一个阶段。它可能涉及按名称解析视图。
      render(mv, request, response);
      if (errorView) {
        WebUtils.clearErrorRequestAttributes(request);
      }
    }
    else {
      if (logger.isTraceEnabled()) {
        logger.trace("No view rendering, null ModelAndView returned.");
      }
    }

    if (WebAsyncUtils.getAsyncManager(request).isConcurrentHandlingStarted()) {
      // Concurrent handling started during a forward
      return;
    }

    if (mappedHandler != null) {
      // Exception (if any) is already handled..
      mappedHandler.triggerAfterCompletion(request, response, null);
    }
  }

  /**
   * Render the given ModelAndView.
   * <p>This is the last stage in handling a request. It may involve resolving the view by name.
   * @param mv the ModelAndView to render
   * @param request current HTTP servlet request
   * @param response current HTTP servlet response
   * @throws ServletException if view is missing or cannot be resolved
   * @throws Exception if there's a problem rendering the view
   * 渲染给定的ModelAndView。 这是处理请求的最后一个阶段。它可能涉及按名称解析视图。
   */
  protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
    // Determine locale for request and apply it to the response.
    // 确定请求的区域设置并将其应用于响应。  处理国际化
    Locale locale =
            (this.localeResolver != null ? this.localeResolver.resolveLocale(request) : request.getLocale());
    response.setLocale(locale);

    View view;
    //获取view name
    String viewName = mv.getViewName();
    if (viewName != null) {
      // We need to resolve the view name. 解析view
      view = resolveViewName(viewName, mv.getModelInternal(), locale, request);
      if (view == null) {
        throw new ServletException("Could not resolve view with name '" + mv.getViewName() +
                "' in servlet with name '" + getServletName() + "'");
      }
    }
    else {
      // No need to lookup: the ModelAndView object contains the actual View object.
      view = mv.getView();
      if (view == null) {
        throw new ServletException("ModelAndView [" + mv + "] neither contains a view name nor a " +
                "View object in servlet with name '" + getServletName() + "'");
      }
    }

    // Delegate to the View object for rendering.
    if (logger.isTraceEnabled()) {
      logger.trace("Rendering view [" + view + "] ");
    }
    try {
      if (mv.getStatus() != null) {
        request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, mv.getStatus());
        response.setStatus(mv.getStatus().value());
      }
      //视图执行渲染
      view.render(mv.getModelInternal(), request, response);
    }
    catch (Exception ex) {
      if (logger.isDebugEnabled()) {
        logger.debug("Error rendering view [" + view + "]", ex);
      }
      throw ex;
    }
  }

  /**
   * Resolve the given view name into a View object (to be rendered).
   * <p>The default implementations asks all ViewResolvers of this dispatcher.
   * Can be overridden for custom resolution strategies, potentially based on
   * specific model attributes or request parameters.
   * @param viewName the name of the view to resolve
   * @param model the model to be passed to the view
   * @param locale the current locale
   * @param request current HTTP servlet request
   * @return the View object, or {@code null} if none found
   * @throws Exception if the view cannot be resolved
   * (typically in case of problems creating an actual View object)
   * @see ViewResolver#resolveViewName
   * 
   * 将给定的视图名称解析为一个视图对象(要呈现)。 
   * 默认的实现询问这个调度程序的所有ViewResolvers。可以覆盖自定义解析策略，可能基于特定的模型属性或请求参数
   */
  @Nullable
  protected View resolveViewName(String viewName, @Nullable Map<String, Object> model,
                                 Locale locale, HttpServletRequest request) throws Exception {

    if (this.viewResolvers != null) {
      for (ViewResolver viewResolver : this.viewResolvers) {
          //视图解析器解析
        View view = viewResolver.resolveViewName(viewName, locale);
        if (view != null) {
          return view;
        }
      }
    }
    return null;
  }
  
}
```
</details>

<details>
  <summary>ContentNegotiatingViewResolver</summary>

```java
public class ContentNegotiatingViewResolver extends WebApplicationObjectSupport
        implements ViewResolver, Ordered, InitializingBean {
    ...
    @Override
    @Nullable
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        //获取请求域属性
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        Assert.state(attrs instanceof ServletRequestAttributes, "No current ServletRequestAttributes");
        List<MediaType> requestedMediaTypes = getMediaTypes(((ServletRequestAttributes) attrs).getRequest());
        if (requestedMediaTypes != null) {
            //获取匹配到的所有视图
            List<View> candidateViews = getCandidateViews(viewName, locale, requestedMediaTypes);
            //筛选最佳视图
            View bestView = getBestView(candidateViews, requestedMediaTypes, attrs);
            if (bestView != null) {
                return bestView;
            }
        }
    
        String mediaTypeInfo = logger.isDebugEnabled() && requestedMediaTypes != null ?
                " given " + requestedMediaTypes.toString() : "";
    
        if (this.useNotAcceptableStatusCode) {
            if (logger.isDebugEnabled()) {
              logger.debug("Using 406 NOT_ACCEPTABLE" + mediaTypeInfo);
            }
            return NOT_ACCEPTABLE_VIEW;
        } else {
            logger.debug("View remains unresolved" + mediaTypeInfo);
            return null;
        }
    }

  private List<View> getCandidateViews(String viewName, Locale locale, List<MediaType> requestedMediaTypes)
          throws Exception {

    List<View> candidateViews = new ArrayList<>();
    if (this.viewResolvers != null) {
      Assert.state(this.contentNegotiationManager != null, "No ContentNegotiationManager set");
      for (ViewResolver viewResolver : this.viewResolvers) {
          //依旧遍历除ContentNegotiatingViewResolver 以外所有viewResolver 看哪个能解析
          //ThymeleafViewResolver.resolveViewName()
        View view = viewResolver.resolveViewName(viewName, locale);
        if (view != null) {
            //加入候选view 中
          candidateViews.add(view);
        }
        //内容协商过程匹配
        for (MediaType requestedMediaType : requestedMediaTypes) {
          List<String> extensions = this.contentNegotiationManager.resolveFileExtensions(requestedMediaType);
          for (String extension : extensions) {
            String viewNameWithExtension = viewName + '.' + extension;
            view = viewResolver.resolveViewName(viewNameWithExtension, locale);
            if (view != null) {
              candidateViews.add(view);
            }
          }
        }
      }
    }
    if (!CollectionUtils.isEmpty(this.defaultViews)) {
      candidateViews.addAll(this.defaultViews);
    }
    return candidateViews;
  }

  @Nullable
  private View getBestView(List<View> candidateViews, List<MediaType> requestedMediaTypes, RequestAttributes attrs) {
      for (View candidateView : candidateViews) {
          if (candidateView instanceof SmartView) {
              SmartView smartView = (SmartView) candidateView;
              if (smartView.isRedirectView()) {
                    return candidateView;
              }
          }
      }
      for (MediaType mediaType : requestedMediaTypes) {
          for (View candidateView : candidateViews) {
            if (StringUtils.hasText(candidateView.getContentType())) {
                MediaType candidateContentType = MediaType.parseMediaType(candidateView.getContentType());
                if (mediaType.isCompatibleWith(candidateContentType)) {
                      mediaType = mediaType.removeQualityValue();
                      if (logger.isDebugEnabled()) {
                          logger.debug("Selected '" + mediaType + "' given " + requestedMediaTypes);
                      }
                      attrs.setAttribute(View.SELECTED_CONTENT_TYPE, mediaType, RequestAttributes.SCOPE_REQUEST);
                      return candidateView;
                  }
              }
          }
      }
      return null;
  }
}

public class ThymeleafViewResolver
        extends AbstractCachingViewResolver
        implements Ordered {

  @Override
  @Nullable
  public View resolveViewName(String viewName, Locale locale) throws Exception {
      //判断是否有缓存
    if (!isCache()) {
      return createView(viewName, locale);
    }
    else {
      Object cacheKey = getCacheKey(viewName, locale);
      View view = this.viewAccessCache.get(cacheKey);
      if (view == null) {
        synchronized (this.viewCreationCache) {
          view = this.viewCreationCache.get(cacheKey);
          if (view == null) {
            // Ask the subclass to create the View object.
            view = createView(viewName, locale);
            if (view == null && this.cacheUnresolved) {
              view = UNRESOLVED_VIEW;
            }
            if (view != null && this.cacheFilter.filter(view, viewName, locale)) {
              this.viewAccessCache.put(cacheKey, view);
              this.viewCreationCache.put(cacheKey, view);
            }
          }
        }
      }
      else {
        if (logger.isTraceEnabled()) {
          logger.trace(formatKey(cacheKey) + "served from cache");
        }
      }
      return (view != UNRESOLVED_VIEW ? view : null);
    }
  }

  @Override
  protected View createView(final String viewName, final Locale locale) throws Exception {
    // First possible call to check "viewNames": before processing redirects and forwards
    if (!this.alwaysProcessRedirectAndForward && !canHandle(viewName, locale)) {
      vrlogger.trace("[THYMELEAF] View \"{}\" cannot be handled by ThymeleafViewResolver. Passing on to the next resolver in the chain.", viewName);
      return null;
    }
    // Process redirects (HTTP redirects) 进程重定向(HTTP重定向)
    //判断是否是 重定向 redirect:  是则返回RedirectView
    if (viewName.startsWith(REDIRECT_URL_PREFIX)) {
      vrlogger.trace("[THYMELEAF] View \"{}\" is a redirect, and will not be handled directly by ThymeleafViewResolver.", viewName);
      final String redirectUrl = viewName.substring(REDIRECT_URL_PREFIX.length(), viewName.length());
      final RedirectView view = new RedirectView(redirectUrl, isRedirectContextRelative(), isRedirectHttp10Compatible());
      return (View) getApplicationContext().getAutowireCapableBeanFactory().initializeBean(view, REDIRECT_URL_PREFIX);
    }
    // Process forwards (to JSP resources)
    //判断是否是转发 forward:   是则返回InternalResourceView
    if (viewName.startsWith(FORWARD_URL_PREFIX)) {
      // The "forward:" prefix will actually create a Servlet/JSP view, and that's precisely its aim per the Spring
      // documentation. See http://docs.spring.io/spring-framework/docs/4.2.4.RELEASE/spring-framework-reference/html/mvc.html#mvc-redirecting-forward-prefix
      vrlogger.trace("[THYMELEAF] View \"{}\" is a forward, and will not be handled directly by ThymeleafViewResolver.", viewName);
      final String forwardUrl = viewName.substring(FORWARD_URL_PREFIX.length(), viewName.length());
      return new InternalResourceView(forwardUrl);
    }
    // Second possible call to check "viewNames": after processing redirects and forwards
    if (this.alwaysProcessRedirectAndForward && !canHandle(viewName, locale)) {
      vrlogger.trace("[THYMELEAF] View \"{}\" cannot be handled by ThymeleafViewResolver. Passing on to the next resolver in the chain.", viewName);
      return null;
    }
    vrlogger.trace("[THYMELEAF] View {} will be handled by ThymeleafViewResolver and a " +
            "{} instance will be created for it", viewName, getViewClass().getSimpleName());
    return loadView(viewName, locale);
  }

}
  
```
</details>

<details>
  <summary>AbstractView</summary>

```java
public abstract class AbstractView extends WebApplicationObjectSupport implements View, BeanNameAware {
  ...
    /**
   * Prepares the view given the specified model, merging it with static
   * attributes and a RequestContext attribute, if necessary.
   * Delegates to renderMergedOutputModel for the actual rendering.
   * @see #renderMergedOutputModel
   * 根据指定的模型准备视图，并将其与静态属性和RequestContext属性合并(如果需要的话)。委托renderMergedOutputModel进行实际渲染。
   */
  @Override
  public void render(@Nullable Map<String, ?> model, HttpServletRequest request,
                     HttpServletResponse response) throws Exception {
      if (logger.isDebugEnabled()) {
        logger.debug("View " + formatViewName() +
                ", model " + (model != null ? model : Collections.emptyMap()) +
                (this.staticAttributes.isEmpty() ? "" : ", static attributes " + this.staticAttributes));
      }
      //创建合并输出模型(合并model中的数据)
      Map<String, Object> mergedModel = createMergedOutputModel(model, request, response);
      prepareResponse(request, response);
      //子类view实现该方法，合并输出模型
      renderMergedOutputModel(mergedModel, getRequestToExpose(request), response);
  }

  /**
   * Creates a combined output Map (never {@code null}) that includes dynamic values and static attributes.
   * Dynamic values take precedence over static attributes.
   * 创建包含动态值和静态属性的组合输出Map(从不为{@code null})。动态值优先于静态属性。
   */
  protected Map<String, Object> createMergedOutputModel(@Nullable Map<String, ?> model,
                                                        HttpServletRequest request, HttpServletResponse response) {
      @SuppressWarnings("unchecked")
      Map<String, Object> pathVars = (this.exposePathVariables ?
              (Map<String, Object>) request.getAttribute(View.PATH_VARIABLES) : null);
  
      // Consolidate static and dynamic model attributes.
      int size = this.staticAttributes.size();
      size += (model != null ? model.size() : 0);
      size += (pathVars != null ? pathVars.size() : 0);
  
      Map<String, Object> mergedModel = CollectionUtils.newLinkedHashMap(size);
      mergedModel.putAll(this.staticAttributes);
      if (pathVars != null) {
        mergedModel.putAll(pathVars);
      }
      if (model != null) {
          //将model中的数据合并
           mergedModel.putAll(model);
      }
  
      // Expose RequestContext?
      if (this.requestContextAttribute != null) {
        mergedModel.put(this.requestContextAttribute, createRequestContext(request, response, mergedModel));
      }
  
      return mergedModel;
  }

/**
 * View that redirects to an absolute, context relative, or current request
 * relative URL. The URL may be a URI template in which case the URI template
 * variables will be replaced with values available in the model. By default
 * all primitive model attributes (or collections thereof) are exposed as HTTP
 * query parameters (assuming they've not been used as URI template variables),
 * but this behavior can be changed by overriding the
 * {@link #isEligibleProperty(String, Object)} method.
 *
 * 视图，重定向到绝对URL、上下文相对URL或当前请求相对URL。URL可以是一个URI模板，在这种情况下，
 * URI模板变量将被替换为模型中可用的值。默认情况下，
 * 所有基本模型属性(或其集合)都被公开为HTTP查询参数(假设它们没有被用作URI模板变量)，
 * 但是这种行为可以通过覆盖{@link isEligibleProperty(String, Object)}方法来改变。
 * 
 * <p>A URL for this view is supposed to be an HTTP redirect URL, i.e.
 * suitable for HttpServletResponse's {@code sendRedirect} method, which
 * is what actually does the redirect if the HTTP 1.0 flag is on, or via sending
 * back an HTTP 303 code - if the HTTP 1.0 compatibility flag is off.
 * 
 * 这个视图的URL应该是一个HTTP重定向URL，即适合HttpServletResponse的{@code sendRedirect}方法，
 * 如果HTTP 1.0标志是打开的，或者如果HTTP 1.0兼容性标志是关闭的，则通过发回一个HTTP 303代码进行重定向。
 * 
 * <p>Note that while the default value for the "contextRelative" flag is off,
 * you will probably want to almost always set it to true. With the flag off,
 * URLs starting with "/" are considered relative to the web server root, while
 * with the flag on, they are considered relative to the web application root.
 * Since most web applications will never know or care what their context path
 * actually is, they are much better off setting this flag to true, and submitting
 * paths which are to be considered relative to the web application root.
 *
 * 注意，虽然“contextRelative”标志的默认值是关闭的，但你可能总是想把它设置为true。
 * 关闭标记时，以“”开头的url被认为是相对于web服务器根，而打开标记时，
 * 它们被认为是相对于web应用程序根。由于大多数web应用程序永远不会知道或关心它们的上下文路径实际上是什么，
 * 所以最好将这个标志设置为true，并提交相对于web应用程序根目录的路径。
 * 
 * <p><b>NOTE when using this redirect view in a Portlet environment:</b> Make sure
 * that your controller respects the Portlet {@code sendRedirect} constraints.
 * 
 * 说明在Portlet环境中使用此重定向视图时:请确保您的控制器遵守Portlet {@code sendRedirect}约束。
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Colin Sampaleanu
 * @author Sam Brannen
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @see #setContextRelative
 * @see #setHttp10Compatible
 * @see #setExposeModelAttributes
 * @see javax.servlet.http.HttpServletResponse#sendRedirect
 */
public class RedirectView extends AbstractUrlBasedView implements SmartView {
    
  /**
   * RedirectView 实现
   * Convert model to request parameters and redirect to the given URL.
   * 将模型转换为请求参数并重定向到给定的URL。
   * @see #appendQueryProperties
   * @see #sendRedirect
   */
  @Override
  protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
                                         HttpServletResponse response) throws IOException {
    // 创建目标URL，首先检查重定向字符串是否是URI模板，然后使用给定的模型展开它，
    // 然后可选地将简单类型模型属性作为查询字符串参数
    // 获取目标url 地址
    String targetUrl = createTargetUrl(model, request);
    targetUrl = updateTargetUrl(targetUrl, model, request, response);

    // Save flash attributes
    // 保存flash属性
    RequestContextUtils.saveOutputFlashMap(targetUrl, request, response);

    // Redirect 重定向
    sendRedirect(request, response, targetUrl, this.http10Compatible);
  }

  /**
   * Create the target URL by checking if the redirect string is a URI template first,
   * expanding it with the given model, and then optionally appending simple type model
   * attributes as query String parameters.
   * 创建目标URL，首先检查重定向字符串是否是URI模板，
   * 然后使用给定的模型展开它，然后可选地将简单类型模型属性作为查询字符串参数。
   */
  protected final String createTargetUrl(Map<String, Object> model, HttpServletRequest request)
          throws UnsupportedEncodingException {

    // Prepare target URL.
    StringBuilder targetUrl = new StringBuilder();
    String url = getUrl();
    Assert.state(url != null, "'url' not set");

    if (this.contextRelative && getUrl().startsWith("/")) {
      // Do not apply context path to relative URLs.
      targetUrl.append(getContextPath(request));
    }
    targetUrl.append(getUrl());

    //设置编码
    String enc = this.encodingScheme;
    if (enc == null) {
      enc = request.getCharacterEncoding();
    }
    if (enc == null) {
      enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
    }

    if (this.expandUriTemplateVariables && StringUtils.hasText(targetUrl)) {
      Map<String, String> variables = getCurrentRequestUriVariables(request);
      targetUrl = replaceUriTemplateVariables(targetUrl.toString(), model, variables, enc);
    }
    if (isPropagateQueryProperties()) {
      appendCurrentQueryParams(targetUrl, request);
    }
    //添加重定向数据  xxx.html?key=value&key1=value1
    if (this.exposeModelAttributes) {
      appendQueryProperties(targetUrl, model, enc);
    }

    return targetUrl.toString();
  }

  /**
   * Send a redirect back to the HTTP client.
   * 发送一个重定向回HTTP客户端。
   * @param request current HTTP request (allows for reacting to request method)
   * @param response current HTTP response (for sending response headers)
   * @param targetUrl the target URL to redirect to
   * @param http10Compatible whether to stay compatible with HTTP 1.0 clients
   * @throws IOException if thrown by response methods
   */
  protected void sendRedirect(HttpServletRequest request, HttpServletResponse response,
                              String targetUrl, boolean http10Compatible) throws IOException {
    //url 编码
    String encodedURL = (isRemoteHost(targetUrl) ? targetUrl : response.encodeRedirectURL(targetUrl));
    if (http10Compatible) {
      HttpStatus attributeStatusCode = (HttpStatus) request.getAttribute(View.RESPONSE_STATUS_ATTRIBUTE);
      if (this.statusCode != null) {
        response.setStatus(this.statusCode.value());
        response.setHeader("Location", encodedURL);
      }
      else if (attributeStatusCode != null) {
        response.setStatus(attributeStatusCode.value());
        response.setHeader("Location", encodedURL);
      }
      else {
        // Send status code 302 by default. 默认情况下发送状态码302。
        //发送重定向码302 返回客户端
        response.sendRedirect(encodedURL);
      }
    }
    else {
      HttpStatus statusCode = getHttp11StatusCode(request, response, targetUrl);
      response.setStatus(statusCode.value());
      response.setHeader("Location", encodedURL);
    }
  }
}

  
  /**
   * InternalResourceView 实现
   * Render the internal resource given the specified model.
   * This includes setting the model as request attributes.
   * 根据指定的模型呈现内部资源。这包括将模型设置为请求属性。
   */
  @Override
  protected void renderMergedOutputModel(
          Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

      // Expose the model object as request attributes. 将模型对象公开为请求属性(遍历model中的数据填入request请求域中)
      exposeModelAsRequestAttributes(model, request);
  
      // Expose helpers as request attributes, if any. 将helper作为请求属性公开(如果有的话)。
      exposeHelpers(request);
  
      // Determine the path for the request dispatcher. 确定请求分派器的路径。
      String dispatcherPath = prepareForRendering(request, response);
  
      // Obtain a RequestDispatcher for the target resource (typically a JSP).
      // 获取目标资源(通常是JSP)的RequestDispatcher。
      RequestDispatcher rd = getRequestDispatcher(request, dispatcherPath);
      if (rd == null) {
        throw new ServletException("Could not get RequestDispatcher for [" + getUrl() +
                "]: Check that the corresponding file exists within your web application archive!");
      }
  
      // If already included or response already committed, perform include, else forward.
      // 如果已经包含或响应已经提交，则执行包含，否则转发。
      if (useInclude(request, response)) {
        response.setContentType(getContentType());
        if (logger.isDebugEnabled()) {
          logger.debug("Including [" + getUrl() + "]");
        }
        rd.include(request, response);
      }
  
      else {
          // Note: The forwarded resource is supposed to determine the content type itself.
          //注意:转发的资源应该决定内容类型本身。
        if (logger.isDebugEnabled()) {
          logger.debug("Forwarding to [" + getUrl() + "]");
        }
        //转发请求
        rd.forward(request, response);
      }
  }

  /**
   * Expose the model objects in the given map as request attributes.
   * Names will be taken from the model Map.
   * This method is suitable for all resources reachable by {@link javax.servlet.RequestDispatcher}.
   * @param model a Map of model objects to expose
   * @param request current HTTP request
   * 将给定映射中的模型对象公开为请求属性。名称将取自模型Map。
   * 这个方法适用于所有可以通过{@link javax.servlet.RequestDispatcher}访问的资源。
   */
  protected void exposeModelAsRequestAttributes(Map<String, Object> model,
                                                HttpServletRequest request) throws Exception {
      //遍历model中的数据设置请求域属性  
      model.forEach((name, value) -> {
          if (value != null) {
            request.setAttribute(name, value);
          }
          else {
            request.removeAttribute(name);
          }
      });
  }
}
	
```
</details>

<details>
  <summary>RequestMappingHandlerAdapter</summary>

<a id="RequestMappingHandlerAdapter"></a>
```java
public class RequestMappingHandlerAdapter extends AbstractHandlerMethodAdapter
        implements BeanFactoryAware, InitializingBean {
    ...
    //适配器处理请求方法
    @Override
    protected ModelAndView handleInternal(HttpServletRequest request,
                                          HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
        ModelAndView mav;
        checkRequest(request);

        // Execute invokeHandlerMethod in synchronized block if required. 如果需要，在同步块中执行invokeHandlerMethod。
        if (this.synchronizeOnSession) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Object mutex = WebUtils.getSessionMutex(session);
                synchronized (mutex) {
                    mav = invokeHandlerMethod(request, response, handlerMethod);
                }
            }
            else {
                // No HttpSession available -> no mutex necessary 没有HttpSession可用->不需要互斥锁
                mav = invokeHandlerMethod(request, response, handlerMethod);
            }
        }
        else {
            // No synchronization on session demanded at all... 完全不需要对会话进行同步…
            //执行RequestMapping 处理方法
            mav = invokeHandlerMethod(request, response, handlerMethod);
        }

        if (!response.containsHeader(HEADER_CACHE_CONTROL)) {
            if (getSessionAttributesHandler(handlerMethod).hasSessionAttributes()) {
                applyCacheSeconds(response, this.cacheSecondsForSessionAttributeHandlers);
            }
            else {
                prepareResponse(response);
            }
        }

        return mav;
    }

    /**
     * Invoke the {@link RequestMapping} handler method preparing a {@link ModelAndView}
     * if view resolution is required.  如果需要视图解析，则调用准备ModelAndView的RequestMapping处理程序方法。
     * @since 4.2
     * @see #createInvocableHandlerMethod(HandlerMethod)
     */
    @Nullable
    protected ModelAndView invokeHandlerMethod(HttpServletRequest request,
                                               HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {

        ServletWebRequest webRequest = new ServletWebRequest(request, response);
        try {
            WebDataBinderFactory binderFactory = getDataBinderFactory(handlerMethod);
            ModelFactory modelFactory = getModelFactory(handlerMethod, binderFactory);

            ServletInvocableHandlerMethod invocableMethod = createInvocableHandlerMethod(handlerMethod);
            //为重新封装后的com.example.running.controller.RestFulController#putUser() 设置参数解析器
            //将确定将要执行的目标方法的每一个参数的值是什么
            if (this.argumentResolvers != null) {
                invocableMethod.setHandlerMethodArgumentResolvers(this.argumentResolvers);
            }
            //为请求设置返回处理器
            //将确定目标方法的返回值类型
            //img13
            if (this.returnValueHandlers != null) {
                invocableMethod.setHandlerMethodReturnValueHandlers(this.returnValueHandlers);
            }
            invocableMethod.setDataBinderFactory(binderFactory);
            invocableMethod.setParameterNameDiscoverer(this.parameterNameDiscoverer);

            ModelAndViewContainer mavContainer = new ModelAndViewContainer();
            mavContainer.addAllAttributes(RequestContextUtils.getInputFlashMap(request));
            modelFactory.initModel(webRequest, mavContainer, invocableMethod);
            mavContainer.setIgnoreDefaultModelOnRedirect(this.ignoreDefaultModelOnRedirect);

            AsyncWebRequest asyncWebRequest = WebAsyncUtils.createAsyncWebRequest(request, response);
            asyncWebRequest.setTimeout(this.asyncRequestTimeout);

            WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);
            asyncManager.setTaskExecutor(this.taskExecutor);
            asyncManager.setAsyncWebRequest(asyncWebRequest);
            asyncManager.registerCallableInterceptors(this.callableInterceptors);
            asyncManager.registerDeferredResultInterceptors(this.deferredResultInterceptors);

            if (asyncManager.hasConcurrentResult()) {
                Object result = asyncManager.getConcurrentResult();
                mavContainer = (ModelAndViewContainer) asyncManager.getConcurrentResultContext()[0];
                asyncManager.clearConcurrentResult();
                LogFormatUtils.traceDebug(logger, traceOn -> {
                    String formatted = LogFormatUtils.formatValue(result, !traceOn);
                    return "Resume with async result [" + formatted + "]";
                });
                invocableMethod = invocableMethod.wrapConcurrentResult(result);
            }

            //执行解析方法
            invocableMethod.invokeAndHandle(webRequest, mavContainer);
            if (asyncManager.isConcurrentHandlingStarted()) {
                return null;
            }
            //处理放在 ModelAndViewContainer 数据视图容器内的数据
            return getModelAndView(mavContainer, modelFactory, webRequest);
        }
        finally {
            webRequest.requestCompleted();
        }
    }

  @Nullable
  private ModelAndView getModelAndView(ModelAndViewContainer mavContainer,
                                       ModelFactory modelFactory, NativeWebRequest webRequest) throws Exception {
    //将列出为@SessionAttributes的模型属性提升到会话。在必要时添加BindingResult属性
    modelFactory.updateModel(webRequest, mavContainer);
    if (mavContainer.isRequestHandled()) {
      return null;
    }
    //img9 获取重定向数据 model
    ModelMap model = mavContainer.getModel();
    //转化为ModelAndView
    ModelAndView mav = new ModelAndView(mavContainer.getViewName(), model, mavContainer.getStatus());
    if (!mavContainer.isViewReference()) {
      mav.setView((View) mavContainer.getView());
    }
    //判断是否重定向
    if (model instanceof RedirectAttributes) {
      Map<String, ?> flashAttributes = ((RedirectAttributes) model).getFlashAttributes();
      HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
      if (request != null) {
        RequestContextUtils.getOutputFlashMap(request).putAll(flashAttributes);
      }
    }
    return mav;
  }
  
}
```
</details>

<details>
  <summary>ServletInvocableHandlerMethod</summary>

<a id="ServletInvocableHandlerMethod"></a>
```java
public class ServletInvocableHandlerMethod extends InvocableHandlerMethod {
    ...
    /**
     * Invoke the method and handle the return value through one of the
     * configured {@link HandlerMethodReturnValueHandler HandlerMethodReturnValueHandlers}. 
     * 调用该方法并通过配置的HandlerMethodReturnValueHandlers 之一处理返回值。
     * @param webRequest the current request
     * @param mavContainer the ModelAndViewContainer for this request
     * @param providedArgs "given" arguments matched by type (not resolved)
     */
    public void invokeAndHandle(ServletWebRequest webRequest, ModelAndViewContainer mavContainer,
                                Object... providedArgs) throws Exception {
        //执行目标请求方法并获得返回值
        Object returnValue = invokeForRequest(webRequest, mavContainer, providedArgs);
        //设置返回状态
        setResponseStatus(webRequest);

        if (returnValue == null) {
            if (isRequestNotModified(webRequest) || getResponseStatus() != null || mavContainer.isRequestHandled()) {
                disableContentCachingIfNecessary(webRequest);
                mavContainer.setRequestHandled(true);
                return;
            }
        }
        else if (StringUtils.hasText(getResponseStatusReason())) {
            mavContainer.setRequestHandled(true);
            return;
        }

        mavContainer.setRequestHandled(false);
        Assert.state(this.returnValueHandlers != null, "No return value handlers");
        try {
            //处理返回值
            this.returnValueHandlers.handleReturnValue(
                    returnValue, getReturnValueType(returnValue), mavContainer, webRequest);
        }
        catch (Exception ex) {
            if (logger.isTraceEnabled()) {
                logger.trace(formatErrorForReturnValue(returnValue), ex);
            }
            throw ex;
        }
    }

    @Nullable
    public Object invokeForRequest(NativeWebRequest request, @Nullable ModelAndViewContainer mavContainer,
                                   Object... providedArgs) throws Exception {

        Object[] args = getMethodArgumentValues(request, mavContainer, providedArgs);
        if (logger.isTraceEnabled()) {
            logger.trace("Arguments: " + Arrays.toString(args));
        }
        return doInvoke(args);
    }

    /**
     * Get the method argument values for the current request, checking the provided
     * argument values and falling back to the configured argument resolvers.
     * 获取当前请求的方法参数值，检查提供的参数值并退回到配置的参数解析器。
     * <p>The resulting array will be passed into {@link #doInvoke}.
     * @since 5.1.2
     */
    protected Object[] getMethodArgumentValues(NativeWebRequest request, @Nullable ModelAndViewContainer mavContainer,
                                               Object... providedArgs) throws Exception {
        //获取该请求所有请求参数
        //img7
        MethodParameter[] parameters = getMethodParameters();
        if (ObjectUtils.isEmpty(parameters)) {
            return EMPTY_ARGS;
        }

        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            MethodParameter parameter = parameters[i];
            parameter.initParameterNameDiscovery(this.parameterNameDiscoverer);
            args[i] = findProvidedArgument(parameter, providedArgs);
            if (args[i] != null) {
                continue;
            }
            //遍历所有参数解析器是否支持当前请求参数
            if (!this.resolvers.supportsParameter(parameter)) {
                throw new IllegalStateException(formatArgumentError(parameter, "No suitable resolver"));
            }
            try {
                //参数解析器 解析参数
                args[i] = this.resolvers.resolveArgument(parameter, mavContainer, request, this.dataBinderFactory);
            }
            catch (Exception ex) {
                // Leave stack trace for later, exception may actually be resolved and handled...
                if (logger.isDebugEnabled()) {
                    String exMsg = ex.getMessage();
                    if (exMsg != null && !exMsg.contains(parameter.getExecutable().toGenericString())) {
                        logger.debug(formatArgumentError(parameter, exMsg));
                    }
                }
                throw ex;
            }
        }
        //返回所有参数解析结果
        return args;
    }
}

    /**
     * Find a registered {@link HandlerMethodArgumentResolver} that supports
     * the given method parameter.
     * 找到一个注册的HandlerMethodArgumentResolver，它支持给定的方法参数。
     */
    @Nullable
    private HandlerMethodArgumentResolver getArgumentResolver(MethodParameter parameter) {
        HandlerMethodArgumentResolver result = this.argumentResolverCache.get(parameter);
        if (result == null) {
            //遍历所有参数解析器是否有支持该参数的
            for (HandlerMethodArgumentResolver resolver : this.argumentResolvers) {
                if (resolver.supportsParameter(parameter)) {
                    result = resolver;
                    //缓存该参数解析匹配结果
                    this.argumentResolverCache.put(parameter, result);
                    break;
                }
            }
        }
        return result;
    }

  /**
   * Iterate over registered {@link HandlerMethodReturnValueHandler HandlerMethodReturnValueHandlers} and invoke the one that supports it.
   * @throws IllegalStateException if no suitable {@link HandlerMethodReturnValueHandler} is found.
   * 迭代已注册的{@link HandlerMethodReturnValueHandler HandlerMethodReturnValueHandlers}并调用支持它的一个。
   * 如果没有找到合适的{@link HandlerMethodReturnValueHandler}， @抛出IllegalStateException。
   */
  @Override
  public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType,
                                ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
      HandlerMethodReturnValueHandler handler = selectHandler(returnValue, returnType);
      if (handler == null) {
          throw new IllegalArgumentException("Unknown return value type: " + returnType.getParameterType().getName());
      }
      handler.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
  }
  
  @Nullable
  private HandlerMethodReturnValueHandler selectHandler(@Nullable Object value, MethodParameter returnType) {
      boolean isAsyncValue = isAsyncReturnValue(value, returnType);
      for (HandlerMethodReturnValueHandler handler : this.returnValueHandlers) {
          if (isAsyncValue && !(handler instanceof AsyncHandlerMethodReturnValueHandler)) {
              continue;
          }
          if (handler.supportsReturnType(returnType)) {
              return handler;
          }
      }
      return null;
  }
  
```
</details>

<details>
  <summary>RequestParamMethodArgumentResolver</summary>

```java
//@RequestParam 注解参数解析器  
public class RequestParamMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver
        implements UriComponentsContributor {
  ...
  /**
   * Supports the following:
   * <ul>
   * <li>@RequestParam-annotated method arguments.
   * This excludes {@link Map} params where the annotation does not specify a name.
   * See {@link RequestParamMapMethodArgumentResolver} instead for such params.
   * <li>Arguments of type {@link MultipartFile} unless annotated with @{@link RequestPart}.
   * <li>Arguments of type {@code Part} unless annotated with @{@link RequestPart}.
   * <li>In default resolution mode, simple type arguments even if not with @{@link RequestParam}.
   * </ul>
   * 
   * 支持 @requestparam注释的方法参数。这排除了注释没有指定名称的Map参数。
   * 这些参数请参见 RequestParamMapMethodArgumentResolver类型为MultipartFile的参数，
   * 排除 @RequestPart注释类型为Part的参数，排除 用@RequestPart注释。
   * 在默认解析模式下，简单类型参数即使没有@RequestParam
   */
  @Override
  public boolean supportsParameter(MethodParameter parameter) {
      //判断参数是否有 @RequestParam注解
      if (parameter.hasParameterAnnotation(RequestParam.class)) {
          if (Map.class.isAssignableFrom(parameter.nestedIfOptional().getNestedParameterType())) {
            RequestParam requestParam = parameter.getParameterAnnotation(RequestParam.class);
            return (requestParam != null && StringUtils.hasText(requestParam.name()));
          }
          else {
            return true;
          }
      }
      else {
          if (parameter.hasParameterAnnotation(RequestPart.class)) {
            return false;
          }
          parameter = parameter.nestedIfOptional();
          if (MultipartResolutionDelegate.isMultipartArgument(parameter)) {
            return true;
          }
          else if (this.useDefaultResolution) {
            return BeanUtils.isSimpleProperty(parameter.getNestedParameterType());
          }
          else {
            return false;
          }
      }
  }
}

/**
 * Resolves {@link Map} method arguments annotated with an @{@link RequestParam}
 * where the annotation does not specify a request parameter name.
 *
 * <p>The created {@link Map} contains all request parameter name/value pairs,
 * or all multipart files for a given parameter name if specifically declared
 * with {@link MultipartFile} as the value type. If the method parameter type is
 * {@link MultiValueMap} instead, the created map contains all request parameters
 * and all their values for cases where request parameters have multiple values
 * (or multiple multipart files of the same name).
 *
 * 解决用@RequestParam注释的Map方法参数，其中注释没有指定请求参数名。 
 * 创建的Map包含所有请求参数名称/值对，或者特定使用MultipartFile作为值类型声明的给定参数名称的所有多部分文件。
 * 如果方法参数类型是MultiValueMap，则创建的映射包含所有请求参数及其所有值(对于请求参数有多个值(或多个同名多部分文件)的情况)。 
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @since 3.1
 * @see RequestParamMethodArgumentResolver
 * @see HttpServletRequest#getParameterMap()
 * @see MultipartRequest#getMultiFileMap()
 * @see MultipartRequest#getFileMap()
 */
//@RequestParam 注解 参数类型为Map的解析器 
public class RequestParamMapMethodArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    RequestParam requestParam = parameter.getParameterAnnotation(RequestParam.class);
    return (requestParam != null && Map.class.isAssignableFrom(parameter.getParameterType()) &&
            !StringUtils.hasText(requestParam.name()));
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {

    ResolvableType resolvableType = ResolvableType.forMethodParameter(parameter);

    if (MultiValueMap.class.isAssignableFrom(parameter.getParameterType())) {
      // MultiValueMap
      Class<?> valueType = resolvableType.as(MultiValueMap.class).getGeneric(1).resolve();
      if (valueType == MultipartFile.class) {
        MultipartRequest multipartRequest = MultipartResolutionDelegate.resolveMultipartRequest(webRequest);
        return (multipartRequest != null ? multipartRequest.getMultiFileMap() : new LinkedMultiValueMap<>(0));
      } else if (valueType == Part.class) {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        if (servletRequest != null && MultipartResolutionDelegate.isMultipartRequest(servletRequest)) {
          Collection<Part> parts = servletRequest.getParts();
          LinkedMultiValueMap<String, Part> result = new LinkedMultiValueMap<>(parts.size());
          for (Part part : parts) {
            result.add(part.getName(), part);
          }
          return result;
        }
        return new LinkedMultiValueMap<>(0);
      } else {
        Map<String, String[]> parameterMap = webRequest.getParameterMap();
        MultiValueMap<String, String> result = new LinkedMultiValueMap<>(parameterMap.size());
        parameterMap.forEach((key, values) -> {
          for (String value : values) {
            result.add(key, value);
          }
        });
        return result;
      }
    } else {
      // Regular Map
      Class<?> valueType = resolvableType.asMap().getGeneric(1).resolve();
      if (valueType == MultipartFile.class) {
        MultipartRequest multipartRequest = MultipartResolutionDelegate.resolveMultipartRequest(webRequest);
        return (multipartRequest != null ? multipartRequest.getFileMap() : new LinkedHashMap<>(0));
      } else if (valueType == Part.class) {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        if (servletRequest != null && MultipartResolutionDelegate.isMultipartRequest(servletRequest)) {
          Collection<Part> parts = servletRequest.getParts();
          LinkedHashMap<String, Part> result = CollectionUtils.newLinkedHashMap(parts.size());
          for (Part part : parts) {
            if (!result.containsKey(part.getName())) {
              result.put(part.getName(), part);
            }
          }
          return result;
        }
        return new LinkedHashMap<>(0);
      } else {
        Map<String, String[]> parameterMap = webRequest.getParameterMap();
        Map<String, String> result = CollectionUtils.newLinkedHashMap(parameterMap.size());
        parameterMap.forEach((key, values) -> {
          if (values.length > 0) {
            result.put(key, values[0]);
          }
        });
        return result;
      }
    }
  }
}
```
</details>

<details>
  <summary>ViewNameMethodReturnValueHandler</summary>

```java
public class ViewNameMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

	@Nullable
	private String[] redirectPatterns;


	/**
	 * Configure one more simple patterns (as described in {@link PatternMatchUtils#simpleMatch})
	 * to use in order to recognize custom redirect prefixes in addition to "redirect:".
	 * <p>Note that simply configuring this property will not make a custom redirect prefix work.
	 * There must be a custom View that recognizes the prefix as well.
	 * @since 4.1
	 */
	public void setRedirectPatterns(@Nullable String... redirectPatterns) {
		this.redirectPatterns = redirectPatterns;
	}

	/**
	 * The configured redirect patterns, if any.
	 */
	@Nullable
	public String[] getRedirectPatterns() {
		return this.redirectPatterns;
	}


	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		Class<?> paramType = returnType.getParameterType();
		return (void.class == paramType || CharSequence.class.isAssignableFrom(paramType));
	}

	@Override
	public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {

		if (returnValue instanceof CharSequence) {
			String viewName = returnValue.toString();
            //设置view 值为string类型
			mavContainer.setViewName(viewName);
            //判断是否是重定向
			if (isRedirectViewName(viewName)) {
				mavContainer.setRedirectModelScenario(true);
			}
		} else if (returnValue != null) {
			// should not happen
			throw new UnsupportedOperationException("Unexpected return type: " +
					returnType.getParameterType().getName() + " in method: " + returnType.getMethod());
		}
	}
    
    protected boolean isRedirectViewName(String viewName) {
      return (PatternMatchUtils.simpleMatch(this.redirectPatterns, viewName) || viewName.startsWith("redirect:"));
    }
}
```
</details>

<details>
  <summary>RequestResponseBodyMethodProcessor</summary>

```java
public class RequestResponseBodyMethodProcessor extends AbstractMessageConverterMethodProcessor {

  /**
   * Whether the given {@linkplain MethodParameter method return type} is
   * supported by this handler.
   * @param returnType the method return type to check
   * @return {@code true} if this handler supports the supplied return type;
   * {@code false} otherwise
   * 此处理程序是否支持给定的{@linkplain MethodParameter方法返回类型}。
   * @param returnType方法返回类型，以检查@return {@code true}该处理程序是否支持提供的返回类型;
   */
  @Override
  public boolean supportsReturnType(MethodParameter returnType) {
    return (AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), ResponseBody.class) ||
            returnType.hasMethodAnnotation(ResponseBody.class));
  }

  /**
   * Handle the given return value by adding attributes to the model and
   * setting a view or setting the
   * {@link ModelAndViewContainer#setRequestHandled} flag to {@code true}
   * to indicate the response has been handled directly.
   * @param returnValue the value returned from the handler method
   * @param returnType the type of the return value. This type must have
   * previously been passed to {@link #supportsReturnType} which must
   * have returned {@code true}.
   * @param mavContainer the ModelAndViewContainer for the current request
   * @param webRequest the current request
   * @throws Exception if the return value handling results in an error
   * 
   * 通过向模型添加属性并设置视图或将{@link ModelAndViewContainersetRequestHandled}标志
   * 设置为{@code true}来处理给定的返回值，以指示已经直接处理了响应。
   * @param returnType返回值的类型。此类型之前必须传递给{@link supportsReturnType}，后者必须返回{@code true}。
   */
  @Override
  public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType,
                                ModelAndViewContainer mavContainer, NativeWebRequest webRequest)
          throws IOException, HttpMediaTypeNotAcceptableException, HttpMessageNotWritableException {

    mavContainer.setRequestHandled(true);
    ServletServerHttpRequest inputMessage = createInputMessage(webRequest);
    ServletServerHttpResponse outputMessage = createOutputMessage(webRequest);

    // Try even with null return value. ResponseBodyAdvice could get involved.
    //即使返回值为空也要尝试。ResponseBodyAdvice可以参与进来。
    //使用消息转换器进行写出操作
    writeWithMessageConverters(returnValue, returnType, inputMessage, outputMessage);
  }

  /**
   * Writes the given return type to the given output message.
   * @param value the value to write to the output message
   * @param returnType the type of the value
   * @param inputMessage the input messages. Used to inspect the {@code Accept} header.
   * @param outputMessage the output message to write to
   * @throws IOException thrown in case of I/O errors
   * @throws HttpMediaTypeNotAcceptableException thrown when the conditions indicated
   * by the {@code Accept} header on the request cannot be met by the message converters
   * @throws HttpMessageNotWritableException thrown if a given message cannot
   * be written by a converter, or if the content-type chosen by the server
   * has no compatible converter.
   * 将给定的返回类型写入给定的输出消息。
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  protected <T> void writeWithMessageConverters(@Nullable T value, MethodParameter returnType,
                                                ServletServerHttpRequest inputMessage, ServletServerHttpResponse outputMessage)
          throws IOException, HttpMediaTypeNotAcceptableException, HttpMessageNotWritableException {

    Object body;
    Class<?> valueType;
    Type targetType;

    if (value instanceof CharSequence) {
      body = value.toString();
      valueType = String.class;
      targetType = String.class;
    }
    else {
      body = value;
      valueType = getReturnValueType(body, returnType);
      targetType = GenericTypeResolver.resolveType(getGenericType(returnType), returnType.getContainingClass());
    }

    //是否是资源类型(流数据)
    if (isResourceType(value, returnType)) {
      outputMessage.getHeaders().set(HttpHeaders.ACCEPT_RANGES, "bytes");
      if (value != null && inputMessage.getHeaders().getFirst(HttpHeaders.RANGE) != null &&
              outputMessage.getServletResponse().getStatus() == 200) {
        Resource resource = (Resource) value;
        try {
          List<HttpRange> httpRanges = inputMessage.getHeaders().getRange();
          outputMessage.getServletResponse().setStatus(HttpStatus.PARTIAL_CONTENT.value());
          body = HttpRange.toResourceRegions(httpRanges, resource);
          valueType = body.getClass();
          targetType = RESOURCE_REGION_LIST_TYPE;
        }
        catch (IllegalArgumentException ex) {
          outputMessage.getHeaders().set(HttpHeaders.CONTENT_RANGE, "bytes */" + resource.contentLength());
          outputMessage.getServletResponse().setStatus(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value());
        }
      }
    }
    //媒体类型
    MediaType selectedMediaType = null;
    //获取响应请求头中是否已有类型 缓存值
    MediaType contentType = outputMessage.getHeaders().getContentType();
    boolean isContentTypePreset = contentType != null && contentType.isConcrete();
    if (isContentTypePreset) {
      if (logger.isDebugEnabled()) {
        logger.debug("Found 'Content-Type:" + contentType + "' in response");
      }
      selectedMediaType = contentType;
    }
    else {
      HttpServletRequest request = inputMessage.getServletRequest();
      List<MediaType> acceptableTypes;
      try {
          //获取该request 能接收的媒体类型(即客户端能支持的媒体类型) img14 (获取客户端请求头Accept字段)
        acceptableTypes = getAcceptableMediaTypes(request);
      }
      catch (HttpMediaTypeNotAcceptableException ex) {
        int series = outputMessage.getServletResponse().getStatus() / 100;
        if (body == null || series == 4 || series == 5) {
          if (logger.isDebugEnabled()) {
            logger.debug("Ignoring error response content (if any). " + ex);
          }
          return;
        }
        throw ex;
      }
      //获取该request 服务器所能生成(可响应)的媒体类型 img14
      List<MediaType> producibleTypes = getProducibleMediaTypes(request, valueType, targetType);

      if (body != null && producibleTypes.isEmpty()) {
        throw new HttpMessageNotWritableException(
                "No converter found for return value of type: " + valueType);
      }
      List<MediaType> mediaTypesToUse = new ArrayList<>();
      //匹配能使用的媒体类型
      for (MediaType requestedType : acceptableTypes) {
        for (MediaType producibleType : producibleTypes) {
          if (requestedType.isCompatibleWith(producibleType)) {
            mediaTypesToUse.add(getMostSpecificMediaType(requestedType, producibleType));
          }
        }
      }
      if (mediaTypesToUse.isEmpty()) {
        if (logger.isDebugEnabled()) {
          logger.debug("No match for " + acceptableTypes + ", supported: " + producibleTypes);
        }
        if (body != null) {
          throw new HttpMediaTypeNotAcceptableException(producibleTypes);
        }
        return;
      }

      MediaType.sortBySpecificityAndQuality(mediaTypesToUse);

      //去重匹配好的媒体类型结果，确定最佳匹配类型
      for (MediaType mediaType : mediaTypesToUse) {
        if (mediaType.isConcrete()) {
          selectedMediaType = mediaType;
          break;
        }
        else if (mediaType.isPresentIn(ALL_APPLICATION_MEDIA_TYPES)) {
          selectedMediaType = MediaType.APPLICATION_OCTET_STREAM;
          break;
        }
      }

      if (logger.isDebugEnabled()) {
        logger.debug("Using '" + selectedMediaType + "', given " +
                acceptableTypes + " and supported " + producibleTypes);
      }
    }

    if (selectedMediaType != null) {
      selectedMediaType = selectedMediaType.removeQualityValue();
      //判断哪个converter能处理 img15
      for (HttpMessageConverter<?> converter : this.messageConverters) {
        GenericHttpMessageConverter genericConverter = (converter instanceof GenericHttpMessageConverter ?
                (GenericHttpMessageConverter<?>) converter : null);
        //MappingJackson2HttpMessageConverter 支持json类型转换
        if (genericConverter != null ?
                ((GenericHttpMessageConverter) converter).canWrite(targetType, valueType, selectedMediaType) :
                converter.canWrite(valueType, selectedMediaType)) {
          body = getAdvice().beforeBodyWrite(body, returnType, selectedMediaType,
                  (Class<? extends HttpMessageConverter<?>>) converter.getClass(),
                  inputMessage, outputMessage);
          if (body != null) {
            Object theBody = body;
            LogFormatUtils.traceDebug(logger, traceOn ->
                    "Writing [" + LogFormatUtils.formatValue(theBody, !traceOn) + "]");
            addContentDispositionHeader(inputMessage, outputMessage);
            if (genericConverter != null) {
                //通过消息转换器 将对应类型数据转换并写入response
              genericConverter.write(body, targetType, selectedMediaType, outputMessage);
            }
            else {
              ((HttpMessageConverter) converter).write(body, selectedMediaType, outputMessage);
            }
          }
          else {
            if (logger.isDebugEnabled()) {
              logger.debug("Nothing to write: null body");
            }
          }
          return;
        }
      }
    }

    if (body != null) {
      Set<MediaType> producibleMediaTypes =
              (Set<MediaType>) inputMessage.getServletRequest()
                      .getAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);

      if (isContentTypePreset || !CollectionUtils.isEmpty(producibleMediaTypes)) {
        throw new HttpMessageNotWritableException(
                "No converter for [" + valueType + "] with preset Content-Type '" + contentType + "'");
      }
      throw new HttpMediaTypeNotAcceptableException(getSupportedMediaTypes(body.getClass()));
    }
  }

  /**
   * This implementation sets the default headers by calling {@link #addDefaultHeaders},
   * and then calls {@link #writeInternal}.
   * 这个实现通过调用{@link addDefaultHeaders}来设置默认头，然后调用{@link writeInternal}。
   */
  @Override
  public final void write(final T t, @Nullable final Type type, @Nullable MediaType contentType,
                          HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {

    final HttpHeaders headers = outputMessage.getHeaders();
    //添加请求头
    addDefaultHeaders(headers, t, contentType);

    if (outputMessage instanceof StreamingHttpOutputMessage) {
      StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage) outputMessage;
      streamingOutputMessage.setBody(outputStream -> writeInternal(t, type, new HttpOutputMessage() {
        @Override
        public OutputStream getBody() {
          return outputStream;
        }
        @Override
        public HttpHeaders getHeaders() {
          return headers;
        }
      }));
    }
    else {
        //MappingJackson2HttpMessageConverter 把对象转为JSON
        //（利用底层的jackson的objectMapper转换的） 再执行写入response操作  img17
      writeInternal(t, type, outputMessage);
      outputMessage.getBody().flush();
    }
  }

  /**
   * Returns the media types that can be produced. The resulting media types are:
   * <ul>
   * <li>The producible media types specified in the request mappings, or
   * <li>Media types of configured converters that can write the specific return value, or
   * <li>{@link MediaType#ALL}
   * </ul>
   * 返回可生成的媒体类型。结果的媒体类型是:在请求映射中指定的可生产的媒体类型，
   * 或配置的转换器的媒体类型，可以写入特定的返回值，或{@link MediaTypeALL}
   * @since 4.2
   */
  @SuppressWarnings("unchecked")
  protected List<MediaType> getProducibleMediaTypes(
          HttpServletRequest request, Class<?> valueClass, @Nullable Type targetType) {

    Set<MediaType> mediaTypes =
            (Set<MediaType>) request.getAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
    if (!CollectionUtils.isEmpty(mediaTypes)) {
      return new ArrayList<>(mediaTypes);
    }
    List<MediaType> result = new ArrayList<>();
    //遍历循环服务器所有的的 MessageConverter，看哪个支持操作这个对象（Person）
    for (HttpMessageConverter<?> converter : this.messageConverters) {
      if (converter instanceof GenericHttpMessageConverter && targetType != null) {
        if (((GenericHttpMessageConverter<?>) converter).canWrite(targetType, valueClass, null)) {
          result.addAll(converter.getSupportedMediaTypes(valueClass));
        }
      }
      else if (converter.canWrite(valueClass, null)) {
        result.addAll(converter.getSupportedMediaTypes(valueClass));
      }
    }
    return (result.isEmpty() ? Collections.singletonList(MediaType.ALL) : result);
  }
}
```
</details>

<details>
  <summary>代码分析过程断点变量阐释</summary>

![img_1.png](assets/img_1.png)
![img_2.png](assets/img_2.png)
![img_3.png](assets/img_3.png)
![img_4.png](assets/img_4.png)
![img_5.png](assets/img_5.png)
![img_6.png](assets/img_6.png)
![img_7.png](assets/img_7.png)
![img_9.png](assets/img_9.png)
![img_10.png](assets/img_10.png)
![img_13.png](assets/img_13.png)
![img_14.png](assets/img_14.png)
![img_15.png](assets/img_15.png)
0. 支持返回值类型为 Byte类型的
1. 支持返回值类型为 String
2. String
3. Resource
4. ResourceRegion
5. DOMSource.class \ SAXSource.class \ StAXSource.class \StreamSource.class \Source.class (xml解析)
6. MultiValueMap
7. true(MappingJackson2HttpMessageConverter)
8. true
9. 支持注解方式xml处理的。

![img_17.png](assets/img_17.png)

</details>

<a id="3.4.2.1"></a>

* HandlerMapping中找到能处理请求的Handler（Controller的请求方法） getHandler()
* 为当前Handler 找一个适配器 HandlerAdapter(RequestMappingHandlerAdapter)
  * RequestMappingHandlerAdapter 负责处理 @RequestMapping标注的请求方法
* argumentResolvers 确定将要执行的目标方法的每一个参数的值是什么;
  * SpringMVC目标方法能写多少种参数类型。取决于参数解析器。
  * RequestParamMethodArgumentResolver
  * RequestParamMapMethodArgumentResolver
  * PathVariableMethodArgumentResolver
  * RequestHeaderMethodArgumentResolver
  * ...
* HandlerMethodArgumentResolver 参数解析器提供2个方法来处理请求
  * 当前解析器是否支持解析这种参数 supportsParameter()
  * 支持就调用 resolveArgument()
* returnValueHandlers 确定该请求方法的返回值类型是否支持
  * ModelAndViewMethodReturnValueHandler
  * ResponseBodyEmitterReturnValueHandler
  * MapMethodProcessor
  * RequestResponseBodyMethodProcessor
* HandlerMethodReturnValueHandler 返回值处理器提供2个方法来处理请求
  * 此处理程序是否支持给定的方法返回类型。 supportsReturnType()
  * 支持就调用 handleReturnValue()
* 接下来执行invocableMethod.invokeAndHandle()方法 适配器将处理请求
  * ServletInvocableHandlerMethod.invokeForRequest()
  * ServletInvocableHandlerMethod.getMethodArgumentValues() 获取方法参数值
  * ServletInvocableHandlerMethod.getMethodParameters() 获取该请求所有请求参数
    * 0 = {HandlerMethod$HandlerMethodParameter@7471} "method 'getCar' parameter 0"   getCar()方法的第0个参数
      * combinedAnnotations = {Annotation[1]@7482}  注解类型
        * 0 = {$Proxy69@7485} "@org.springframework.web.bind.annotation.PathVariable(name="id", required=true, value="id")"   @PathVariable 注解
      * this$0 = {HandlerMethod@7463} "com.example.running.controller.RunningController#getCar(Integer, String, Map, String, String, Map, Integer, List, Map)"
    * 1 = {HandlerMethod$HandlerMethodParameter@7472} "method 'getCar' parameter 1"   getCar()方法的第1个参数
    * 2 = {HandlerMethod$HandlerMethodParameter@7473} "method 'getCar' parameter 2"   getCar()方法的第2个参数
  * 遍历所有参数，再遍历所有解析器是否有支持当前请求参数的，支持则解析该参数
  * 执行doInvoke()方法，将解析后的请求参数，放行给对应的请求方法去执行 
* 请求方法去执行完毕后获取到返回值 returnValue，设置返回状态 setResponseStatus(webRequest)
* 处理返回值 returnValueHandlers.handleReturnValue 确定返回值类型
  * 遍历返回值处理器判断是否支持这种类型返回值 supportsReturnType()
  * 返回值处理器调用 handleReturnValue() 进行处理
  * RequestResponseBodyMethodProcessor 可以处理返回值标了@ResponseBody 注解的。
    * 支持以下2种写法的请求方法， @ResponseBody可放在类或者方法上
      * @RestController (@Controller/@ResponseBody) 写法
      * @Controller (@PostMapping/@ResponseBody) 写法
    * 利用 MessageConverters 消息转换器处理@ResponseBody注解的数据(Bean对象),将数据写为json
      * 内容协商（浏览器默认会以请求头的方式告诉服务器他能接受什么样的内容类型）
        * Accept: "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8"
      * 服务器最终根据自己自身的能力，决定服务器能生产出什么样内容类型的数据，
      * SpringMVC会挨个遍历所有容器底层的 HttpMessageConverter ，看谁能处理？
        * 得到MappingJackson2HttpMessageConverter可以将对象写为json
        * 利用MappingJackson2HttpMessageConverter将对象转为json再写出去。
* 请求处理完毕后，执行getModelAndView(mavContainer, modelFactory, webRequest)，处理放在 ModelAndViewContainer 数据视图容器内的数据(Model=数据/View=数据返回地址)
* (RequestMappingHandlerAdapter) ha.handle(processedRequest, response, mappedHandler.getHandler()) 执行完毕， 返回ModelAndView
* mappedHandler.applyPostHandle(processedRequest, response, mv) 倒序执行已注册拦截器的postHandle方法。
* processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException) 处理派发结果。处理程序选择和处理程序调用的结果，它要么是ModelAndView，要么是要解析为ModelAndView的异常。
  * [可参考视图解析源码分析总结](#5.1.1)
  * render(mv, request, response); 渲染View
    * resolveViewName(viewName, mv.getModelInternal(), locale, request) 解析view name
    * view.render(mv.getModelInternal(), request, response) 
      * createMergedOutputModel(model, request, response) 创建合并输出模型(合并model中的数据)
      * renderMergedOutputModel(mergedModel, getRequestToExpose(request), response); 合并request/response输出模型，最终输出response 结束
        * exposeModelAsRequestAttributes(model, request); 将model中的数据存入请求域中 request.setAttribute(name, value);

![img_mvc.png](assets/img_mvc.png)  
       


### 3.4.3 Servlet API

* ServletRequest
* WebRequest
* MultipartRequest
* HttpSession
* javax.servlet.http.PushBuilder.
* HttpMethod
* Reader
* Principal
* InputStream
* Locale
* TimeZone
* ZoneId

<details>
  <summary>ServletRequestMethodArgumentResolver</summary>

```java
/**
 * Resolves servlet backed request-related method arguments. Supports values of the
 * following types:
 * 解决servlet支持的请求相关方法参数。支持以下类型的值:
 * <ul>
 * <li>{@link WebRequest}
 * <li>{@link ServletRequest}
 * <li>{@link MultipartRequest}
 * <li>{@link HttpSession}
 * <li>{@link PushBuilder} (as of Spring 5.0 on Servlet 4.0)
 * <li>{@link Principal} but only if not annotated in order to allow custom
 * resolvers to resolve it, and the falling back on
 * {@link PrincipalMethodArgumentResolver}.
 * <li>{@link InputStream}
 * <li>{@link Reader}
 * <li>{@link HttpMethod} (as of Spring 4.0)
 * <li>{@link Locale}
 * <li>{@link TimeZone} (as of Spring 4.0)
 * <li>{@link java.time.ZoneId} (as of Spring 4.0 and Java 8)
 * </ul>
 * 
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @since 3.1
 */
public class ServletRequestMethodArgumentResolver implements HandlerMethodArgumentResolver {

  @Nullable
  private static Class<?> pushBuilder;

  static {
    try {
      pushBuilder = ClassUtils.forName("javax.servlet.http.PushBuilder",
              ServletRequestMethodArgumentResolver.class.getClassLoader());
    } catch (ClassNotFoundException ex) {
      // Servlet 4.0 PushBuilder not found - not supported for injection
      pushBuilder = null;
    }
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    Class<?> paramType = parameter.getParameterType();
    return (WebRequest.class.isAssignableFrom(paramType) ||
            ServletRequest.class.isAssignableFrom(paramType) ||
            MultipartRequest.class.isAssignableFrom(paramType) ||
            HttpSession.class.isAssignableFrom(paramType) ||
            (pushBuilder != null && pushBuilder.isAssignableFrom(paramType)) ||
            (Principal.class.isAssignableFrom(paramType) && !parameter.hasParameterAnnotations()) ||
            InputStream.class.isAssignableFrom(paramType) ||
            Reader.class.isAssignableFrom(paramType) ||
            HttpMethod.class == paramType ||
            Locale.class == paramType ||
            TimeZone.class == paramType ||
            ZoneId.class == paramType);
  }
}

```
</details>

### 3.4.4 复杂参数

* Map
* Model
  * Resolver 解析器返回值均为 BindingAwareModelMap(是Model 也是Map)
  * Map、Model、HttpServletRequest里面的数据会被放在request的请求域(request.setAttribute)
  * 通过request.getAttribute("") 可被获取
* ServletRequest
* Errors/BindingResult
* RedirectAttributes（重定向携带数据）
* ServletResponse（response)
* SessionStatus
* UriComponentsBuilder
* ServletUriComponentsBuilder

> Map<String,Object> map,  Model model, HttpServletRequest request 都是可以给request域中放数据 
>
> 通过forward 转发请求后，request.getAttribute() 可获取到保存的数据

```java
    @GetMapping("/getModel")
    public String getModel(Map<String, Object> map,
                           Model model,
                           HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse){
        map.put("map", "map666");
        model.addAttribute("model", "model666");
        httpServletRequest.setAttribute("request", "request666");
        Cookie cookie = new Cookie("cookie", "cookie666");
        httpServletResponse.addCookie(cookie);

        //设置请求域request属性
        //转发到 gotoSuccess请求
        return "forward:/gotoSuccess";
    }
    
    @ResponseBody
    @GetMapping("/gotoSuccess")
    public Map<String,Object> successCar(@RequestAttribute(value = "msg", required = false) String msg,
            HttpServletRequest request){
            Map<String,Object> map = new HashMap<>();
            //请求属性，获取request域中属性，通过用于页面转发时保存数据
            map.put("msg", msg);
            map.put("map", request.getAttribute("map"));
            map.put("model", request.getAttribute("model"));
            map.put("request", request.getAttribute("request"));
            map.put("cookie", request.getAttribute("cookie"));
            map.put("cookie1", request.getCookies());
            return map;
            }
            
```

<details>
  <summary>MapMethodProcessor</summary>

```java
/**
 * Resolves {@link Map} method arguments and handles {@link Map} return values.
 *
 * <p>A Map return value can be interpreted in more than one ways depending
 * on the presence of annotations like {@code @ModelAttribute} or
 * {@code @ResponseBody}. As of 5.2 this resolver returns false if the
 * parameter is annotated.
 * 
 * 解析{@link Map}方法参数并处理{@link Map}返回值。
 * 一个Map返回值可以用多种方式解释，这取决于注释的存在，
 * 如{@code @ModelAttribute}或{@code @ResponseBody}。从5.2开始，如果参数被注释，该解析器返回false。
 * 
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class MapMethodProcessor implements HandlerMethodArgumentResolver, HandlerMethodReturnValueHandler {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
      return (Map.class.isAssignableFrom(parameter.getParameterType()) &&
              parameter.getParameterAnnotations().length == 0);
    }
    
    @Override
    @Nullable
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
      Assert.state(mavContainer != null, "ModelAndViewContainer is required for model exposure");
      return mavContainer.getModel();
    }
    ...
}

/**
 * Resolves {@link Model} arguments and handles {@link Model} return values.
 *
 * <p>A {@link Model} return type has a set purpose. Therefore this handler
 * should be configured ahead of handlers that support any return value type
 * annotated with {@code @ModelAttribute} or {@code @ResponseBody} to ensure
 * they don't take over.
 *
 * 解析{@link Model}参数并处理{@link Model}返回值。
 * <p>A {@link Model}返回类型有一个设定的目的。
 * 因此，应该在支持任何带有{@code @ModelAttribute}或{@code @ResponseBody}
 * 注释的返回值类型的处理程序之前配置此处理程序，以确保它们不会接管。
 * 
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class ModelMethodProcessor implements HandlerMethodArgumentResolver, HandlerMethodReturnValueHandler {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
      return Model.class.isAssignableFrom(parameter.getParameterType());
    }
  
    @Override
    @Nullable
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
  
      Assert.state(mavContainer != null, "ModelAndViewContainer is required for model exposure");
      return mavContainer.getModel();
    }
   ...
}

public class ModelAndViewContainer {
    
    private final ModelMap defaultModel = new BindingAwareModelMap();
  
    public ModelMap getModel() {
      if (useDefaultModel()) {
        return this.defaultModel;
      } else {
        if (this.redirectModel == null) {
          this.redirectModel = new ModelMap();
        }
        return this.redirectModel;
      }
    }
}
  
```
</details>

![img_8.png](assets/img_8.png)

### 3.4.5 自定义对象参数

> 自定义 Bean实体类数据:可以自动类型转换与格式化，可以级联封装，和对象属性进行绑定

* ServletModelAttributeMethodProcessor 负责解析参数
  * WebDataBinder binder = binderFactory.createBinder(webRequest, attribute, name); 
  * Web数据绑定器 使用Converters 数据转换器，将request请求域中参数通过反射转化为指定的数据类型，再与具体的Bean实例属性绑定
  * GenericConversionService 类型转化器-在设置每一个值的时候，遍历Converters 中哪个转换器可以将这个数据类型（request请求域中的参数）转换到指定的类型（JavaBean -> Integer）/ byte -- > file
  * 回顾SpringMvc 自动配置原理
    * ● Automatic registration of Converter, GenericConverter, and Formatter beans.
    * ○ 自动注册 Converter，GenericConverter，Formatter (类型转化器)
    * ● Automatic use of a ConfigurableWebBindingInitializer bean (covered later in this document).
    * ○ 自动使用 ConfigurableWebBindingInitializer ，（DataBinder负责将请求数据绑定到JavaBean上）(数据绑定器)
* 通过WebMvcConfigurer 定制化springmvc 配置addFormatters() 可自定义参数类型转换器 FormatterRegistry.addConverter()

```java
/**
 *     姓名： <input name="userName"/> <br/>
 *     年龄： <input name="age"/> <br/>
 *     生日： <input name="birth"/> <br/>
 *     宠物姓名：<input name="pet.name"/><br/>
 *     宠物年龄：<input name="pet.age"/>
 */
@Data
public class Person {
  
    private String userName;
    private Integer age;
    private Date birth;
    private Pet pet;
  
}

@Data
public class Pet {

    private String name;
    private String age;

}
```

<details>
  <summary>ServletModelAttributeMethodProcessor</summary>

```java
/**
 * Resolve {@code @ModelAttribute} annotated method arguments and handle
 * return values from {@code @ModelAttribute} annotated methods.
 *
 * <p>Model attributes are obtained from the model or created with a default
 * constructor (and then added to the model). Once created the attribute is
 * populated via data binding to Servlet request parameters. Validation may be
 * applied if the argument is annotated with {@code @javax.validation.Valid}.
 * or Spring's own {@code @org.springframework.validation.annotation.Validated}.
 *
 * <p>When this handler is created with {@code annotationNotRequired=true}
 * any non-simple type argument and return value is regarded as a model
 * attribute with or without the presence of an {@code @ModelAttribute}.
 *
 * 解析{@code @ModelAttribute}注释方法参数并处理{@code @ModelAttribute}注释方法的返回值。
 * 模型属性从模型中获得，或者用默认构造函数创建(然后添加到模型中)。
 * 一旦创建了属性，就通过与Servlet请求参数的数据绑定来填充属性。
 * 如果参数用{@code @javax. validate .valid}注释，则可以应用验证。
 * 或者Spring自己的{@code @org.springframework.validation.annotation.Validated}。
 * 当使用{@code annotationnotrerequired =true}创建此处理程序时，
 * 任何非简单类型参数和返回值都被视为带有或不带有{@code @ModelAttribute}的模型属性。
 * 
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @author Sebastien Deleuze
 * @author Vladislav Kisel
 * @since 3.1
 */
public class ServletModelAttributeMethodProcessor implements HandlerMethodArgumentResolver, HandlerMethodReturnValueHandler {

  /**
   * Returns {@code true} if the parameter is annotated with
   * {@link ModelAttribute} or, if in default resolution mode, for any
   * method parameter that is not a simple type.
   * 如果参数用{@link ModelAttribute}注释，则返回{@code true};
   * 如果在默认解析模式下，则返回任何非简单类型的方法参数。
   */
  @Override
  public boolean supportsParameter(MethodParameter parameter) {
      //非简单类型的方法参数，则满足条件
      return (parameter.hasParameterAnnotation(ModelAttribute.class) ||
            (this.annotationNotRequired && !BeanUtils.isSimpleProperty(parameter.getParameterType())));
  }

  /**
   * Resolve the argument from the model or if not found instantiate it with
   * its default if it is available. The model attribute is then populated
   * with request values via data binding and optionally validated
   * if {@code @java.validation.Valid} is present on the argument.
   * @throws BindException if data binding and validation result in an error
   * and the next method parameter is not of type {@link Errors}
   * @throws Exception if WebDataBinder initialization fails
   * 从模型中解析参数，如果未找到参数，则使用默认值实例化它。然后，模型属性通过数据绑定填充请求值，
   * 如果{@code @java.validation则可选验证。Valid}存在于实参中。如果数据绑定和验证导致错误，
   * 并且下一个方法参数不是{@link Errors}类型
   */
  @Override
  @Nullable
  public final Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
                                      NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {

    Assert.state(mavContainer != null, "ModelAttributeMethodProcessor requires ModelAndViewContainer");
    Assert.state(binderFactory != null, "ModelAttributeMethodProcessor requires WebDataBinderFactory");

    String name = ModelFactory.getNameForParameter(parameter);
    ModelAttribute ann = parameter.getParameterAnnotation(ModelAttribute.class);
    if (ann != null) {
      mavContainer.setBinding(name, ann.binding());
    }

    Object attribute = null;
    BindingResult bindingResult = null;

    if (mavContainer.containsAttribute(name)) {
      attribute = mavContainer.getModel().get(name);
    }
    else {
      // Create attribute instance 创建一个空的属性实例
      try {
        attribute = createAttribute(name, parameter, binderFactory, webRequest);
      }
      catch (BindException ex) {
        if (isBindExceptionRequired(parameter)) {
          // No BindingResult parameter -> fail with BindException
          throw ex;
        }
        // Otherwise, expose null/empty value and associated BindingResult
        if (parameter.getParameterType() == Optional.class) {
          attribute = Optional.empty();
        }
        else {
          attribute = ex.getTarget();
        }
        bindingResult = ex.getBindingResult();
      }
    }

    if (bindingResult == null) {
      // Bean property binding and validation; Bean属性绑定和验证; 
      // skipped in case of binding failure on construction. 如果在构造时绑定失败，则跳过。
      //创建数据绑定器
      WebDataBinder binder = binderFactory.createBinder(webRequest, attribute, name);
      if (binder.getTarget() != null) {
        if (!mavContainer.isBindingDisabled(name)) {
          //将request请求域中参数通过反射与之前创建的实例属性绑定
          bindRequestParameters(binder, webRequest);
        }
        validateIfApplicable(binder, parameter);
        if (binder.getBindingResult().hasErrors() && isBindExceptionRequired(binder, parameter)) {
          throw new BindException(binder.getBindingResult());
        }
      }
      // Value type adaptation, also covering java.util.Optional
      if (!parameter.getParameterType().isInstance(attribute)) {
        attribute = binder.convertIfNecessary(binder.getTarget(), parameter.getParameterType(), parameter);
      }
      bindingResult = binder.getBindingResult();
    }

    // Add resolved attribute and BindingResult at the end of the model
    //在模型末尾添加解析属性和BindingResult
    //将请求域Model(Bean 自定义类型请求参数) 数据添加到视图容器defaultModel中
    Map<String, Object> bindingResultModel = bindingResult.getModel();
    mavContainer.removeAttributes(bindingResultModel);
    mavContainer.addAllAttributes(bindingResultModel);

    return attribute;
  }
  
}
```
</details>

```java
    @Bean
    public WebMvcConfigurer webMvcConfigurer(){
        WebMvcConfigurer webMvcConfigurer = new WebMvcConfigurer(){
          /**
           * Add {@link Converter Converters} and {@link Formatter Formatters} in addition to the ones
           * registered by default.
           * 在默认注册的基础上增加{@link Converter Converter}和{@link Formatter Formatters}。
           */
          @Override
          public void addFormatters(FormatterRegistry registry){}
        }
    }
        
```
![img_11.png](assets/img_11.png)


# 4.数据响应与内容协商

## 4.1 数据响应
![img_12.png](assets/img_12.png)

### 4.1.1 响应Json
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<!--web场景自动引入了json场景-->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-json</artifactId>
  <version>2.3.4.RELEASE</version>
  <scope>compile</scope>
</dependency>

<dependency>
  <groupId>com.fasterxml.jackson.core</groupId>
  <artifactId>jackson-databind</artifactId>
  <version>2.13.3</version>
  <scope>compile</scope>
</dependency>

<dependency>
  <groupId>com.fasterxml.jackson.datatype</groupId>
  <artifactId>jackson-datatype-jdk8</artifactId>
  <version>2.13.3</version>
  <scope>compile</scope>
</dependency>
```

```java
@PostMapping("/getPerson")
public Person getPerson(@RequestParam Integer personId){
    Map<String,Object> map = new HashMap<>();
    Person person = new Person("name-" + personId, 0);
    return person;
}
```

[详见RequestMappingHandlerAdapter 返回值解析器分析](#RequestMappingHandlerAdapter)

[详见ServletInvocableHandlerMethod 返回值处理分析](#ServletInvocableHandlerMethod)


### 4.1.2 SpringMvc支持的返回值类型
* ModelAndView
* Model
* View
* ResponseEntity
* ResponseBodyEmitter
* StreamingResponseBody
* HttpEntity
* HttpHeaders
* Callable
* DeferredResult
* ListenableFuture
* CompletionStage
* WebAsyncTask
* 返回值标注 @ModelAttribute 且为对象类型的
* @ResponseBody 注解 ---> RequestResponseBodyMethodProcessor
  * 支持以下2种写法的请求方法， @ResponseBody可放在类或者方法上
    * @RestController (@Controller/@ResponseBody) 写法
    * @Controller (@PostMapping/@ResponseBody) 写法

### 4.1.3 HTTPMessageConverter原理

`MessageConverter规范`
![img_16.png](assets/img_16.png)
* 判断该Meidatype 类型是否支持转换该class 对象
  * 如Person转换为json， json转换为Person Bean对象

## 4.2 内容协商
> 根据客户端接收能力不同，返回不同媒体类型的数据。
```xml
<!--支持返回xml 类型数据解析-->
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-xml</artifactId>
</dependency>
```
引入xml返回值解析包后，因浏览器可接收数据类型xml 优先级高于其他类型，则优先返回xml数据类型
```xml
请求 /saveUser?userName=zhangsan&age=18&birth=2022%2F12%2F10&pet.name=%E9%98%BF%E7%8C%AB&pet.age=5 
Content-Type:application/xhtml+xml;charset=UTF-8
Accept:text/html,application/xhtml+xml,application/xml;q=0.9,  image/avif,image/webp,*/*;q=0.8
<Map>
  <person>Person{userName='zhangsan', age=18, weight='null', birth=Sat Dec 10 00:00:00 CST 2022, pet=Pet{name='阿猫', age=5}}</person>
</Map>
```
![img_18.png](assets/img_18.png)

### 4.2.1 postman分别测试返回json和xml
> 只需要改变请求头中Accept字段。Http协议中规定的，告诉服务器本客户端可以接收的数据类型。

![img_19.png](assets/img_19.png)

### 4.2.2 内容协商原理
> 在响应请求时，处理返回值类型时体现

[详见请求响应源码分析总结](#3.4.2.1)

1. 判断当前响应头中是否已经有之前请求缓存了的，确定的媒体类型。MediaType
2. 获取客户端（PostMan、浏览器）支持接收的内容类型。（获取客户端Accept请求头字段）[application/xml] [application/json]
   * ContentNegotiationManager 内容协商管理器 默认使用基于请求头的策略
     * 原理就是利用 request.getHeaderValues(HttpHeaders.ACCEPT) 获取请求头中Accept字段内容
   * HeaderContentNegotiationStrategy (请求头内容协商策略)确定客户端可以接收的内容类型
3. 遍历循环所有当前系统的 MessageConverter，看谁支持操作这个对象（Person）
4. 找到支持操作Person的converter，把converter支持的媒体类型统计出来。
5. 客户端需要 [application/xml]。服务端能力[10种、json、xml]
6. 将二者进行内容协商的最佳匹配媒体类型
7. 再匹配所有MessageConverter 看哪个支持将对象转为最佳匹配媒体类型(xml/json)，将对应类型数据转换并写入response
![img_15.png](assets/img_15.png)

### 4.2.3 开启浏览器参数方式内容协商功能
> 为了方便内容协商，开启基于请求参数的内容协商功能。

`开启请求参数内容协商模式` spring.mvc.contentnegotiation.favor-parameter=true

![img_20.png](assets/img_20.png)
示例

* http://localhost:8088/getPerson?personId=9&format=xml
* http://localhost:8088/getPerson?personId=9&format=json

![img_21.png](assets/img_21.png)

* ParameterContentNegotiationStrategy 请求参数内容协商策略(支持json/xml 两种参数类型)
* HeaderContentNegotiationStrategy 请求头内容协商策略

有多个内容协商策略时，确定客户端接收什么样的内容类型
1. ParameterContentNegotiationStrategy 策略优先解析，确定是要返回json数据(获取请求参数中的format的值) request.getParameter("format")
2. 最终进行内容协商返回给客户端指定媒体类型即可(json/xml)

### 4.2.4 自定义 MessageConverter
> 实现多协议数据兼容。json/xml/x-guigu
0. @ResponseBody 响应数据出去 调用 RequestResponseBodyMethodProcessor 处理
1. Processor 处理方法返回值。通过 MessageConverter 处理
2. 所有 MessageConverter 合起来可以支持各种媒体类型数据的操作（读、写）
3. 内容协商找到最终的 messageConverter

<details>
  <summary>代码示例</summary>

```java
/**
 * @author Aloha
 * @date 2022/12/5 15:59
 * @description 自定义HttpMessageConverter
 */
public class CustomHttpMessageConverter implements HttpMessageConverter<Person> {

    @Override
    public boolean canRead(Class clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public boolean canWrite(Class clazz, MediaType mediaType) {
        return clazz.isAssignableFrom(Person.class);
    }

    @Override
    public Person read(Class<? extends Person> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return null;
    }

    @Override
    public void write(Person person, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        //自定义协议数据的输出
        String data = person.getUserName() + ";" + person.getAge() + ";" +person.getWeight() + ";" +person.getBirth() + ";" +person.getPet() + ";";
        //将数据写入到响应数据里
        OutputStream body = outputMessage.getBody();
        body.write(data.getBytes());
    }

    /**
     * @author Aloha
     * @date 2022/12/5 16:12
     * @description 内容协商过程服务器要统计所有 MessageConverter 能哪些媒体内容类型
     * 既使其能支持我们自定义协议类型
     */
    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return MediaType.parseMediaTypes("application/x-guigu");
    }
}

  /**
   * @author Aloha
   * @date 2022/12/5 15:32
   * @description WebMvcConfigurer 定制化SpringMVC 的功能
   */
  @Bean
  public WebMvcConfigurer webMvcConfigurer(){
    WebMvcConfigurer webMvcConfigurer = new WebMvcConfigurer() {
      /**
       * @author Aloha
       * @date 2022/12/5 16:00
       * @description img22 img23 扩展MessageConverter 配置，添加自定义MessageConverter，支持自定义协议 [application/x-guigu]
       */
      @Override
      public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        CustomHttpMessageConverter httpMessageConverter = new CustomHttpMessageConverter();
        converters.add(httpMessageConverter);
        WebMvcConfigurer.super.extendMessageConverters(converters);
      }

      /**
       * @author Aloha
       * @date 2022/12/5 18:35
       * @description 配置内容协商，修改默认的ParameterContentNegotiationStrategy 媒体类型使其支持自定义协议 [application/x-guigu]
       */
      @Override
      public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
          //参考 WebMvcConfigurationSupport.mvcContentNegotiationManager() img24 img25
          //设置自定义 ParameterContentNegotiationStrategy 支持的媒体类型
          Map<String, MediaType> mediaTypes = new HashMap<>(4);
          mediaTypes.put("xml", MediaType.APPLICATION_XML);
          mediaTypes.put("json", MediaType.APPLICATION_JSON);
          mediaTypes.put("gg", MediaType.parseMediaType("application/x-guigu"));
          //指定支持解析哪些参数对应的媒体类型
          ParameterContentNegotiationStrategy parameterContentNegotiationStrategy = new ParameterContentNegotiationStrategy(mediaTypes);
          //添加默认的请求头内容协商
          HeaderContentNegotiationStrategy headerContentNegotiationStrategy = new HeaderContentNegotiationStrategy();
          configurer.strategies(Arrays.asList(parameterContentNegotiationStrategy,headerContentNegotiationStrategy));
    
          WebMvcConfigurer.super.configureContentNegotiation(configurer);
      }
    };
    return webMvcConfigurer;
  }

  /**
   * @author Aloha
   * @date 2022/12/5 12:06
   * @description
   *
   * 1.浏览器发请求直接返回xml [application/xml]  jacksonXmlConverter
   * 2.如果是ajax请求则返回json [application/json]  jacksonJsonConverter
   * 3.如果是 硅谷App请求，则返回自定义协议数据  [application/x-guigu]  xxxConverter
   *
   * 步骤：
   * 1.添加自定义 MessageConverter 进入系统内
   * 2.系统解析请求时就会统计出所有 MessageConverter 能操作哪些类型
   * 3.根据内容协商规则，在请求参数中 format 指定接收 [application/x-guigu] 类型数据 (属性值1;属性值2;)，并且具备该数据类型解析能力 xxxConverter
   */
  @PostMapping("/getPersonForNs")
  public Person getPersonForNs(@RequestParam String personName, @RequestParam Integer personAge, @RequestParam Date personBirth){
    Map<String,Object> map = new HashMap<>();
    Person person = new Person(personName, personAge, "110kg", personBirth, null);
    return person;
  }
```
</details>

![img_22.png](assets/img_22.png)
![img_23.png](assets/img_23.png)

> 通过自定义ParameterContentNegotiationStrategy 添加自定义MediaType 类型，使得format参数可实现除了 json/xml 以外的数据类型。
> 
> 使得自定义协商协议除了修改请求头中Accept 参数规则外，同时也可在format参数中使用 x-guigu 的自定义协议类型

![img_24.png](assets/img_24.png)
![img_25.png](assets/img_25.png)


# 5.视图解析与模板引擎

## 5.1 视图解析
> 视图解析：SpringBoot默认不支持 JSP，需要引入第三方模板引擎技术实现页面渲染。freemarker/groovy-templates/thymeleaf

![img_26.png](assets/img_26.png)

<a id="5.1.1"></a>
### 5.1.1 视图解析原理流程

1. 响应处理过程中，所有数据都会被放在 **ModelAndViewContainer** 里面。包括数据(Model)和视图(View)地址
2. 若请求方法(如验证用户登录请求/login)的请求参数是一个自定义类型对象(Bean) `img28`，Spring视图解析时会将该参数也设置在 ModelAndViewContainer defaultModel中(ModelAttributeMethodProcessor.resolveArgument()-->mavContainer.addAllAttributes(bindingResultModel))
3. **RequestMappingHandlerAdapter.handleInternal()->invokeHandlerMethod()** 方法解析请求参数/响应返回值处理后存入到ModelAndViewContainer，再通过getModelAndView() 处理返回 ModelAndView(数据和视图地址)供下一步视图解析
4. processDispatchResult  处理派发结果(页面该如何响应/视图解析/视图渲染/视图跳转)
   * render(mv, request, response) 进行页面渲染逻辑
     * resolveViewName() 根据View name(redirect:/main.html)解析得到 View 对象 [定义了页面的渲染逻辑]
       1. 所有的视图解析器viewResolvers 遍历尝试是否能根据当前返回值得到View对象 `img29`
          1. 匹配到 ContentNegotiatingViewResolver.resolveViewName() 内容协商视图解析器 可解析当前view
             1. getCandidateViews() `img30`依旧遍历除ContentNegotiatingViewResolver 以外所有viewResolver 看哪个能解析
             2. 匹配到ThymeleafViewResolver.resolveViewName() 解析判断是否是 **redirect:** 重定向，是则创建RedirectView
             3. 或判断转发 **forward:**，则返回InternalResourceView(forwardUrl)
             4. 都不满足则创建一个默认的 ThymeleafView(因为引入了Thymeleaf 模板引擎)，如返回字符串"table/basic_table",跳转模板页面
          2. 得到解析后的view 加入候选中
          3. ContentNegotiatingViewResolver.getBestView() 筛选最佳视图并返回
       2. 根据遍历 view name为redirect:/main.html 解析得到 RedirectView(ThymeleafViewResolver解析返回RedirectView)
       3. view.render(mv.getModelInternal(), request, response) `AbstractView`视图对象调用自定义的render进行页面渲染工作
          1. createMergedOutputModel(model, request, response) 创建合并输出模型(合并model中的数据)
          2. renderMergedOutputModel(mergedModel, getRequestToExpose(request), response); 合并request/response输出模型，最终输出response 结束
             * 由子类`InternalResourceView` `RedirectView` `ThymeleafView` 实现该方法进行视图渲染并合并model数据进行输出 
               1. RedirectView 渲染过程 [重定向到一个页面]
                  * createTargetUrl() 获取目标url地址
                  * response.sendRedirect() 响应发送302 重定向码返回HTTP客户端
               2. InternalResourceView 渲染过程 [转发请求]
                  * exposeModelAsRequestAttributes() 将模型对象公开为请求属性(遍历model中的数据填入request请求域中)
                  * getRequestDispatcher()-->request.getRequestDispatcher(path) 获取请求分发器
                  * rd.forward(request, response)-->ApplicationDispatcher.forward()-->doForward(request,response) 请求分发器转发请求
               3. ThymeleafView 渲染过程 [渲染页面片段] 跳转模板页面"table/basic_table"
                  * renderFragment()方法处理
                  * 处理合并Model以及request 中的数据
                  * SpringTemplateEngine 获取模板引擎配置
                  * viewTemplateEngine.process() 模板引擎执行处理 `img31`
                  * templateManager.parseAndProcess() 引擎执行输出操作
                

![img_28.png](assets/img_28.png)
![img_29.png](assets/img_29.png)
![img_30.png](assets/img_30.png)
![img_31.png](assets/img_31.png)

[可参考请求响应源码分析总结](#3.4.2.1)

## 5.2 thymeleaf模板引擎
> https://www.thymeleaf.org/doc/tutorials/3.1/usingthymeleaf.html#standard-expression-syntax

```xml
<!--thymeleaf语法示例-->
<table>
  <thead>
    <tr>
      <!--#通配符表示获取到的值，有则填充，无则显示标签默认值-->
      <th th:text="#{msgs.headers.name}">Name</th>
      <th th:text="#{msgs.headers.price}">Price</th>
    </tr>
  </thead>
  <tbody>
    <tr th:each="prod: ${allProducts}">
      <td th:text="${prod.name}">Oranges</td>
      <td th:text="${#numbers.formatDecimal(prod.price, 1, 2)}">0.99</td>
    </tr>
  </tbody>
</table>
```
### 5.2.1 基本语法

#### 5.2.1.1 表达式
| 表达式名字 | 语法      | 用途  |
|-------|---------| ---  |
| 变量取值  | ${...}  | 获取请求域、session域、对象等值 |
| 选择变量  | *{...}  | 获取上下文对象值 |
| 消息    | #{...}	 | 获取国际化等值 |
| 链接    | @{...}	 | 生成链接 |
| 片段表达式 | ~{...}  | jsp:include 作用，引入公共页面片段 |

#### 5.2.1.2 字面量
文本值: 'one text' , 'Another one!' ,…数字: 0 , 34 , 3.0 , 12.3 ,…布尔值: true , false
空值: null
变量： one，two，.... 变量不能有空格

#### 5.2.1.3 文本操作
字符串拼接: +
变量替换: |The name is ${name}|

#### 5.2.1.4 数学运算
运算符: + , - , * , / , %

#### 5.2.1.5 布尔运算
运算符:  and , or
一元运算: ! , not

#### 5.2.1.6 比较运算
比较: > , < , >= , <= ( gt , lt , ge , le )等式: == , != ( eq , ne )

#### 5.2.1.7 条件运算
If-then: (if) ? (then)
If-then-else: (if) ? (then) : (else)
Default: (value) ?: (defaultvalue)

#### 5.2.1.8 特殊操作
无操作: _

![img_27.png](assets/img_27.png)

### 5.2.2 thymeleaf使用
```xml
<!--thymeleaf 模板视图解析-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```
> SpringBoot 自动配置好了thymeleaf

```java
@AutoConfiguration(after = { WebMvcAutoConfiguration.class, WebFluxAutoConfiguration.class })
@EnableConfigurationProperties(ThymeleafProperties.class)
@ConditionalOnClass({ TemplateMode.class, SpringTemplateEngine.class })
@Import({ TemplateEngineConfigurations.ReactiveTemplateEngineConfiguration.class,
		TemplateEngineConfigurations.DefaultTemplateEngineConfiguration.class })
public class ThymeleafAutoConfiguration {}
```

自动配好的策略
1. 所有thymeleaf的配置值都在 ThymeleafProperties
   * spring.thymeleaf.prefix=classpath:/templates/"  设置thymeleaf模板路径
     * spring.thymeleaf.suffix=.html  设置thymeleaf模板后缀
2. 配置好了 SpringTemplateEngine(Spring模板引擎)
3. 配好了 ThymeleafViewResolver(Thymeleaf视图解析器)
4. 我们只需要直接开发页面

```java
	public static final String DEFAULT_PREFIX = "classpath:/templates/";

	public static final String DEFAULT_SUFFIX = ".html";  //xxx.html
```


<details>
  <summary>代码示例</summary>

```xml
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Thymeleaf 首页</title>
</head>
<body>
<h1 th:text="${title}">Hello</h1>
<h2>
    <a href="www.baidu.com" th:text="${linkTitle}">网址名称</a>  <br/>
    <a href="www.baidu.com" th:href="${link}">qq</a>
</h2>
</body>
</html>
```

```java
    @GetMapping("/index-thymeleaf")
    public String goIndexThymeleaf(Model model){
        //model中的数据会被放在请求域中 request.setAttribute()
        model.addAttribute("title", "使用thymeleaf模板");
        model.addAttribute("linkTitle", "小破站");
        model.addAttribute("link", "http://www.bilibili.com");
        //跳转到页面
        return "index-thymeleaf";
    }
```
</details>

### 5.2.3 构建后台管理系统

#### 5.2.3.1 项目创建
thymeleaf、web-starter、devtools、lombok

#### 5.2.3.2 静态资源处理
自动配置好，我们只需要把所有静态资源放到 static 文件夹下

#### 5.2.3.3 路径构建
th:action="@{/login}"

#### 5.2.3.4 模板抽取
th:insert/replace/include

#### 5.2.3.5 页面跳转
<details>
  <summary>代码示例</summary>

```java
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
```
</details>

# 6.拦截器
![img_32.png](assets/img_32.png)

## 6.1 HandlerInterceptor 接口
1. 编写一个拦截器实现HandlerInterceptor接口
2. 拦截器注册到容器中(实现WebMvcConfigurer的addInterceptors)
3. 指定拦截规则 [**如果是拦截所有，静态资源也会被拦截**]

<details>
  <summary>代码示例</summary>

```java
    @Bean
    public WebMvcConfigurer webMvcConfigurer(){
        WebMvcConfigurer webMvcConfigurer = new WebMvcConfigurer() {
            
            /**
             * @author Aloha
             * @date 2022/12/7 23:44
             * @description 添加拦截器
             */
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                //添加拦截器并配置拦截路径，排除哪些路径  /** 表示拦截所有请求
                registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**") //所有请求都被拦截，包括静态资源
                .excludePathPatterns("/","/login","/css/**","/font/**","/images/**","/js/**"); //放行的请求
                WebMvcConfigurer.super.addInterceptors(registry);
            }
        };
        return webMvcConfigurer;
   }

/**
 * 登录检查
 * 1.配置好拦截器要拦截哪些请求
 * 2.注册拦截器
 */
public class LoginInterceptor implements HandlerInterceptor {

    /**
     * 目标方法执行前
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LoggerFactory.getLogger(LoginInterceptor.class).info("————执行前———— {}", request.getRequestURL());
        //登录逻辑检查
        HttpSession session = request.getSession();
        Object loginUser = session.getAttribute("loginUser");
        if(loginUser==null){
            //登录拦截
            //session.setAttribute("msg","登录过期");
            //重定向到首页
            //response.sendRedirect("/");

            //使用重定向redirect会导致 request之前放的数据丢失，而使用转发 forward则不会
            request.setAttribute("msg","登录过期");
            request.getRequestDispatcher("/").forward(request, response);
            return false;
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    /**
     * 目标方法执行完成后
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        LoggerFactory.getLogger(LoginInterceptor.class).info("————方法执行完毕————{}", request.getRequestURL());
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * 页面渲染完成后
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LoggerFactory.getLogger(LoginInterceptor.class).info("————页面渲染异常————{}", ex!=null? ex.getMessage(): null);
        LoggerFactory.getLogger(LoginInterceptor.class).info("————页面渲染后————{}", request.getRequestURL());
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
```
</details>

## 6.2 拦截器原理
1. doDispatch() 请求分发时根据当前请求，找到HandlerExecutionChain 处理程序执行链 [可以处理请求的handler以及handler的所有拦截器 HandlerInterceptor]
2. mappedHandler.applyPreHandle() 顺序执行已注册的所有拦截器 preHandle()方法 `img33`
   1. 如果当前拦截器preHandle()返回为true。则执行下一个拦截器的preHandle()
   2. 如果当前拦截器返回为false, 将triggerAfterCompletion() 倒序执行所有已经执行了preHandle()的拦截器 afterCompletion()
3. 如果任何一个拦截器返回false, 直接跳出不执行目标方法
4. 所有拦截器都返回True, 执行目标请求方法
5. 请求方法执行完毕后，执行 mappedHandler.applyPostHandle()--> 倒序执行拦截器责任链 postHandle() `img34`
6. 前面的步骤若有执行异常都会触发 triggerAfterCompletion()-->mappedHandler.applyAfterConcurrentHandlingStarted()--> 倒序执行拦截器责任链 afterCompletion()
7. 页面成功渲染完成,触发 mappedHandler.applyAfterConcurrentHandlingStarted()--> 倒序执行拦截器责任链 afterCompletion()

![img_33.png](assets/img_33.png)
![img_34.png](assets/img_34.png)
![img_35.png](assets/img_35.png)


# 7.过滤器
![img_filter.png](assets/img_filter.png)
> Spring 中不能处理用户请求，但可以用来提供过滤作用的一种Servlet规范。在请求进入容器之后，
> 还未进入Servlet之前进行预处理，并且在请求结束返回给前端这之间进行后期处理。
> 具体则是通过截取用户端的请求与响应信息，并对之进行过滤，
> 即在Servlet被调用之前检查Request对象，
> 修改Request Header和Request内容；在Servlet被调用之后检查Response对象，修改Response Header和Response内容。

`常用场景有:登陆过滤, 日志记录, 权限控制等`

<a id="7.1"></a>
## 7.1 Filter 接口
![img_36.png](assets/img_36.png)

## 7.2 过滤器实现
1. 编写一个过滤器实现Filter接口
2. 使用@WebFilter 注解，Spring启动时将把该过滤器注册到容器中
3. urlPatterns 指定拦截规则 [**如果是拦截所有/*，静态资源也会被拦截**]

<details>
  <summary>代码示例</summary>

```java
@WebFilter(urlPatterns = "/*", filterName = "LoginFilter")
public class LoginFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(LoginFilter.class);

    //添加排除路径
    private Set<String> excludePatterns = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("/", "/login")));

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("[ {} ] 登录过滤器初始化", this.getClass().getSimpleName());
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        logger.info("[ {} ] 执行", this.getClass().getSimpleName());
        //登录逻辑检查
        HttpServletRequest httpRequest = ((HttpServletRequest)request);
        HttpSession session = httpRequest.getSession();
        //获取当前请求路径信息
        String requestURI = httpRequest.getRequestURI();
        //匹配路径是否放行
        if(excludePatterns.contains(requestURI)){
            chain.doFilter(request, response);
            return;
        }

        Object loginUser = session.getAttribute("loginUser");
        if(loginUser==null){
            //登录拦截
            request.setAttribute("msg","登录过期");
            request.getRequestDispatcher("/").forward(request, response);
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        logger.info("[ {} ] 摧毁", this.getClass().getSimpleName());
        Filter.super.destroy();
    }
}
```
</details>

## 7.3 过滤器与拦截器的区别
1. 实现原理不同: 过滤器是基于函数回调,拦截器是基于java的反射机制的。
2. 使用范围不同: 过滤器依赖与servlet容器，拦截器不依赖servlet容器（拦截器是Spring容器内的，是Spring框架支持的）。
   * 因为Filter是Servlet规范中规定的，所有只能用于Web中；而拦截器既可以用于Web，也可以用于Application、Swing中。
3. 触发时机不同: 过滤器是在请求进入tomcat容器后，但请求进入servlet之前进行预处理的；请求结束返回也是，是在servlet处理完后，返回给前端之前。
    * 拦截器可以深入到方法的前后、异常抛出前后等更深层次的程度作处理（这里也在一定程度上论证了拦截器是利用java的反射机制实现的），所以在Spring框架中，优先使用拦截器。
4. 拦截的请求范围不同: 过滤器则可以对几乎所有的请求起作用；拦截器只能对action请求起作用。
5. 注入Bean情况不同: 拦截器先于ApplicationContext加载，所以拦截器无法注入Spring容器管理的bean。
    * 解决办法：拦截器不使用@Component加载，改为使用@Configuration+@Bean加载。
6. 控制执行顺序不同：过滤器用@Order注解控制执行顺序，通过@Order控制过滤器的级别，值越小级别越高越先执行；拦截器默认的执行顺序，就是它的注册顺序，也可以通过@Order手动设置控制，值越小越先执行。
    * 注意：拦截器有前置处理和后置处理，前置处理越先，后置处理就越后。

![img_37.png](assets/img_37.png)

## 7.4 过滤器和拦截器执行顺序图
![img_interceptor.png](assets/img_interceptor.png)

# 8.文件上传

## 8.1 页面表单
```xml
<form method="post" action="/upload" enctype="multipart/form-data">
    <input type="file" name="file"/>
    <input type="submit" value="提交"/>
</form>
```

## 8.2 文件上传
<details>
  <summary>代码示例</summary>

```java
    /**
     * @description MultipartFile 自动封装上传过来的文件
     */
    @PostMapping("/upload")
    public String uploadFile(@RequestParam String email, @RequestParam String username,
                             @RequestPart("headerImg") MultipartFile uploadFile,  MultipartFile[] photos) throws IOException {
        //获取项目编译路径
        String path = ClassUtils.getDefaultClassLoader().getResource("").getPath();
        String filePath = path + "static" + File.separator;
        String format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date());

        if(!uploadFile.isEmpty()){
            //为文件命名增加随机码和时间戳
            String originalFilename = format + "-" + UUID.randomUUID() + "-" + uploadFile.getOriginalFilename();
            File file = new File(filePath + originalFilename);
            uploadFile.transferTo(file);
        }
        return "main";
    }
```
</details>

## 8.3 自动配置原理
文件上传自动配置类-MultipartAutoConfiguration-MultipartProperties `img38`
1. 自动配置好了 StandardServletMultipartResolver  [标准Servlet文件上传解析器]
2. 原理步骤
 * doDispatch()->checkMultipart() 检测是否是文件上传请求
   * checkMultipart() `img39`使用文件上传解析器isMultipart()判断
   * 解析器封装原生request multipartResolver.resolveMultipart()，解析文件流parseRequest()包装成Part，返回StandardMultipartHttpServletRequest 文件上传请求
 * RequestPartMethodArgumentResolver 参数解析器来解析注解 @RequestPart，将之前已经封装好的文件内容，根据参数名返回 MultipartFile
   * 将request中文件信息封装为一个Map(MultiValueMap<String, MultipartFile>)-> MultipartHttpServletRequest.getMultipartFiles().get("key"")

![img_38.png](assets/img_38.png)
![img_39.png](assets/img_39.png)

# 9.异常处理
![img_40.png](assets/img_40.png)

## 9.1 默认规则
* 默认情况下，Spring Boot提供`/error`处理所有错误的映射
* 对于机器客户端，它将生成JSON响应，其中包含错误，HTTP状态和异常消息的详细信息 `img41`
* 对于浏览器客户端，响应一个 `whitelabel`错误视图，以HTML格式呈现相同的数据
* 要对其进行自定义，添加`View`解析为`error`
* 要完全替换默认行为，可以实现`ErrorController`并注册该类型的Bean定义，或添加`ErrorAttributes`类型的组件以使用现有机制但替换其内容。
* /error/下的4xx，5xx 等错误页面会被自动解析 `img42`

![img_41.png](assets/img_41.png)
![img_42.png](assets/img_42.png)

## 9.2 异常处理自动配置原理
> ErrorMvcAutoConfiguration 自动配置异常处理规则
1. 容器中的组件: 类型：默认错误属性 `DefaultErrorAttributes` -> id: errorAttributes(方法名即为id)
   * public class DefaultErrorAttributes implements ErrorAttributes, HandlerExceptionResolver {}
   * DefaultErrorAttributes: 定义错误页面中可以包含哪些数据(exception/trace/message/errors) `img45`
2. 容器中的组件: 类型：`BasicErrorController` -> id：basicErrorController (负责响应/error 请求, 并返回对应响应内容 json/error页面)
   * ${server.error.path:${error.path:/error} 读取配置为server.error.path 的值作为请求路径，无配置则默认为 /error
   * errorHtml() 响应返回错误html页面 -> ModelAndView("error", model)
     * 可自定义 /error 请求，返回自定义错误页面
   * error() 响应返回responseBody(json)数据 -> ResponseEntity<Map<String, Object>> 
   * 容器中有组件 View->id是error`ErrorView` （响应默认错误页）
   * 容器中放组件 `BeanNameViewResolver`(视图解析器), 避免被遗漏添加而无法来解析ErrorView(按照返回的视图名作为组件的id去容器中找View对象)
3. 容器中的组件: 类型: `DefaultErrorViewResolver` -> id：conventionErrorViewResolver `img44`
   * 如果发生错误，会以HTTP的状态码(404/500) 作为viewName(视图页地址)，找到真正的页面(404.html/500.html)
   * error/404、5xx.html


![img_43.png](assets/img_43.png)
![img_44.png](assets/img_44.png)
![img_45.png](assets/img_45.png)

## 9.3 异常处理步骤流程
1. doDispatch() 请求方法运行期间有任何异常(Exception/Throwable)都会被catch，转化为 dispatchException， 并且调用请求完成方法 webRequest.requestCompleted()
2. 无论报错与否，都会进入视图解析流程(页面渲染) `processDispatchResult()` `img46`
   * processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException)
3. processHandlerException() 处理请求过程中发生的异常，通过注册的HandlerExceptionResolvers确定错误ModelAndView  `img47`
   * 遍历所有的 handlerExceptionResolvers `img49`，看谁能处理当前异常 [HandlerExceptionResolver处理器异常解析器] `img48`
     * DefaultErrorAttributes先来处理异常，把异常信息保存到request域(storeErrorAttributes(request, ex)),并且返回null
     * 遍历结果没有resolver 能处理异常，抛出异常被 doDispatch() 方法最外层被捕获
       * 异常捕获里执行 triggerAfterCompletion(processedRequest, response, mappedHandler, ex)，依旧无法处理异常
       * 如果没有任何逻辑能处理这个异常，则Spring 底层就会发送/error请求，重新进去 doDispatch()方法进行处理
       * 匹配到底层的`BasicErrorController` 进行处理
       * errorHtml()->resolveErrorView() 解析错误视图；遍历所有的 ErrorViewResolver 匹配解析
       * 匹配到`ErrorMvcAutoConfiguration` 自动配置的默认的 DefaultErrorViewResolver, 结合响应状态码等响应错误页的地址-> error/500.html 
       * 模板引擎最终响应对应的状态码页面 error/500.html  error/404.html

![img_46.png](assets/img_46.png)
![img_47.png](assets/img_47.png)
![img_48.png](assets/img_48.png)
![img_49.png](assets/img_49.png)


## 9.4 定制错误处理逻辑
1. 自定义错误页
    * error/404.html   error/5xx.html；有精确的错误状态码页面就匹配精确，没有就找 4xx.html；如果都没有就触发白页
2. `@ControllerAdvice` + `@ExceptionHandler` 处理全局异常, 通过配置捕获异常类型，异常处理时 `ExceptionHandlerExceptionResolver` 会自动匹配异常处理逻辑
3. `@ResponseStatus` + 自定义异常, 底层是 `ResponseStatusExceptionResolver`, `img51` 把 `@Responsestatus` 注解的信息组装成MV(无效mv)，再调用 response.sendError(statusCode, resolvedReason)向服务器发送响应，最后由tomcat 服务器处理 /error请求，再次执行 /error解析
4. Spring底层的异常，如 MissingServletRequestParameterException(缺少请求参数异常), `DefaultHandlerExceptionResolver` 负责处理框架底层的异常 `img52`
    * 底层处理发送response.sendError(), 由tomcat 服务器处理 /error请求  `img_tomcat_error`
5. 自定义实现 HandlerExceptionResolver 处理异常, 可以作为默认的全局异常处理规则
6. ErrorViewResolver 实现自定义处理异常(/error请求)
    * response.sendError, /error请求就会转给controller
    * 异常没有解析器能处理, response.sendError->tomcat, /error请求就会转给controller
    * BasicErrorController 接收/error请求处理， 最后由ErrorViewResolver 解析 ModelAndView

<details>
  <summary>代码示例</summary>

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * @author Aloha
     * @date 2022/12/13 15:56
     * @description ExceptionHandler 指定处理异常数组, 返回 ModelAndView 或 viewname
     */
    @ExceptionHandler({ArithmeticException.class, NullPointerException.class})
    public String handlerException(Exception e){
        logger.info("自定义异常捕获:{}", e.getMessage());
        //返回视图地址
        return "login";
    }
}

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "用户太多")
public class UserTooManyException extends RuntimeException{

    public UserTooManyException() {}

    public UserTooManyException(String message) {
        super(message);
    }
}

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class CustomHandlerExceptionResolver implements HandlerExceptionResolver {

    private Logger logger = LoggerFactory.getLogger(CustomHandlerExceptionResolver.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        logger.info("自定义异常解析:{}", ex.toString());
        try {
            response.sendError(666, "服务器炸了");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ModelAndView();
    }
}

```
</details>

![img_51.png](assets/img_51.png)
![img_52.png](assets/img_52.png)
![img_tomcat_error.png](assets/img_tomcat_error.png)

# 10.Web原生组件注入（Servlet、Filter、Listener）
![img_53.png](assets/img_53.png)

## 10.1 使用注解注入
* @WebServlet(urlPatterns = "/my") `img55`
  * 继承`HttpServlet`， 配置`urlPatterns` 过滤url路径和 `@ServletComponentScan` 注解
  * @SpringBootApplication()->@ServletComponentScan() 配置Servlet 组件扫描路径 `img54`
  * 效果：直接响应，没有经过Spring的拦截器？
* @WebFilter() [详见过滤器实现](#7.1)
* @WebListener 
  * ContextListener implements ServletContextListener{} 可实现监听Servlet 初始化和销毁事件

![img_55.png](assets/img_55.png)
![img_54.png](assets/img_54.png)

## 10.2 使用Spring Bean注入
* ServletRegistrationBean
* FilterRegistrationBean
* ServletListenerRegistrationBean

```java
    @Bean
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
    }
```

## 10.3 自定义的Servlet 为什么没有经过之前定义的 登录拦截器(LoginInterceptor)
> 扩展: DispatchServlet注册流程 `img57`
* `DispatcherServletAutoConfiguration` -> `DispatcherServletConfiguration` -> `DispatcherServletRegistrationConfiguration`
* 通过自动配置注册了`DispatcherServlet`, 并通过注入`DispatcherServletRegistrationBean` 传入之前配置的Servlet实现注册
* 容器中自动配置了 DispatcherServlet, 读取WebMvcProperties 配置并绑定到属性, 对应的配置文件配置项是`spring.mvc`
  * 通过ServletRegistrationBean<DispatcherServlet> 把之前配置DispatcherServlet 注册到Spring中
* WebMvcProperties 中默认配置path 是 /, 即默认映射的是 / 路径

> 机制 使用Tomcat开发Servlet时，如果多个Servlet都能处理到同一层路径(/)，则以精确优先原则处理
访问/my 路径下请求，依据Tomcat 优先匹配逻辑会由B Servlet来处理
A Servlet: /my/
B Servlet: /my/1

![img_56.png](assets/img_56.png)
![img_57.png](assets/img_57.png)

## 10.4 嵌入式Servlet容器
![img_58.png](assets/img_58.png)
![img_59.png](assets/img_59.png)

### 10.4.1 Spring 嵌入式Servlet
> Spring 嵌入式Servlet原理
* SpringBoot应用启动发现当前是Web应用, 配置了web-starter 依赖包(内置了tomcat)
* Web应用会创建一个Web版的ioc容器`ServletWebServerApplicationContext`
* `ServletWebServerApplicationContext` 启动的时候寻找`ServletWebServerFactory`(Servlet 的web服务器工厂-> Servlet 的web服务器), 只允许配置一个web服务器
* SpringBoot底层默认有很多的WebServer工厂, `TomcatServletWebServerFactory`, `JettyServletWebServerFactory`, `UndertowServletWebServerFactory`
* 底层直接会有一个自动配置类 `ServletWebServerFactoryAutoConfiguration`
* `ServletWebServerFactoryAutoConfiguration` 导入了`ServletWebServerFactoryConfiguration` (工厂配置类)
* `ServletWebServerFactoryConfiguration` 配置类`img60` 根据动态判断系统中导入的Web服务器的包 (默认是web-starter中已导入tomcat包) 来创建工厂, 容器中就有 `TomcatServletWebServerFactory`
* `TomcatServletWebServerFactory` 创建了Tomcat服务器类对象, 并配置服务器属性启动, `img61` TomcatWebServer 的构造器拥有初始化方法initialize->this.tomcat.start() 启动了tomcat服务器
* 内嵌服务器，本质上就是创建了Tomcat服务器类对象, 手动把启动服务器的代码调用(Tomcat核心jar包存在), 区别于双击启动Tomcat服务器(tomcat-start.exe)

![img_60.png](assets/img_60.png)
![img_61.png](assets/img_61.png)

### 10.4.2 切换嵌入式Servlet容器
* 默认支持的webServer
    * Tomcat, Jetty, or Undertow
    * ServletWebServerApplicationContext 容器启动寻找ServletWebServerFactory 并引导创建服务器
* 切换服务器 `img62`

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <!-- 移除web tomcat依赖 -->
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>

 <!--切换嵌入式服务器jetty/undertow-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jetty</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-undertow</artifactId>
</dependency>

```

![img_62.png](assets/img_62.png)

### 10.4.3 定制Servlet容器
* 修改配置文件`ServerProperties`-> server.tomcat.xxx 
* 直接自定义 ConfigurableServletWebServerFactory `img63`
* 实现定制化器 `WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>`  `img63`
   * 把配置文件的值和`ServletWebServerFactory` 进行绑定
   * xxxxxCustomizer(定制化器), 可以改变xxxx的默认规则

![img_63.png](assets/img_63.png)

# 11.定制化原理
![img_64.png](assets/img_64.png)

## 11.1 定制化的常见方式 
> 原理分析:**场景starter**->xxxxAutoConfiguration(自动配置)->导入xxx组件(@Bean)->绑定xxxProperties(属性配置)->**绑定配置文件项**

* 根据xxxProperties 属性配置类来修改相应属性
* Web应用编写一个配置类 `@Configuration` 实现 `WebMvcConfigurer` 即可定制化web功能, 通过注入 @Bean 给容器中再扩展一些组件
* xxxxxCustomizer 定制化类
* 编写自定义的配置类-> xxxConfiguration+ @Bean替换, 增加容器中默认组件, 视图解析器

## 11.2 完全控制Spring MVC

* @EnableWebMvc + WebMvcConfigurer-> 注入@Bean, 可实现全面接管SpringMVC，所有规则全部自己重新配置, 实现定制和扩展功能
* 原理
1. `WebMvcAutoConfiguration` 默认的SpringMVC的自动配置功能类。静态资源、欢迎页.....
2. 一旦使用 `@EnableWebMvc` 会导入 `@Import(DelegatingWebMvcConfiguration.class)`
3. `DelegatingWebMvcConfiguration` 的作用，只保证SpringMVC最基本的使用
   * 把所有系统中的 `WebMvcConfigurer` 进行注册生效, 所有功能的定制都是这些 `WebMvcConfigurer` 合起来一起生效
   * 自动配置了一些非常底层的组件, `RequestMappingHandlerMapping` 等依赖的组件都是从容器中获取
   * public class DelegatingWebMvcConfiguration extends WebMvcConfigurationSupport {}
4. `WebMvcAutoConfiguration` 里面的配置要能生效必须 `@ConditionalOnMissingBean(WebMvcConfigurationSupport.class)`
5. `@EnableWebMvc` 导致了 `WebMvcAutoConfiguration`  没有生效。



# 12.数据访问
![img_65.png](assets/img_65.png)

## 11.1 SQL
### 11.1.1 数据源的自动配置-HikariDataSource
> 导入JDBC场景
```xml
<!--jdbc连接依赖-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jdbc</artifactId>
</dependency>

<!--mysql 驱动依赖-->
<!-- https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
```

`数据库版本需和驱动版本对应`
![img_67.png](assets/img_67.png)

```xml
默认版本：<mysql.version>8.0.29</mysql.version>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
<!--            <version>5.1.49</version>-->
        </dependency>
想要修改版本
1. 直接依赖引入具体版本(maven的就近依赖原则)-><version>
2. 修改pom mysql默认依赖版本->${mysql.version}
<properties>
    <java.version>1.8</java.version>
    <mysql.version>5.1.49</mysql.version>
</properties>
```

### 11.1.2 分析自动配置
* `DataSourceAutoConfiguration`: 数据源的自动配置
  * `DataSourceProperties` 修改数据源相关的配置: spring.datasource
  * 数据库连接池的配置 `PooledDataSourceCondition` `img68`, 是当容器中没有DataSource才会自动配置的
  * 底层配置好的数据源连接池是: `HikariDataSource`
* `DataSourceTransactionManagerAutoConfiguration`: 事务管理器的自动配置
* `JdbcTemplateAutoConfiguration`: JdbcTemplate的自动配置, 可以来对数据库进行crud
  * 可以修改这个配置项 `JdbcProperties` @ConfigurationProperties(prefix = "spring.jdbc") 来修改JdbcTemplate
  * `JdbcTemplateConfiguration` @Bean @Primary  注入了 JdbcTemplate 对象
* `JndiDataSourceAutoConfiguration`: jndi的自动配置
* `XADataSourceAutoConfiguration`: 分布式事务相关的

![img_68.png](assets/img_68.png)

### 11.1.3 修改配置项
```text
#数据库连接配置
spring.datasource.url=jdbc:mysql://localhost:3306/study_db
spring.datasource.username=root
spring.datasource.password=123456
```
![img_69.png](assets/img_69.png)


## 11.2 使用Druid数据源
> druid官方github地址 https://github.com/alibaba/druid
> https://github.com/alibaba/druid/tree/master/druid-spring-boot-starter

### 11.2.1 整合第三方技术的两种方式
* 自定义
* 找starter

### 11.2.2 自定义
### 11.2.2.1 创建数据源
```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.1.17</version>
</dependency>
<bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource"
		destroy-method="close">
		<property name="url" value="${jdbc.url}" />
		<property name="username" value="${jdbc.username}" />
		<property name="password" value="${jdbc.password}" />
		<property name="maxActive" value="20" />
		<property name="initialSize" value="1" />
		<property name="maxWait" value="60000" />
		<property name="minIdle" value="1" />
		<property name="timeBetweenEvictionRunsMillis" value="60000" />
		<property name="minEvictableIdleTimeMillis" value="300000" />
		<property name="testWhileIdle" value="true" />
		<property name="testOnBorrow" value="false" />
		<property name="testOnReturn" value="false" />
		<property name="poolPreparedStatements" value="true" />
		<property name="maxOpenPreparedStatements" value="20" />
```

```java
    /**
     * @author Aloha
     * @date 2022/12/21 16:12
     * @description 将配置属性与对象绑定
     */
    @ConfigurationProperties("spring.datasource")
    @Bean
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
    @Bean
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
    @Bean
    public FilterRegistrationBean webStatFilter(){
        WebStatFilter webStatFilter = new WebStatFilter();
        FilterRegistrationBean<WebStatFilter> filterRegistrationBean = new FilterRegistrationBean(webStatFilter);
        filterRegistrationBean.setUrlPatterns(Arrays.asList("/*"));
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }
    
```

### 11.2.2.2 StatViewServlet
StatViewServlet的用途包括：
* 提供监控信息展示的html页面
* 提供监控信息的JSON API


## 11.2.3 使用官方starter方式
```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-starter</artifactId>
    <version>1.1.17</version>
</dependency>
```

### 11.2.3.1 分析自动配置
* 扩展配置项 spring.datasource.druid
* DruidSpringAopConfiguration.class, 监控SpringBean的；配置项: spring.datasource.druid.aop-patterns
* DruidStatViewServletConfiguration.class, `img71` 监控页的配置：spring.datasource.druid.stat-view-servlet; 默认开启
* DruidWebStatFilterConfiguration.class, web监控配置: spring.datasource.druid.web-stat-filter; 默认开启
* DruidFilterConfiguration.class 所有Druid自己filter的配置(防火墙/监控功能等) `img72`

![img_71.png](assets/img_71.png)
![img_72.png](assets/img_72.png)

### 11.2.3.1 配置示例
```xml
#Druid配置
#开启StatViewServlet配置(整体的监控功能/监控页)
spring.datasource.druid.stat-view-servlet.enabled=true
#spring.datasource.druid.stat-view-servlet.url-pattern=/dddd/*
spring.datasource.druid.stat-view-servlet.login-username=druid
spring.datasource.druid.stat-view-servlet.login-password=123456

#开启内置filters
spring.datasource.druid.filters=stat,wall

#配置Spring Aop 监控
spring.datasource.druid.aop-patterns=com.example.running.*

#开启WebStatFilter配置(Web监控)
spring.datasource.druid.web-stat-filter.enabled=true
spring.datasource.druid.web-stat-filter.url-pattern=/*
spring.datasource.druid.web-stat-filter.exclusions=*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*

# Druid filter配置
# 个性化配置StatFilter
spring.datasource.druid.filter.stat.enabled=true
spring.datasource.druid.filter.stat.db-type=h2
spring.datasource.druid.filter.stat.log-slow-sql=true
spring.datasource.druid.filter.stat.slow-sql-millis=2000

# 个性化配置WallFilter
spring.datasource.druid.filter.wall.enabled=true
spring.datasource.druid.filter.wall.db-type=h2
spring.datasource.druid.filter.wall.config.delete-allow=false
spring.datasource.druid.filter.wall.config.drop-table-allow=false
```

# 12 整合MyBatis操作
> https://github.com/mybatis

* SpringBoot官方的Starter: spring-boot-starter-*
* 第三方的: *-spring-boot-starter

```xml
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>2.1.4</version>
</dependency>
```
![img_73.png](assets/img_73.png)
![img_74.png](assets/img_74.png)

## 12.1 配置模式
* 全局配置文件 `MybatisAutoConfiguration` `MybatisProperties`
* SqlSessionFactory: 自动配置了
* SqlSession: 自动配置了 `SqlSessionTemplate` 组合了`SqlSession`
* `MapperScannerRegistrarNotFoundConfiguration` @Import(AutoConfiguredMapperScannerRegistrar.class）
  * @Mapper:`AutoConfiguredMapperScannerRegistrar` 自动扫描标注了 @Mapper 注解的接口

```java
@EnableConfigurationProperties(MybatisProperties.class) //MyBatis配置项绑定类
@AutoConfigureAfter({ DataSourceAutoConfiguration.class, MybatisLanguageDriverAutoConfiguration.class })
public class MybatisAutoConfiguration{}

@ConfigurationProperties(prefix = "mybatis")
public class MybatisProperties{}
```

```xml
# mybatis配置  全局配置/mapper映射
# 指定全局配置文件路径
# mybatis.config-location=classpath:mybatis/mybatis-config.xml
mybatis.mapper-locations=classpath:mybatis/mapper/*.xml

# mybatis 个性化配置 (也可在 mybatis-config.xml 中配置), 与mybatis.config-location 配置冲突, 只能存在一个配置
# 是否开启驼峰命名自动映射
mybatis.configuration.map-underscore-to-camel-case=true
```

```java
//Sql Mapper 文件
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.running.service.mapper.CarBusniessMapper">

    <!--    List<Business> selectAll(); -->

    <select id="selectAll" resultType="com.example.running.bean.Business">
        select * from car_busniess where id=#{id}
    </select>
</mapper>

//Mapper 映射
@Mapper
public interface CarBusniessMapper {
    List<Business> selectAll(Long id);
}

@Service
public class CurdService {

    @Autowired
    CarBusniessMapper carBusniessMapper;

    //供外部调用，如controller
    public List<Business> selectCarBusiness(Long id){
        return carBusniessMapper.selectAll(id);
    }
}
```

* 导入mybatis官方starter
* 编写mapper接口, 标注`@Mapper`注解
* 编写sql映射文件并绑定mapper接口
* 在 `application.properties` 中指定Mapper配置文件的位置, 指定 mapper映射文件路径, 并且也可个性化配置mybatis 相关操作

## 12.2 注解模式
```java
@Mapper
public interface CityMapper {

    @Select("select * from city where id=#{id}")
    City getCityById(@Param("id") Long id);

    // useGeneratedKeys = true, keyProperty = "id"  使用自增主键，值为id
    /*@Insert("INSERT INTO city (name, state, country) VALUES(#{name}, #{state}, #{country})")
    @Options(useGeneratedKeys = true, keyProperty = "id")*/
    void insertCity(City city);

    @Select("select * from city")
    List<City> getAll();
}
```

## 12.3 混合模式
```xml

<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.running.service.mapper.CityMapper">
    <!--    void insertCity(City city); -->
    <insert id="insertCity" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO city (name, state, country) VALUES(#{name}, #{state}, #{country})
    </insert>

</mapper>
```
> 最佳实战
* 引入mybatis-starter
* 配置application.properties中，指定mapper-location路径
* 编写Mapper接口并标注@Mapper注解
* 简单方法直接注解方式
* 复杂方法编写mapper.xml进行绑定映射
* `@MapperScan("com.example.running.service.mapper")` MainApplication 增加Mapper文件扫描路径，Mapper接口就可以不用标注@Mapper注解 



## 12.4 整合 MyBatis-Plus 完成CRUD
> https://baomidou.com/
> 
> 建议安装 MybatisX 插件 https://baomidou.com/pages/ba5b24/

![img_75.png](assets/img_75.png)

### 12.4.1 自动配置
* `MybatisPlusAutoConfiguration` 配置类，`MybatisPlusProperties` 配置项绑定 
  * mybatis-plus.xxx 对mybatis-plus进行配置
* `SqlSessionFactory` 已自动配置, 底层将自动导入容器中默认的数据源`DataSource`
* `mapperLocations` 已自动配置, 默认值:`classpath*:/mapper/**/*.xml`
  * 任意包的类路径下的所有mapper文件夹下任意路径下的所有xml都是sql映射文件,  建议sql mapper映射文件，放在 resources/mapper下
* 自动配置了 `SqlSessionTemplate`
* `AutoConfiguredMapperScannerRegistrar` 注册自动扫描组件
  * `@Mapper` 标注的方法将被自动扫描, 建议直接 `@MapperScan`批量扫描就行

```java
/**
 * User 表接口, 继承 mybatisplus 数据库查询通用Service
 */
public interface UserService extends IService<User> {}

@Service("UserService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {}

@Controller
public class CurdController {

    @Autowired
    UserService userService;

    @ResponseBody
    @PostMapping("/selectUser")
    public List<User> selectUser(){
        //使用mybatisplus 查询数据库
        List<User> userList = userService.list();
        return userList;
    }

    @ResponseBody
    @PostMapping("/deleteUser")
    public String deleteUser(@RequestParam String userId){
        //使用mybatisplus
        boolean result = userService.removeById(userId);
        logger.info("deleteUser:{}", result);
        return "success";
    }

    @GetMapping("/dynamic_table")
    public String dynamicTable(@RequestParam(value = "pn", defaultValue = "1")Integer pageNum, Model model){
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
        boolean result = userService.removeById(id);
        //携带重定向 当前页数据
        redirectAttributes.addAttribute("pn", pageNum);
        //删除完成重定向回原列表
        return "redirect:/dynamic_table";
    }
    
}
```

## 12.5 NoSQL-Redis
> https://www.redis.net.cn
> 
> https://redis.io/docs/stack/get-started

![img_76.png](assets/img_76.png)


### 12.5.1 Redis自动配置
![img_77.png](assets/img_77.png)

* `RedisAutoConfiguration` 自动配置类 `RedisProperties` `spring.redis.xxx`是对redis的配置类
* `LettuceConnectionConfiguration` `JedisConnectionConfiguration` 自动注入连接工厂
* 自动注入了Redis模板引擎`RedisTemplate<Object, Object>` (xxxTemplate)
* 自动注入了`StringRedisTemplate` key:value都是String
* 底层使用 `StringRedisTemplate` `RedisTemplate` 来操作redis

### 12.5.2 RedisTemplate与Lettuce

```java
void testRedis() {
    //k-v形式存储数据
    ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
    valueOperations.set("redisTest1", "11");
    valueOperations.set("redisTest2", "22");
    String redisTest1 = valueOperations.get("redisTest1");
}
```


# 13 单元测试JUnit
![img_78.png](assets/img_78.png)

## 13.1 配置
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

> SpringBoot整合Junit
* 编写测试方法 `@Test`标注
* Junit类具有Spring的功能, 可使用`@Autowired`引入容器组件
* 比如 `@Transactional` 标注测试方法，测试完成后自动回滚

## 13.2 JUnit5常用注解
JUnit5的注解与JUnit4的注解有所变化
> https://junit.org/junit5/docs/current/user-guide/#writing-tests-annotations
* @Test: 表示方法是测试方法。但是与JUnit4的@Test不同，他的职责非常单一不能声明任何属性，拓展的测试将会由Jupiter提供额外测试
* @ParameterizedTest: 表示方法是参数化测试，下方会有详细介绍
* @RepeatedTest: 表示方法可重复执行，下方会有详细介绍
* @DisplayName: 为测试类或者测试方法设置展示名称
* @BeforeEach: 表示在每个单元测试之前执行
* @AfterEach: 表示在每个单元测试之后执行
* @BeforeAll: 表示在所有单元测试之前执行
* @AfterAll: 表示在所有单元测试之后执行
* @Tag: 表示单元测试类别，类似于JUnit4中的@Categories
* @Disabled: 表示测试类或测试方法不执行，类似于JUnit4中的@Ignore
* @Timeout: 表示测试方法运行如果超过了指定时间将会返回错误
* @ExtendWith: 为测试类或测试方法提供扩展类引用
  * @SpringBootTest 可参考该注解, 本质上是用了 @ExtendWith
    * @BootstrapWith(SpringBootTestContextBootstrapper.class)
    * @ExtendWith(SpringExtension.class)


## 13.3 断言(assertions)
> 断言（assertions）是测试方法中的核心部分，用来对测试需要满足的条件进行验证
这些断言方法都是 org.junit.jupiter.api.Assertions 的静态方法
JUnit 5 内置的断言可以分成如下几个类别:
检查业务逻辑返回的数据是否合理
所有的测试运行结束以后，会有一个详细的测试报告

### 13.3.1 简单断言
* assertEquals: 判断两个对象或两个原始类型是否相等
* assertNotEquals: 判断两个对象或两个原始类型是否不相等
* assertSame: 判断两个对象引用是否指向同—个对象
* assertNotSame: 判断两个对象引用是否指向不同的对象
* assertTrue: 判断给定的布尔值是否为true
* assertFalse: 判断给定的布尔值是否为false
* assertNull: 判断给定的对象引用是否为null
* assertNotNull: 判断给定的对象引用是否不为null

```java
    @Test
    void testAssertEquals(){
        int result = add(3,3);
        Assertions.assertEquals(4, result, "终于错了");
        logger.info("testAssertEquals:{}", result);
    }

    int add(int a, int b){
        return a+b;
    }

    /**
     * 断言所有逻辑
     */
    @Test
    void assertAll(){
        Assertions.assertAll("AssertAll Error",
                () -> Assertions.assertTrue(true),
                () -> Assertions.assertEquals("heihei1", "heihei", "不相同！"));
        logger.info("assertAll:{}", "assertAll");

    }

    /**
     * 异常断言
     */
    @Test
    void assertThrows(){
        Assertions.assertThrows(Throwable.class,
                () -> Assertions.assertTrue(false), "居然正常");
        logger.info("assertThrows:{}", "assertThrows");
    }


    //重复执行测试方法
    @RepeatedTest(10)
    void testRepeatedTest() {
        logger.info("test:{}", "testRepeatedTest");
    }

    @BeforeEach
    void testBeforeEach() {
        logger.info("test:{}", "testBeforeEach");
    }

    @BeforeAll
    static void testBeforeAll() {
        logger.info("test:{}", "testBeforeAll");
    }

    @AfterEach
    void testAfterEach() {
        logger.info("test:{}", "testAfterEach");
    }

    @AfterAll
    static void testAfterAll() {
        logger.info("test:{}", "testAfterAll");
    }
```

### 13.3.2 前置条件(Assertions)
> JUnit 5 中的前置条件（Assertions[假设]）类似于断言，不同之处在于不满足的断言会使得测试方法失败，
而不满足的前置条件只会使得测试方法的执行终止。前置条件可以看成是测试方法执行的前提，当该前提不满足时，就没有继续执行的必要。
```java
    @Test
    void testAssumptions(){
        Assumptions.assumeTrue(false);
    }
```

### 13.3.3 嵌套测试
> JUnit5可以通过Java中的内部类和@Nested 注解实现嵌套测试，从而可以更好的把相关的测试方法组织在一起。
在内部类中可以使用@BeforeEach 和@AfterEach 注解，而且嵌套的层次没有限制。


### 13.3.4 参数化测试
> 参数化测试是JUnit5很重要的一个新特性，它使得用不同的参数多次运行测试成为了可能，也为我们的单元测试带来许多便利。
利用@ValueSource等注解，指定入参，我们将可以使用不同的参数进行多次单元测试，而不需要每新增一个参数就新增一个单元测试，省去了很多冗余代码。

* @ValueSource: 为参数化测试指定入参来源, 支持八大基础类以及String类型,Class类型
* @NullSource: 表示为参数化测试提供一个null的入参
* @EnumSource: 表示为参数化测试提供一个枚举入参
* @CsvFileSource: 表示读取指定CSV文件内容作为参数化测试入参
* @MethodSource: 表示读取指定方法的返回值作为参数化测试入参(注意方法返回需要是一个流)

```java
    @ParameterizedTest
    @DisplayName("参数化测试")
    @ValueSource(strings = { "racecar", "radar", "able was I ere I saw elba" })
    @NullSource
    void parameterizedTest(String candidate) {
        assertTrue(true);
    }
            
    @ParameterizedTest
    @MethodSource("stringProvider")
    void testWithExplicitLocalMethodSource(String argument) {
        assertNotNull(argument);
    }

    static Stream<String> stringProvider() {
        return Stream.of("apple", "banana");
    }
```

> 当然如果参数化测试仅仅只能做到指定普通的入参还达不到让我觉得惊艳的地步。让我真正感到他的强大之处的地方在于他可以支持外部的各类入参。
如:CSV,YML,JSON 文件甚至方法的返回值也可以作为入参。只需要去实现ArgumentsProvider接口，任何外部文件都可以作为它的入参。



# 14 指标监控
## 14.1 SpringBoot Actuator
> https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html
![img_79.png](assets/img_79.png)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### 14.1.1 使用
> Actuator 可通过HTTP和JMX的公开端口来访问
```xml
# 通过 HTTP 公开除env和beans端点之外的所有内容
management.endpoints.web.exposure.include=*
management.endpoints.web.exposure.exclude=env,beans
```
> JMX访问: 命令jconsole
![img_82.png](assets/img_82.png)

* Http访问 http://localhost:8080/actuator/**
* 暴露所有监控信息为HTTP

> 可视化 https://github.com/codecentric/spring-boot-admin

## 14.2 Actuator Endpoint
> 最常使用的端点(见img79)

> http://localhost:8088/actuator/beans
> 
> http://localhost:8088/actuator/configprops
> 
> http://localhost:8088/actuator/metrics
> 
> http://localhost:8088/actuator/metrics/jvm.gc.pause

* Health：监控状况
* Metrics：运行时指标
* Loggers：日志记录

### 14.2.1 Health Endpoint
> 健康检查端点，我们一般用于在云平台，平台会定时的检查应用的健康状况，我们就需要Health Endpoint可以为平台返回当前应用的一系列组件健康状况的集合。
重要的几点
* health endpoint返回的结果，应该是一系列健康检查后的一个汇总报告
* 很多的健康检查默认已经自动配置好了，比如：数据库、redis等
* 可以很容易的添加自定义的健康检查机制
![img_80.png](assets/img_80.png)

### 14.2.2 Metrics Endpoint
> 提供详细的、层级的、空间指标信息，这些信息可以被pull（主动推送）或者push（被动获取）方式得到；

* 通过Metrics对接多种监控系统
* 简化核心Metrics开发
* 添加自定义Metrics或者扩展已有Metrics
![img_81.png](assets/img_81.png)

# 15 原理解析

## 15.1 Profile
> 为了方便多环境适配，springboot简化了profile功能

* 默认配置文件  application.yaml 任何时候都会加载
* 指定环境配置文件 application-{env}.yaml(`application-prod.properties/application-test.properties`)
* 激活指定环境
  * 配置文件激活: spring.profiles.active=prod
  * 使用命令行修改配置项: java -jar xxx.jar --spring.profiles.active=prod  --person.name=haha
    * 修改配置文件的任意值，命令行最高优先级
* 默认配置与环境配置同时生效
* 同名配置项，profile配置优先

### 15.1.1 @Profile条件装配功能
```java
@Configuration(proxyBeanMethods = false)
@Profile("production")
public class ProductionConfiguration {

    // ...

}
```
### 15.1.2 profile分组
spring.profiles.group.production[0]=proddb
spring.profiles.group.production[1]=prodmq
使用: --spring.profiles.active=production  激活

![img_83.png](assets/img_83.png)


## 15.2 外部化配置
![img_84.png](assets/img_84.png)

### 15.2.1  外部配置源
> 常用: Java属性文件、YAML文件、环境变量、命令行参数

`指定环境优先，外部优先，后面的可以覆盖前面的同名配置项`

### 15.2.2 配置文件查找位置
* classpath 根路径
* classpath 根路径下config目录
* jar包当前目录
* jar包当前目录的config目录
* /config子目录的直接子目录

![img_85.png](assets/img_85.png)
![img_87.png](assets/img_87.png)

### 15.2.3 配置文件加载顺序(优先级从低到高)
* 当前jar包内部的application.properties和application.yml
* 当前jar包内部的application-{profile}.properties 和 application-{profile}.yml
* 引用的外部jar包的application.properties和application.yml
* 引用的外部jar包的application-{profile}.properties 和 application-{profile}.yml
* 指定环境优先，外部优先，后面的可以覆盖前面的同名配置项


# 16 自定义starter
## 16.1 starter启动原理
> starter-pom引入 autoconfigurer 包

![img_86.png](assets/img_86.png)

* autoconfigure包中配置使用 `META-INF/spring.factories` 中 `EnableAutoConfiguration` 的值，使得项目启动加载指定的自动配置类
* 编写自动配置类 xxxAutoConfiguration -> xxxxProperties
  * @Configuration
  * @Conditional
  * @EnableConfigurationProperties
  * @Bean
  * ......
> 引入starter --- xxxAutoConfiguration --- 容器中放入组件 ---- 绑定xxxProperties ---- 配置项

## 16.2 自定义starter
* atguigu-hello-spring-boot-starter(启动器)
* atguigu-hello-spring-boot-starter-autoconfigure(自动配置包)
* maven 打包到本地仓库(install)

![img_88.png](assets/img_88.png)
![img_89.png](assets/img_89.png)
![img_90.png](assets/img_90.png)
![img_91.png](assets/img_91.png)

# 17 SpringBoot原理
> Spring原理`Spring注解`、SpringMVC原理、自动配置原理、SpringBoot原理

## 17.1 Application Events and Listeners
> 涉及到的系统组件包含如下

> https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-application-events-and-listeners

* ApplicationContextInitializer(初始化器)
* ApplicationListener(监听器)
* SpringApplicationRunListener(运行监听器)

## 17.2 SpringBoot启动过程
* 创建 SpringApplication
  * 保存一些信息 `img92`
  * 判断当前应用的类型。ClassUtils->Servlet
  * bootstrappers：初始启动引导器（List<Bootstrapper>）->去`spring.factories`文件中寻找类型 org.springframework.boot.Bootstrapper
  * 找 ApplicationContextInitializer-> 去spring.factories找 ApplicationContextInitializer
    * List<ApplicationContextInitializer<?>> initializers
  * 找 ApplicationListener->应用监听器, 去`spring.factories`找 `ApplicationListener`
    * List<ApplicationListener<?>> listeners
* 运行 SpringApplication
  * StopWatch
  * 记录应用的启动时间
  * 创建引导上下文（Context环境）`createBootstrapContext()`
    * 获取到所有之前的 `bootstrappers` 挨个执行 `intitialize()` 来完成对引导启动器上下文环境设置
  * 让当前应用进入headless模式。java.awt.headless
  * 获取所有 `RunListener`（运行监听器）`为了方便所有Listener进行事件感知` `img94`
    * getSpringFactoriesInstances 去spring.factories找 SpringApplicationRunListener.
    * 遍历 `SpringApplicationRunListener` 调用 starting 方法；
    * 相当于通知所有感兴趣系统正在启动过程的监听器，项目正在 starting。
  * 保存命令行参数`ApplicationArguments`
  * 准备环境 `prepareEnvironment（）`
    * 返回或者创建Servlet基础环境信息对象 `StandardServletEnvironment`
    * 配置环境信息对象 `configurePropertySources()`
      * 读取所有的配置源的配置属性值 
    * 绑定环境信息
    * 监听器`RunListener`调用 `listener.environmentPrepared()` 通知所有的监听器当前环境准备完成
  * 创建IOC容器 `createApplicationContext()`
    * 根据项目类型(Servlet)创建容器，
    * 当前会创建 `AnnotationConfigServletWebServerApplicationContext`
    * 将系统启动信息保存到容器中 `setApplicationStartup()`
  * 准备ApplicationContext IOC容器的基本信息 `prepareContext()`
    * 保存环境信息
    * IOC容器的后置处理流程。
    * 应用初始化器操作 `applyInitializers`
      * 遍历所有的 `ApplicationContextInitializer`, 调用 `initialize()` 来对ioc容器进行初始化扩展功能 `img95`
      * 遍历所有的 `RunListener` 监听器调用 `contextPrepared()` (EventPublishRunListenr)
    * 所有的监听器`RunListener` 监听器调用 `contextLoaded()`
  * 刷新IOC容器 `refreshContext()` `img96`
    * **调用Spring IOC 的 `refresh()` 进行Spring的初始化过程**
    * 实例化容器中的所有组件
  * 容器刷新完成后工作 `afterRefresh()`
  * 所有监听器调用 `listeners.started(context)` (系统启动已完成)
  * 调用所有 runners的`callRunners()`方法 (ApplicationRunner/CommandLineRunner) `img97`
    * 获取容器中的 `ApplicationRunner`
    * 获取容器中的 `CommandLineRunner`
    * 合并所有`runner`并且按照`@Order`进行排序
    * 遍历所有的`runner`, 调用 `run()` 方法
  * 如果以上有异常
    * 调用`RunListener` 的 `failed()`方法
  * 调用所有监听器`RunListener`的 `running()` 方法, 通知所有的监听器 running
  * `RunListener`的 `running()` 如果有问题, 继续通知 `failed()`, 调用所有 Listener 的 `failed()`


![img_92.png](assets/img_92.png)
![img_93.png](assets/img_93.png)
![img_94.png](assets/img_94.png)
![img_95.png](assets/img_95.png)
![img_96.png](assets/img_96.png)
![img_97.png](assets/img_97.png)
![img_98.png](assets/img_98.png)


# 答疑

## 1.1 @ConfigurationProperties
![img_70.png](assets/img_70.png)
> 用于外部化配置的注释，如果您想绑定和验证一些外部属性(例如，从. Properties文件中)，可以将此添加到类定义或@Configuration类中的@Bean方法中。

* JavaBean要映射的属性需要有public的setter方法 比如: @Data
* JavaBean需要交给容器管理 比如：@Component
* JavaBean中静态成员变量的不支持映射/绑定

```java
//绑定到类上
@ConfigurationProperties(prefix = "spring.datasource.hikari")
static class Hikari {

    //绑定到 @Bean方法上
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    HikariDataSource dataSource(DataSourceProperties properties) {
        HikariDataSource dataSource = createDataSource(properties, HikariDataSource.class);
        if (StringUtils.hasText(properties.getName())) {
            dataSource.setPoolName(properties.getName());
        }
        return dataSource;
    }

}
```


## 1.2 @Autowired自动注入
` @Autowired @Resource 二者依赖注入的区别`
* 来源不同: @Autowired 来自 Spring 框架，而 @Resource 来自于（Java）JSR-250；
* 依赖查找的顺序不同: @Autowired 先根据类型再根据名称查询，而 @Resource 先根据名称再根据类型查询
  * https://www.jb51.net/article/260980.htm
  * `@Resource` 注解源码可以在 Spring 源码的 org.springframework.context.annotation.CommonAnnotationBeanPostProcessor#postProcessPropertyValues 中分析得出。虽然 @Resource 是 JSR-250 定义的，但是由 Spring 提供了具体实现
* 支持的参数不同: @Autowired 只支持设置 1 个参数，而 @Resource 支持设置 7 个参数；
* 依赖注入的用法支持不同: @Autowired 既支持构造方法注入，又支持属性注入和 Setter 注入，而 @Resource 只支持属性注入和 Setter 注入；
* 编译器 IDEA 的提示不同: 当注入 Mapper 对象时，使用 @Autowired 注解编译器会提示错误，而使用 @Resource 注解则不会提示错误。

```java
class MainApplicationTests {
    
    //成员变量注入
    @Autowired //@Resource
    DataSource dataSource;

    //构造方法注入
    @Autowired
    public MainApplicationTests(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    //方法参数注入
    @Autowired //@Resource
    public void setService(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
```





