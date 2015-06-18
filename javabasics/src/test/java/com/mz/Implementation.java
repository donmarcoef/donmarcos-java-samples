/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mz;

import java.util.function.Function;

/**
 *
 * @author MZimmermann
 */
public class Implementation<A,R> implements Abstraction<A, R>{

    private final Function<A,R> f1, f2;
    
    private Implementation(Function<A,R> f1, Function<A,R> f2) {
        this.f1 = f1;
        this.f2 = f2;
    }
    
    @Override
    public R m1(A a) {
        return f1.apply(a);
    }

    @Override
    public R m2(A a) {
        return f2.apply(a);
    }
    
    public static <S, T> Implementation<S, T> of(Function<S, T> f1, Function<S, T> f2) {
        return new Implementation<>(f1, f2);
    }
}
