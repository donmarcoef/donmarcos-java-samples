/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author MZimmermann
 */
public class LambdaTest {

    @Test
    public void streamForEach() {
        System.out.println("streamForEach");

        createTestPerson().getFriends().stream().forEach((p) -> System.out.println(p));
    }

    @Test
    public void methodReference() {
        System.out.println("methodReference");

        createTestPerson().getFriends().stream().forEach(Person::output);
    }

    @Test
    public void unboundedReeiver() {
        System.out.println("unboundedReeiver");

        List<String> sortedFriendsByName = createTestPerson().getFriends().stream().map((p) -> p.getName()).sorted(String::compareToIgnoreCase).collect(Collectors.toList());

        sortedFriendsByName.stream().forEach(System.out::println);
    }

    @Test
    public void streamFilter() {
        System.out.println("streamFilter");

        List<Person> filteredFriends = createTestPerson().getFriends().stream().filter((p) -> p.getAge() >= 30).collect(Collectors.toList());

        assertEquals(2, filteredFriends.size());
    }

    @Test
    public void streamMap() {
        System.out.println("streamMap");

        Person person = createTestPerson();
        List<String> friendNames = person.getFriends().stream().map((p) -> p.getName()).collect(Collectors.toList());

        for (int i = 0; i < friendNames.size(); i++) {
            assertEquals(person.getFriends().get(i).getName(), friendNames.get(i));
        }

        assertEquals(3, friendNames.size());
    }

    @Test
    public void bindingToLocalVariables() throws Exception {
        System.out.println("bindingToLocalVariables");

        AtomicLong cnt = new AtomicLong();
        ExecutorService pool = Executors.newCachedThreadPool();
        Runnable task = () -> {
            while (cnt.get() < 10) {
                System.out.println(cnt.incrementAndGet());
            }
        };
        pool.execute(task);
        while (cnt.get() < 10) { /* nothing */ }
        pool.shutdownNow();
    }

    /**
     * new programming pattern *
     */
    @Test
    public void executeAround() {
        System.out.println("executeAround");

        Supplier<Long> sup = () -> {
            System.out.println("critical block");
            return System.currentTimeMillis();
        };

        Long time = withLock(new ReentrantReadWriteLock().readLock(), sup);

        System.out.println(time);
    }

    private <T> T withLock(Lock lock, Supplier<? extends T> critical) {
        lock.lock();

        try {
            return critical.get();
        } finally {
            lock.unlock();
        }
    }

    /**
     * reactive-style programming
     */
    @Test
    public void reactiveStyleProgramming() {
        System.out.println("reactiveStyleProgramming");

        ExecutorService pool = Executors.newCachedThreadPool();
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "long operation", pool);
        future.thenAccept(info -> System.out.println("info: " + info));
    }

    @Test
    public void lazyEvaluation() {
        System.out.println("lazyEvaluation");

        lazy(() -> {
            System.out.println("calc x");
            return 0;
        }, () -> {
            System.out.println("calc y");
            return 1;
        });
    }

    private void lazy(IntSupplier xs, IntSupplier ys) {
        if (xs.getAsInt() != 0 && ys.getAsInt() != 0) {
            // exec
        }
    }

    /*
     * parameterization
     */
    @Test
    public void parameterization() {
        System.out.println("parameterization");

        Function<Integer, Runnable> tastaskGen = (a) -> () -> {
            System.out.println("use param " + a);
        };

        new Thread(tastaskGen.apply(5)).start();
        new Thread(tastaskGen.apply(100)).start();
    }

    /*
     * fluent programming
     */
    @Test
    public void fluentProgramming() {
        System.out.println("fluentProgramming");

        List<Person> friends = createTestPerson().getFriends();

        List<String> filterSortedList = friends.stream().filter(p -> p.getAge() > 30).map(p -> p.getName()).sorted(String::compareToIgnoreCase).collect(Collectors.toList());
    }

    /*
     * highter order function -> functions that except and return functions
     * 
     * example comparator
     */
    @Test
    public void higherOrderFunctions() {
        System.out.println("higherOrderFunctions");

        List<Person> friends = createTestPerson().getFriends();
        Comparator<Person> ageComparator = Comparator.comparing((p) -> p.getAge());
        Comparator<Person> nameCompartor = Comparator.comparing((p) -> p.getName());

        friends.stream().sorted(ageComparator.thenComparing(nameCompartor)).forEach(System.out::println);
    }
    
    /*
    * abstraction
    */
    @Test
    public void abstraction() {
        System.out.println("abstraction");
        
        Function<String, Long> f1 = (s) -> { System.out.println("impl for m1 parameter " + s); return 10l; };
        Function<String, Long> f2 = (s) -> { System.out.println("impl for m2 parameter " + s); return 20l; };
        
        Implementation<String, Long> impl = Implementation.of(f1, f2);
        
        impl.m1("parameter 1");
        impl.m2("parameter 2");
    }

    @After
    public void after() {
        System.err.println("-----------");
    }

    private Person createTestPerson() {
        return new Person("Marco", 31).addFriend(new Person("Christian", 30)).addFriend(new Person("Daniel", 30)).addFriend(new Person("Patrick", 26));
    }

    private class Person {

        private final String name;
        private final int age;
        private final List<Person> friends = new ArrayList<>();

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public int getAge() {
            return age;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "name: " + name + " age: " + age;
        }

        public Person addFriend(Person friend) {
            Objects.requireNonNull(friend, "friend can't be null");

            friends.add(friend);

            return this;
        }

        public List<Person> getFriends() {
            return Collections.unmodifiableList(friends);
        }

        public void output() {
            System.out.println(this);
        }
    }
}
