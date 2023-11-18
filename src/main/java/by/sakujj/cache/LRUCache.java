package by.sakujj.cache;

import by.sakujj.collections.DoublyLinkedList;
import by.sakujj.collections.DoublyLinkedList.Node;
import by.sakujj.model.Entity;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Optional;
import java.util.PriorityQueue;

/**
 * LRU cache implementation
 */
@Slf4j
@ToString
public class LRUCache implements Cache {
    private final DoublyLinkedList<Entity<?, ?>> entities;
    private final HashMap<Object, Node<Entity<?, ?>>> nodesById;
    private final int capacity;

    /**
     * Used to get current cache size.
     *
     * @return number of entities in cache
     */
    public synchronized int getSize() {
        return entities.getSize();
    }

    public LRUCache(int capacity) {
        this.capacity = capacity;
        entities = new DoublyLinkedList<>();
        nodesById = new HashMap<>();
    }

    /**
     * Used to add or update entity with cache.
     *
     * @param entity instance to add or update
     */
    public synchronized void addOrUpdate(Entity<?, ?> entity) {
        if (entities.getSize() == capacity) {
            var lruEntity = entities.getLast();
            Object lruId = lruEntity.getId();

            entities.removeLast();
            nodesById.remove(lruId);
        }

        entities.removeByCondition(e -> e.getId().equals(entity.getId()));
        var node = entities.addFirst(entity);
        nodesById.put(entity.getId(), node);
    }

    /**
     * Used to get by id from cache.
     *
     * @param id id to get by
     */
    public synchronized Optional<Entity<?, ?>> getById(Object id) {
        if (!nodesById.containsKey(id)) {
            log.info("CACHE MISS");
            return Optional.empty();
        }

        log.info("CACHE HIT");

        var nodeToGet = nodesById.get(id);
        entities.removeNode(nodeToGet);
        entities.addNodeFirst(nodeToGet);
        return Optional.of(entities.getFirst());
    }

    /**
     * Used to remove by id from cache.
     *
     * @param id id to remove by
     */
    public synchronized void removeById(Object id) {
        if (!nodesById.containsKey(id)) {
            return;
        }

        var nodeToDelete = nodesById.get(id);
        entities.removeNode(nodeToDelete);
        nodesById.remove(id);
    }


}
