package expression.generic;

import expression.Const;

public class Main {
    public static void main(String[] args) throws Exception {
     //   String type = args[0];
     //   String ex = args[1];

        int rw = -1;
        String type = "p";
        String ex = "(4 / -4)";
        GenericTabulator gt = new GenericTabulator();
//        Object[][][] result = gt.tabulate(type, ex,-2147483648, -2147483648, -2147483648, -2147483640 ,-2147483648, -2147483643);
          Object[][][] result = gt.tabulate(type, ex,-7, 1, -5, 8 ,-7, 5);

        Const<Integer> a = new Const<>(24);
        int r = a.evaluate(2,2,2);
        System.out.println(r);

        int y = 5;
    }
}
