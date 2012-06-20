package se.linkon.sabine.docutil.shared;

import com.sun.javadoc.Tag;

public interface TagHandler {
    boolean handles(Tag tag);

    String toString(Tag tag) throws TagHandlerException;
}
