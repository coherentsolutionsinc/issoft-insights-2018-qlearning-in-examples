package com.issoft.conf.util;

import com.issoft.conf.action.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collector;

public class CustomCollectors {

    public static Collector RANDOM_COLLECTOR = Collector.of(ArrayList::new, List::add, (actions1, actions2) -> {
        actions1.addAll(actions2);
        return actions1;
    }, total -> total.get(ThreadLocalRandom.current().nextInt(total.size())));

    public static Collector<Action, List<Action>, Action> RANDOM_ACTION_COLLECTOR = Collector.of(ArrayList::new, List::add, (actions1, actions2) -> {
        actions1.addAll(actions2);
        return actions1;
    }, total -> total.get(ThreadLocalRandom.current().nextInt(total.size())));

    public static <T> Collector<T, List<T>, T> randomItem() {
        return Collector.of(ArrayList::new,
                List::add, (actions1, actions2) -> {
                    actions1.addAll(actions2);
                    return actions1;
                }, total -> total.get(ThreadLocalRandom.current().nextInt(total.size())));
    }
}
