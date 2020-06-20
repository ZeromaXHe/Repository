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

