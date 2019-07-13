package dao_service;

public class UserServiceImpl implements UserService {

    private UserDao userDao;

    @Override
    public void addUser() {
        userDao.addUser();
    }

    //初始化和销毁（如果需要代理执行，接口中也要有对应方法）
    public void init(){
        System.out.println("init");
    }
    public void destory(){
        System.out.println("destory");
    }
















    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}
