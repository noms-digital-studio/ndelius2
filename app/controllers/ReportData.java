package controllers;

import play.data.validation.Constraints;

public class ReportData {

    @Constraints.Required
    private String name;

    @Constraints.Required
    private Integer age;

    public ReportData() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return String.format("ReportData(%s, %s)", name, age);
    }

}
