package ru.varfolomeev;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.IntStream;

public class LRUCacheTest {
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
    public void testEmptinessAfterClear() {
        cache.put(1, 1);
        cache.put(2, 2);
        cache.clear();
        Assert.assertTrue(cache.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeMaxSize() {
        LRUCache<Integer, Integer> invalidCache = new LRUCache<>(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZeroMaxSize() {
        LRUCache<Integer, Integer> invalidCache = new LRUCache<>(0);
    }

    @Test
    public void testInsertedValues() {
        cache.put(1, 4);
        cache.put(2, 5);
        cache.put(3, 6);
        Assert.assertEquals((Integer)4, cache.get(1));
        Assert.assertEquals((Integer)5, cache.get(2));
        Assert.assertEquals((Integer)6, cache.get(3));
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

    @Test
    public void testDataInsertionWithOneOverflow() {
        cache.put(1, 5);
        cache.put(2, 6);
        cache.put(3, 7);
        cache.put(4, 8);
        Assert.assertFalse(cache.isEmpty());
        Assert.assertEquals(3, cache.size());
        Assert.assertTrue(cache.containsKey(2));
        Assert.assertTrue(cache.containsKey(3));
        Assert.assertTrue(cache.containsKey(4));
        Assert.assertFalse(cache.containsKey(1));
        cache.put(1, 5);
        Assert.assertEquals(3, cache.size());
        Assert.assertTrue(cache.containsKey(1));
        Assert.assertFalse(cache.containsKey(2));
    }

    @Test
    public void testDataInsertionWithOverflow() {
        IntStream.range(0, 10).forEach(i -> cache.put(i, i));

        Assert.assertTrue(cache.containsKey(7));
        Assert.assertTrue(cache.containsKey(8));
        Assert.assertTrue(cache.containsKey(9));
    }

    @Test
    public void testDataInsertionWithDuplicateKey() {
        cache.put(1, 5);
        cache.put(2, 6);
        cache.put(3, 7);
        cache.put(1, 8);
        Assert.assertTrue(cache.containsKey(1));
        Assert.assertTrue(cache.containsKey(2));
        Assert.assertTrue(cache.containsKey(3));
        Assert.assertEquals((Integer)8, cache.get(1));
    }
}
