package io.woolford;

public class GradeRecord {

    private String _id;
    private int courseID;
    private String courseName;
    private int sectionID;
    private int taskID;
    private String taskName;
    private String progressScore;
    private Double progressPercent;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getSectionID() {
        return sectionID;
    }

    public void setSectionID(int sectionID) {
        this.sectionID = sectionID;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getProgressScore() {
        return progressScore;
    }

    public void setProgressScore(String progressScore) {
        this.progressScore = progressScore;
    }

    public Double getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(Double progressPercent) {
        this.progressPercent = progressPercent;
    }

    @Override
    public String toString() {
        return "GradeRecord{" +
                "_id='" + _id + '\'' +
                ", courseID=" + courseID +
                ", courseName='" + courseName + '\'' +
                ", sectionID=" + sectionID +
                ", taskID=" + taskID +
                ", taskName='" + taskName + '\'' +
                ", progressScore='" + progressScore + '\'' +
                ", progressPercent=" + progressPercent +
                '}';
    }

}
