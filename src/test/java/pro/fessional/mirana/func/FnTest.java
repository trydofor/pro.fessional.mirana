package pro.fessional.mirana.func;


import org.junit.jupiter.api.Test;
import pro.fessional.mirana.data.U;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pro.fessional.mirana.func.Fn.distinct;
import static pro.fessional.mirana.func.Fn.duplicate;

/**
 * @author trydofor
 * @since 2019-10-29
 */
public class FnTest {

    @Test
    public void testDistinct() {
        List<U.Two<Integer, String>> twos = Arrays.asList(U.of(1, "A"), U.of(2, "B"), U.of(1, "A"));
        List<U.Two<Integer, String>> col = twos.stream().filter(distinct(U.Two::one, U.Two::two)).collect(Collectors.toList());
        assertEquals(col, Arrays.asList(
                U.of(1, "A"),
                U.of(2, "B")));
    }

    @Test
    public void testDuplicate() {
        List<U.Two<Integer, String>> twos = Arrays.asList(U.of(1, "A"), U.of(2, "B"), U.of(1, "A"));
        List<U.Two<Integer, String>> col = twos.stream().filter(duplicate(U.Two::one, U.Two::two)).collect(Collectors.toList());
        assertEquals(col, Collections.singletonList(
                U.of(1, "A")));

    }
}