package tinyq;

import android.database.Cursor;
import tinyq.Query;

import java.util.Iterator;

public class CursorIterator<T> implements Iterator<T>{

    private final Query.F<Cursor, T> _mapper;
    private final Cursor _cursor;

    public CursorIterator(Query.F<Cursor,T> mapper, Cursor cursor){
        _mapper = mapper;
        _cursor = cursor;
    }

    public boolean hasNext() {
        return _cursor.moveToNext();
    }

    public T next() {
        return _mapper.run(_cursor);
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
