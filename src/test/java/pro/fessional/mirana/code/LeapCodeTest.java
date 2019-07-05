package pro.fessional.mirana.code;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/**
 * @author trydofor
 * @since 2019-05-27
 */
public class LeapCodeTest {


    private LeapCode codeSystem = new LeapCode();
    private LeapCode codeCustom = new LeapCode();

    private Random random = new Random();

    private void checkRandom(int base, LeapCode code) {
        for (int i = 0; i < 100000; i++) {
            String e = code.encode(base, i, 30);
            long d = code.decode(e);
            Assert.assertEquals(i, d);
        }
        for (int i = 0; i < 100000; i++) {
            long n = random.nextLong() & Crc8Long.MAX_NUMBER;
            String e = code.encode(base, n, 30);
            long d = code.decode(e);
            Assert.assertEquals(n, d);
        }
    }

    private void checkBound(int base, LeapCode code) {
        String e2 = code.encode(base, LeapCode.MAX_NUMBER, 30);
        long d2 = code.decode(e2);
        Assert.assertEquals(LeapCode.MAX_NUMBER, d2);

        String e1 = code.encode(base, LeapCode.MIN_NUMBER, 30);
        long d1 = code.decode(e1);
        Assert.assertEquals(LeapCode.MIN_NUMBER, d1);
    }

    @Test
    public void testRandom() {
        checkRandom(26, codeSystem);
        checkRandom(26, codeCustom);
        checkRandom(32, codeSystem);
        checkRandom(32, codeCustom);
    }

    @Test
    public void testBound() {
        checkBound(26, codeSystem);
        checkBound(26, codeCustom);
        checkBound(32, codeSystem);
        checkBound(32, codeCustom);
    }

    @Test
    public void printRandom() {
        for (int i = 0; i < 1000; i++) {
            String e = codeSystem.encode(26, i, 20);
            System.out.println("e=" + e + ",i=" + i);
        }
        for (int i = 0; i < 1000; i++) {
            String e = codeSystem.encode(32, i, 20);
            System.out.println("e=" + e + ",i=" + i);
        }
    }
}