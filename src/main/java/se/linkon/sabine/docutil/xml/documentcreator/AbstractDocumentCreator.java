/*
 * Copyright (c) 2012, Mikael Svensson
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the copyright holder nor the names of the
 *       contributors of this software may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL MIKAEL SVENSSON BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package se.linkon.sabine.docutil.xml.documentcreator;

import com.sun.javadoc.RootDoc;
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

    protected Document createDocument(final String rootElementName) throws ParserConfigurationException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
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
                // TODO: MISV 20120621 It would be nice if this method could also fix the cause of the '"p" must be terminated by the matching end-tag' issue. The problem stems from invalid HTML code genereated by wsimport/wsgen (?). Solve by counting the number of '</p' between one '<p' and the next (count=0 => prepend '</p>' before the next '<p')?
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
