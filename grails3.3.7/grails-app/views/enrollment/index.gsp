<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Welcome to Grails</title>
</head>
<body>

<h1>Student Enrollment</h1>

<b>Student Name:</b> <pre>${student.name}</pre>

<b>Available Coures...</b>

<form action ="/enrollment/save" method="POST">

<input type="hidden" name="student.id" value="${student.id}">
    <table>
<g:each var="course" in="${courses}">
        <tr>
          <td>
              <input type="checkbox" name="courses[${course.id}].course.id" value="${course.id}"> </td>
        <td>
            Name  :


          </td>

            <td>
                ${course.name}
</td>
 </tr>

</g:each>



    </table>


    <input type="submit">





</form>

</body>
</html>
