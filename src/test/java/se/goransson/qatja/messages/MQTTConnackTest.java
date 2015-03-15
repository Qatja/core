package se.goransson.qatja.messages;

import junit.framework.TestCase;

public class MQTTConnackTest extends TestCase {

    MQTTConnack msg;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        byte[] buffer = {(1 << 5), (1 << 1), (0), (0)};

        msg = new MQTTConnack(buffer, buffer.length);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        msg = null;
    }

    public void testGetReturnCode() throws Exception {
        int expected = 0;

        int actual = msg.getReturnCode();

        assertEquals(expected, actual);
    }

    public void testGenerateFixedHeader() throws Exception {
        byte[] fixed = msg.generateFixedHeader();

        assertNull(fixed);
    }

    public void testGenerateVariableHeader() throws Exception {
        byte[] fixed = msg.generateVariableHeader();

        assertNull(fixed);
    }

    public void testGeneratePayload() throws Exception {
        byte[] fixed = msg.generatePayload();

        assertNull(fixed);
    }
}