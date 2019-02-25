package student;

import grails.databinding.SimpleDataBinder;
import grails.databinding.SimpleMapDataBindingSource
import grails.web.databinding.GrailsWebDataBinder
import org.grails.databinding.bindingsource.DataBindingSourceCreator
import org.grails.web.databinding.bindingsource.AbstractRequestBodyDataBindingSourceCreator
import org.grails.web.databinding.bindingsource.DefaultDataBindingSourceCreator
import spock.lang.Shared;
import spock.lang.Specification;

import java.util.Map;
import java.util.stream.Collectors;
import grails.databinding.CollectionDataBindingSource
import grails.databinding.DataBindingSource
import grails.databinding.SimpleMapDataBindingSource
import grails.web.http.HttpHeaders
import grails.web.mime.MimeType
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.GrailsApplicationAttributes
import org.springframework.http.HttpMethod
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.mock.web.MockServletContext
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import javax.servlet.ServletContext

//
// HTTP request body: student.id=1&courses[10].course.id=10
//
public class DataBindingSpec extends Specification {

    @Shared
    DefaultDataBindingSourceCreator bindingSourceCreator

    @Shared
    ServletContext servletContext = new MockServletContext()

    void setupSpec() {
        bindingSourceCreator = new DefaultDataBindingSourceCreator();
    }

    def setup() {
    }

    def cleanup() {
    }

    //
    void 'databinding from request parameters'() {
        given:
        // request with simple formdata: student.id=1&courses[10].course.id=10
        MockHttpServletRequest request = buildMockRequestWithParams('POST',['student.id':'1','courses[10].course.id':'10']);
        DataBindingSource source = bindingSourceCreator.createDataBindingSource(null,null,request);

        // databinder & command object
        def binder = new SimpleDataBinder()
        def obj = new StudentEnrollmentCmd()

        when:
        binder.bind(obj,source)

        then:
        // this should not throw an exception, but throws an exception
        MissingPropertyException ex = thrown()
        System.out.println ( "Exception thrown:" + ex.message );

        // the following should work, but does not work
        obj.student.id == 1
        obj.courses['10'].course.id == 10
    }

    void 'test data binding with course object properties'() {
        // the following works,
        // but the params being constructed from HTTP form data "student.id=1&courses[10].course.id=10"
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

    void 'test data binding "course.id" in databinding map'() {
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
        //System.out.println ( ex.message );

        // the following tests should work
        obj.student.id == 1
        obj.courses['10'].course.id == 10
    }

    MockHttpServletRequest buildMockRequestWithParams(String method, Map params) {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.request(method, new URI(""))
        params.each {k,v-> builder.param(k,v); }

        MockHttpServletRequest request = builder.buildRequest(servletContext)
        GrailsWebRequest grailsWebRequest = new GrailsWebRequest(request, new MockHttpServletResponse(), servletContext);

        request.setAttribute(GrailsApplicationAttributes.WEB_REQUEST, grailsWebRequest)
        request
    }

}

class StudentEnrollmentCmd {
    MyStudent student
    Map<String,CourseCmd> courses;
}

class MyStudent {
    Long id;
}

class CourseCmd {
    CourseDomain course
}

class CourseDomain {
    Long id
}
