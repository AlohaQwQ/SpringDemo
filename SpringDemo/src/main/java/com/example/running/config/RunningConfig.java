package com.example.running.config;

import com.example.running.annotations.Pet;
import com.example.running.bean.Cat;
import com.example.running.bean.Dog;
import com.example.running.bean.Zhouzhou;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.convert.converter.ConditionalConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;
import org.yaml.snakeyaml.LoaderOptions;

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
@Import(LoaderOptions.class)
@Configuration(proxyBeanMethods = true)
@ConditionalOnMissingBean(name = "cat")
//@ImportResource("classpath:beans.xml")
@EnableConfigurationProperties(Dog.class)
public class RunningConfig {

    /**
     * @author hongyuan
     * @since 2022/7/25 20:26
     * 在容器中添加bena组件，方法名为组件id，返回类型就是组件类型，返回的值，就是组件在容器中的实例。可重命名组件ID
     * @Bean 注册的对象都为单实例，无论被调用多少次
     * @ConditionalOnBean 当容器中有指定bean 组件时，本注入才生效
     **/
//    @ConditionalOnBean(value = Cat.class)
//    @Bean
    public Zhouzhou zhouzhou(){
        Zhouzhou zhouzhou = new Zhouzhou();
        zhouzhou.setCat(cat());
        zhouzhou.setDog(dog());
        System.out.println("zhouzhou-name:" + zhouzhou.getName());
        System.out.println("zhouzhou-cat:" + zhouzhou.getCat());
       // System.out.println("zhouzhou-dog:" + zhouzhou.getDog() + "-name:" + zhouzhou.getDog().getName() + "-Autowired:"+ dog.getName());
        return zhouzhou;
    }

    @Bean
    public Cat cat(){
        return new Cat("food", "miao");
    }


//    @Bean
    public Dog dog(){
        return new Dog();
    }

    //自定义HiddenHttpMethodFilter 隐藏Http方法过滤器，自定义方法前缀(_method)
    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter(){
        //获取springboot 容器中HiddenHttpMethodFilter 实体类
        //HiddenHttpMethodFilter hiddenHttpMethodFilter = context.getBean(HiddenHttpMethodFilter.class);
        //hiddenHttpMethodFilter.setMethodParam("qq");
        HiddenHttpMethodFilter methodFilter = new HiddenHttpMethodFilter();
        methodFilter.setMethodParam("_m");
        return methodFilter;
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

            /**
             * Add {@link Converter Converters} and {@link Formatter Formatters} in addition to the ones
             * registered by default.
             * 在默认注册的基础上增加{@link Converter Converter}和{@link Formatter Formatters}。
             */
            @Override
            public void addFormatters(FormatterRegistry registry) {
                //增加自定义参数类型转换器
                registry.addConverter(new Converter<String, Pet>() {

                    /**
                     * A converter converts a source object of type {@code S} to a target of type {@code T}.
                     * <p>Implementations of this interface are thread-safe and can be shared.
                     * <p>Implementations may additionally implement {@link ConditionalConverter}.
                     * 转换器将类型为{@code S}的源对象转换为类型为{@code T}的目标。该接口的实现是线程安全的，可以共享。<p>实现可以额外实现{@link ConditionalConverter}。
                     * @author Keith Donald
                     * @author Josh Cummings
                     * @since 3.0
                     * @param <S> the source type
                     * @param <T> the target type
                     */


                    /**
                     * Convert the source object of type {@code S} to target type {@code T}.
                     * @param source the source object to convert, which must be an instance of {@code S} (never {@code null})
                     * @return the converted object, which must be an instance of {@code T} (potentially {@code null})
                     * @throws IllegalArgumentException if the source cannot be converted to the desired target type
                     * 将类型{@code S}的源对象转换为目标类型{@code T}。
                     */
                    @Override
                    public Pet convert(String source) {
                        //喵喵，3
                        if(StringUtils.hasLength(source)){
                            String[] split = source.split(",");
                            Pet pet = new Pet();
                            pet.setName(split[0]);
                            pet.setAge(split[1]);
                            return pet;
                        }
                        return null;
                    }

                    /**
                     * Construct a composed {@link Converter} that first applies this {@link Converter}
                     * to its input, and then applies the {@code after} {@link Converter} to the
                     * result.
                     * 构造一个组合的{@link Converter}，首先将这个{@link Converter}应用于它的输入，然后将}{@link Converter}之后的{@code应用于结果。
                     *
                     * @param after the {@link Converter} to apply after this {@link Converter}
                     * is applied
                     * @param <U> the type of output of both the {@code after} {@link Converter}
                     * and the composed {@link Converter}
                     * @return a composed {@link Converter} that first applies this {@link Converter}
                     * and then applies the {@code after} {@link Converter}
                     * @since 5.3
                     */
                    @Override
                    public <U> Converter<String, U> andThen(Converter<? super Pet, ? extends U> after) {
                        return Converter.super.andThen(after);
                    }
                });
            }
        };
        return webMvcConfigurer;
    }

}
