# Java并发-不可变

## 1.可变类安全问题

### 1.1 日期转换问题

可变类如果不加线程安全保护，会有线程安全问题。

SimpleDateFormat 可变类会造成线程安全问题。

DateTimeFormatter 不会造成线程安全问题。

```java
package com.concurrent.p8;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

@Slf4j(topic = "c.Test_SimpleDateFormat")
public class Test_SimpleDateFormat {

    /**
     * SimpleDateFormat可变类会造成线程安全问题
     */
    @Test
    public void test1() throws InterruptedException {
        //定义共享可变类对象
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //10个线程访问共享变量
        for (int i = 0; i < 10; i++) {
            //出异常 java.lang.NumberFormatException: multiple points
            new Thread(() -> {
                try {
                    log.debug(sdf.parse("2021-09-20") + "");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        Thread.sleep(1000);
    }

    /**
     * synchronized上锁，可以解决问题，但是效率低
     */
    @Test
    public void test2() throws InterruptedException {
        //定义共享可变类对象
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //10个线程访问共享变量
        for (int i = 0; i < 10; i++) {
            //出异常
            new Thread(() -> {
                //添加synchronized后可以正常运行
                synchronized (sdf) {
                    try {
                        log.debug(sdf.parse("2021-09-20") + "");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        Thread.sleep(1000);
    }

    /**
     * 使用DateTimeFormatter不可变类
     */
    @Test
    public void test3() throws InterruptedException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //10个线程访问共享变量
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                TemporalAccessor parse = dtf.parse("2021-09-20");
                log.debug(parse + "");
            }).start();
        }
        Thread.sleep(1000);
    }

}
```

有很大几率出现 java.lang.NumberFormatException 或者出现不正确的日期解析结果,例如:

```
19:10:40.859 [Thread-2] c.TestDateParse - {}
java.lang.NumberFormatException: For input string: ""
```



### 1.2 解决方法

#### (1) 同步锁synchronized

使用synchronized可以解决,但是synchronized是重量级锁,效率低.

```java
 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < 50; i++) {
            new Thread(() -> {
                synchronized (sdf) {  //加锁
                    try {
                        log.debug("{}", sdf.parse("1951-04-21"));
                    } catch (Exception e) {
                        log.error("{}", e);
                    }
                }
            }).start();
        }
```



#### (2)使用不可变类

如果一个对象在不能够修改其内部状态（属性），那么它就是线程安全的，因为不存在并发修改啊！这样的对象在
Java 中有很多，例如在 Java 8 后，提供了一个新的日期格式化类：  

```java
//使用不可变类DateTimeFormatter
DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                LocalDate date = dtf.parse("2018-10-01", LocalDate::from);
                log.debug("{}", date);
            }).start();
        }
```

不可变对象，实际是另一种避免竞争的方式。

  

## 2.不可变设计

String类是不可变的，用final修饰，不能被继承。

```java
public final class String
        implements java.io.Serializable, Comparable<String>, CharSequence {
    /**
     * The value is used for character storage.
     */
    private final char value[];
    /**
     * Cache the hash code for the string
     */
    private int hash; // Default to 0
// ...
}
```

### 2.1 final 的使用:
发现该类、类中所有属性都是 final 的

- 属性用 final 修饰保证了该属性是只读的，不能修改

- 类用 final 修饰保证了该类中的方法不能被覆盖，防止子类无意间破坏不可变性  



### 2.2 保护性拷贝

substtring方法采用了保护性拷贝,

```java
    public String substring(int beginIndex) {
        if (beginIndex < 0) {
            throw new StringIndexOutOfBoundsException(beginIndex);
        }
        int subLen = value.length - beginIndex;
        if (subLen < 0) {
            throw new StringIndexOutOfBoundsException(subLen);
        }
        return (beginIndex == 0) ? this : new String(value, beginIndex, subLen);
    }
```

发现其内部是调用 String 的构造方法创建了一个新字符串，再进入这个构造看看，是否对 final char[] value 做出
了修改：  

```java
    public String(char value[], int offset, int count) {
        if (offset < 0) {
            throw new StringIndexOutOfBoundsException(offset);
        }
        if (count <= 0) {
            if (count < 0) {
                throw new StringIndexOutOfBoundsException(count);
            }
            if (offset <= value.length) {
                this.value = "".value;
                return;
            }
        }
        if (offset > value.length - count) {
            throw new StringIndexOutOfBoundsException(offset + count);
        }
        this.value = Arrays.copyOfRange(value, offset, offset+count);
    }
```

结果发现也没有，构造新字符串对象时，会生成新的 char[] value，对内容进行复制 。这种通过创建副本对象来避
免共享的手段称之为【保护性拷贝（defensive copy）】  



## 3.共享模式-享元模式

重用对象，减少对内存的使用。

### 3.1.体现

#### 包装类

在JDK中 Boolean，Byte，Short，Integer，Long，Character 等包装类提供了 valueOf 方法，例如 Long 的valueOf 会缓存 -128~127 之间的 Long 对象，在这个范围之间会重用对象，大于这个范围，才会新建 Long 对象。

![image-20210924154813917](C:\Users\tom\AppData\Roaming\Typora\typora-user-images\image-20210924154813917.png)

注意：

Byte, Short, Long 缓存的范围都是 -128~127

Character 缓存的范围是 0~127

Integer的默认范围是 -128~127

最小值不能变

但最大值可以通过调整虚拟机参数 `-Djava.lang.Integer.IntegerCache.high` 来改变

Boolean 缓存了 TRUE 和 FALSE



#### String串池



#### BigDecimal  ,  BigInteger

虽然本身是安全的，但是这些安全方法组合在一起就可能是不安全的。



### 3.2 享元模式应用-数据库连接池

例如：一个线上商城应用，QPS 达到数千，如果每次都重新创建和关闭数据库连接，性能会受到极大影响。 这时预先创建好一批连接，放入连接池。一次请求到达后，从连接池获取连接，使用完毕后再还回连接池，这样既节约了连接的创建和关闭时间，也实现了连接的重用，不至于让庞大的连接数压垮数据库。

```java
package com.concurrent.p8;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicIntegerArray;

@Slf4j(topic = "c.Test_DBConnPool")
public class Test_DBConnPool {

    @Test
    public void test_ConnPool() {
        //创建连接池对象
        ConnPool pool = new ConnPool(2);
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                Connection connection = pool.borrow();
                //模拟等待时间
                try {
                    Thread.sleep(new Random().nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //归还连接
                pool.free(connection);
            }).start();
        }
        while (true) ;
    }
}

/**
 * 自定义数据库连接池
 */
@Slf4j(topic = "c.ConnPool")
class ConnPool {
    //1:数据库连接池大小
    private int poolSize;
    //2:数据库连接池的状态
    private AtomicIntegerArray status;
    //3:数据库连接池对象
    private Connection[] conn;

    //4:构造方法
    public ConnPool(int poolSize) {
        this.poolSize = poolSize;
        this.status = new AtomicIntegerArray(poolSize);
        this.conn = new Connection[poolSize];
        for (int i = 0; i < poolSize; i++) {
            this.conn[i] = new MyConnection("conn-" + i );

        }
    }

    //5:借连接
    public Connection borrow() {
        while (true) {
            for (int i = 0; i < poolSize; i++) {
                if (status.get(i) == 0) {
                    //cas变更状态
                    if (status.compareAndSet(i, 0, 1)) {
                        log.debug("get conn...");
                        return conn[i];
                    }
                }
            }
            //如果遍历1次后没有空闲的连接，则进入等待
            synchronized (this) {
                try {
                    log.debug("wait...");
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //6:还连接
    public void free(Connection conn) {
        //归还连接时要检查该连接是否属于连接池
        for (int i = 0; i < poolSize; i++) {
            if (this.conn[i] == conn) {
                this.status.set(i, 0);
                //唤醒等待线程
                synchronized (this) {
                    log.debug("free {}", conn);
                    this.notifyAll();
                }
                break;
            }
        }

    }
}

class MyConnection implements Connection {

    private String name;

    public MyConnection(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
 .......
```

>  输出

```

08:27:36.694 [Thread-1] DEBUG c.ConnPool - get conn...
08:27:36.694 [Thread-0] DEBUG c.ConnPool - get conn...
08:27:36.694 [Thread-2] DEBUG c.ConnPool - wait...
08:27:36.698 [Thread-4] DEBUG c.ConnPool - wait...
08:27:36.698 [Thread-3] DEBUG c.ConnPool - wait...
08:27:36.899 [Thread-0] DEBUG c.ConnPool - free MyConnection{name='conn-0'}
08:27:36.907 [Thread-3] DEBUG c.ConnPool - get conn...
08:27:36.907 [Thread-4] DEBUG c.ConnPool - wait...
08:27:36.907 [Thread-2] DEBUG c.ConnPool - wait...
08:27:37.251 [Thread-1] DEBUG c.ConnPool - free MyConnection{name='conn-1'}
08:27:37.251 [Thread-2] DEBUG c.ConnPool - get conn...
08:27:37.251 [Thread-4] DEBUG c.ConnPool - wait...
08:27:37.327 [Thread-3] DEBUG c.ConnPool - free MyConnection{name='conn-0'}
08:27:37.327 [Thread-4] DEBUG c.ConnPool - get conn...
08:27:37.547 [Thread-2] DEBUG c.ConnPool - free MyConnection{name='conn-1'}
08:27:38.166 [Thread-4] DEBUG c.ConnPool - free MyConnection{name='conn-0'}
```

以上实现没有考虑：

- 连接的动态增长与收缩

- 连接保活（可用性检测）

- 等待超时处理

- 分布式 hash

对于关系型数据库，有比较成熟的连接池实现，例如c3p0, druid等 对于更通用的对象池，可以考虑使用apache commons pool，例如redis连接池可以参考jedis中关于连接池的实现。



## 4.final原理

### 4.1 设置final变量的原理

![image-20210924155437262](C:\Users\tom\AppData\Roaming\Typora\typora-user-images\image-20210924155437262.png)

![image-20210924155445226](C:\Users\tom\AppData\Roaming\Typora\typora-user-images\image-20210924155445226.png)

发现 final 变量的赋值也会通过 putfield 指令来完成，同样在这条指令之后也会加入写屏障，保证在其它线程读到

它的值时不会出现为 0 的情况。

### 4.2 获取final变量的原理

待写...



## 5.无状态

在 web 阶段学习时，设计 Servlet 时为了保证其线程安全，都会有这样的建议，不要为 Servlet 设置成员变量，这
种没有任何成员变量的类是线程安全的
因为成员变量保存的数据也可以称为状态信息，因此没有成员变量就称之为【无状态】  







