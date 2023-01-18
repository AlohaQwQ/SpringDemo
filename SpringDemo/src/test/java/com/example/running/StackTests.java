package com.example.running;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EmptyStackException;
import java.util.Stack;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("嵌套测试")
class StackTests {

    static Logger logger = LoggerFactory.getLogger(StackTests.class);

    Stack<Object> stack;
    
    /**
     * @author Aloha
     * @date 2023/1/18 22:01
     * @description 参数化测试
     */
    @ParameterizedTest
    @DisplayName("参数化测试")
    @ValueSource(strings = { "racecar", "radar", "able was I ere I saw elba" })
    @NullSource
    void parameterizedTest(String candidate) {
        logger.info("parameterizedTest:{}", candidate);
        assertTrue(true);
    }

    /**
     * @author Aloha
     * @date 2023/1/18 22:05
     * @description 读取指定方法的返回值作为参数化测试入参(注意方法返回需要是一个流)
     */
    @ParameterizedTest
    @MethodSource("stringProvider")
    void testWithExplicitLocalMethodSource(String argument) {
        logger.info("testWithExplicitLocalMethodSource:{}", argument);
        assertNotNull(argument);
    }

    static Stream<String> stringProvider() {
        return Stream.of("apple", "banana");
    }


    @Test
    @DisplayName("is instantiated with new Stack()")
    void isInstantiatedWithNew() {
        new Stack<>();
        //嵌套测试使用时，内层的 @BeforeEach 等前置测试方法不影响外层逻辑
        assertNull(stack);
    }

    @Nested
    @DisplayName("when new")
    class WhenNew {

        @BeforeEach
        void createNewStack() {
            stack = new Stack<>();
        }

        @Test
        @DisplayName("is empty")
        void isEmpty() {
            assertTrue(stack.isEmpty());
        }

        @Test
        @DisplayName("throws EmptyStackException when popped")
        void throwsExceptionWhenPopped() {
            assertThrows(EmptyStackException.class, stack::pop);
        }

        @Test
        @DisplayName("throws EmptyStackException when peeked")
        void throwsExceptionWhenPeeked() {
            assertThrows(EmptyStackException.class, stack::peek);
        }

        @Nested
        @DisplayName("after pushing an element")
        class AfterPushing {

            String anElement = "an element";

            @BeforeEach
            void pushAnElement() {
                //外层已创建了stack 对象
                stack.push(anElement);
            }

            /**
             * 内层的Test方法不仅会触发内层的 @BeforeEach 等前置方法，也会触发外层的方法
             */
            @Test
            @DisplayName("it is no longer empty")
            void isNotEmpty() {
                //stack 不为空并且有元素
                assertFalse(stack.isEmpty());
            }

            @Test
            @DisplayName("returns the element when popped and is empty")
            void returnElementWhenPopped() {
                assertEquals(anElement, stack.pop());
                assertTrue(stack.isEmpty());
            }

            @Test
            @DisplayName("returns the element when peeked but remains not empty")
            void returnElementWhenPeeked() {
                assertEquals(anElement, stack.peek());
                assertFalse(stack.isEmpty());
            }
        }
    }
}
