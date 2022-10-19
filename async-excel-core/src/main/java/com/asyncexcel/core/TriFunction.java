package com.asyncexcel.core;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/10/18 17:25
 */
@FunctionalInterface
public interface TriFunction<S,T, U, R> {
    R apply(S s, T t, U u);
}
