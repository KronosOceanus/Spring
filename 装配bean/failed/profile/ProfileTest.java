package profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

//指定使用哪个资源
@ActiveProfiles("dev")
public class ProfileTest {

    @Autowired
    private static ProfileDataSource dataSource;

    public static void main(String[] args){
        System.out.println(dataSource.getClass().getName());
    }
}
