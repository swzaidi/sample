package student2

import org.apache.commons.collections.FactoryUtils
import org.apache.commons.collections.MapUtils

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
    Map<String, CourseCommand> courses = MapUtils.lazyMap(
            new HashMap<String, CourseCommand>(),
            FactoryUtils.instantiateFactory( CourseCommand ) )



}

class CourseCommand {
    Course course
}

