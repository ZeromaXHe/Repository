# Java8实战

## 第1章 为什么要关心Java8

1.1.2 流处理

Java8可以透明地把输入的不相关部分拿到几个CPU内核上去分别执行你的Stream操作流水线——这几乎免费的并行，用不着去费劲搞Thread了。

## 第3章 Lambda表达式

3.2.2 函数描述符

@FunctionalInterface又是怎么一回事？

这个标注用于表示该接口会设计成一个函数式接口。如果你用@FunctionalInterface定义了一个接口，而它却不是函数式接口的话，编译器将返回一个提示原因的错误。例如，错误信息可能是“Multiple non-overriding abstract methods found in interface Foo”，表明存在多个抽象方法。请注意，@FunctionalInterface不是必需的，但对于为此设计的接口而言，使用它是比较好的做法。它就像是@Override标注表示方法被重写了。

3.4.3 Function

异常、Lambda，还有函数式接口又是怎么回事呢？

请注意，任何函数式接口都不允许抛出受检异常（checked exception）。如果你需要Lambda表达式来抛出异常，有两种办法：定义一个自己的函数式接口，并声明受检异常，或者把Lambda包在一个try/catch块中。