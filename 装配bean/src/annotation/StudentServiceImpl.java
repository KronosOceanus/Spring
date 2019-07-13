package annotation;

import org.springframework.stereotype.Service;

//注意 Service 注解
@Service
public class StudentServiceImpl implements StudentService{

    @Override
    public void addStudent(){
        System.out.println("add student!");
    }

}
