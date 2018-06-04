package I;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Use insertion sort on tiny arrays
 *
 * @author <a href="mailto:ge.sf.chn@gmail.com">shaofeng</a>
 * @see Arrays#sort(Object[])
 * @see Collections#sort(List, Comparator)
 * @since 2018/6/4
 */
public class ITest {

    public static void main(String[] args) {
        int[] arr = new int[46];
        for(int i=0;i<46;i++) {
            arr[i] = i;
        }
        arr[0] = 2;
        m1(0, 45, arr);
    }

    public static void m1(int left, int right, int[] a) {
        do {
            if (left >= right) {
                return;
            }
        } while (a[++left] >= a[left - 1]);
        /*
         * Every element from adjoining part plays the role
         * of sentinel, therefore this allows us to avoid the
         * left range check on each iteration. Moreover, we use
         * the more optimized algorithm, so called pair insertion
         * sort, which is faster (in the context of Quicksort)
         * than traditional implementation of insertion sort.
         */
        for (int k = left; ++left <= right; k = ++left) {
            int a1 = a[k], a2 = a[left];

            if (a1 < a2) {
                a2 = a1; a1 = a[left];
            }
            while (a1 < a[--k]) {
                a[k + 2] = a[k];
            }
            a[++k + 1] = a1;

            while (a2 < a[--k]) {
                a[k + 1] = a[k];
            }
            a[k + 1] = a2;
        }
        int last = a[right];

        while (last < a[--right]) {
            a[right + 1] = a[right];
        }
        a[right + 1] = last;
    }
}
