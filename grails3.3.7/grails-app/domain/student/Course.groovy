package student

class Course {

    String name

    static hasMany = [studentCourse: StudentCourse]


    static constraints = {
    }
}
