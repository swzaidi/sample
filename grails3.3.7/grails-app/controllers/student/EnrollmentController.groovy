package student

class EnrollmentController {

    def index() {

        Student s = Student.get(1000)
        Course[] courses = Course.findAll()

        def model = [student:s, courses:courses]

        render view: '/enrollment/index', model: model


    }


def save(EnrollmentCommand cmd) {

    println "+++++++ cmd has errors" + cmd.hasErrors()
    println "+++++++ cmd errors " + cmd.errors

}

}
class EnrollmentCommand {

    Student student
    Map<String, CourseCommand> courses = new HashMap<String, CourseCommand>()

}

class CourseCommand {
    Course course
}
