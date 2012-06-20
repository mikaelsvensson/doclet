package se.linkon.sabine.docutil.xml.documentcreator;

import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import se.linkon.sabine.docutil.shared.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.regex.Pattern;

public abstract class AbstractDocumentCreator implements DocumentCreator {
    private TagHandler[] tagHandlers = new TagHandler[]{
            new CodeTagHandler(),
            new ImageTagHandler(),
            new LinkTagHandler(),
            new SourceFileTagHandler()};
    public static final Pattern COMMENT_PARAGRAPH = Pattern.compile("<(p|br)\\s*/\\s*>");
    protected static final String ATTR_NAME = "name";
    protected static final String ATTR_Q_NAME = "qualified-name";

    private DocumentBuilder documentBuilder;

    protected AbstractDocumentCreator() throws ParserConfigurationException {
        documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }

    protected Document createDocument(final String rootElementName) {
        Document doc = documentBuilder.newDocument();
        doc.appendChild(doc.createElement(rootElementName));
        return doc;
    }

    protected void addComment(ElementWrapper parentEl, Tag[] inlineTags, RootDoc root) {
        StringBuilder sb = new StringBuilder();

        tag:
        for (Tag tag : inlineTags) {
            for (TagHandler handler : tagHandlers) {
                if (handler.handles(tag)) {
                    try {
                        sb.append(handler.toString(tag));
                    } catch (TagHandlerException e) {
                        root.printWarning("Could not print tag '" + tag.name() + "'. " + e.getMessage());
                    }
                    continue tag;
                }
            }
            sb.append(tag.text());
        }
        addComment(parentEl, sb.toString(), root);
    }

    private void addComment(ElementWrapper parentEl, String comment, RootDoc root) {
        String c = comment != null && comment.length() > 0 ? comment.replace("\n", "") : "";
        if (c.length() > 0) {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("<comment>");
                for (String paragraph : COMMENT_PARAGRAPH.split(c)) {
                    sb.append("<p>").append(paragraph).append("</p>");
                }
                sb.append("</comment>");
                parentEl.addChildFromSource(sb.toString());
            } catch (IOException e) {
//                root.printWarning("Could not parse comment for " + parentEl.getTagName() + " " + parentEl.getAttribute(ATTR_NAME) + ". " + e.getMessage());
                parentEl.addChildWithText("comment", comment);
            } catch (SAXException e) {
//                root.printWarning("Could not parse comment for " + parentEl.getTagName() + " " + parentEl.getAttribute(ATTR_NAME) + ". " + e.getMessage());
                parentEl.addChildWithText("comment", comment);
            }
        }
    }
}
