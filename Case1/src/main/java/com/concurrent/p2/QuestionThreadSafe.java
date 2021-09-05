package com.concurrent.p2;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j(topic = "c.QuestionThreadSafe")
public class QuestionThreadSafe {
    /**
     * 例1：
     */
    class MyServlet1 extends HttpServlet {

        //HashMap不是线程安全，HashTable是
        Map<String, Object> map = new HashMap<>();

        //String 不可变类，安全
        String s1 = "...";

        //安全
        final String s2 = "...";

        //不安全
        Date d1 = new Date();

        //不安全，final指d2引用值是固定，但是new Data()中的其他属性是可变的
        final Date d2 = new Date();

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {

        }
    }

    /**
     * 例2：
     */
    class MyServlet2 extends HttpServlet {

        //不安全，多线程可以访问该对象并且修改实例变量
        private UserService userService = new UserServiceImpl();

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
            userService.update();
        }
    }

    interface UserService {
        void update();
    }

    class UserServiceImpl implements UserService {
        //共享资源
        //记录调用次数
        private int count = 0;

        public void update() {
            //...
            count++;    //临界区
        }
    }

    /**
     * 例3：
     */
/*    @Aspect
    @Component
    class MyAspect {
        //默认是单例，成员变量都是可以共享的，不安全
        //可以使用环绕通知解决
        private long start = 0L;

        @Befor("execution(* *(..))")
        public void before() {
            start = System.nanoTime();
        }

        @After("execution(* *(..))")
        public void after() {
            long end = System.nanoTime();
            System.out.println("const time:" + (end - start));
        }
    }*/

    /**
     * 例4：
     */
    interface UserDao4 {
        void update();
    }

    //安全的，没有成员变量
    class UserDaoImpl4 implements UserDao4 {
        //update方法也是安全
        public void update() {
            String sql = "Update user set password = ? where username = ?";
            try {
                Connection conn = DriverManager.getConnection("", "", "");
                //....
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    interface UserService4 {
        void update();
    }

    //是安全的，虽然有成员变量，但是成员变量不会变，因此是安全的
    class UserServiceImpl4 implements UserService4 {
        private UserDao4 userDao4 = new UserDaoImpl4();

        @Override
        public void update() {
            userDao4.update();
        }
    }

    //是安全的，成员变量不会变化，是安全的
    class MyServlet4 extends HttpServlet {
        private UserService4 userService4 = new UserServiceImpl4();

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
            userService4.update();
        }
    }

    /**
     * 例5
     */
    interface UserDao5 {
        void update();
    }

    //不安全
    class UserDaoImpl5 implements UserDao5 {

        //把conn没有做成方法内的局部变量，做成了成员变量
        //是被多线程共享的，不安全！
        private Connection conn = null;

        public void update() {
            String sql = "Update user set password = ? where username = ?";
            try {
                conn = DriverManager.getConnection("", "", "");
                //....
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }


    /**
     * 例6：
     */


    interface UserDao6 {
        void update();
    }

    class UserDaoImpl6 implements UserDao6 {

        private Connection conn = null;

        public void update() {
            String sql = "Update user set password = ? where username = ?";
            try {
                conn = DriverManager.getConnection("", "", "");
                //....
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    interface UserService6 {
        void update();
    }

    class UserServiceImpl6 implements UserService6 {
        //安全，不推荐
        @Override
        public void update() {
            UserDao6 userDao6 = new UserDaoImpl6();
            userDao6.update();
        }
    }

    /**
     * 例7
     */
    static abstract class T {
        //foo的行为不是确定的，可能导致不安全的发生，称为外星方法
        //要遵虚开闭原则（可以参考String的实现：class final String{}）
        public abstract void foo(SimpleDateFormat sdf);

        public void bar() {
            //SimpleDateFormat不是安全的
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            foo(sdf);
        }
    }

}
