package annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

@Controller("studentAction")
public class StudentAction {

    @Value("蔡帅")
    private String name;

    //直接在构造器使用注解注入
    private final User user;
    private final StudentService studentService;


    //Ioc 容器自动寻找 bean，注解直接写在参数类型前
    @Autowired
    public StudentAction(StudentService studentService, @Qualifier("user") User user) {
        this.studentService = studentService;
        this.user = user;
    }

    public void execute(){
        System.out.println("execute!");
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public StudentService getStudentService() {
        return studentService;
    }
}
