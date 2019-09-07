package validator;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.util.Date;

public class Transaction {

    @NotNull
    private int productId;

    @NotNull
    private int userId;

    @Future //只能是将来的日期
    @DateTimeFormat(pattern = "yyyy-MM-dd") //日期格式
    @NotNull
    private Date date;

    @NotNull
    @DecimalMin(value = "0.1") //最小值 0.1 元
    private Double price;

    @Min(1)
    @Max(100)
    @NotNull
    private Integer quantity;

    @NotNull
    @DecimalMax("50000.00") //最大金额
    @DecimalMin("1.00")
    private Double amount;

    @Pattern(regexp = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@" +
            "([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][a-zA-Z]{2,3}" +
            "([\\.][A-Za-z]{2})?$",
            message = "不符合邮件格式")
    private String email;

    @Size(min = 0, max = 256) // 0-256 个字符
    private String note;






    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
