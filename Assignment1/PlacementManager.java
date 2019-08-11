import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

class TechRoundNode{
    public String companyName;
    public int score;
    TechRoundNode next;
    TechRoundNode(String companyName, int score){
        this.companyName = companyName;
        this.score = score;
        this.next = null;
    }
}

class ScoreLinkedList{
    TechRoundNode front;
    ScoreLinkedList(){
        this.front = null;
    }
    public void addNode(String companyName, int score){
        if(front == null){
            TechRoundNode node = new TechRoundNode(companyName, score);
            front = node;
        }
        else{
            TechRoundNode tmp = new TechRoundNode(companyName, score);
            tmp.next = front;
            front = tmp;
        }
    }
    public int getScore(String companyName){
        TechRoundNode t = front;
        while(t != null){
            if(t.companyName.equals(companyName)){
                return t.score;
            }
            else{
                t = t.next;
            }
        }
        return 0;
    }
    public void showAll(){
        TechRoundNode t = front;
        while(t != null){
            System.out.println(t.companyName + " " + Integer.toString(t.score));
            t = t.next;
        }
    }
}

class Company{
    private String name;
    private int numEligibleCourses;
    private String[] courseCriteria;
    private int studentsRequired;
    private boolean isOpen;
    private boolean isRemoved;
    private ArrayList<Student> StudentsShortlisted;
    private ArrayList<Student> StudentsSelected;
    Company(String name, int numEligibleCourses, String[] courseCriteria, int studentsRequired, ArrayList<Student> allStudents, Scanner s){
        this.name = name;
        this.numEligibleCourses = numEligibleCourses;
        this.courseCriteria = courseCriteria;
        this.studentsRequired = studentsRequired;
        this.StudentsShortlisted = new ArrayList<Student>();
        this.StudentsSelected = new ArrayList<Student>();
        for(int i=0; i<allStudents.size(); i++){
            for(int j=0; j<courseCriteria.length; j++){
                if(allStudents.get(i).getCourseName().equals(courseCriteria[j])){
                    StudentsShortlisted.add(allStudents.get(i));
                }
            }
        }
        this.isOpen = true;
        this.isRemoved = false;
        System.out.println(this.name);
        System.out.println("Course Criteria: ");
        for(int i =0; i< this.courseCriteria.length; i++){
            System.out.println(this.courseCriteria[i]);
        }
        System.out.println("Number of required students = " + Integer.toString(this.studentsRequired));
        if(isOpen){
            System.out.println("Application status = OPEN");
        }
        else{
            System.out.println("Application status = CLOSED");
        }
        System.out.println("Enter scores for the technical round");
        for(int i=0; i<StudentsShortlisted.size(); i++){
            System.out.println("Enter score for Roll No. " + Integer.toString(StudentsShortlisted.get(i).getRollNo()));
            int addScore = s.nextInt();
            StudentsShortlisted.get(i).addScore(name, addScore);
        }
        Comparator<Student> sortByScores = new Comparator<Student>() {
            @Override
            public int compare(Student s1, Student s2){
                if(s1.getScore(name) < s2.getScore(name)){
                    return -1;
                }
                else if(s1.getScore(name) == s2.getScore(name)){
                    return Float.compare(s1.getCGPA(), s2.getCGPA());
                }
                return 0;
            }
        };
        Comparator<Student> mainComparator = sortByScores.reversed();
        Collections.sort(StudentsShortlisted, mainComparator);
    }
    public void displayCompany(){
        System.out.println(this.name);
        System.out.println("Course Criteria: ");
        System.out.println("Number of required students = " + Integer.toString(this.studentsRequired));
        if(isOpen){
            System.out.println("Application status = OPEN");
        }
        else{
            System.out.println("Application status = CLOSED");
        }
    }
    public String getName(){
        return this.name;
    }
    public boolean isOpen(){
        return this.isOpen;
    }
    public int selectStudents(){
        int totalPlaced = 0;    
        if(isOpen){    
            for(Student a:StudentsShortlisted){
                if(!a.isPlaced() && totalPlaced < studentsRequired){
                    StudentsSelected.add(a);
                    totalPlaced++;
                    a.togglePlaced();
                    a.setCompanyPlaced(name);
                }
            }
            if(totalPlaced <= studentsRequired){
                isOpen = false;
            }
            System.out.println("Roll Nos of students placed");
            for (Student a:StudentsSelected){
                System.out.println(a.getRollNo());
            }
            return totalPlaced;
        }
        else{
            System.out.println("Company application closed");
            return 0;
        }
    }
    public void toggleRemoved(){
        isRemoved = !isRemoved;
    }
}

class Student{
    private int rollNo;
    private float cgpa;
    private String course;
    private boolean isPlaced;
    private ScoreLinkedList scores;
    private String placedCompany;
    Student(int rollNo, float cgpa, String course){
        this.rollNo = rollNo;
        this.cgpa = cgpa;
        this.course = course;
        isPlaced = false;
        placedCompany = null;
        scores = new ScoreLinkedList();
    }
    public void printStudent(){
        System.out.println(this.rollNo);
        System.out.println(this.cgpa);
        System.out.println(this.course);
        if(isPlaced){
            System.out.println("Placement status : Placed");
            System.out.println("Placed at " + placedCompany);
        }
        else{
            System.out.println("Placement status : Not placed");
        }
    }
    public int getRollNo(){
        return this.rollNo;
    }
    public boolean isPlaced(){
        return this.isPlaced;
    }
    public float getCGPA(){
        return this.cgpa;
    }
    public String getCourseName(){
        return this.course;
    }
    public void addScore(String company, int score){
        scores.addNode(company, score);
    }
    public int getScore(String companyName){
        return scores.getScore(companyName);
    }
    public void showScores(){
        scores.showAll();
    }
    public void togglePlaced(){
        isPlaced = (!isPlaced);
    }
    public void setCompanyPlaced(String placedCompany){
        this.placedCompany = placedCompany;
    }
}

public class PlacementManager{
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter data of all Students");
        int totalStudents = s.nextInt();
        int unplacedStudents = totalStudents;
        int placedStudents = 0;
        ArrayList<Student> allStudents = new ArrayList();
        ArrayList<Company> allCompanies = new ArrayList();
        for(int i=0; i<totalStudents; i++){
            float gradePoint = Float.parseFloat(s.next());
            String courseAdd = s.next();            
            Student newStudent = new Student(i+1, gradePoint, courseAdd);
            allStudents.add(newStudent);
        }
        System.out.println("1) Add Company");
        System.out.println("2) Remove accounts of placed students");
        System.out.println("3) Remove closed application companies");
        System.out.println("4) Display number of unplaced students");
        System.out.println("5) Display open application companies");
        System.out.println("6) Select students");
        System.out.println("7) Display company details");
        System.out.println("8) Display student details ");
        System.out.println("9) Display student tech round scores in applied companies");
        while(unplacedStudents > 0){
            int query = s.nextInt();
            if(query == 1){
                String name = s.next();
                System.out.println("Enter number of eligible courses");
                int numCourses = s.nextInt();
                System.out.println("Enter course criteria");
                String[] courseCriteria = new String[numCourses];
                for(int i=0; i<numCourses; i++){
                    courseCriteria[i] = s.next();
                }
                System.out.println("Enter number of students required");
                int numStudentsRequired = s.nextInt();
                Company newCompany = new Company(name, numCourses, courseCriteria, numStudentsRequired, allStudents, s);
                allCompanies.add(newCompany);
            }
            if(query == 2){
                ArrayList<Student> newStudents = new ArrayList();
                int countRemoved = 0;
                System.out.println("Student accounts removed for:");
                for(int i=0; i<allStudents.size(); i++){
                    if(!(allStudents.get(i).isPlaced())){
                        newStudents.add(allStudents.get(i));
                    }
                    else{
                        System.out.println(allStudents.get(i).getRollNo());
                        countRemoved++;
                    }
                } 
                if(countRemoved == 0){
                    System.out.println("None");
                }
                allStudents = newStudents;
            }
            if(query == 3){
                int countRemoved = 0;
                System.out.println("Company accounts removed for:");
                for(int i=0; i<allCompanies.size(); i++){
                    if((allCompanies.get(i).isOpen())){
                       continue; 
                    }
                    else{
                        System.out.println(allCompanies.get(i).getName());
                        allCompanies.get(i).toggleRemoved();
                        countRemoved++;
                    }
                } 
                if(countRemoved == 0){
                    System.out.println("None");
                }
            }
            if(query == 4){
                System.out.println(Integer.toString(totalStudents - placedStudents) + " students not placed");
            }
            if(query == 5){
                for(int i = 0; i<allCompanies.size(); i++){
                    if(allCompanies.get(i).isOpen()){
                        System.out.println(allCompanies.get(i).getName());
                    }
                }
            }
            if(query == 6){
                String companyName = s.next();
                boolean found = false;
                for(int i=0; i<allCompanies.size(); i++){
                    if(allCompanies.get(i).getName().equals(companyName)){
                        found = true;
                        int numPlaced = allCompanies.get(i).selectStudents();
                        placedStudents += numPlaced;
                        unplacedStudents = totalStudents - placedStudents;
                    }
                }
                if(!found){
                    System.out.println("No company is associated with the given name");
                }
            }
            if(query == 7){
                String companyName = s.next();
                boolean found = false;
                for(int i=0; i<allCompanies.size(); i++){
                    if(allCompanies.get(i).getName().equals(companyName)){
                        found = true;
                        allCompanies.get(i).displayCompany();
                    }
                }
                if(!found){
                    System.out.println("No company is associated with the given name");
                }
            }
            if(query == 8){
                int rNo = s.nextInt();
                boolean found = false;
                for(int i=0; i<allStudents.size(); i++){
                    if(allStudents.get(i).getRollNo() == rNo){
                        found = true;
                        allStudents.get(i).printStudent();
                    }
                }
                if(!found){
                    System.out.println("No student is associated with the given roll number");
                }
            }            
            if(query == 9){
                int rNo = s.nextInt();
                boolean found = false;
                for(int i=0; i<allStudents.size(); i++){
                    if(allStudents.get(i).getRollNo() == rNo){
                        found = true;
                        allStudents.get(i).showScores();
                    }
                }
                if(!found){
                    System.out.println("No student is associated with the given roll number");
                }
            }
            if(unplacedStudents == 0){
                System.out.println("All students placed!");
            }
        }
    }
}