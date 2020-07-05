package mybatis_generator_test.pojo;

import java.math.BigDecimal;

public class UserEntity {
    private Integer id;

    private String name;

    private String sex;

    private String description;

    private BigDecimal money;

    public UserEntity(Integer id, String name, String sex, String description, BigDecimal money) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.description = description;
        this.money = money;
    }

    public UserEntity() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex == null ? null : sex.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }
}