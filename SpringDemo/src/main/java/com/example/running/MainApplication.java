package com.example.running;

import com.example.running.bean.Cat;
import com.example.running.bean.Dog;
import com.example.running.bean.Zhouzhou;
import com.example.running.config.RunningConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.yaml.snakeyaml.LoaderOptions;

/**
 * @author Aloha
 * @date 2022/7/24 17:11
 * @description 这是一个SpringBoot主程序入口类
 */
//scanBasePackages-修改包扫描路径
@SpringBootApplication()
@SpringBootConfiguration(proxyBeanMethods = false)
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

        boolean bendan = context.containsBean("bendan");
//        Zhouzhou zhouzhou = context.getBean(Zhouzhou.class);
        System.out.println("zhouzhou:" + bendan);

        //7.通过 @ImportResource 的方式导入组件
        Cat cat22 = (Cat) context.getBean("cat22");
        Dog dog22 = (Dog) context.getBean("dog22");
        System.out.println("cat22:" + cat22);
        System.out.println("dog22:" + dog22);



    }

}
