package op.warehouse.backend.entity;

import cn.hutool.core.date.DateTime;

import java.time.LocalDateTime;


public interface User {
    public Long getId();
    public void setId(Long id);

    public String getUsername();
    public void setUsername(String username);


    public String getEmail();
    public void setEmail(String email);


    public String getPhoneNumber();
    public void setPhoneNumber(String phoneNumber);

    public LocalDateTime getRegistrationDate();
    public void setRegistrationDate(LocalDateTime dateTime);


    public String getPassword();
    public void setPassword(String password);

    public RoleType getRoleType();

}

