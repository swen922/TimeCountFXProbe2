package com.horovod.timecountfxprobe.project;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Objects;

@JsonAutoDetect
@XmlRootElement(name = "worktimeinstance")
public class WorkTime {

    @JsonDeserialize(as = Integer.class)
    private int projectID;

    @JsonDeserialize(as = String.class)
    private String dateString;

    @JsonDeserialize(as = Integer.class)
    private int designerID;

    @JsonDeserialize(as = Integer.class)
    private int year;

    @JsonDeserialize(as = Integer.class)
    private int month;

    @JsonDeserialize(as = Integer.class)
    private int weekNumber;

    @JsonIgnore
    private LocalDate date;

    /**
     Время храним умноженное на 10, чтобы обойтись без double или BigDecimal,
     а при выводе на экран делим на 10 и выдаем double
     */
    @JsonDeserialize(as = Integer.class)
    private volatile int time = 0;

    public WorkTime(int newProjectID, String dateString, int ID, double time) {
        this.projectID = newProjectID;
        this.dateString = dateString;
        this.date = AllData.parseDate(dateString);
        this.year = date.getYear();
        this.month = date.getMonth().getValue();
        this.weekNumber = date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
        this.designerID = ID;
        setTime(AllData.doubleToInt(time));
    }

    public WorkTime() {
    }

    @XmlElement(name = "projectidnumber")
    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    @XmlElement(name = "datestring")
    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    @XmlTransient
    public LocalDate getDate() {
        return AllData.parseDate(this.dateString);
    }

    @XmlElement(name = "designerid")
    public int getDesignerID() {
        return designerID;
    }

    public void setDesignerID(int designerID) {
        this.designerID = designerID;
    }

    @XmlElement(name = "worktimeyear")
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @XmlElement(name = "worktimemonth")
    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    @XmlElement(name = "worktimeweek")
    public int getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    @XmlElement(name = "time")
    public int getTime() {
        return this.time;
    }

    @XmlTransient
    public double getTimeDouble() {
        return AllData.intToDouble(time);
    }

    public void setTime(int newTime) {
        this.time = newTime >= 0 ? newTime : 0;
    }

    public void setTimeDouble(double newTimeDouble) {
        if (newTimeDouble == 0) {
            setTime(0);
        }
        else {
            setTime(AllData.doubleToInt(newTimeDouble));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkTime workTime = (WorkTime) o;
        return projectID == workTime.projectID &&
                designerID == workTime.designerID &&
                time == workTime.time &&
                Objects.equals(dateString, workTime.dateString);
    }

    @Override
    public int hashCode() {

        return Objects.hash(projectID, dateString, designerID, time);
    }

    @Override
    public String toString() {
        return "WorkTime{" +
                "projectID=" + projectID +
                ", dateString='" + dateString + '\'' +
                ", designerID=" + designerID +
                ", time=" + time +
                '}';
    }

}
