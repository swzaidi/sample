package student;

import grails.databinding.SimpleDataBinder;
import grails.databinding.SimpleMapDataBindingSource;
import spock.lang.Specification;

import java.util.Map;
import java.util.stream.Collectors;

public class DataBindingSpec extends Specification {

    // HTTP request body: student.id=1&courses[1].course.id=1

    def setup() {
    }

    def cleanup() {
    }


    void 'test data binding with course object properties'() {
        // the following works,
        // but the params being constructed from HTTP form data "student.id=1&courses[1].course.id=1"
        // has additional properties that is causing it to fail, illustrated in the next test
        given:
        def binder = new SimpleDataBinder()
        def obj = new StudentEnrollmentCmd()
        def source = new SimpleMapDataBindingSource([ 'student':[id:1], 'courses[10]':[course:[id:10]] ] )

        when:
        binder.bind obj, source

        then:
        obj.student.id == 1
        obj.courses['10'].course.id == 10
    }

    void 'test data binding "course.id"'() {
        // the following binding throws an exception, because of 'course.id'
        // we are sending just "student.id=1&courses[1].course.id=1" in the HTTP formdata

        // params as seen in controller:
        // [student.id:1, student:[id:1], courses[10].course.id:10, courses[10]:[course.id:10, course:[id:10]], controller:enrollment, format:null, action:save]
        //                                                                       ^^^^^^^^^ this is causing the problem
        // if you see the exception it says No such property "course.id" in CourseCmd
        given:
        def binder = new SimpleDataBinder()
        def obj = new StudentEnrollmentCmd()
        def source = new SimpleMapDataBindingSource(
                ['student':[id:1],
                'courses[10]':['course.id':10, course:[id:10]] ] )
        //                     ^^^^^^^^^^ that is causing the problem

        when:
        binder.bind obj, source

        then:
        // It throws the following exception, it doesn't like the 'course.id' property which seems to exist in the params object
        //                                                         ^^^^^^^^^
        // Exception:
        //    No such property: course.id for class: student.CourseCmd
        //    Possible solutions: course
        MissingPropertyException ex = thrown()
        System.out.println ( ex.message );
        //obj.student.id == 1
        //obj.courses['10'].course.id == 10
    }

}

class StudentEnrollmentCmd {
    Student student
    Map<String,CourseCmd> courses;

    public String toString() {
        "student="+student.toString()+",courses="+courses.values()*.toString().stream().collect(Collectors.joining(","))
    }
}

class CourseCmd {
    CourseDomain course

    public String toString() {
        "course=[id:${course.id}]"
    }
}

class CourseDomain {
    Long id
}

class Store {
    Map mapOfStuff
}
