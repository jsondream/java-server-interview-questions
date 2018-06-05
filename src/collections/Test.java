package collections;

import java.util.*;

/**
 * TODO
 *
 * @author <a href="mailto:ge.sf.chn@gmail.com">shaofeng</a>
 * @see Collections#sort(java.util.List)
 * @since 2018/6/5
 */
public class Test {

    public static void main(String[] args) {
        List list = new LinkedList<Integer>();
        for (int i = 0; i < 32; i++) {
            list.add(i);
        }
        Collections.sort(list);
    }
}
