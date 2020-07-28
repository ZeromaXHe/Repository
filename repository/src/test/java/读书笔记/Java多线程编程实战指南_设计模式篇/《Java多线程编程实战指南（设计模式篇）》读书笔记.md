# Java多线程编程实战指南（设计模式篇）

# 第1章 Java多线程编程实战基础

## 1.1 无处不在的线程

进程(Process)代表运行中的程序。一个运行的Java程序就是一个进程。从操作系统的角度来看，线程（Thread）是进程中可独立执行的子任务。一个进程可以包含多个线程，同一个进程中线程共享该进程所申请到的资源，如内存空间和文件句柄等。从JVM的角度来看，线程是进程中的一个组件（Component），它可以看作执行Java代码的最小单位。Java程序中任何一段代码总是执行某个确定的线程中的。JVM启动的时候会创建一个main线程，该线程负责执行Java程序的入口方法（main方法）。如清单1-1所示的代码展示了Java程序中的代码总是由某个确定的线程运行的。

清单1-1 Java代码的执行线程
~~~java
public class JavaThreadAnywhere {
    public static void main(String[] args){
        System.out.println("The main method was executed by thread:" 
            + Thread.currentThread().getName());
        Helper helper = new Helper("Java Thread Anywhere");
        helper.run();
    }

    static class Helper implements Runnable {
        private final String message;
    
        public Helper(String message){
            this.message = message;
        }

        private void doSomething(String message){
            System.out.println("The doSomething method was executed by thread:" 
                + Thread.currentThread().getName());
            System.out.println("Do something with " + message);
        }

        @Override
        public void run(){
            doSomething(message);
        }
    }
}
~~~

如清单1-1所示的程序运行时输出如下：
~~~
The main method was executed by thread:main
The doSomething method was executed by thread:main
Do something with Java Thread Anywhere
~~~

从上面的输出可以看出，类JavaThreadAnywhere的main方法以及类Helper的doSomething方法都是由main方法负责执行的。

在多线程编程中，弄清楚一段代码具体是由哪个（或者哪种）线程去负责执行的这点很重要，这关系到性能问题、线程安全问题等。

Java中的线程可以分为守护线程（Daemon Thread）和用户线程（User Thread）。用户线程阻止JVM的正常停止，即JVM正常停止前应用程序中的所有用户线程必须先停止完毕；否则JVM无法停止。而守护线程则不会影响JVM的正常停止，即应用程序中有守护线程在运行也不影响JVM的正常停止。因此，守护线程通常用于执行一些重要性不是很高的任务，例如用于监视其他线程的运行情况。

## 1.2 线程的创建与运行

在Java语言中，一个线程就是一个java.lang.Thread类的实例。因此，在Java语言中创建一个线程就是创建一个Thread类的实例，当然这离不开内存的分配。创建一个Thread实例与创建其他类的实例所不同的是，JVM会为一个Thread实例分配两个调用栈（Call Stack）所需的内存空间。这两个调用栈一个用于跟踪Java代码间的调用关系，另一个用于跟踪Java代码对本地代码（即Native代码，通常是C代码）的调用关系。

一个Thread实例通常对应两个线程。一个是JVM中的线程（或称之为Java线程），另一个是与JVM中的线程相对应的依赖于JVM宿主机操作系统的本地（Native）线程。启动一个Java线程只需要调用相应Thread实例的start方法即可。线程启动后，当相应的线程被JVM的线程调度器调度到运行时，相应Thread实例的run方法会被JVM调用，如清单1-2所示。

清单1-2 Java线程的创建与运行
~~~java
public class JavaThreadCreationAndRun {
    public static void main(String[] args) {
        System.out.println("The main method was executed by thread:" 
                    + Thread.currentThread().getName());
        Helper helper = new Helper("Java Thread Anywhere");

        // 创建一个线程
        Thread thread = new Thread(helper);
        
        // 设置线程名
        thread.setName("A-Worker-Thread");

        // 启动线程
        thread.start();
    }

    static class Helper implements Runnable {
        private final String message;
    
        public Helper(String message){
            this.message = message;
        }

        private void doSomething(String message){
            System.out.println("The doSomething method was executed by thread:" 
                + Thread.currentThread().getName());
            System.out.println("Do something with " + message);
        }

        @Override
        public void run(){
            doSomething(message);
        }
    }
}
~~~

清单1-2中，我们通过直接new一个Thread类实例来创建一个线程。Thread类的其中一个构造器支持传入一个java.lang.Runnable接口实例，当相应线程启动时该实例的run方法会被JVM调用。

如清单1-2所示的程序运行时输出如下：
~~~
The main method was executed by thread:main
The doSomething method was executed by thread:A-Worker-Thread
Do something with Java Thread Anywhere
~~~

与清单1-1所示的代码相比，同样的类Helper的同一个方法doSomething此时是由名为A-Worker-Thread的线程而非main线程负责执行。这是因为清单1-1中，类Helper的run方法由main线程所执行的main方法直接调用，而清单1-2中类Helper的run方法我们并没有在代码中直接对其进行调用，而是由JVM通过其创建的线程（线程名为A-Worker-Thread）进行调用。

清单1-2中，对线程对象的start方法的调用（thread.start()）这段代码时运行在main方法中的，而main方法是由main线程负责执行。因此，这里我们所创建的线程thread就可以看成是main线程的一个子线程，而main线程就是该线程的父线程。

Java语言中，子线程是否是一个守护线程取决于其父线程：默认情况下父线程是守护线程则子线程也是守护线程，父线程是用户线程则子线程也是用户线程。当然，父线程在创建子线程后，启动子线程之前可以调用Thread实例的setDaemon方法来修改线程的这一属性。

Thread类自身是一个实现java.lang.Runnable接口的对象，我们也可以通过定义一个Thread类的子类来创建线程，自定义的线程类要覆盖其父类的run方法，如清单1-3所示。

清单1-3 以创建Thread子类的方式创建线程
~~~java
public class ThreadCreationViaSubclass {
    public static void main(String[] args) {
        Thread thread = new CustomThread();
        thread.start();
    }

    static class CustomThread extends Thread {
        @Override
        public void run() {
            System.out.println("Running...");
        }
    }
}
~~~

## 1.3 线程的状态与上下文切换

Java语言中，一个线程从其创建、启动到其运行结束的整个生命周期可能经历若干个状态：

- new 
- runnable （从new进行Thread.start()进入）
    - ready （从running进行Thread.yield()、 suspended by Scheduler）
    - running （selected by Scheduler）
- blocked （request blocked I/O 、try to acquire a lock从runnable进入本状态；当I/O completed或lock acquired时返回runnable状态）
- waiting （Object.wait()、Thread.join()、LockSupport.park()时从runnable进入本状态；当Object.notify()、Object.notifyAll()、waited Thread terminated、LockSupport.unpark时返回runnable状态 ）
- timed_waiting (Thread.sleep(sleepTime)、Thread.wait(timeOut)、LockSupport.parkXXX时从runnable进入本状态；timeout elapsed时返回runnable状态)
- terminated （Thread.run() exits时从runnable转为本状态）

Java线程的状态可以通过相应Thread实例的getState方法获取。该方法的返回值类型Thread.State是一个枚举类型（Enum）。Thread.State所定义的线程状态包括以下几种。

- NEW: 一个刚创建而未启动的线程处于该状态。由于一个线程实例只能够被启动一次，因此一个线程只可能有一次处于该状态。
- RUNNABLE：该状态可以看成是一个符合的状态。它包括两个子状态：READY和RUNNING。前者表示处于该状态的线程可以被JVM的线程调度器（Scheduler）进行调度而使之处于RUNNING状态。后者表示处于该状态的线程正在运行，即相应线程对象的run方法中的代码所对应的指令正在由CPU执行。当Thread实例的yield方法被调用时或者由于线程调度器的原因，相应线程的状态会由RUNNING转换为READY。
- BLOCKED：一个线程发起一个阻塞式I/O（Blocking I/O）操作后，或者试图去获得一个由其他线程持有的锁时，相应的线程会处于该状态。处于该状态的线程并不会占用CPU资源。当相应的I/O操作完成后，或者相应的锁被其他线程释放后，该线程的状态又可以转换为RUNNABLE。
- WAITING：一个线程执行了某些方法调用之后就会处于这种无限等待其他线程执行特定操作的状态。这些方法包括：Object.wait()、Thread.join()和LockSupport.park()。能够使相应线程从WAITING转换到RUNNABLE的相应方法包括：Object.notify()、Object.notifyAll()和LockSupport.unpark(thread)。
- TIMED_WAITING：该状态和WAITING类似，差别在于处于该状态的线程并非无限等待其他线程执行特定操作，而是处于带有时间限制的等待状态。当其他线程没有在指定时间内执行该线程所期望的特定操作时，该线程的状态自动转换为RUNNABLE。
- TERMINATED：已经执行结束的线程处于该状态。由于一个线程实例只能够被启动一次，因此一个线程也只可能有一次处于该状态。Thread实例的run方法正常返回或者由于抛出异常而提前终止都会导致相应线程处于该状态。

从上述描述可知，一个线程在其整个生命周期中，只可能一次处于NEW状态和TERMINATED状态。而一个线程的状态从RUNNABLE状态转换为BLOCKED、WAITING和TIMED_WAITING这几个状态中的任何一个状态都意味着上下文切换（Context Switch）的产生。
