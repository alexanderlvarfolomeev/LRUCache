package ru.varfolomeev;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.IntStream;

public class LinkedListTest {
    private int DEFAULT_MAX_SIZE = 3;

    private LRUCache<Integer, Integer> cache;

    @Before
    public void clearCache() {
        cache = new LRUCache<>(DEFAULT_MAX_SIZE);
    }

    @Test
    public void testNewCacheIsEmpty() {
        Assert.assertTrue(cache.isEmpty());
        Assert.assertEquals(0, cache.size());
    }

    @Test
    public void testDataInsertionWithoutOverflow() {
        cache.put(1, 4);
        cache.put(2, 5);
        cache.put(3, 6);
        Assert.assertFalse(cache.isEmpty());
        Assert.assertEquals(3, cache.size());
        Assert.assertTrue(cache.containsKey(1));
        Assert.assertTrue(cache.containsKey(2));
        Assert.assertTrue(cache.containsKey(3));
    }
}
