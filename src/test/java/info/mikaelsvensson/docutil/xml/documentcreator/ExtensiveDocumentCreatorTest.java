package info.mikaelsvensson.docutil.xml.documentcreator;

import info.mikaelsvensson.docutil.ClassA;
import org.junit.Test;

public class ExtensiveDocumentCreatorTest extends AbstractDocumentCreatorTest {
    @Test
    public void testClassA() throws Exception {
        performTest(ClassA.class, ExtensiveDocumentCreator.NAME);
    }
}