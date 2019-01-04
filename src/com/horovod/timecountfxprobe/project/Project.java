package com.horovod.timecountfxprobe.project;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.time.LocalDate;
import java.util.*;

@XmlRootElement(name = "project")
public class Project {

    private int idNumber;
    //private StringProperty idNumberProperty;
    private IntegerProperty idNumberProperty;
    private String company;
    private StringProperty companyProperty;
    private String manager;
    private StringProperty managerProperty;
    private String description;
    private StringProperty descriptionProperty;
    private String dateCreationString;
    private volatile boolean isArchive = false;
    private String comment;
    private String folderPath;
    private String linkedProjects;
    private String PONumber;
    private StringProperty PONumberProperty;
    private volatile int workSum = 0;
    private volatile StringProperty workSumProperty;
    private int budget = 0;
    private StringProperty budgetProperty;
    private List<WorkTime> work = new ArrayList<>();


    public Project(String comp, String manager, String description) {
        this.idNumber = AllData.incrementIdNumberAndGet();
        //this.idNumberProperty = new SimpleStringProperty(String.valueOf(idNumber));
        this.idNumberProperty = new SimpleIntegerProperty(idNumber);
        this.company = comp;
        this.companyProperty = new SimpleStringProperty(comp);
        this.manager = manager;
        this.managerProperty = new SimpleStringProperty(manager);
        this.description = description;
        this.descriptionProperty = new SimpleStringProperty(description);
        this.dateCreationString = AllData.formatDate(LocalDate.now());
        this.workSumProperty = new SimpleStringProperty(String.valueOf(AllData.intToDouble(workSum)));
        //this.workSumProperty = new SimpleDoubleProperty(AllData.intToDouble(workSum));
    }

    public Project(String comp, String manager, String description, LocalDate newDate) {
        this.idNumber = AllData.incrementIdNumberAndGet();
        //this.idNumberProperty = new SimpleStringProperty(String.valueOf(idNumber));
        this.idNumberProperty = new SimpleIntegerProperty(idNumber);
        this.company = comp;
        this.companyProperty = new SimpleStringProperty(comp);
        this.manager = manager;
        this.managerProperty = new SimpleStringProperty(manager);
        this.description = description;
        this.descriptionProperty = new SimpleStringProperty(description);
        this.dateCreationString = AllData.formatDate(newDate);
        this.workSumProperty = new SimpleStringProperty(String.valueOf(AllData.intToDouble(workSum)));
        //this.workSumProperty = new SimpleDoubleProperty(AllData.intToDouble(workSum));
    }

    public Project() {
        this.idNumber = 0;
        this.idNumberProperty = new SimpleIntegerProperty(idNumber);
        this.company = "";
        this.companyProperty = new SimpleStringProperty("");
        this.manager = "";
        this.managerProperty = new SimpleStringProperty("");
        this.description = "";
        this.descriptionProperty = new SimpleStringProperty("");
        this.dateCreationString = AllData.formatDate(LocalDate.now());
        this.workSumProperty = new SimpleStringProperty(String.valueOf(AllData.intToDouble(workSum)));
        //this.workSumProperty = new SimpleDoubleProperty(AllData.intToDouble(workSum));
    }

    @XmlElement(name = "projectidnumber")
    public int getIdNumber() {
        return idNumber;
    }

    public synchronized void setIdNumber(int newIdNumber) {
        this.idNumber = newIdNumber;
        this.idNumberProperty.set(newIdNumber);
    }

    @XmlTransient
    public int getIdNumberProperty() {
        return idNumberProperty.get();
    }

    @XmlTransient
    public IntegerProperty idNumberProperty() {
        return idNumberProperty;
    }

    public synchronized void setIdNumberProperty(int idNumberProperty) {
        this.idNumberProperty.set(idNumberProperty);
    }

    @XmlElement(name = "clientcompany")
    public String getCompany() {
        return company;
    }

    public synchronized void setCompany(String newCompany) {
        this.company = newCompany;
        this.companyProperty.set(newCompany);
    }

    @XmlTransient
    public String getCompanyProperty() {
        return companyProperty.get();
    }

    @XmlTransient
    public StringProperty companyProperty() {
        return companyProperty;
    }

    public synchronized void setCompanyProperty(String newCompanyProperty) {
        this.companyProperty.set(newCompanyProperty);
    }


    @XmlElement(name = "manager")
    public String getManager() {
        return manager;
    }

    public synchronized void setManager(String manager) {
        this.manager = manager;
        this.managerProperty.set(manager);
    }

    @XmlTransient
    public String getManagerProperty() {
        return managerProperty.get();
    }

    @XmlTransient
    public StringProperty managerProperty() {
        return managerProperty;
    }

    public synchronized void setManagerProperty(String manager) {
        this.managerProperty.set(manager);
    }


    @XmlElement(name = "descr")
    public String getDescription() {
        return description;
    }

    public synchronized void setDescription(String newDescription) {
        this.description = newDescription;
        this.descriptionProperty.set(newDescription);
    }

    @XmlTransient
    public String getDescriptionProperty() {
        return descriptionProperty.get();
    }

    @XmlTransient
    public StringProperty descriptionProperty() {
        return descriptionProperty;
    }

    public synchronized void setDescriptionProperty(String newDescriptionProperty) {
        this.descriptionProperty.set(newDescriptionProperty);
    }

    @XmlElement(name = "datecreationstring")
    public String getDateCreationString() {
        return dateCreationString;
    }

    public synchronized void setDateCreationString(String newDateCreationString) {
        this.dateCreationString = newDateCreationString;
    }

    @XmlElement(name = "isarchive")
    public boolean isArchive() {
        return isArchive;
    }

    /** Этот метод использовать ТОЛЬКО через метод
     * changeProjectArchiveStatus(int changedProject, boolean projectIsArchive)
     * в классе AllData !!!*/
    public synchronized void setArchive(boolean archive) {
        isArchive = archive;
    }

    @XmlElement(name = "comment")
    public String getComment() {
        return comment;
    }

    public synchronized void setComment(String newComment) {
        this.comment = newComment;
    }

    @XmlElement(name = "folderpath")
    public String getFolderPath() {
        return folderPath;
    }

    public synchronized void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    @XmlElement(name = "linkedprojects")
    public String getLinkedProjects() {
        return linkedProjects;
    }

    public synchronized void setLinkedProjects(String linkedProjects) {
        this.linkedProjects = linkedProjects;
    }

    @XmlElement(name = "ponumber")
    public String getPONumber() {
        return PONumber;
    }

    @XmlTransient
    public StringProperty PONumberProperty() {
        return PONumberProperty;
    }

    public void setPONumber(String PONumber) {
        this.PONumber = PONumber;
        if (PONumberProperty == null) {
            PONumberProperty = new SimpleStringProperty();
        }
        this.PONumberProperty.set(PONumber);
    }

    @XmlElement(name = "worksumint")
    public int getWorkSum() {
        return workSum;
    }

    private synchronized void setWorkSum(int newWorkSum) {
        this.workSum = newWorkSum >= 0 ? newWorkSum : 0;
    }

    @XmlTransient
    public double getWorkSumDouble() {
        return AllData.intToDouble(workSum);
    }

    protected synchronized void setWorkSumDouble(double newWorkSumDouble) {
        if (newWorkSumDouble <= 0) {
            this.workSum = 0;
        }
        else {
            this.workSum = AllData.doubleToInt(newWorkSumDouble);
        }
    }

    @XmlTransient
    public String getWorkSumProperty() {
        return workSumProperty.get();
    }

    @XmlTransient
    public StringProperty workSumProperty() {
        return workSumProperty;
    }

    public synchronized void setWorkSumProperty(String workSumProperty) {
        this.workSumProperty.set(workSumProperty);
    }



    @XmlElement(name = "projectbudget")
    public int getBudget() {
        return budget;
    }

    public synchronized void setBudget(int budget) {
        this.budget = budget;
        if (this.budgetProperty == null) {
            this.budgetProperty = new SimpleStringProperty();
        }
        this.budgetProperty.set(String.valueOf(budget));
    }

    public StringProperty budgetProperty() {
        return budgetProperty;
    }



    @XmlElement(name = "listworks")
    public List<WorkTime> getWork() {
        return work;
    }

    public synchronized void setWork(List<WorkTime> newWork) {
        this.work = newWork;
        computeWorkSum();
        this.workSumProperty = new SimpleStringProperty(String.valueOf(AllData.intToDouble(workSum)));
        //this.workSumProperty = new SimpleDoubleProperty(AllData.intToDouble(workSum));
    }


    /** Метод рассчитан на вызов из класса AllData,
     * в котором содержится одноименный метод для обращения извне.
     * Возвращаемое значение int используется в AllData для добавления
     * к суммарному общему рабочему времени workSumProjects
     * */
    public synchronized int addWorkTime(LocalDate newDate, int idUser, double newTimeDouble) {

        int newTimeInt = AllData.doubleToInt(newTimeDouble);

        Iterator<WorkTime> iter = work.iterator();
        while (iter.hasNext()) {
            WorkTime wt = iter.next();
            // Проверяем наличие такого дня + дизайнера
            if (wt.getDate().equals(newDate) && wt.getDesignerID() == idUser) {
                // сначала правим суммарное рабочее время всего проекта
                int diff = newTimeInt - wt.getTime();
                int newWorkSumInt = getWorkSum() + diff;

                setWorkSum(newWorkSumInt);
                setWorkSumProperty(String.valueOf(AllData.intToDouble(newWorkSumInt)));

                // удаляем экземпляр WorkTime, если время в нем стало равно 0
                if (newTimeInt <= 0) {
                    iter.remove();
                    return diff;
                }

                // теперь вносим время в экземпляр рабочего времени
                wt.setTime(newTimeInt);
                return diff;
            }
        }

        // Если существующего экземпляра WorkTime с такой же датой и дизайнером не обнаружено,
        // то создаем новый экземпляр WorkTime и кладем в список
        work.add(new WorkTime(this.idNumber, newDate, idUser, newTimeDouble));
        int newWorkSumInt = getWorkSum() + newTimeInt;
        setWorkSum(newWorkSumInt);
        setWorkSumProperty(String.valueOf(AllData.intToDouble(newWorkSumInt)));
        return newTimeInt;

    }

    /** Методы проверки наличия рабочего времени по разным параметрам */


    public boolean containsWorkTime() {
        return !work.isEmpty();
    }


    public boolean containsWorkTime(int designerIDnumber) {
        for (WorkTime wt : work) {
            if (designerIDnumber == wt.getDesignerID()) {
                return true;
            }
        }
        return false;
    }

    public boolean containsWorkTime(LocalDate date) {

        if (date == null) {
            return false;
        }

        for (WorkTime wt : work) {
            if (wt.getDate().equals(date)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsWorkTime(int designerIDnumber, LocalDate date) {

        if (date == null) {
            return containsWorkTime(designerIDnumber);
        }

        for (WorkTime wt : work) {
            if ((designerIDnumber == wt.getDesignerID()) && (wt.getDate().equals(date))) {
                return true;
            }
        }
        return false;
    }

    public boolean containsWorkTime(LocalDate fromDate, LocalDate tillDate) {
        for (WorkTime wt : work) {
            if ((wt.getDate().compareTo(fromDate) >= 0) && (wt.getDate().compareTo(tillDate) <= 0)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsWorkTime(int designerIDnumber, LocalDate fromDate, LocalDate tillDate) {

        if (fromDate == null || tillDate == null) {
            return containsWorkTime(designerIDnumber);
        }

        for (WorkTime wt : work) {
            if (designerIDnumber == wt.getDesignerID()) {
                if ((wt.getDate().compareTo(fromDate) >= 0) && (wt.getDate().compareTo(tillDate) <= 0)) {
                    return true;
                }
            }
        }
        return false;
    }


    public boolean containsWorkTimeForWeek(int year, int weekNumber) {
        for (WorkTime wt : work) {
            if (year == wt.getYear() && weekNumber == wt.getWeekNumber()) {
                return true;
            }
        }
        return false;
    }

    public boolean containsWorkTimeForMonth(int year, int month) {
        for (WorkTime wt : work) {
            if (year == wt.getYear() && month == wt.getMonth()) {
                return true;
            }
        }
        return false;
    }

    public boolean containsWorkTimeForYear(int year) {
        for (WorkTime wt : work) {
            if (year == wt.getYear()) {
                return true;
            }
        }
        return false;
    }


    public boolean containsWorkTimeForDesignerAndWeek(int designerIDnumber, int year, int weekNumber) {
        for (WorkTime wt : work) {
            if (designerIDnumber == wt.getDesignerID()) {
                if (year == wt.getYear() && weekNumber == wt.getWeekNumber()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsWorkTimeForDesignerAndMonth(int designerIDnumber, int year, int month) {
        for (WorkTime wt : work) {
            if (designerIDnumber == wt.getDesignerID()) {
                if (year == wt.getYear() && month == wt.getMonth()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsWorkTimeForDesignerAndYear(int designerIDnumber, int year) {
        for (WorkTime wt : work) {
            if (designerIDnumber == wt.getDesignerID()) {
                if (year == wt.getYear()) {
                    return true;
                }
            }
        }
        return false;
    }



    /** Методы получения суммы рабочего времени по разным параметрам */

    public int getWorkSumForDesigner(int designerIDnumber) {
        int result = 0;
        for (WorkTime wt : work) {
            if (wt.getDesignerID() == designerIDnumber) {
                result += wt.getTime();
            }
        }
        return result;
    }

    public int getWorkSumForDate(LocalDate date) {
        int result = 0;
        for (WorkTime wt : work) {
            if (wt.getDate().equals(date)) {
                result += wt.getTime();
            }
        }
        return result;
    }

    public int getWorkSumForDesignerAndDate(int designerIDnumber, LocalDate date) {
        int result = 0;
        for (WorkTime wt : work) {
            if (wt.getDesignerID() == designerIDnumber && wt.getDate().equals(date)) {
                result += wt.getTime();
            }
        }
        return result;
    }


    public int getWorkSumForPeriod(LocalDate fromDate, LocalDate tillDate) {
        int result = 0;
        for (WorkTime wt : work) {
            if ((wt.getDate().compareTo(fromDate) >= 0) && (wt.getDate().compareTo(tillDate) <= 0)) {
                result += wt.getTime();
            }
        }
        return result;
    }

    public int getWorkSumForDesignerAndPeriod(int designerIDnumber, LocalDate fromDate, LocalDate tillDate) {
        int result = 0;
        for (WorkTime wt : work) {
            if (wt.getDesignerID() == designerIDnumber) {
                if ((wt.getDate().compareTo(fromDate) >= 0) && (wt.getDate().compareTo(tillDate) <= 0)) {
                    result += wt.getTime();
                }
            }
        }
        return result;
    }


    public int getWorkSumForWeek(int year, int week) {
        int result = 0;
        for (WorkTime wt : work) {
            if (year == wt.getYear() && week == wt.getWeekNumber()) {
                result += wt.getTime();
            }
        }
        return result;
    }

    public int getWorkSumForMonth(int year, int month) {
        int result = 0;
        for (WorkTime wt : work) {
            if (year == wt.getYear() && month == wt.getMonth()) {
                result += wt.getTime();
            }
        }
        return result;
    }

    public int getWorkSumForYear(int year) {
        int result = 0;
        for (WorkTime wt : work) {
            if (year == wt.getYear()) {
                result += wt.getTime();
            }
        }
        return result;
    }


    public int getWorkSumForDesignerAndWeek(int designerIDnumber, int year, int week) {
        int result = 0;
        for (WorkTime wt : work) {
            if (wt.getDesignerID() == designerIDnumber) {
                if (year == wt.getYear() && week == wt.getWeekNumber()) {
                    result += wt.getTime();
                }
            }
        }
        return result;
    }

    public int getWorkSumForDesignerAndMonth(int designerIDnumber, int year, int month) {
        int result = 0;
        for (WorkTime wt : work) {
            if (wt.getDesignerID() == designerIDnumber) {
                if (year == wt.getYear() && month == wt.getMonth()) {
                    result += wt.getTime();
                }
            }
        }
        return result;
    }

    public int getWorkSumForDesignerAndYear(int designerIDnumber, int year) {
        int result = 0;
        for (WorkTime wt : work) {
            if (wt.getDesignerID() == designerIDnumber) {
                if (year == wt.getYear()) {
                    result += wt.getTime();
                }
            }
        }
        return result;
    }



    /** Методы получения всех экземпляров WorkTime по разным параметрам */

    public List<WorkTime> getWorkTimeForDesigner(int designerIDnumber) {
        List<WorkTime> result = new ArrayList<>();
        for (WorkTime wt : work) {
            if (wt.getDesignerID() == designerIDnumber) {
                result.add(wt);
            }
        }
        return result;
    }

    public List<WorkTime> getWorkTimeForDate(LocalDate date) {
        List<WorkTime> result = new ArrayList<>();
        for (WorkTime wt : work) {
            if (wt.getDate().equals(date)) {
                result.add(wt);
            }
        }
        return result;
    }

    public List<WorkTime> getWorkTimeForDesignerAndDate(int designerIDnumber, LocalDate date) {
        List<WorkTime> result = new ArrayList<>();
        for (WorkTime wt : work) {
            if (wt.getDesignerID() == designerIDnumber && wt.getDate().equals(date)) {
                result.add(wt);
            }
        }
        return result;
    }


    public List<WorkTime> getWorkTimeForPeriod(LocalDate fromDate, LocalDate tillDate) {
        List<WorkTime> result = new ArrayList<>();
        for (WorkTime wt : work) {
            if ((wt.getDate().compareTo(fromDate) >= 0) && (wt.getDate().compareTo(tillDate) <= 0)) {
                result.add(wt);
            }
        }
        return result;
    }

    public List<WorkTime> getWorkTimeForDesignerAndPeriod(int designerIDnumber, LocalDate fromDate, LocalDate tillDate) {
        List<WorkTime> result = new ArrayList<>();
        for (WorkTime wt : work) {
            if (wt.getDesignerID() == designerIDnumber) {
                if ((wt.getDate().compareTo(fromDate) >= 0) && (wt.getDate().compareTo(tillDate) <= 0)) {
                    result.add(wt);
                }
            }
        }
        return result;
    }


    private synchronized void computeWorkSum() {
        int result = 0;
        for (WorkTime wt : this.work) {
            result += wt.getTime();
        }
        this.workSum = result;
        this.workSumProperty.set(String.valueOf(AllData.intToDouble(result)));
        //this.workSumProperty.set(AllData.intToDouble(result));
    }

    public synchronized void computeProperties() {
        //this.idNumberProperty = new SimpleStringProperty(String.valueOf(idNumber));
        this.idNumberProperty = new SimpleIntegerProperty(idNumber);
        this.companyProperty = new SimpleStringProperty(company);
        this.managerProperty = new SimpleStringProperty(manager);
        this.descriptionProperty = new SimpleStringProperty(description);
        this.workSumProperty = new SimpleStringProperty(String.valueOf(AllData.intToDouble(workSum)));
        //this.workSumProperty = new SimpleDoubleProperty(AllData.intToDouble(workSum));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return idNumber == project.idNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idNumber);
    }

    @Override
    public String toString() {
        return "Project{" +
                "idNumber=" + idNumber +
                ", company='" + company + '\'' +
                ", initiator='" + manager + '\'' +
                ", description='" + description + '\'' +
                ", dateCreationString='" + dateCreationString + '\'' +
                ", isArchive=" + isArchive +
                ", workSum=" + AllData.intToDouble(workSum) +
                '}';
    }


}
