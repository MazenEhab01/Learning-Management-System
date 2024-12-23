package org.software.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.software.lms.model.Lesson;
import org.software.lms.model.User;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {
    private Long id;
    private String title;
    private String description;
    private Integer duration;
    private List<User> instructors ;
    private List<Lesson> lessons;
    private Set<User> studentEnrolledCourses;
    private Date createdAt;
    private Date updatedAt;
}
