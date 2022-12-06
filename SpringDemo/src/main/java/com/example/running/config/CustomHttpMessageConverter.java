package com.example.running.config;

import com.example.running.annotations.Person;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

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
