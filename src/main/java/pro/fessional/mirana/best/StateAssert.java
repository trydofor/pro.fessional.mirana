package pro.fessional.mirana.best;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.data.CodeEnum;
import pro.fessional.mirana.pain.BadStateException;

import java.util.Collection;
import java.util.Map;

/**
 * 后置检查，条件不满足时，抛出 BadStateException
 *
 * @author trydofor
 * @since 2019-10-05
 */
public class StateAssert {

    @Contract("false, _ -> fail")
    public static void isTrue(boolean b, @NotNull String msg) {
        if (!b) throw new BadStateException(msg);
    }

    @Contract("false, _, _ -> fail")
    public static void isTrue(boolean b, @NotNull String code, @NotNull String msg) {
        if (!b) throw new BadStateException(code, msg);
    }

    @Contract("false, _, _ -> fail")
    public static void isTrue(boolean b, @NotNull CodeEnum code, @NotNull Object... args) {
        if (!b) throw new BadStateException(code, args);
    }

    // 
    @Contract("true, _ -> fail")
    public static void isFalse(boolean b, @NotNull String msg) {
        if (b) throw new BadStateException(msg);
    }

    @Contract("true, _, _ -> fail")
    public static void isFalse(boolean b, @NotNull String code, @NotNull String msg) {
        if (b) throw new BadStateException(code, msg);
    }

    @Contract("true, _, _ -> fail")
    public static void isFalse(boolean b, @NotNull CodeEnum code, @NotNull Object... args) {
        if (b) throw new BadStateException(code, args);
    }

    // ////
    @Contract("!null, _ -> fail")
    public static void isNull(Object b, @NotNull String msg) {
        if (b != null) throw new BadStateException(msg);
    }

    @Contract("!null, _, _ -> fail")
    public static void isNull(Object b, @NotNull String code, @NotNull String msg) {
        if (b != null) throw new BadStateException(code, msg);
    }

    @Contract("!null, _, _ -> fail")
    public static void isNull(Object b, @NotNull CodeEnum code, @NotNull Object... args) {
        if (b != null) throw new BadStateException(code, args);
    }

    // 
    @Contract("null, _ -> fail")
    public static void notNull(Object b, @NotNull String msg) {
        if (b == null) throw new BadStateException(msg);
    }

    @Contract("null, _, _ -> fail")
    public static void notNull(Object b, @NotNull String code, @NotNull String msg) {
        if (b == null) throw new BadStateException(code, msg);
    }

    @Contract("null, _, _ -> fail")
    public static void notNull(Object b, @NotNull CodeEnum code, @NotNull Object... args) {
        if (b == null) throw new BadStateException(code, args);
    }

    // ////
    public static void isEmpty(CharSequence c, @NotNull String msg) {
        if (c != null && c.length() > 0) throw new BadStateException(msg);
    }

    public static void isEmpty(CharSequence c, @NotNull String code, @NotNull String msg) {
        if (c != null && c.length() > 0) throw new BadStateException(code, msg);
    }

    public static void isEmpty(CharSequence c, @NotNull CodeEnum code, @NotNull Object... args) {
        if (c != null && c.length() > 0) throw new BadStateException(code, args);
    }

    // 
    public static void notEmpty(CharSequence c, @NotNull String msg) {
        if (c == null || c.length() == 0) throw new BadStateException(msg);
    }

    public static void notEmpty(CharSequence c, @NotNull String code, @NotNull String msg) {
        if (c == null || c.length() == 0) throw new BadStateException(code, msg);
    }

    public static void notEmpty(CharSequence c, @NotNull CodeEnum code, @NotNull Object... args) {
        if (c == null || c.length() == 0) throw new BadStateException(code, args);
    }

    // ////
    public static void isEmpty(Collection<?> c, @NotNull String msg) {
        if (c != null && !c.isEmpty()) throw new BadStateException(msg);
    }

    public static void isEmpty(Collection<?> c, @NotNull String code, @NotNull String msg) {
        if (c != null && !c.isEmpty()) throw new BadStateException(code, msg);
    }

    public static void isEmpty(Collection<?> c, @NotNull CodeEnum code, @NotNull Object... args) {
        if (c != null && !c.isEmpty()) throw new BadStateException(code, args);
    }

    // 
    public static void notEmpty(Collection<?> c, @NotNull String msg) {
        if (c == null || c.isEmpty()) throw new BadStateException(msg);
    }

    public static void notEmpty(Collection<?> c, @NotNull String code, @NotNull String msg) {
        if (c == null || c.isEmpty()) throw new BadStateException(code, msg);
    }

    public static void notEmpty(Collection<?> c, @NotNull CodeEnum code, @NotNull Object... args) {
        if (c == null || c.isEmpty()) throw new BadStateException(code, args);
    }

    // ////
    public static void isEmpty(Map<?, ?> c, @NotNull String msg) {
        if (c != null && !c.isEmpty()) throw new BadStateException(msg);
    }

    public static void isEmpty(Map<?, ?> c, @NotNull String code, @NotNull String msg) {
        if (c != null && !c.isEmpty()) throw new BadStateException(code, msg);
    }

    public static void isEmpty(Map<?, ?> c, @NotNull CodeEnum code, @NotNull Object... args) {
        if (c != null && !c.isEmpty()) throw new BadStateException(code, args);
    }

    // 
    public static void notEmpty(Map<?, ?> c, @NotNull String msg) {
        if (c == null || c.isEmpty()) throw new BadStateException(msg);
    }

    public static void notEmpty(Map<?, ?> c, @NotNull String code, @NotNull String msg) {
        if (c == null || c.isEmpty()) throw new BadStateException(code, msg);
    }

    public static void notEmpty(Map<?, ?> c, @NotNull CodeEnum code, @NotNull Object... args) {
        if (c == null || c.isEmpty()) throw new BadStateException(code, args);
    }

    // ////
    public static void isEmpty(Object[] c, @NotNull String msg) {
        if (c != null && c.length > 0) throw new BadStateException(msg);
    }

    public static void isEmpty(Object[] c, @NotNull String code, @NotNull String msg) {
        if (c != null && c.length > 0) throw new BadStateException(code, msg);
    }

    public static void isEmpty(Object[] c, @NotNull CodeEnum code, @NotNull Object... args) {
        if (c != null && c.length > 0) throw new BadStateException(code, args);
    }

    // 
    public static void notEmpty(Object[] c, @NotNull String msg) {
        if (c == null || c.length == 0) throw new BadStateException(msg);
    }

    public static void notEmpty(Object[] c, @NotNull String code, @NotNull String msg) {
        if (c == null || c.length == 0) throw new BadStateException(code, msg);
    }

    public static void notEmpty(Object[] c, @NotNull CodeEnum code, @NotNull Object... args) {
        if (c == null || c.length == 0) throw new BadStateException(code, args);
    }


    //
    public static <T extends Comparable<T>> void aEqb(T a, T b, @NotNull String msg) {
        if (a == null && b == null) return;
        if (a == null || !a.equals(b)) throw new BadStateException(msg);
    }

    public static <T extends Comparable<T>> void aEqb(T a, T b, @NotNull String code, @NotNull String msg) {
        if (a == null && b == null) return;
        if (a == null || !a.equals(b)) throw new BadStateException(code, msg);
    }

    public static <T extends Comparable<T>> void aEqb(T a, T b, @NotNull CodeEnum code, @NotNull Object... args) {
        if (a == null && b == null) return;
        if (a == null || !a.equals(b)) throw new BadStateException(code, args);
    }

    //
    public static <T extends Comparable<T>> void aGeb(T a, T b, @NotNull String msg) {
        if (a == null && b == null) return;
        if (a == null || b == null || a.compareTo(b) < 0) throw new BadStateException(msg);
    }

    public static <T extends Comparable<T>> void aGeb(T a, T b, @NotNull String code, @NotNull String msg) {
        if (a == null && b == null) return;
        if (a == null || b == null || a.compareTo(b) < 0) throw new BadStateException(code, msg);
    }

    public static <T extends Comparable<T>> void aGeb(T a, T b, @NotNull CodeEnum code, @NotNull Object... args) {
        if (a == null && b == null) return;
        if (a == null || b == null || a.compareTo(b) < 0) throw new BadStateException(code, args);
    }

    //
    public static <T extends Comparable<T>> void aGtb(T a, T b, @NotNull String msg) {
        if (a == null || b == null || a.compareTo(b) <= 0) throw new BadStateException(msg);
    }

    public static <T extends Comparable<T>> void aGtb(T a, T b, @NotNull String code, @NotNull String msg) {
        if (a == null || b == null || a.compareTo(b) <= 0) throw new BadStateException(code, msg);
    }

    public static <T extends Comparable<T>> void aGtb(T a, T b, @NotNull CodeEnum code, @NotNull Object... args) {
        if (a == null || b == null || a.compareTo(b) <= 0) throw new BadStateException(code, args);
    }

    //
    public static <T extends Comparable<T>> void aLeb(T a, T b, @NotNull String msg) {
        if (a == null && b == null) return;
        if (a == null || b == null || a.compareTo(b) > 0) throw new BadStateException(msg);
    }

    public static <T extends Comparable<T>> void aLeb(T a, T b, @NotNull String code, @NotNull String msg) {
        if (a == null && b == null) return;
        if (a == null || b == null || a.compareTo(b) > 0) throw new BadStateException(code, msg);
    }

    public static <T extends Comparable<T>> void aLeb(T a, T b, @NotNull CodeEnum code, @NotNull Object... args) {
        if (a == null && b == null) return;
        if (a == null || b == null || a.compareTo(b) > 0) throw new BadStateException(code, args);
    }

    //
    public static <T extends Comparable<T>> void aLtb(T a, T b, @NotNull String msg) {
        if (a == null || b == null || a.compareTo(b) >= 0) throw new BadStateException(msg);
    }

    public static <T extends Comparable<T>> void aLtb(T a, T b, @NotNull String code, @NotNull String msg) {
        if (a == null || b == null || a.compareTo(b) >= 0) throw new BadStateException(code, msg);
    }

    public static <T extends Comparable<T>> void aLtb(T a, T b, @NotNull CodeEnum code, @NotNull Object... args) {
        if (a == null || b == null || a.compareTo(b) >= 0) throw new BadStateException(code, args);
    }
}
