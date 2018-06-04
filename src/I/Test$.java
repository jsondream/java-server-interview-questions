package I;

/**
 * @author <a href="mailto:ge.sf.chn@gmail.com">shaofeng</a>
 * @see
 * @since 2018/6/4
 */
public class Test$ {

    public static void main(String[] args) {
        int[] arr = new int[]{0, 1, 2};
        int i = 0;
        arr[++i] = 1;//iinc、iload,此时arr[1]
    }
}
