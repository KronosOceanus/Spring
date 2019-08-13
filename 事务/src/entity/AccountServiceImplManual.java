package entity;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class AccountServiceImplManual implements AccountService {

    private AccountDao accountDao;
    public void setAccountDao(AccountDao accountDao){
        this.accountDao = accountDao;
    }

    //事务模板实现事务
    private TransactionTemplate transactionTemplate;
    public void setTransactionTemplate(TransactionTemplate transactionTemplate){
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    //包含事务的目标方法（或者直接加在类上）       两个默认值
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public void transfer(String outer, String inner, Integer money) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                accountDao.out(outer, money);
                int i=1/0;
                accountDao.in(inner, money);
            }
        });
    }
}