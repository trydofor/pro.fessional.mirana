package pro.fessional.mirana.jaxb;

import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author trydofor
 * @since 2019-12-31
 */
public class StringMapXmlWriterTest {

    @XmlRootElement(name = "xml")
    public static class TestXml {
        @XmlElement(name = "bool-val")
        private boolean boolVal = false;
        @XmlElement(name = "int-val")
        private int intVal = Integer.MAX_VALUE - 1;
        @XmlElement(name = "long-val")
        private long longVal = Long.MAX_VALUE - 1;
        @XmlElement(name = "float-val")
        private float floatVal = 1.1F;
        @XmlElement(name = "double-val")
        private double doubleVal = 2.2D;
        @XmlElement(name = "decimal-val")
        private BigDecimal decimalVal = new BigDecimal("3.3");

        private Integer intNull = null;
        private Long longNull = null;
        private String stringNull = null;
        private Integer intObj = 1;
        private String stringObj = "good";
    }

    @Test
    public void map() throws JAXBException {
        JAXBContext contextObj = JAXBContext.newInstance(TestXml.class);
        Marshaller marshallerObj = contextObj.createMarshaller();
        marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        TestXml xml = new TestXml();
        StringMapXmlWriter writer = StringMapXmlWriter.linkMap();
        marshallerObj.marshal(xml, writer);
        Map<String, String> tree = writer.getResultTree();
        System.out.println(tree);
        System.out.println(new TreeMap<>(tree));
    }

    @Test
    public void xml() throws JAXBException {
        JAXBContext contextObj = JAXBContext.newInstance(TestXml.class);

        Marshaller marshallerObj = contextObj.createMarshaller();
        marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        TestXml xml = new TestXml();
        StringWriter strw = new StringWriter();
        marshallerObj.marshal(xml, strw);
        System.out.println(strw.toString());
    }
}