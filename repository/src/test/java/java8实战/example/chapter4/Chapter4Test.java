package java8实战.example.chapter4;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Chapter4Test {

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

        System.out.println("4.2 流简介：");
        //从menu获得流（菜肴列表）
        List<String> threeHighCaloricDishNames =
                //建立操作流水线
                menu.stream()
                        //首先选出高热量的菜肴
                        .filter(d -> d.getCalories() > 300)
                        //获取菜名
                        .map(Dish::getName)
                        //只选择头三个
                        .limit(3)
                        //将结果保存在另一个List中 java.util.stream.Collectors.toList;
                        .collect(toList());
        //结果是[pork, beef, chicken]
        System.out.println(threeHighCaloricDishNames);

        System.out.println("===================================");
        System.out.println("4.4.1 中间操作:");
        List<String> names =
                menu.stream()
                        .filter(d -> {
                            //打印当前筛选的菜肴
                            System.out.println("filtering " + d.getName());
                            return d.getCalories() > 300;
                        })
                        .map(d -> {
                            //提取菜名时打印出来
                            System.out.println("mapping " + d.getName());
                            return d.getName();
                        })
                        .limit(3)
                        .collect(toList());
        System.out.println(names);
        //结果是：
        //filtering pork
        //mapping pork
        //filtering beef
        //mapping beef
        //filtering chicken
        //mapping chicken
        //[pork, beef, chicken]
        // 你会发现，有好几种优化利用了流的延迟性质。
        // 第一，尽管很多菜的热量都高于300卡路里，但只选出了前三个！这是因为limit操作和一种称为短路的技巧，我们会在下一章中解释。
        // 第二，尽管filter和map是两个独立的操作，但它们合并到同一次遍历中了（我们把这种技术叫作循环合并）。
    }
}
