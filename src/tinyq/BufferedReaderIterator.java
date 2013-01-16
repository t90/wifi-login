package tinyq;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;

public class BufferedReaderIterator<T extends String> implements Iterator<String> {
    private BufferedReader _reader;

    public BufferedReaderIterator(BufferedReader reader) {
        _reader = reader;
    }

    String _str = null;
    boolean _hasCached = false;


    public boolean hasNext() {
        if (_hasCached) {
            return true;
        }
        try {
            _str = _reader.readLine();
            if (_str != null) {
                _hasCached = true;
                return true;
            }
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder();
            String var = e.getMessage();
            if (var != null) {
                sb.append(var);
                sb.append("\n");
            }

            StackTraceElement[] stackTrace = e.getStackTrace();
            if (stackTrace != null) {
                Query<String> query = (new Query<StackTraceElement>(stackTrace)).select(new Query.F<StackTraceElement, String>() {
                    public String run(StackTraceElement in) {
                        return in.toString();
                    }
                });
                sb.append(query.aggregate(new StringBuilder(), new Query.Accum<String, StringBuilder>() {
                    public StringBuilder run(String in, StringBuilder in2) {
                        in2.append(in);
                        return in2;
                    }
                }).toString());
            }
            throw new IndexOutOfBoundsException(sb.toString());
        }
        return false;
    }

    public String next() {
        if (hasNext()) {
            _hasCached = false;
            return _str;
        }
        throw new IndexOutOfBoundsException();
    }

    public void remove() {
        throw new IndexOutOfBoundsException("Operation not supported");
    }
}
