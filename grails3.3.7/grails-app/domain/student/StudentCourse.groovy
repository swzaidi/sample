package student

class StudentCourse {


    static belongsTo = [student: Student, course: Course]

    static constraints = {
    }
}
