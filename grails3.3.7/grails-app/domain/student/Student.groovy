package student

class Student {

    String name
    int age

    static hasMany = [studentCourse: StudentCourse]


    static constraints = {
    }
}
