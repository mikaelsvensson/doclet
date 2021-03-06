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

package info.mikaelsvensson.docutil.xml;

import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.standard.Standard;
import info.mikaelsvensson.docutil.shared.propertyset.PropertySet;
import info.mikaelsvensson.docutil.shared.propertyset.PropertySetException;

/**
 * Depcrated. Use ChainDoclet and XmlDoclet instead.
 *
 * @doclet
 * @deprecated
 */
@Deprecated
public class XmlAndStandardDoclet extends XmlDoclet {

    protected XmlAndStandardDoclet(RootDoc root, XmlDocletOptions options) {
        super(root, options);
    }

    public static int optionLength(String option) {
        int len = XmlDoclet.optionLength(option);
        if (len > 0) {
            return len;
        } else {
            return Standard.optionLength(option);
        }
    }

    public static boolean validOptions(String[][] strings, com.sun.javadoc.DocErrorReporter docErrorReporter) {
        return Standard.validOptions(strings, docErrorReporter);
    }

    public static boolean start(RootDoc root) {
        root.printNotice("Generating standard API documentation");
        Standard standard = new Standard();
        standard.start(root);

        root.printNotice("Generating XML based API documentation");
        try {
            new XmlDoclet(root, new XmlDocletOptions(new PropertySet(root.options()))).generate();
        } catch (PropertySetException e) {
            root.printError(e.getMessage());
        }

        return true;
    }
}
