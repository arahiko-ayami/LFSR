import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        LFSR lfsr = new LFSR(87328131, new int[]{12,4});
        String secret = "This is a secret message";
        byte[] cipher = lfsr.encrypt(secret.getBytes(StandardCharsets.UTF_8));
        System.out.println(new String(cipher, StandardCharsets.UTF_8));
        System.out.println(new String (lfsr.decrypt(cipher)));
    }
}
