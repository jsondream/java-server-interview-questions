package I;

import java.util.Arrays;

/**
 * TODO
 *
 * @author <a href="mailto:ge.sf.chn@gmail.com">shaofeng</a>
 * @see
 * @since 2018/6/4
 */
public class IITest {

    public static void main(String[] args) {
        int[] arr = new int[47];
        for(int i=0;i<47;i++) {
            arr[i] = i;
        }
        arr[31] = 29;
        Arrays.sort(arr);
    }
}
