package com.gaop.huthelperdao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "USER".
 */
public class User implements java.io.Serializable {

    private Long id;
    private String user_id;
    private String rember_code;
    private String studentKH;
    private String TrueName;
    private String username;
    private String dep_name;
    private String class_name;
    private String address;
    private String active;
    private String last_login;
    private String login_cnt;
    private String sex;

    public User() {
    }

    public User(Long id) {
        this.id = id;
    }

    public User(Long id, String user_id, String rember_code, String studentKH, String TrueName, String username, String dep_name, String class_name, String address, String active, String last_login, String login_cnt, String sex) {
        this.id = id;
        this.user_id = user_id;
        this.rember_code = rember_code;
        this.studentKH = studentKH;
        this.TrueName = TrueName;
        this.username = username;
        this.dep_name = dep_name;
        this.class_name = class_name;
        this.address = address;
        this.active = active;
        this.last_login = last_login;
        this.login_cnt = login_cnt;
        this.sex = sex;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getRember_code() {
        return rember_code;
    }

    public void setRember_code(String rember_code) {
        this.rember_code = rember_code;
    }

    public String getStudentKH() {
        return studentKH;
    }

    public void setStudentKH(String studentKH) {
        this.studentKH = studentKH;
    }

    public String getTrueName() {
        return TrueName;
    }

    public void setTrueName(String TrueName) {
        this.TrueName = TrueName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDep_name() {
        return dep_name;
    }

    public void setDep_name(String dep_name) {
        this.dep_name = dep_name;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getLast_login() {
        return last_login;
    }

    public void setLast_login(String last_login) {
        this.last_login = last_login;
    }

    public String getLogin_cnt() {
        return login_cnt;
    }

    public void setLogin_cnt(String login_cnt) {
        this.login_cnt = login_cnt;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

}
