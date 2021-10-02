package ru.varfolomeev;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LRUCache<K, V> {
    private final HashMap<K, EntryList.Node<K, V>> nodeHashMap;
    private final EntryList<K, V> list;

    private final int maxSize;

    public LRUCache(int maxSize) {
        this.nodeHashMap = new HashMap<>();
        this.list = new EntryList<>();
        if (maxSize > 0) {
            this.maxSize = maxSize;
        } else {
            throw new IllegalArgumentException("Illegal Max Size: " + maxSize);
        }
    }

    public int size() {
        return nodeHashMap.size();
    }

    public boolean isEmpty() {
        return nodeHashMap.isEmpty();
    }

    public boolean containsKey(K key) {
        assert nodeHashMap.containsKey(key) == (nodeHashMap.get(key) != null);
        return nodeHashMap.containsKey(key);
    }

    public V get(K key) {
        return nodeHashMap.get(key).getValue();
    }

    public void put(K key, V value) {
        EntryList.Node<K, V> addedNode;
        if (containsKey(key)) {
            addedNode = nodeHashMap.get(key);
            addedNode.setValue(value);
        } else {
            if (size() == maxSize) {
                K removedKey = list.removeFirst();
                nodeHashMap.remove(removedKey);
            }
            addedNode = list.addLast(key, value);
            nodeHashMap.put(key, addedNode);
        }
        list.propagate(addedNode);
        assert get(key) == value;
        assert size() <= maxSize;
    }

    public void clear() {
        nodeHashMap.clear();
        list.clear();
        assert isEmpty();
    }

    private static class EntryList<K, V> {
        private Node<K, V> first;
        private Node<K, V> last;

        @NotNull
        Node<K, V> addLast(K key, V value) {
            return addLast(new Node<>(key, value));
        }

        @NotNull
        Node<K, V> addLast(@NotNull Node<K, V> newNode) {
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

        K removeFirst() {
            assert first != null;
            Node<K, V> removedNode = first;
            first = first.next;
            first.previous = null;
            assert removedNode != first;
            return removedNode.getKey();
        }

        void remove(@NotNull Node<K, V> node) {
            assert first != null && last != null;
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
            assert node != first;
            assert node != last;
        }

        void propagate(@NotNull Node<K, V> node) {
            if (node != last) {
                remove(node);
                addLast(node);
            }
            assert node == last;
        }

        void clear() {
            first = null;
            last = null;
        }

        private static class Node<K, V> {
            private final K key;
            private V value;
            private Node<K, V> previous;
            private Node<K, V> next;


            private Node(K key, V value) {
                this.key = key;
                this.value = value;
                previous = null;
                next = null;
            }

            public K getKey() {
                return key;
            }

            public V getValue() {
                return value;
            }

            public void setValue(V value) {
                this.value = value;
            }
        }
    }
}
