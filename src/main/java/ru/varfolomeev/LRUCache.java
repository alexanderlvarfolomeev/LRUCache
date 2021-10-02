package ru.varfolomeev;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LRUCache<K, V> extends AbstractMap<K, V> implements Map<K, V> {
    private final HashMap<K, V> valueHashMap;
    private final HashMap<K, LinkedList.Node<K>> nodeHashMap;
    private final LinkedList<K> list;

    private final int maxSize;

    public LRUCache(int maxSize) {
        this.valueHashMap = new HashMap<>();
        this.nodeHashMap = new HashMap<>();
        this.list = new LinkedList<>();
        if (maxSize >= 0) { //TODO: check maxSize == 0
            this.maxSize = maxSize;
        } else {
            throw new IllegalArgumentException("Illegal Max Size: " + maxSize);
        }
    }

    @Override
    public int size() {
        return valueHashMap.size();
    }

    @Override
    public boolean isEmpty() {
        return valueHashMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return valueHashMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return valueHashMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return valueHashMap.get(key);
    }

    @Override
    public V put(K key, V value) {
        V previousValue = null;
        LinkedList.Node<K> addedNode;
        if (containsKey(key)) {
            previousValue = valueHashMap.put(key, value);
            addedNode = nodeHashMap.get(key);
        } else {
            if (size() == maxSize) {
                K removedKey = list.removeFirst();
                nodeHashMap.remove(removedKey);
                valueHashMap.remove(removedKey);
            }
            valueHashMap.put(key, value);
            addedNode = list.addLast(key);
            nodeHashMap.put(key, addedNode);
        }
        list.propagate(addedNode);
        return previousValue;
    }

    @Override
    public V remove(Object key) {
        V removedValue = valueHashMap.remove(key);
        LinkedList.Node<K> removedNode = nodeHashMap.remove(key);
        list.remove(removedNode);
        return removedValue;
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException("putAll");
    }

    @Override
    public void clear() {
        valueHashMap.clear();
        list.clear();
    }

    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException("entrySet");
    }

    private static class LinkedList<V> {
        private Node<V> first;
        private Node<V> last;

        @NotNull
        public Node<V> addLast(V v) {
            return addLast(new Node<>(v));
        }

        @NotNull
        private Node<V> addLast(@NotNull Node<V> newNode) {
            newNode.previous = last;
            newNode.next = null;

            if (last == null) {
                first = newNode;
            } else {
                last.next = newNode;
            }
            last = newNode;
            return newNode;
        }

        public V removeFirst() {
            if (first == null) {
                throw new NoSuchElementException();
            } else {
                V removedValue = first.value;
                first = first.next;
                first.previous = null;
                return removedValue;
            }
        }

        public void remove(@NotNull Node<V> node) {
            if (node == last) {
                last = node.previous;
            } else {
                node.next.previous = node.previous;
            }

            if (node == first) {
                first = node.next;
            } else {
                node.previous.next = node.next;
            }
        }

        public void propagate(@NotNull Node<V> node) {
            if (node == last) {
                return;
            }
            remove(node);
            addLast(node);
        }

        public void clear() {
            first = null;
            last = null;
        }

        private static class Node<V> {
            private final V value;
            private Node<V> previous;
            private Node<V> next;


            private Node(V value) {
                this.value = value;
                previous = null;
                next = null;
            }
        }
    }
}
