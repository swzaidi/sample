package student2

class Course {

    String name

    static hasMany = [studentCourse: StudentCourse]


    static constraints = {
    }
}