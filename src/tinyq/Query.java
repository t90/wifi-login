/*
Copyright (c) 2011, Vladimir Vasiltsov
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package tinyq;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Query<T> implements Iterable<T>{

    private final Iterator<T> _iterator;

    public interface F<TIN, TOUT> {
        public TOUT run(TIN in);
    }

    public interface Accum<TIN1,TOUT>{
        public TOUT run(TIN1 item, TOUT accumulator);
    }

    private static class ArrayIterator<T> implements Iterator<T>{

        private T[] _array;
        private int _index =0;

        public ArrayIterator(T[] array){
            _array = array;
        }

        public boolean hasNext() {
            return _index < _array.length;
        }

        public T next() {
            if(hasNext())
                return _array[_index++];
            throw new IndexOutOfBoundsException();
        }

        public void remove() {
            throw new IndexOutOfBoundsException("Operation not supported");
        }
    }

    private static class WhereIterator<T1> implements Iterator<T1>{

        private Iterator<T1> _iterator;
        private F<T1, Boolean> _selector;
        boolean hasCached = false;
        T1 _cached = null;

        public WhereIterator(Iterator<T1> iterator, F<T1, Boolean> selector) {
            _iterator = iterator;
            _selector = selector;
        }

        public boolean hasNext() {
            if(hasCached) return true;
            if(!_iterator.hasNext()) return false;
            while(_iterator.hasNext()){
                _cached = _iterator.next();
                if(_selector.run(_cached)){
                    hasCached = true;
                    return true;
                }
            }
            return false;
        }

        public T1 next() {
            if(hasCached){
                hasCached = false;
                return _cached;
            }
            if(hasNext()){
                hasCached = false;
                return _cached;
            }
            throw new IndexOutOfBoundsException("no more elements");
        }

        public void remove() {
            throw new IndexOutOfBoundsException("Operation not supported");
        }
    }

    private static class SelectIterator<TIN,TOUT> implements Iterator<TOUT>{

        private Iterator<TIN> _inInIterator;
        private F<TIN, TOUT> _selector;

        public SelectIterator(Iterator<TIN> inIterator, F<TIN,TOUT> selector){
            _inInIterator = inIterator;
            _selector = selector;
        }

        public boolean hasNext() {
            return _inInIterator.hasNext();
        }

        public TOUT next() {
            if(hasNext()){
                return _selector.run(_inInIterator.next());
            }
            throw new IndexOutOfBoundsException();
        }

        public void remove() {
            _inInIterator.remove();
        }
    }

    private static class SelectManyIterator<T1IN, T1OUT> implements  Iterator<T1OUT>{

        private final Iterator<T1IN> _iterator;
        private final F<T1IN, Query<T1OUT>> _selector;
        private Query<T1OUT> _cached = null;
        private boolean _hasCached = false;

        public SelectManyIterator (Iterator<T1IN> inIterator, F<T1IN, Query<T1OUT>> selector){
            _iterator = inIterator;
            _selector = selector;
        }

        public boolean hasNext() {
            if(!_hasCached){
                if(!_iterator.hasNext()) return false;
                _cached = _selector.run(_iterator.next());
                _hasCached = true;
            }
            if(_cached.iterator().hasNext()){
                return true;
            }
            if(_iterator.hasNext()){
                _cached = null;
                _hasCached = false;
                return hasNext();
            }
            return false;
        }

        public T1OUT next() {
            if(!hasNext()){
                throw new IndexOutOfBoundsException();
            }
            return _cached.iterator().next();
        }

        public void remove() {
            throw new IndexOutOfBoundsException("Operation not supported");
        }
    }

    /**
     * Process the query and get the amount of the items in it.
     * @return number of items in collection.
     */
    public int size() {
        int size = 0;
        while(_iterator.hasNext()){
            _iterator.next();
            ++size;
        }
        return size;
    }

    /**
     * Check if any items can be yeilded by the Query
     * @return true if there are any items, false in other case.
     */
    public boolean isEmpty() {
        return !_iterator.hasNext();
    }

    public Iterator<T> iterator() {
        return _iterator;
    }

    /**
     * Process the query and return all items from the query as a List<?>
     * @return list with all the items
     */
    public List<T> toList(){
        ArrayList<T> r = new ArrayList<T>();
        for(T t : this){
            r.add(t);
        }
        return r;
    }

    /**
     * Process query and return all values as an array
     *
     * CAUTION, this runs twice runs of the query as soon as it calculates array size.
     * so in case your source changes after first run you might get unexpected result
     * I highly recommend to use toList, which is more predictable.
     * @param c - Class for an array item
     * @return array of emelents returned by the query
     */
    public T[] toArray(Class c) {
        T[] retVal = (T[]) Array.newInstance(c, size());
        int i = 0;
        for(T t : this){
            retVal[i] = t;
            i++;
        }
        return retVal;
    }

    /**
     * Filter sequence yielding only the items where selector.run returns true
     * @param selector - checks if an item should stay in output query, selector.run(..) should return true
     * if we want to keep the item, or false if not.
     * @return a new Query with items filtered with selector
     */
    public Query<T> where(F<T,Boolean> selector){
        return new Query<T>(new WhereIterator<T>(_iterator,selector));
    }

    /**
     * Map one sequence to another, processing each item.
     * @param selector - gets every item and returns an item for a new Query
     * @return a new query with items processed by selector
     */
    public <TOUT> Query<TOUT> select(F<T,TOUT> selector){
        return new Query<TOUT>(new SelectIterator<T,TOUT>(_iterator,selector));
    }

    /**
     * Convert sequence of sequences into a flat sequence.
     * @param selector Ftion returning a new sequence for an item in input sequence
     * @return a new query flat with all items from evey collection returned by selector.
     */
    public <TOUT> Query<TOUT> selectMany(F<T, Query<TOUT>> selector){
        return new Query<TOUT>(new SelectManyIterator<T,TOUT>(_iterator,selector));
    }

    /**
     * Fold the sequence.
     * @param accumulator - accumulator to fold Ftion into
     * @param fold - folding Ftion
     * @return - a new value based on items from the sequence
     */
    public <TAGGR> TAGGR aggregate(TAGGR accumulator, Accum<T,TAGGR> fold){
        if(accumulator == null)
            throw new NullPointerException();
        for(T t : this){
            accumulator = fold.run(t,accumulator);
        }
        return accumulator;
    }

    public Query(T[] array) {
        _iterator = new ArrayIterator<T>(array);
    }

    public Query(Iterator<T> iterator) {
        _iterator = iterator;
    }

    public Query(Iterable<T> collection){
        _iterator = collection.iterator();
    }

}