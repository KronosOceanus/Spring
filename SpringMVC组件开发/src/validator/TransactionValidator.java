package validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class TransactionValidator implements Validator {

    //如果表单与 Transaction 类匹配，则校验
    @Override
    public boolean supports(Class<?> aClass) {
        return Transaction.class.equals(aClass);
    }

    //具体校验逻辑
    @Override
    public void validate(Object o, Errors errors) {
        //先获取 bean 对象，再根据内容校验
        Transaction trans = (Transaction) o;
        double dis = trans.getAmount() - (trans.getPrice() * trans.getQuantity());
        if (Math.abs(dis) > 0.01){
            //加入错误信息
            errors.rejectValue("amount", null, "交易金额和购买数量与价格不匹配！");
        }
    }
}
