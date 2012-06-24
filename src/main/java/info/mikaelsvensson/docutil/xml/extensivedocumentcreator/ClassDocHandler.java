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

package info.mikaelsvensson.docutil.xml.extensivedocumentcreator;

import com.sun.javadoc.ClassDoc;
import info.mikaelsvensson.docutil.shared.ElementWrapper;

class ClassDocHandler<T extends ClassDoc> extends ProgramElementDocHandler<T> {

    static final String ABSTRACT = "abstract";

/*
    interface Callback {
        void onTrigger(ClassDoc doc) throws JavadocItemHandlerException;
    }
*/

    enum ClassType {
        ENUM,
        INTERFACE,
        CLASS,
        ANNOTATION;

        static ClassType valueOf(ClassDoc doc) {
            if (doc.isEnum()) {
                return ENUM;
            } else if (doc.isInterface()) {
                return INTERFACE;
            } else if (doc.isAnnotationType()) {
                return ANNOTATION;
            } else {
                return CLASS;
            }
        }
    }

    // --------------------------- CONSTRUCTORS ---------------------------

    ClassDocHandler(final Dispatcher dispatcher) {
        this((Class<T>) ClassDoc.class, dispatcher/*, false, null*/);
    }

/*
    protected ClassDocHandler(final Dispatcher dispatcher, boolean referenceOnlyOutput, Callback callback) {
        this((Class<T>) ClassDoc.class, dispatcher, referenceOnlyOutput, callback);
    }
*/
    protected ClassDocHandler(final Class<T> cls, final Dispatcher dispatcher/*, boolean referenceOnlyOutput, Callback callback*/) {
        super(cls, dispatcher);
//        this.callback = callback;
//        this.referenceOnlyOutput = referenceOnlyOutput;
    }

//    private Callback callback;
//    private boolean referenceOnlyOutput;

// -------------------------- OTHER METHODS --------------------------

    @Override
    void handleImpl(final ElementWrapper el, final T doc) throws JavadocItemHandlerException {
//        boolean isExcluded = getProperty(ExtensiveDocumentCreator.EXCLUDE_PACKAGE) != null && doc.qualifiedName().startsWith(getProperty(ExtensiveDocumentCreator.EXCLUDE_PACKAGE));
//        ElementWrapper attrEl = el;
//        String attrNamePrefix = "";
//        if (getBooleanProperty(ExtensiveDocumentCreator.SIMPLE_TYPE_DATA, false)) {
//            attrEl = el.getParent();
//            attrNamePrefix = el.getTagName() + "-";
//            el.remove();
//        }

//        referenceOnlyOutput = !el.getTagName().equals("class");

        if (!el.getTagName().equals("class")) {
            setTypeAttributes(el, doc);
            el.setAttribute(QUALIFIED_NAME, doc.qualifiedName());
//        } else if (isExcluded) {
//            setTypeAttributes(el, doc);
//            el.setAttribute(QUALIFIED_NAME, doc.qualifiedName());
//            el.setAttribute("excluded", Boolean.TRUE.toString());
        } else {
            super.handleImpl(el, doc);

            el.setAttributes(
                    ABSTRACT, Boolean.toString(doc.isAbstract()),
                    "externalizable", Boolean.toString(doc.isExternalizable()),
                    "serializable", Boolean.toString(doc.isSerializable()));

            String classMemberTypeFilter = null;
            ClassType classType = ClassType.valueOf(doc);
            switch (classType) {
                case ENUM:
                    classMemberTypeFilter = getProperty(ExtensiveDocumentCreator.ENUM_MEMBER_TYPE_FILTER, "efnim");
                    break;
                case INTERFACE:
                    classMemberTypeFilter = getProperty(ExtensiveDocumentCreator.INTERFACE_MEMBER_TYPE_FILTER, "sfnimtp");
                    break;
                default:
                    classMemberTypeFilter = getProperty(ExtensiveDocumentCreator.CLASS_MEMBER_TYPE_FILTER, "scfnimtp");
                    break;
            }
            el.setAttribute("type", classType.name().toLowerCase());

            for (char c : classMemberTypeFilter.toCharArray()) {
                switch (c) {
                    case 's':
                        handleDocImpl(el, "superclass", doc.superclassType());
                        break;
                    case 'c':
                        handleDocImpl(el, doc.constructors(), "constructors", "constructor");
                        break;
                    case 'e':
                        handleDocImpl(el, doc.enumConstants(), "enum-constants", "enum-constant");
                        break;
                    case 'f':
                        handleDocImpl(el, doc.fields(), "fields", "field");
                        break;
                    case 'n':
                        handleDocImpl(el, doc.innerClasses(), "inner-classes", "inner-class");
                        break;
                    case 'i':
                        handleDocImpl(el, doc.interfaceTypes(), "interfaces", "interface");
                        break;
                    case 'm':
                        handleDocImpl(el, doc.methods(), "methods", "method");
                        break;
                    case 't':
                        handleDocImpl(el, doc.typeParameters(), "type-parameters", "type-parameter");
                        break;
                    case 'p':
                        handleDocImpl(el, doc.typeParamTags(), "type-parameter-tags", "type-parameter-tag");
                        break;
                }
            }
        }

//        if (callback != null) {
//            callback.onTrigger(doc);
//        }
    }
}
