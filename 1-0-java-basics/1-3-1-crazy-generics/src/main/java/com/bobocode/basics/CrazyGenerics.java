package com.bobocode.basics;

import com.bobocode.basics.util.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.*;
import java.util.function.Predicate;

/**
 * {@link CrazyGenerics} is an exercise class. It consists of classes, interfaces and methods that should be updated
 * using generics.
 */
public class CrazyGenerics {

    /* ===================== 1. Sourced ===================== */

    @Data
    public static class Sourced<T> {
        private T value;
        private String source;
    }

    /* ===================== 2. Limited ===================== */

    @Data
    public static class Limited<T extends Number> {
        private final T actual;
        private final T min;
        private final T max;
    }

    /* ===================== 3. Converter ===================== */

    public interface Converter<T, R> {
        R convert(T value);
    }

    /* ===================== 4. MaxHolder ===================== */

    public static class MaxHolder<T extends Comparable<? super T>> {
        private T max;

        public MaxHolder(T max) {
            this.max = max;
        }

        public void put(T val) {
            if (val == null) {
                return;
            }
            if (max == null || val.compareTo(max) > 0) {
                max = val;
            }
        }

        public T getMax() {
            return max;
        }
    }

    /* ===================== 5. StrictProcessor ===================== */

    interface StrictProcessor<T extends Serializable & Comparable<? super T>> {
        void process(T obj);
    }

    /* ===================== 6. CollectionRepository ===================== */

    interface CollectionRepository<T extends BaseEntity, C extends Collection<T>> {
        void save(T entity);

        C getEntityCollection();
    }

    /* ===================== 7. ListRepository ===================== */

    interface ListRepository<T extends BaseEntity>
            extends CollectionRepository<T, List<T>> {
    }

    /* ===================== 8. ComparableCollection ===================== */

    interface ComparableCollection<E>
            extends Collection<E>, Comparable<Collection<?>> {

        @Override
        default int compareTo(Collection<?> o) {
            return Integer.compare(this.size(), o.size());
        }
    }

    /* ===================== 9. CollectionUtil ===================== */

    static class CollectionUtil {

        static final Comparator<BaseEntity> CREATED_ON_COMPARATOR =
                Comparator.comparing(BaseEntity::getCreatedOn);

        /* ---- print ---- */

        public static void print(List<?> list) {
            list.forEach(e -> System.out.println(" – " + e));
        }

        /* ---- hasNewEntities ---- */

        public static boolean hasNewEntities(Collection<? extends BaseEntity> entities) {
            for (BaseEntity e : entities) {
                if (e.getUuid() == null) {
                    return true;
                }
            }
            return false;
        }

        /* ---- isValidCollection ---- */

        public static boolean isValidCollection(
                Collection<? extends BaseEntity> entities,
                Predicate<? super BaseEntity> validationPredicate) {

            for (BaseEntity e : entities) {
                if (!validationPredicate.test(e)) {
                    return false;
                }
            }
            return true;
        }

        /* ---- hasDuplicates ---- */

        public static <T extends BaseEntity> boolean hasDuplicates(
                List<T> entities,
                T targetEntity) {

            if (targetEntity == null) {
                return false;
            }

            UUID targetUuid = targetEntity.getUuid();
            int count = 0;

            for (T e : entities) {
                if (Objects.equals(e.getUuid(), targetUuid)) {
                    count++;
                    if (count > 1) {
                        return true;
                    }
                }
            }
            return false;
        }

        /* ---- findMax ---- */

        public static <T> Optional<T> findMax(
                Iterable<T> elements,
                Comparator<? super T> comparator) {

            Iterator<T> it = elements.iterator();
            if (!it.hasNext()) {
                return Optional.empty();
            }

            T max = it.next();
            while (it.hasNext()) {
                T cur = it.next();
                if (comparator.compare(cur, max) > 0) {
                    max = cur;
                }
            }
            return Optional.ofNullable(max);
        }

        /* ---- findMostRecentlyCreatedEntity ---- */

        public static <T extends BaseEntity> T findMostRecentlyCreatedEntity(
                Collection<T> entities) {

            @SuppressWarnings("unchecked")
            Comparator<? super T> comparator =
                    (Comparator<? super T>) CREATED_ON_COMPARATOR;

            return findMax(entities, comparator)
                    .orElseThrow(NoSuchElementException::new);
        }

        /* ---- swap ---- */

        public static void swap(List<?> elements, int i, int j) {
            Objects.checkIndex(i, elements.size());
            Objects.checkIndex(j, elements.size());
            swapCaptured(elements, i, j);
        }

        private static <T> void swapCaptured(List<T> elements, int i, int j) {
            T tmp = elements.get(i);
            elements.set(i, elements.get(j));
            elements.set(j, tmp);
        }
    }
}