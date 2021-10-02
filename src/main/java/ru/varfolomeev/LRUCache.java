package ru.varfolomeev;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * LRU Cache implementation based on {@link HashMap} and Bidirectional List {@link EntryList}
 *
 * @param <K> key type
 * @param <V> value type
 * @author Alexander Varfolomeev
 */
public class LRUCache<K, V> {
    private final HashMap<K, EntryList.Node<K, V>> nodeHashMap;
    private final EntryList<K, V> entryList;

    private final int maxSize;

    /**
     * Constructor with given {@link LRUCache#maxSize}.
     *
     * @param maxSize maximum amount of simultaneously supported key-value mappings
     */
    public LRUCache(int maxSize) {
        this.nodeHashMap = new HashMap<>();
        this.entryList = new EntryList<>();
        if (maxSize > 0) {
            this.maxSize = maxSize;
        } else {
            throw new IllegalArgumentException("Illegal Max Size: " + maxSize);
        }
    }

    /**
     * Return the number of key-value mappings in cache.
     *
     * @return the number of key-value mappings in cache
     */
    public int size() {
        return nodeHashMap.size();
    }

    /**
     * Return if cache contains key-value mappings.
     *
     * @return {@code true} if cache contains no key-value mappings or {@code false} otherwise
     */
    public boolean isEmpty() {
        return nodeHashMap.isEmpty();
    }

    /**
     * Return if cache contains mapping with the specified key.
     *
     * @param key key to check
     * @return {@code true} if cache contains mapping with the specified key or {@code false} otherwise
     */
    public boolean containsKey(K key) {
        assert nodeHashMap.containsKey(key) || (nodeHashMap.get(key) == null);
        return nodeHashMap.containsKey(key);
    }

    /**
     * Return the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * @param key key to check
     * @return value from mapping with the specified key or null
     */
    public V get(K key) {
        return nodeHashMap.get(key).getValue();
    }

    /**
     * Add key-value mapping to cache.
     * If cache size exceed {@link LRUCache#maxSize} least recently used mapping removed from map/
     *
     * @param key   key of mapping
     * @param value value of mapping
     */
    public void put(K key, V value) {
        EntryList.Node<K, V> addedNode;
        if (containsKey(key)) {
            addedNode = nodeHashMap.get(key);
            addedNode.setValue(value);
        } else {
            if (size() == maxSize) {
                K removedKey = entryList.removeFirst();
                nodeHashMap.remove(removedKey);
            }
            addedNode = entryList.addLast(key, value);
            nodeHashMap.put(key, addedNode);
        }
        entryList.moveToEnd(addedNode);
        assert get(key) == value;
        assert size() <= maxSize;
    }

    /**
     * Remove all of the mappings from cache.
     */
    public void clear() {
        nodeHashMap.clear();
        entryList.clear();
        assert isEmpty();
    }

    /**
     * Bidirectional List which contains key-value mappings.
     *
     * @param <K> key type
     * @param <V> value type
     */
    private static class EntryList<K, V> {
        private Node<K, V> first;
        private Node<K, V> last;

        /**
         * Add key-value mapping to the end of list.
         *
         * @param key   key of mapping
         * @param value value of mapping
         * @return newly created {@link Node} with mapping
         */
        @NotNull
        Node<K, V> addLast(K key, V value) {
            return addLast(new Node<>(key, value));
        }

        /**
         * @param newNode to add
         * @return {@code newNode}
         * @see EntryList#addLast(Object, Object)
         */
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

        /**
         * Remove key-value mapping to the start of list.
         *
         * @return key of removed mapping
         */
        K removeFirst() {
            assert first != null;
            Node<K, V> removedNode = first;
            first = first.next;
            first.previous = null;
            assert removedNode != first;
            return removedNode.getKey();
        }

        /**
         * Remove node from list.
         *
         * @param node to remove
         */
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

        /**
         * Move specified node to end of list.
         *
         * @param node to move.
         */
        void moveToEnd(@NotNull Node<K, V> node) {
            if (node != last) {
                remove(node);
                addLast(node);
            }
            assert node == last;
        }

        /**
         * Remove all nodes from list.
         */
        void clear() {
            first = null;
            last = null;
        }

        /**
         * Node of the {@link EntryList} with mapping.
         *
         * @param <K> key type
         * @param <V> value type
         */
        private static class Node<K, V> {
            private final K key;
            private V value;
            private Node<K, V> previous;
            private Node<K, V> next;

            Node(K key, V value) {
                this.key = key;
                this.value = value;
                previous = null;
                next = null;
            }

            K getKey() {
                return key;
            }

            V getValue() {
                return value;
            }

            void setValue(V value) {
                this.value = value;
            }
        }
    }
}
