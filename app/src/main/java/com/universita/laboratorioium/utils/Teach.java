package com.universita.laboratorioium.utils;

import java.io.Serializable;

public class Teach implements Serializable {
    Teacher teacher;
    Course course;

    public Teach(Teacher teacher, Course course) {
        this.teacher = teacher;
        this.course = course;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public Course getCourse() {
        return course;
    }

    @Override
    public String toString() {
        return "Teach{" +
                "teacher=" + teacher +
                ", course=" + course +
                '}';
    }
}
