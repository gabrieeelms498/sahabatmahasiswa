package com.example.sahabatmahasiswa;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class GoalModel implements Serializable {
    private String capaian;
    private String userId;
    private String goalsId;
    private List<Map<String, Object>> todoList;

    public String getCapaian() {
        return capaian;
    }

    public void setCapaian(String capaian) {
        this.capaian = capaian;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGoalsId() {
        return goalsId;
    }

    public void setGoalsId(String goalsId) {
        this.goalsId = goalsId;
    }

    public List<Map<String, Object>> getTodoList() {
        return todoList;
    }

    public void setTodoList(List<Map<String, Object>> todoList) {
        this.todoList = todoList;
    }
}

