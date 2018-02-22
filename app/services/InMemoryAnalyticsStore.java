package services;

import com.google.common.collect.ImmutableMap;
import interfaces.AnalyticsStore;
import lombok.val;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.stream.Collectors.*;

public class InMemoryAnalyticsStore implements AnalyticsStore {

    private BoundedStack<Map<String, Object>> events = new BoundedStack<>(10000);

    @Override
    public void recordEvent(Map<String, Object> data) {
        events.push(data);
    }

    @Override
    public CompletableFuture<List<Map<String, Object>>> recentEvents(int limit) {
        return CompletableFuture.supplyAsync(() -> new ArrayList<>(events.getAll()));
    }

    @Override
    public CompletableFuture<Map<Integer, Long>> pageVisits() {
        Map<Integer, Long> pageVisits = events.getAll().stream()
            .filter(event -> event.containsKey("pageNumber"))
            .collect(groupingBy(event -> (int) event.get("pageNumber"),
                                counting()));

        return CompletableFuture.supplyAsync(() -> pageVisits);
    }

    @Override
    public CompletableFuture<Long> pageVisits(String eventType, LocalDateTime from) {
        val allVisits = events.getAll().stream()
                .filter(event -> event.containsKey("type"))
                .filter(event -> event.get("type").equals(eventType))
                .count();

        return CompletableFuture.supplyAsync(() -> allVisits);
    }

    @Override
    public CompletableFuture<Long> uniquePageVisits(String eventType, LocalDateTime from) {
        val uniqueUserVisits = (long)events.getAll().stream()
                .filter(event -> event.containsKey("type"))
                .filter(event -> event.get("type").equals(eventType))
                .collect(groupingBy(event -> (String)event.get("username"),
                        counting())).size();

        return CompletableFuture.supplyAsync(() -> uniqueUserVisits);
    }

    @Override
    public CompletableFuture<Map<Integer, Long>> rankGrouping(String eventType, LocalDateTime from) {
        Map<Integer, Long> rankGrouping = events.getAll().stream()
                .filter(event -> event.containsKey("type"))
                .filter(event -> event.get("type").equals(eventType))
                .collect(groupingBy(event -> (int) event.get("rankingIndex"),
                        counting()));

        return CompletableFuture.supplyAsync(() -> rankGrouping);
    }

    @Override
    public CompletableFuture<Map<String, Long>> eventOutcome(String eventType, LocalDateTime from) {
        Map<String, Long> outcomeGrouping = events.getAll().stream()
                .filter(event -> event.containsKey("type"))
                .filter(event -> event.get("type").equals(eventType))
                .collect(groupingBy(event -> (String)event.get("eventType"),
                        counting()));

        return CompletableFuture.supplyAsync(() -> outcomeGrouping);
    }

    @Override
    public CompletableFuture<Map<Long, Long>> durationBetween(String firstEventType, String secondEventType, LocalDateTime from, long groupBySeconds) {
        return CompletableFuture.supplyAsync(() -> ImmutableMap.of());
    }

    @Override
    public CompletableFuture<Boolean> isUp() {
        return CompletableFuture.completedFuture(Boolean.TRUE);
    }

    public class BoundedStack<T> {
        private final Deque<T> list = new ConcurrentLinkedDeque<>();
        private final int maxEntries;
        private final ReentrantLock lock = new ReentrantLock();

        BoundedStack(final int maxEntries) {
            this.maxEntries = maxEntries;
        }

        public void push(final T item) {
            lock.lock();
            try {
                list.push(item);
                if (list.size() > maxEntries) {
                    list.removeLast();
                }
            } finally {
                lock.unlock();
            }
        }

        public Collection<T> getAll() {
            return list;
        }

        @Override
        public String toString() {
            return "BoundedStack{" +
                "list=" + list +
                '}';
        }
    }


}
