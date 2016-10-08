package cn.spinsoft.wdq.org.biz;

/**
 * Created by hushujun on 16/1/8.
 */
public class CourseBean {
    private int state;
    private int courseId;
    private String courseName;
    private int orgId;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }
}
