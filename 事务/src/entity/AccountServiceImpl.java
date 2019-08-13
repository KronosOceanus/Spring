package entity;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

public class AccountServiceImpl implements AccountService {

    private AccountDao accountDao;
    public void setAccountDao(AccountDao accountDao){
        this.accountDao = accountDao;
    }


    @Override
    //包含事务的目标方法（或者直接加在类上）       两个默认值
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public void transfer(String outer, String inner, Integer money) {
        accountDao.out(outer, money);
        int i=1/0;
        accountDao.in(inner, money);
    }
}
