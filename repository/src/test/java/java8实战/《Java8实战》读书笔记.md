# Java8实战

## 第1章 为什么要关心Java8

### 1.1.2 流处理

Java8可以透明地把输入的不相关部分拿到几个CPU内核上去分别执行你的Stream操作流水线——这几乎免费的并行，用不着去费劲搞Thread了。

## 第3章 Lambda表达式

### 3.2.2 函数描述符

@FunctionalInterface又是怎么一回事？

这个标注用于表示该接口会设计成一个函数式接口。如果你用@FunctionalInterface定义了一个接口，而它却不是函数式接口的话，编译器将返回一个提示原因的错误。例如，错误信息可能是“Multiple non-overriding abstract methods found in interface Foo”，表明存在多个抽象方法。请注意，@FunctionalInterface不是必需的，但对于为此设计的接口而言，使用它是比较好的做法。它就像是@Override标注表示方法被重写了。

### 3.4.3 Function

异常、Lambda，还有函数式接口又是怎么回事呢？

请注意，任何函数式接口都不允许抛出受检异常（checked exception）。如果你需要Lambda表达式来抛出异常，有两种办法：定义一个自己的函数式接口，并声明受检异常，或者把Lambda包在一个try/catch块中。

### 3.5.4 使用局部变量

尽管如此，还有一点点小麻烦：关于能对这些变量做什么有一些限制。 Lambda可以没有限制地捕获（也就是在其主体中引用）实例变量和静态变量。但局部变量必须显式声明为final，或事实上是final。换句话说， Lambda表达式只能捕获指派给它们的局部变量一次。（注：捕获实例变量可以被看作捕获最终局部变量this。） 例如，下面的代码无法编译，因为portNumber变量被赋值两次：

~~~ java
int portNumber = 1337;
Runnable r = () -> System.out.println(portNumber);
portNumber = 31337;
~~~

对局部变量的限制

你可能会问自己，为什么局部变量有这些限制。第一，实例变量和局部变量背后的实现有一个关键不同。实例变量都存储在堆中，而局部变量则保存在栈上。如果Lambda可以直接访问局部变量，而且Lambda是在一个线程中使用的，则使用Lambda的线程，可能会在分配该变量的线程将这个变量收回之后，去访问该变量。因此， Java在访问自由局部变量时，实际上是在访问它的副本，而不是访问原始变量。如果局部变量仅仅赋值一次那就没有什么区别了——因此就有了这个限制。

第二，这一限制不鼓励你使用改变外部变量的典型命令式编程模式（我们会在以后的各章中解释，这种模式会阻碍很容易做到的并行处理）。

### 3.6.2 构造方法引用

对于一个现有构造函数，你可以利用它的名称和关键字new来创建它的一个引用：`ClassName::new`。它的功能与指向静态方法的引用类似。例如，假设有一个构造函数没有参数。它适合Supplier的签名`() -> Apple`。你可以这样做：

```java
Supplier<Apple> c1 = Apple::new;
Apple a1 = c1.get();
```
这就等价于：

```java
Supplier<Apple> c1 = () -> new Apple();
Apple a1 = c1.get();
```

如果你的构造函数的签名是Apple(Integer weight)，那么它就适合Function接口的签名，于是你可以这样写：

```java
Function<Integer, Apple> c2 = Apple::new;
Apple a2 = c2.apply(110);
```

这就等价于：

```java
Function<Integer, Apple> c2 = (weight) -> new Apple(weight);
Apple a2 = c2.apply(110);
```

如果你有一个具有两个参数的构造函数Apple(String color, Integer weight)，那么它就适合BiFunction接口的签名，于是你可以这样写：

```java
BiFunction<String, Integer, Apple> c3 = Apple::new;
Apple a3 = c3.apply("green", 110);
```

这就等价于：

~~~java
BiFunction<String, Integer, Apple> c3 =
    (color, weight) -> new Apple(color, weight);
Apple a3 = c3.apply("green", 110);
~~~

测验3.7：构造函数引用

你已经看到了如何将有零个、一个、两个参数的构造函数转变为构造函数引用。那要怎么样才能对具有三个参数的构造函数，比如Color(int, int, int)， 使用构造函数引用呢？

答案：你看，构造函数引用的语法是ClassName::new，那么在这个例子里面就是Color::new。但是你需要与构造函数引用的签名匹配的函数式接口。但是语言本身并没有提供这样的函数式接口，你可以自己创建一个：
~~~java
public interface TriFunction<T, U, V, R>{
    R apply(T t, U u, V v);
}
~~~
现在你可以像下面这样使用构造函数引用了：
~~~java
TriFunction<Integer, Integer, Integer, Color> colorFactory = Color::new;
~~~

### 3.8.1 比较器复合

我们前面看到，你可以使用静态方法Comparator.comparing，根据提取用于比较的键值的Function来返回一个Comparator，如下所示：
~~~java
Comparator<Apple> c = Comparator.comparing(Apple::getWeight);
~~~
1.逆序

如果你想要对苹果按重量递减排序怎么办？用不着去建立另一个Comparator的实例。接口有一个默认方法reversed可以使给定的比较器逆序。因此仍然用开始的那个比较器，只要修改一下前一个例子就可以对苹果按重量递减排序：
~~~java
inventory.sort(comparing(Apple::getWeight).reversed());
~~~
2.比较器链

上面说得都很好，但如果发现有两个苹果一样重怎么办？哪个苹果应该排在前面呢？你可能需要再提供一个Comparator来进一步定义这个比较。比如，在按重量比较两个苹果之后，你可能想要按原产国排序。 thenComparing方法就是做这个用的。它接受一个函数作为参数（就像comparing方法一样），如果两个对象用第一个Comparator比较之后是一样的，就提供第二个Comparator。你又可以优雅地解决这个问题了：
~~~java
inventory.sort(comparing(Apple::getWeight)
    .reversed()
    .thenComparing(Apple::getCountry));
~~~

### 3.8.2 谓词复合

谓词接口包括三个方法： negate、 and和or，让你可以重用已有的Predicate来创建更复杂的谓词。比如，你可以使用negate方法来返回一个Predicate的非，比如苹果不是红的：
~~~java
Predicate<Apple> notRedApple = redApple.negate();
~~~
你可能想要把两个Lambda用and方法组合起来，比如一个苹果既是红色又比较重：
~~~java
Predicate<Apple> redAndHeavyApple =
    redApple.and(a -> a.getWeight() > 150);
~~~
你可以进一步组合谓词，表达要么是重（ 150克以上）的红苹果，要么是绿苹果：
~~~java
Predicate<Apple> redAndHeavyAppleOrGreen =
    redApple.and(a -> a.getWeight() > 150)
    .or(a -> "green".equals(a.getColor()));
~~~
这一点为什么很好呢？从简单Lambda表达式出发，你可以构建更复杂的表达式，但读起来仍然和问题的陈述差不多！请注意， and和or方法是按照在表达式链中的位置，从左向右确定优先级的。因此， a.or(b).and(c)可以看作(a || b) && c。

### 3.8.3 函数复合

最后，你还可以把Function接口所代表的Lambda表达式复合起来。 Function接口为此配了andThen和compose两个默认方法，它们都会返回Function的一个实例。

andThen方法会返回一个函数，它先对输入应用一个给定函数，再对输出应用另一个函数。

你也可以类似地使用compose方法，先把给定的函数用作compose的参数里面给的那个函数，然后再把函数本身用于结果。比如在上一个例子里用compose的话，它将意味着f(g(x))，而andThen则意味着g(f(x))

## 第4章 引入流

### 4.3.1 只能遍历一次

请注意，和迭代器类似，流只能遍历一次。遍历完之后，我们就说这个流已经被消费掉了。你可以从原始数据源那里再获得一个新的流来重新遍历一遍，就像迭代器一样（这里假设它是集合之类的可重复的源，如果是I/O通道就没戏了）。例如，以下代码会抛出一个异常，说流已被消费掉了：
~~~java
List<String> title = Arrays.asList("Java8", "In", "Action");
Stream<String> s = title.stream();
s.forEach(System.out::println);
s.forEach(System.out::println); // java.lang.IllegalStateException: 流已被操作或关闭
~~~
所以要记得，流只能消费一次！

## 第5章 使用流

### 5.1 筛选和切片：

5.1.1 用谓词筛选：Streams接口支持filter方法（你现在应该很熟悉了）。该操作会接受一个谓词（一个返回boolean的函数）作为参数，并返回一个包括所有符合谓词的元素的流。

5.1.2 筛选各异的元素：流还支持一个叫作distinct的方法，它会返回一个元素各异（根据流所生成元素的 hashCode和equals方法实现）的流。

5.1.3 截短流：流支持limit(n)方法，该方法会返回一个不超过给定长度的流。所需的长度作为参数传递给limit。如果流是有序的，则最多会返回前n个元素。请注意limit也可以用在无序流上，比如源是一个Set。这种情况下， limit的结果不会以任何顺序排列。

5.1.4 跳过元素：流还支持skip(n)方法，返回一个扔掉了前n个元素的流。如果流中元素不足n个，则返回一个空流。请注意， limit(n)和skip(n)是互补的！

### 5.2 映射

5.2.1 对流中每一个元素应用函数：流支持map方法，它会接受一个函数作为参数。这个函数会被应用到每个元素上，并将其映射成一个新的元素（使用映射一词，是因为它和转换类似，但其中的细微差别在于它是“创建一个新版本”而不是去“修改”）

如果你要找出每道菜的名称有多长，怎么做？你可以像下面这样，再链接上一个map：

~~~java
List<Integer> dishNameLengths = menu.stream()
    .map(Dish::getName)
    .map(String::length)
    .collect(toList());
~~~

5.2.2 流的扁平化：使用flatMap方法的效果是，各个数组并不是分别映射成一个流，而是映射成流的内容。所有使用map(Arrays::stream)时生成的单个流都被合并起来，即扁平化为一个流。

### 5.3 查找和匹配

5.3.1 检查谓词是否至少匹配一个元素：anyMatch方法可以回答“流中是否有一个元素能匹配给定的谓词”。

5.3.2 检查谓词是否匹配所有元素：allMatch方法的工作原理和anyMatch类似，但它会看看流中的元素是否都能匹配给定的谓词。和allMatch相对的是noneMatch。它可以确保流中没有任何元素与给定的谓词匹配。

anyMatch、 allMatch和noneMatch这三个操作都用到了我们所谓的短路，这就是大家熟悉的Java中&&和||运算符短路在流中的版本。

5.3.3 查找元素：findAny方法将返回当前流中的任意元素。它可以与其他流操作结合使用。

~~~java
Optional<Dish> dish =
    menu.stream()
    .filter(Dish::isVegetarian)
    .findAny();
~~~
Optional\<T>类（ java.util.Optional）是一个容器类，代表一个值存在或不存在。在上面的代码中， findAny可能什么元素都没找到。 Java 8的库设计人员引入了Optional\<T>，这样就不用返回众所周知容易出问题的null了。

Optional里面几种可以迫使你显式地检查值是否存在或处理值不存在的情形的方法也不错。
 isPresent()将在Optional包含值的时候返回true, 否则返回false。
 ifPresent(Consumer\<T> block)会在值存在的时候执行给定的代码块。我们在第3章介绍了Consumer函数式接口；它让你传递一个接收T类型参数，并返回void的Lambda表达式。
 T get()会在值存在时返回值，否则抛出一个NoSuchElement异常。
 T orElse(T other)会在值存在时返回值，否则返回一个默认值。

例如，在前面的代码中你需要显式地检查Optional对象中是否存在一道菜可以访问其名称：
~~~java
menu.stream()
    .filter(Dish::isVegetarian)
    .findAny()
    .ifPresent(d -> System.out.println(d.getName());
~~~

5.3.4 查找第一个元素：有些流有一个出现顺序（ encounter order）来指定流中项目出现的逻辑顺序（比如由List或排序好的数据列生成的流）。对于这种流，你可能想要找到第一个元素。为此有一个findFirst方法，它的工作方式类似于findAny。

何时使用findFirst和findAny

你可能会想，为什么会同时有findFirst和findAny呢？答案是并行。找到第一个元素在并行上限制更多。如果你不关心返回的元素是哪个，请使用findAny，因为它在使用并行流时限制较少。

### 5.4 规约

到目前为止，你见到过的终端操作都是返回一个boolean（ allMatch之类的）、 void（ forEach）或Optional对象（ findAny等）。你也见过了使用collect来将流中的所有元素组合成一个List。

在本节中，你将看到如何把一个流中的元素组合起来，使用reduce操作来表达更复杂的查询，比如“计算菜单中的总卡路里”或“菜单中卡路里最高的菜是哪一个”。此类查询需要将流中所有元素反复结合起来，得到一个值，比如一个Integer。这样的查询可以被归类为归约操作（将流归约成一个值）。用函数式编程语言的术语来说，这称为折叠(fold），因为你可以将这个操作看成把一张长长的纸（你的流）反复折叠成一个小方块，而这就是折叠操作的结果。

你可以像下面这样对流中所有的元素求和：
~~~java
int sum = numbers.stream().reduce(0, (a, b) -> a + b);
~~~
你也很容易把所有的元素相乘，只需要将另一个Lambda： (a, b) -> a * b传递给reduce操作就可以了：
~~~java
int product = numbers.stream().reduce(1, (a, b) -> a * b);
~~~
你可以使用方法引用让这段代码更简洁。在Java 8中， Integer类现在有了一个静态的sum方法来对两个数求和，这恰好是我们想要的，用不着反复用Lambda写同一段代码了：
~~~java
int sum = numbers.stream().reduce(0, Integer::sum);
~~~
reduce还有一个重载的变体，它不接受初始值，但是会返回一个Optional对象：
~~~java
Optional<Integer> sum = numbers.stream().reduce((a, b) -> (a + b));
~~~
为什么它返回一个Optional\<Integer>呢？考虑流中没有任何元素的情况。 reduce操作无法返回其和，因为它没有初始值。这就是为什么结果被包裹在一个Optional对象里，以表明和可能不存在。

你可以像下面这样使用reduce来计算流中的最大值:
~~~java
Optional<Integer> max = numbers.stream().reduce(Integer::max);
~~~
要计算最小值，你需要把Integer.min传给reduce来替换Integer.max：
~~~java
Optional<Integer> min = numbers.stream().reduce(Integer::min);
~~~

**归约方法的优势与并行化**

相比于前面写的逐步迭代求和，使用reduce的好处在于，这里的迭代被内部迭代抽象掉了，这让内部实现得以选择并行执行reduce操作。而迭代式求和例子要更新共享变量sum，这不是那么容易并行化的。如果你加入了同步，很可能会发现线程竞争抵消了并行本应带来的性能提升！这种计算的并行化需要另一种办法：将输入分块，分块求和，最后再合并起来。但这样的话代码看起来就完全不一样了。你在第7章会看到使用分支/合并框架来做是什么样子。但现在重要的是要认识到，可变的累加器模式对于并行化来说是死路一条。你需要一种新的模式，这正是reduce所提供的。你还将在第7章看到，使用流来对所有的元素并行求和时，你的代码几乎不用修改： stream()换成了parallelStream()。
~~~java
int sum = numbers.parallelStream().reduce(0, Integer::sum);
~~~
但要并行执行这段代码也要付一定代价，我们稍后会向你解释：传递给reduce的Lambda不能更改状态（如实例变量），而且操作必须满足结合律才可以按任意顺序执行。

**流操作：无状态和有状态**

你已经看到了很多的流操作。乍一看流操作简直是灵丹妙药，而且只要在从集合生成流的时候把Stream换成parallelStream就可以实现并行。

当然，对于许多应用来说确实是这样，就像前面的那些例子。你可以把一张菜单变成流，用filter选出某一类的菜肴，然后对得到的流做map来对卡路里求和，最后reduce得到菜单的总热量。这个流计算甚至可以并行进行。但这些操作的特性并不相同。它们需要操作的内部状态还是有些问题的。

诸如map或filter等操作会从输入流中获取每一个元素，并在输出流中得到0或1个结果。这些操作一般都是**无状态**的：它们没有内部状态（假设用户提供的Lambda或方法引用没有内部可变状态）。

但诸如reduce、 sum、 max等操作需要内部状态来累积结果。在上面的情况下，内部状态很小。在我们的例子里就是一个int或double。不管流中有多少元素要处理，内部状态都是有界的。

相反，诸如sort或distinct等操作一开始都和filter和map差不多——都是接受一个流，再生成一个流（中间操作），但有一个关键的区别。从流中排序和删除重复项时都需要知道先前的历史。例如，排序要求所有元素都放入缓冲区后才能给输出流加入一个项目，这一操作的存储要求是无界的。要是流比较大或是无限的，就可能会有问题（把质数流倒序会做什么呢？它应当返回最大的质数，但数学告诉我们它不存在）。我们把这些操作叫作**有状态操作**。

| 操作      | 类型              | 返回类型    | 使用的类型/函数式接口  | 函数描述符     |
| --------- | ----------------- | ----------- | ---------------------- | -------------- |
| filter    | 中间              | Stream\<T>   | Predicate\<T>           | T -> boolean   |
| distinct  | 中间(有状态-无界) | Stream\<T>   |                        |                |
| skip      | 中间(有状态-有界) | Stream\<T>   | long                   |                |
| limit     | 中间(有状态-有界) | Stream\<T> | long                   |                |
| map       | 中间              | Stream\<R>  | Function\<T, R>         | T -> R         |
| flatMap   | 中间              | Stream\<R>  | Function\<T, Stream\<R>> | T -> Stream\<R> |
| sorted    | 中间(有状态-无界) | Stream\<T>  | Comparator\<T>          | (T, T) -> int  |
| anyMatch  | 终端              | boolean     | Predicate\<T>           | T -> boolean   |
| noneMatch | 终端              | boolean     | Predicate\<T>           | T -> boolean   |
| allMatch  | 终端              | boolean     | Predicate\<T>           | T -> boolean   |
| findAny   | 终端              | Optional\<T> |                        |                |
| findFirst | 终端              | Optional\<T> |                        |                |
| forEach   | 终端              | void        | Consumer\<T>            | T -> void      |
| collect   | 终端              | R           | Collector\<T, A, R>     |                |
| reduce    | 终端(有状态-有界) | Optional\<T> | BinaryOperator\<T>      | (T, T) -> T    |
| count     | 终端              | long        |                        |                |

### 5.6 数值流

我们在前面看到了可以使用reduce方法计算流中元素的总和。例如，你可以像下面这样计算菜单的热量：
~~~java
int calories = menu.stream()
    .map(Dish::getCalories)
    .reduce(0, Integer::sum);
~~~
这段代码的问题是，它有一个暗含的装箱成本。每个Integer都必须拆箱成一个原始类型，再进行求和。

但不要担心， Stream API还提供了原始类型流特化，专门支持处理数值流的方法。

### 5.6.1 原始类型流特化

Java 8引入了三个原始类型特化流接口来解决这个问题： IntStream、 DoubleStream和LongStream，分别将流中的元素特化为int、 long和double，从而避免了暗含的装箱成本。每个接口都带来了进行常用数值归约的新方法，比如对数值流求和的sum，找到最大元素的max。此外还有在必要时再把它们转换回对象流的方法。要记住的是，这些特化的原因并不在于流的复杂性，而是装箱造成的复杂性——即类似int和Integer之间的效率差异。

**1. 映射到数值流**

将流转换为特化版本的常用方法是mapToInt、 mapToDouble和mapToLong。这些方法和前面说的map方法的工作方式一样，只是它们返回的是一个特化流，而不是Stream\<T>。例如，你可以像下面这样用mapToInt对menu中的卡路里求和：
~~~java
int calories = menu.stream()
    .mapToInt(Dish::getCalories)
    .sum();
~~~
这里， mapToInt会从每道菜中提取热量（用一个Integer表示），并返回一个IntStream（而不是一个Stream\<Integer>）。然后你就可以调用IntStream接口中定义的sum方法，对卡路里求和了！请注意，如果流是空的， sum默认返回0。 IntStream还支持其他的方便方法，如max、 min、 average等。

**2. 转换回对象流**

同样，一旦有了数值流，你可能会想把它转换回非特化流。例如， IntStream上的操作只能产生原始整数：IntStream 的 map 操作接受的Lambda必须接受int并返回 int（一个IntUnaryOperator）。但是你可能想要生成另一类值，比如Dish。为此，你需要访问Stream接口中定义的那些更广义的操作。要把原始流转换成一般流（每个int都会装箱成一个Integer），可以使用boxed方法，如下所示：

~~~java
IntStream intStream = menu.stream().mapToInt(Dish::getCalories);
Stream<Integer> stream = intStream.boxed();
~~~

在下一节中会看到，在需要将数值范围装箱成为一个一般流时， boxed尤其有用。  

**3. 默认值OptionalInt  **

求和的那个例子很容易，因为它有一个默认值： 0。但是，如果你要计算IntStream中的最大元素，就得换个法子了，因为0是错误的结果。如何区分没有元素的流和最大值真的是0的流呢？前面我们介绍了Optional类，这是一个可以表示值存在或不存在的容器。 Optional可以用Integer、 String等参考类型来参数化。对于三种原始流特化，也分别有一个Optional原始类型特化版本： OptionalInt、 OptionalDouble和OptionalLong。

例如，要找到IntStream中的最大元素，可以调用max方法，它会返回一个OptionalInt：

~~~java
OptionalInt maxCalories = menu.stream()
	.mapToInt(Dish::getCalories)
	.max();
~~~

现在，如果没有最大值的话，你就可以显式处理OptionalInt去定义一个默认值了：

~~~java
int max = maxCalories.orElse(1);  
~~~

### 5.6.2 数值范围

和数字打交道时，有一个常用的东西就是数值范围。比如，假设你想要生成1和100之间的所有数字。 Java 8引入了两个可以用于IntStream和LongStream的静态方法，帮助生成这种范围：range和rangeClosed。这两个方法都是第一个参数接受起始值，第二个参数接受结束值。但range是不包含结束值的，而rangeClosed则包含结束值。  

~~~java
IntStream evenNumbers = IntStream.rangeClosed(1, 100)
	.filter(n -> n % 2 == 0);
System.out.println(evenNumbers.count());
~~~

这里我们用了rangeClosed方法来生成1到100之间的所有数字。它会产生一个流，然后你可以链接filter方法，只选出偶数。到目前为止还没有进行任何计算。最后，你对生成的流调用count。因为count是一个终端操作，所以它会处理流，并返回结果50，这正是1到100（包括两端）中所有偶数的个数。请注意，比较一下，如果改用IntStream.range(1, 100)，则结果将会是49个偶数，因为range是不包含结束值的。

