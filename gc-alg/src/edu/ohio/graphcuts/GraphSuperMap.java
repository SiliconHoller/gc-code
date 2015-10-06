/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

/**
 * <p>Generics class to map graph values to multiple outside values</p>
 * <p>This class is primarily intended for "super pixel/voxel" one-to-many graph-cut implementations
 * of the graph-cut.</p>
 * <p>For simple one-to-one usage, use a standard Map<E,T> implementation.</p>
 * @author David Days <david.c.days@gmail.com>
 */
public class GraphSuperMap<E,T> implements Map<E,T>{


    /**
     * Internal one-to-many mapping
     */
    protected HashMap<E,Vector<T>> map;

    /**
     * Simple constructor.
     */
    public GraphSuperMap() {
        map = new HashMap<E,Vector<T>>();
    }


    /**
     * Returns the number of keys in this map.
     * @return size of the key set.
     */
    public int size() {
        return map.keySet().size();
    }

    /**
     * Simple pass-through to determine if the mapping is empty
     * of key-value pairs.
     * @return
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Simple pass-through to determine if the particular key
     * is contained in the mapping.
     * @param key Key to be checked.
     * @return True if the map key set contains that key; otherwise, returns
     * false.
     */
    public boolean mapContainsKey(E key) {
        return map.containsKey(key);
    }

    /**
     * Returns true if the mapping contains the value somewhere in
     * the internal structure.
     * @param value Value to be checked.
     * @return True if the value is in the internal structure; otherwise false.
     */
    public boolean mapContainsValue(T value) {
        boolean contains = false;
        Collection<Vector<T>> c = map.values();
        Iterator<Vector<T>> itc = c.iterator();
        while (itc.hasNext() && !contains) {
            Vector<T> v = itc.next();
            if (v.contains(value)) contains = true;
        }
        return contains;
    }

    /**
     * Returns the a collection containing all values mapped to the
     * given key.  If no values are mapped (or the key is mapped to null),
     * returns an empty collection.
     * @param key Entry to be selected.
     * @return Collection of the values mapped to the entry.  If there are
     * no values mapped (or the key is not contained in the key-set), returns
     * an empty collection.
     */
    public Collection<T> getMappedValues(E key) {
        Vector<T> vals = map.get(key);
        if (vals == null) {
            vals = new Vector<T>();
        }
        return vals;
    }

    /**
     * <p>Adds the given value to the key mappings.</p>
     * <p>If the key is not previously contained in the mapping,
     * a new mapping is added, and the value is then added.</p>
     * <p>If the key is previously mapped, the previous value (if any)
     * is replaced.</p>
     * @param key Key to either be added or checked.
     * @param value Value to be added.
     * @return Null if there is no previous mapping.  Otherwise, the
     * value that was replaced.
     */
    public T put(E key, T value) {
        T prev = null;
        Vector<T> prevv = map.get(key);
        if (prevv == null) {
            prev = null;
            prevv = new Vector<T>();
            prevv.add(value);
            map.put(key, prevv);
        } else {
            if (prevv.contains(value)) {
                int vindex = prevv.indexOf(value);
                prev = prevv.elementAt(vindex);
                prevv.set(vindex, value);
            } else {
                prevv.add(value);
            }
        }

        return prev;
    }


    /**
     * <p>Adds all the given map key-value pairs to the current
     * map.</p>
     * <p>Internally, it uses this instance's put(E key, T value) method.</p>
     * @param m Map to be added.
     */
    public void putAll(Map<? extends E, ? extends T> m) {
        Iterator<? extends E> it = m.keySet().iterator();
        while (it.hasNext()) {
            E el = (E) it.next();
            put(el,m.get(el));
        }
    }

    /**
     * Clears all entries, restoring to an empty state.
     */
    public void clear() {
        map.clear();
    }

    /**
     * Returns a set of all key values.
     * @return All key values contained in the current mapping.
     */
    public Set<E> keySet() {
        return map.keySet();
    }

    /**
     * Returns a single collection of all values contained in this
     * mapping.  Multiple instances of values may be returned.
     * @return A Collection containing all values.  If not values
     * are mapped (or all null values), then an empty set is returned.
     */
    public Collection<T> values() {
        Vector<T> vals = new Vector<T>();
        Collection<Vector<T>> mvals = map.values();
        Iterator<Vector<T>> it = mvals.iterator();
        while (it.hasNext()) {
            Vector<T> v = it.next();
            if (v != null) {
                vals.addAll(v);
            }
        }

        return vals;
    }


    /**
     * Returns a set of the keys in the current mapping <em>and the
     * first value of the one-to-many mapping</em>.
     *
     * @return A set containing the key mappings and the first value
     * mapped to each key, if any.
     */
    public Set<Entry<E, T>> entrySet() {
        HashMap<E,T> emap = new HashMap<E,T>();
        Iterator<E> it = map.keySet().iterator();
        while (it.hasNext()) {
            E key = it.next();
            Vector<T> vals = map.get(key);
            if (vals != null) {
                //put in the first entry, if it exists
                if (vals.isEmpty()) {
                    emap.put(key,null);
                } else {
                    emap.put(key, vals.elementAt(0));
                }
            } else {
                emap.put(key,null);
            }
        }
        return emap.entrySet();
    }

    /**
     * Returns true if the mapping contains the given key.
     * @param key Key to be checked.
     * @return True if the map contains an the given key.  False if the
     * given object is not an instance of E or not in the map.
     */
    public boolean containsKey(Object key) {
        try {
            E ekey = (E) key;
            return mapContainsKey(ekey);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns true if the mapping contains the given value anywhere within.
     * @param value Value to be checked.
     * @return True if the object/value is contained in the mapping.  False if
     * the object is not an instance of class T or not contained in the mapping.
     */
    public boolean containsValue(Object value) {
        try {
            T eval = (T) value;
            return mapContainsValue(eval);
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Returns the first value mapped to the given key, if exists.
     * @param key Key to be checked.
     * @return Null if the key is mapped to a null value, the key does
     * not exist in the key-set, or the object is not an instance of class E.
     */
    public T get(Object key) {

        T rval = null;
        try {
            E ekey = (E) key;
            Vector<T> v = map.get(ekey);
            if (v != null) {
                if (v.isEmpty()) {
                    //nothing
                } else {
                    rval = v.elementAt(0);
                }
            }
        } catch (Exception e) {
            //do nothing..should return a null
        }
        return rval;
    }

    /**
     * Removes the key and its mapping value.  If the key is not
     * in the mapping, or the object is not an instance of E, no action
     * is performed.
     * @param key Key to be removed.
     * @return Always returns null.
     */
    public T remove(Object key) {
        try {
            map.remove((E)key);
        } catch (Exception nothing) {
            //do nothing
        }
        return null;
    }

}
