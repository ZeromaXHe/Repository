package java8实战.example.chapter5;

import java8实战.example.chapter4.Dish;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;


/**
 * 《Java8实战》第5章 使用流
 */
public class Chapter5Test {
    public static void main(String[] args) {
        List<Dish> menu = Arrays.asList(
                new Dish("pork", false, 800, Dish.Type.MEAT),
                new Dish("beef", false, 700, Dish.Type.MEAT),
                new Dish("chicken", false, 400, Dish.Type.MEAT),
                new Dish("french fries", true, 530, Dish.Type.OTHER),
                new Dish("rice", true, 350, Dish.Type.OTHER),
                new Dish("season fruit", true, 120, Dish.Type.OTHER),
                new Dish("pizza", true, 550, Dish.Type.OTHER),
                new Dish("prawns", false, 300, Dish.Type.FISH),
                new Dish("salmon", false, 450, Dish.Type.FISH));

        //5.1.2 筛选各异的元素
        five_1_2_filter();
        System.out.println("=========================");
        //5.1.3 截短流
        five_1_3_limit(menu);
        System.out.println("=========================");
        //5.1.4 跳过元素
        five_1_4_Skip(menu);
        System.out.println("=========================");
        //5.2.2 流的扁平化
        five_2_2_flatMap();
        System.out.println("=========================");

        List<Integer> numbers1 = Arrays.asList(1, 2, 3);
        List<Integer> numbers2 = Arrays.asList(3, 4);

        //测验5.2 映射 (2)
        test_5_2_test2(numbers1, numbers2);
        System.out.println("=========================");
        //测验5.2 映射 (3)
        test_5_2_test3(numbers1, numbers2);
        System.out.println("=========================");

        Trader raoul = new Trader("Raoul", "Cambridge");
        Trader mario = new Trader("Mario", "Milan");
        Trader alan = new Trader("Alan", "Cambridge");
        Trader brian = new Trader("Brian", "Cambridge");
        List<Transaction> transactions = Arrays.asList(
                new Transaction(brian, 2011, 300),
                new Transaction(raoul, 2012, 1000),
                new Transaction(raoul, 2011, 400),
                new Transaction(mario, 2012, 710),
                new Transaction(mario, 2012, 700),
                new Transaction(alan, 2012, 950)
        );

        //5.5 付诸实践
        five_5_practice(transactions);
    }

    private static void five_5_practice(List<Transaction> transactions) {
        System.out.println("5.5 付诸实践");
        //(1) 找出2011年发生的所有交易，并按交易额排序（从低到高）。
        //(2) 交易员都在哪些不同的城市工作过？
        //(3) 查找所有来自于剑桥的交易员，并按姓名排序。
        //(4) 返回所有交易员的姓名字符串，按字母顺序排序。
        //(5) 有没有交易员是在米兰工作的？
        //(6) 打印生活在剑桥的交易员的所有交易额。
        //(7) 所有交易中，最高的交易额是多少？
        //(8) 找到交易额最小的交易。

        System.out.println("（1）找出2011年发生的所有交易，并按交易额排序（从低到高）");
        List<Transaction> transactionList =
                transactions.stream()
                        .filter(t -> t.getYear() == 2011)
                        .sorted(comparing(Transaction::getValue))
                        .collect(toList());
        System.out.println(transactionList);
        System.out.println("---------------");

        System.out.println("(2) 交易员都在哪些不同的城市工作过？");
        List<String> cityList =
                transactions.stream()
                        // 下面两个map可以合并为.map(transaction -> transaction.getTrader().getCity())
                        .map(Transaction::getTrader)
                        .map(Trader::getCity)
                        .distinct()
                        .collect(toList());
        System.out.println("cityList： " + cityList);
        // 这里还有一个新招：你可以去掉distinct()，改用toSet()，这样就会把流转换为集合。
        Set<String> cities =
                transactions.stream()
                        .map(transaction -> transaction.getTrader().getCity())
                        // java.util.stream.Collectors.toSet;
                        .collect(toSet());
        System.out.println("citySet: " + cities);
        System.out.println("---------------");

        System.out.println("(3) 查找所有来自于剑桥的交易员，并按姓名排序。");
        List<Trader> traderList =
                transactions.stream()
                        .map(Transaction::getTrader)
                        .filter(t -> "Cambridge".equals(t.getCity()))
                        //不要忘记去重
                        .distinct()
                        .sorted(comparing(Trader::getName))
                        .collect(toList());
        System.out.println(traderList);
        System.out.println("---------------");

        System.out.println("(4) 返回所有交易员的姓名字符串，按字母顺序排序。");
        String tradeNameStr = transactions.stream()
                .map(t -> t.getTrader().getName())
                .distinct()
                //直接排序，sorted内不需要传参数
                .sorted()
                .reduce("", (a, b) -> a + b);
        System.out.println("tradeNameStr: " + tradeNameStr);
        // 请注意，此解决方案效率不高（所有字符串都被反复连接，每次迭代的时候都要建立一个新的String对象）。
        // 下一章中， 你将看到一个更为高效的解决方案，它像下面这样使用joining（其内部会用到StringBuilder）
        String traderStr =
                transactions.stream()
                        .map(transaction -> transaction.getTrader().getName())
                        .distinct()
                        .sorted()
                        // java.util.stream.Collectors.joining
                        .collect(joining());
        System.out.println("traderStr: " + traderStr);
        System.out.println("---------------");

        System.out.println("(5) 有没有交易员是在米兰工作的？");
        boolean milanExist = transactions.stream()
                .anyMatch(t -> "Milan".equals(t.getTrader().getCity()));
        System.out.println(milanExist);
        System.out.println("---------------");

        System.out.println("(6) 打印生活在剑桥的交易员的所有交易额。");
        transactions.stream()
                .filter(t -> "Cambridge".equals(t.getTrader().getCity()))
                .map(Transaction::getValue)
                .forEach(System.out::println);
        System.out.println("---------------");

        System.out.println("(7) 所有交易中，最高的交易额是多少？");
        Optional<Integer> maxTradeVal =
                transactions.stream()
                        .map(Transaction::getValue)
                        .reduce(Integer::max);
        System.out.println(maxTradeVal.isPresent() ? maxTradeVal.get() : "无最高交易额");
        System.out.println("---------------");

        System.out.println("(8) 找到交易额最小的交易。");
        Optional<Transaction> minTradeVal =
                transactions.stream()
                        .reduce((t1, t2) -> t1.getValue() < t2.getValue() ? t1 : t2);
        System.out.println("使用reduce方法： " + (minTradeVal.isPresent() ? minTradeVal.get() : "无交易额最小的交易"));
        //你还可以做得更好。流支持min和max方法，它们可以接受一个Comparator作为参数，指定计算最小或最大值时要比较哪个键值：
        Optional<Transaction> smallestTransaction =
                transactions.stream()
                        .min(comparing(Transaction::getValue));
        System.out.println("使用min方法： " + (smallestTransaction.isPresent() ? smallestTransaction.get() : "无交易额最小的交易"));
    }

    private static void test_5_2_test3(List<Integer> numbers1, List<Integer> numbers2) {
        System.out.println("测验5.2 映射 (3)");
        // 如何扩展前一个例子，只返回总和能被3整除的数对呢？例如(2, 4)和(3, 3)是可以的。
        List<int[]> pairs2 =
                numbers1.stream()
                        .flatMap(i ->
                                numbers2.stream()
                                        .filter(j -> (i + j) % 3 == 0)
                                        .map(j -> new int[]{i, j})
                        )
                        .collect(toList());
        System.out.println("pairs2:");
        pairs2.stream()
                .map(Arrays::toString)
                .forEach(System.out::print);
        System.out.println();
    }

    private static void test_5_2_test2(List<Integer> numbers1, List<Integer> numbers2) {
        System.out.println("测验5.2 映射 (2)");
        // 给定两个数字列表，如何返回所有的数对呢？
        // 例如，给定列表[1, 2, 3]和列表[3, 4]，应该返回[(1, 3), (1, 4), (2, 3), (2, 4), (3, 3), (3, 4)]。
        // 为简单起见，你可以用有两个元素的数组来代表数对。
        List<int[]> pairs =
                numbers1.stream()
                        .flatMap(i -> numbers2.stream()
                                .map(j -> new int[]{i, j})
                        )
                        .collect(toList());
        System.out.println("pairs: ");
        pairs.stream()
                .map(Arrays::toString)
                .forEach(System.out::print);
        System.out.println();
    }

    private static void five_2_2_flatMap() {
        System.out.println("5.2.2 流的扁平化");
        // 对于一张单词表，如何返回一张列表，列出里面各不相同的字符呢？
        // 例如，给定单词列表["Hello","World"]，你想要返回列表["H","e","l", "o","W","r","d"]。
        // 首先，你需要一个字符流，而不是数组流。有一个叫作Arrays.stream()的方法可以接受一个数组并产生一个流
        // 使用flatMap方法的效果是，各个数组并不是分别映射成一个流，而是映射成流的内容。所有使用map(Arrays::stream)时生成的单个流都被合并起来，即扁平化为一个流。
        List<String> words = Arrays.asList("Hello", "World");
        List<String> uniqueCharacters =
                words.stream()
                        .map(w -> w.split(""))
                        .flatMap(Arrays::stream)
                        .distinct()
                        .collect(toList());
        System.out.println(uniqueCharacters);
    }

    private static void five_1_4_Skip(List<Dish> menu) {
        System.out.println("5.1.4 跳过元素");
        // 流还支持skip(n)方法，返回一个扔掉了前n个元素的流。如果流中元素不足n个，则返回一个空流。
        // 请注意， limit(n)和skip(n)是互补的！
        List<Dish> dishes2 = menu.stream()
                .filter(d -> d.getCalories() > 300)
                .skip(2)
                .collect(toList());
        System.out.println(dishes2);
    }

    private static void five_1_3_limit(List<Dish> menu) {
        System.out.println("5.1.3 截短流：");
        //流支持limit(n)方法，该方法会返回一个不超过给定长度的流。所需的长度作为参数传递给limit。如果流是有序的，则最多会返回前n个元素。
        //请注意limit也可以用在无序流上，比如源是一个Set。这种情况下， limit的结果不会以任何顺序排列。
        List<Dish> dishes = menu.stream()
                .filter(d -> d.getCalories() > 300)
                .limit(3)
                .collect(toList());
        System.out.println(dishes);
    }

    private static void five_1_2_filter() {
        System.out.println("5.1.2 筛选各异的元素：");
        //流还支持一个叫作distinct的方法，它会返回一个元素各异（根据流所生成元素的hashCode和equals方法实现）的流。
        List<Integer> numbers = Arrays.asList(1, 2, 1, 3, 3, 2, 4);
        numbers.stream()
                .filter(i -> i % 2 == 0)
                .distinct()
                .forEach(System.out::println);
    }
}
