package student2

class StudentCourse {


    static belongsTo = [student: Student, course: Course]

    static constraints = {
    }
}
