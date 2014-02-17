package es.showcase.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author: yearsaaaa
 */
public class Employee {

    private String id;
    private String name;
    private Sex sex;
    private Integer age;
    private float salary;
    private Date birthday;
    private String descript;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public static enum Sex{
        MAN,
        WOMAN,
        WO_MAN,
        SOFT_GIRL,
        UNKNOW_404;
    }

    public static List<Employee> CreateFakerEmp() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        List<Employee> employees = new ArrayList<Employee>();

        Employee emp1 = new Employee();
        emp1.setId("HP-"+ UUID.randomUUID().toString());
        emp1.setName("Jack");
        emp1.setSex(Sex.MAN);
        emp1.setAge(30);
        emp1.setSalary(12000);
        emp1.setBirthday(formatter.parse("1985-01-01"));
        emp1.setDescript("2014的第一场雪,比以往来的更晚一些");
        employees.add(emp1);

        Employee emp2 = new Employee();
        emp2.setId("HP-"+UUID.randomUUID().toString());
        emp2.setName("Lily");
        emp2.setSex(Sex.WOMAN);
        emp2.setAge(23);
        emp2.setSalary(6500);
        emp2.setBirthday(formatter.parse("1992-02-12"));
        emp2.setDescript("mom don't worry my study,so easy!");
        employees.add(emp2);

        Employee emp3 = new Employee();
        emp3.setId("HP-"+UUID.randomUUID().toString());
        emp3.setName("阮玫梓");
        emp3.setSex(Sex.SOFT_GIRL);
        emp3.setAge(26);
        emp3.setSalary(8000);
        emp3.setBirthday(formatter.parse("1989-10-29"));
        emp3.setDescript("我是软妹子,软妹子你好,软妹子再见...");
        employees.add(emp3);

        Employee emp4 = new Employee();
        emp4.setId("HP-"+UUID.randomUUID().toString());
        emp4.setName("吕涵紫");
        emp4.setSex(Sex.WO_MAN);
        emp4.setAge(25);
        emp4.setSalary(7500);
        emp4.setBirthday(formatter.parse("1990-05-18"));
        emp4.setDescript("你的就是我的,我的还是我的,我是女汉子,我为自己代言");
        employees.add(emp4);

        Employee emp5 = new Employee();
        emp5.setId("HP-"+UUID.randomUUID().toString());
        emp5.setName("Unknown");
        emp5.setSex(Sex.UNKNOW_404);
        emp5.setAge(25);
        emp5.setSalary(7500);
        emp5.setBirthday(formatter.parse("1990-08-02"));
        emp5.setDescript("时而superman,时而小清新,时而女汉子,时而抠脚大汉,我真......的不知道ta是谁，泪奔~");
        employees.add(emp5);

        return employees;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", sex=" + sex +
                ", age=" + age +
                ", salary=" + salary +
                ", birthday=" + birthday +
                ", descript='" + descript + '\'' +
                '}';
    }
}
