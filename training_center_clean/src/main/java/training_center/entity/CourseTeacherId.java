package training_center.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class CourseTeacherId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "teacher_id")
    private Long teacherId;

    public CourseTeacherId() {
    }

    public CourseTeacherId(Long courseId, Long teacherId) {
        this.courseId = courseId;
        this.teacherId = teacherId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CourseTeacherId)) return false;
        CourseTeacherId that = (CourseTeacherId) o;
        return Objects.equals(courseId, that.courseId) &&
               Objects.equals(teacherId, that.teacherId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, teacherId);
    }
}
