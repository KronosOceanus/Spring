package exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//定义异常（对应异常状态码）
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "找不到角色，异常！")
public class RoleException extends RuntimeException {

    private static final long serialVersionUID = 1554516546161184L;
}
