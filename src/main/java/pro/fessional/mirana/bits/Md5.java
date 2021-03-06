package pro.fessional.mirana.bits;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.io.InputStreams;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author trydofor
 * @since 2019-10-12
 */
public class Md5 {

    @NotNull
    public static String sum(@Nullable String str) {
        if (str == null) return Null.Str;
        return sum(str.getBytes(UTF_8));
    }

    @NotNull
    public static String sum(@Nullable InputStream ins) {
        if (ins == null) return Null.Str;
        byte[] bytes = InputStreams.readBytes(ins);
        return sum(bytes);
    }

    @NotNull
    public static String sum(@Nullable byte[] bytes) {
        if (bytes == null) return Null.Str;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(bytes);
            byte[] hash = digest.digest();
            return Bytes.hex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("can not init MD5,", e);
        }
    }

    public static boolean check(@Nullable String sum, @Nullable byte[] bytes) {
        if (bytes == null || sum == null) return false;
        String md5 = sum(bytes);
        return sum.equalsIgnoreCase(md5);
    }

    public static boolean check(@Nullable String sum, @Nullable String str) {
        if (str == null || sum == null) return false;
        String md5 = sum(str);
        return sum.equalsIgnoreCase(md5);
    }

    public static boolean check(@Nullable String sum, @Nullable InputStream ins) {
        if (ins == null || sum == null) return false;
        String md5 = sum(ins);
        return sum.equalsIgnoreCase(md5);
    }
}
