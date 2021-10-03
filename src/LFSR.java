public class LFSR {
    private static int[] defaultPolinom = {24, 4, 3, 1};
    private int[] polinom;
    protected long register;
    private long currRegister;
    private long mask;


    public LFSR(long initRegister, int[] polinom) {
        this.register = initRegister;
        this.polinom = polinom;
        generateMask();
    }

    /**
     * Constructor
     * @param initRegister Trạng thái mặc định của thanh ghi
     * @param polinom Một mảng chứa các phần tử của đa thức có thừa số là 1
     */
    public LFSR(String initRegister, int[] polinom) {
        this.polinom = polinom;
        if(initRegister.length() > polinom[0]) {
            throw new Error(String.format("The length of the obtained register (%s) exceeds the" +
                    " maximum degree of the polynomial (%s)", initRegister.length(), polinom[0])
            );
        }
        register = Long.parseLong(initRegister, 2);
        generateMask();
    }


    /**
     * Constructor với đa thức mặc định là (x^24 + x^4 + x^3 + x + 1)
     * @param initRegister Trạng thái mặc định của thanh ghi
     */
    public LFSR(String initRegister) {
        this(initRegister, defaultPolinom);
    }

    /**
     * Chuyển một khoá (ở dạng mảng byte) thành chuỗi nhị phân
     * @param key Khoá
     * @return Khoá ở dạng chuỗi
     */
    public static String keyToStr(byte[] key, int bytesCount) {
        StringBuilder strKey = new StringBuilder();
        if (bytesCount > key.length) {
            bytesCount = key.length;
        }
        for(int i = 0; i < bytesCount; i++) {
            StringBuilder binarByte = new StringBuilder(Integer.toBinaryString(key[i] & 255));
            for(int j = binarByte.length(); j < 8; j++) {
                binarByte.insert(0, "0");
            }
            strKey.append(binarByte);
        }
        return strKey.toString();
    }

    /**
     * Lấy bit ở vị tri xác định
     * @param pos Vị trí của bit
     * @return bit
     */
    private byte getBitAtPos(int pos) {
        return (byte) ((byte) (currRegister >> pos - 1) & 1);
    }

    /**
     * Tạo mask cho kích thước thanh ghi
     */
    private void generateMask() {
        StringBuilder max = new StringBuilder();
        for(int i = 0; i < polinom[0]; i++) {
            max.append('1');
        }
        mask = Long.parseLong(max.toString(), 2);
    }


    /**
     * Tạo một khoá
     * @param len Độ dài khoá
     * @return Khoá
     */
    public byte[] generateKey(int len) {
        currRegister = register;
        byte[] key = new byte[len];
        for(int i = 0; i < key.length; i++) { // Tạo khoá byte
            for(int j = 0; j < 8; j++) {

                byte abortedBit = getBitAtPos(polinom[0]);
                key[i] = (byte) (key[i] | (abortedBit << (8 - j - 1)));

                byte newFirstBit = abortedBit; // Tính toán bit đầu tiên thành bit xor theo đa thức
                for(int k = 1; k < polinom.length; k++) {
                    newFirstBit ^= getBitAtPos(polinom[k]);
                }
                currRegister = (currRegister << 1) & mask; // Chuyển thanh ghi
                currRegister = currRegister | newFirstBit; // Đặt bit mới sau khi tính toán
            }
        }
        return key;
    }

    public byte[] encrypt(byte[] plainBytes) {
        byte[] key = generateKey(plainBytes.length);
        byte[] cipherBytes = new byte[plainBytes.length];
        for(int i = 0; i < plainBytes.length; i++) {
            cipherBytes[i] = (byte) (plainBytes[i] ^ key[i]);
        }
        return cipherBytes;
    }

    public byte[] decrypt(byte[] cipherBytes) {
        return encrypt(cipherBytes);
    }

}