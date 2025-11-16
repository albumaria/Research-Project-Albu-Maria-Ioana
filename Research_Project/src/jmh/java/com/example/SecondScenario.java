package com.example;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@State(Scope.Thread)
@Warmup(iterations = 2, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 2, time = 5, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class SecondScenario {
    @Param({"1000000"})
    private int N;

    @Param({"2", "3", "5", "10", "20"})
    private int M;

    private List<Integer> list;

    @Setup(Level.Trial)
    public void setupTrial() {
        list = new ArrayList<>();
        Random random = new Random(42);

        for(int i = 0; i < N; i++) {
            list.add(random.nextInt());
        }
    }

    @Benchmark
    public void CollectionCaching(Blackhole blackhole) {
        List<Integer> streamResult = list.stream()
                .map(x -> (int)(Math.sqrt(x) * Math.log(x)))
                .sorted()
                .collect(Collectors.toCollection(ArrayList::new));

        for (int i = 0; i < M; i++) {
            int result = streamResult.stream().max(Integer::compare).get();
            blackhole.consume(result);
        }
    }

    @Benchmark
    public void PipelineRecreation(Blackhole blackhole) {
        for (int i = 0; i < M; i++) {
            int result = list.stream()
                    .map(x -> (int)(Math.sqrt(x) * Math.log(x)))
                    .sorted()
                    .max(Integer::compare)
                    .get();
            blackhole.consume(result);
        }
    }

}
