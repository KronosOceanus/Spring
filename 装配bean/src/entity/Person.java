package entity;

public class Person {

    private String name;
    private Integer age;

    private Address homeAddr;
    private Address companyAddr;

    //部分参数构造器
    private Person(String name, Address homeAddr){
        this.name = name;
        this.homeAddr = homeAddr;
    }

    @Override
    public String toString() {
        return "Person[name = " + name + ", age = " + age + "," +
                " homeAddr = " + homeAddr + ", companyAddr = " + companyAddr + "]" ;
    }

    public Person() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Address getHomeAddr() {
        return homeAddr;
    }

    public void setHomeAddr(Address homeAddr) {
        this.homeAddr = homeAddr;
    }

    public Address getCompanyAddr() {
        return companyAddr;
    }

    public void setCompanyAddr(Address companyAddr) {
        this.companyAddr = companyAddr;
    }
}
