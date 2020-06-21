package java8实战.example.chapter2;

/**
 * 《Java8实战》2.3.2 测验2.2匿名类谜题
 */
public class X_20200617_java8实战2_2匿名类谜题 {
    public final int value = 4;
    public void doIt(){
        int value = 6;
        Runnable r = new Runnable() {
            public final int value = 5;
            public void run() {
                int value = 10;
                System.out.println(this.value); // 5 因为this指的是包含它的Runnable，而不是外面的类
            }
        };
        r.run();
    }

    public static void main(String[] args) {
        X_20200617_java8实战2_2匿名类谜题 m = new X_20200617_java8实战2_2匿名类谜题();
        m.doIt();
    }
}
