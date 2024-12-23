# **Learning Management System (LMS)**

A comprehensive **Java-based Learning Management System** (LMS) designed to manage and organize online courses, assessments, and performance tracking for students, instructors, and administrators. This web-based application leverages **Spring Boot**, **Spring Security**, and a relational database to deliver a feature-rich, scalable solution for online education.

---

## **Key Features**

### **1. User Management**
- **User Types**:
  - **Admin**: Manages system settings, creates users, and oversees courses.
  - **Instructor**: Creates courses, manages course content, adds assignments/quizzes, grades students, and manages enrollments.
  - **Student**: Enrolls in courses, accesses materials, submits assignments, and views grades.
  
- **Core Features**:
  - Role-based **Registration and Login**.
  - **Profile Management** for updating user information.

---

### **2. Course Management**
- **Course Creation**:
  - Instructors can create courses with details such as title, description, and duration.
  - Upload support for **media files** (e.g., videos, PDFs, audio files).
  - Courses consist of multiple lessons for students to attend.
  
- **Enrollment Management**:
  - Students can browse and enroll in courses.
  - Admins and Instructors can view enrolled students for each course.

- **Attendance Management**:
  - Instructors generate an **OTP per lesson** to manage attendance.
  - Students select a lesson and input the OTP to mark attendance.

---

### **3. Assessment & Grading**
- **Assessment Types**:
  - **Quizzes**:
    - Instructors create quizzes with various question types: MCQs, true/false, short answers.
    - Build a **question bank** for each course.
    - Support for randomized question selection for quiz attempts.
  - **Assignments**:
    - Students upload files to submit assignments for instructor review.
  
- **Grading and Feedback**:
  - Instructors grade assignments and quizzes.
  - Automated feedback for quizzes; manual feedback for assignments.

---

### **4. Performance Tracking**
- **Student Progress**:
  - Instructors can track:
    - Quiz scores.
    - Assignment submissions.
    - Attendance records.

---

### **5. Notifications**
- **System Notifications**:
  - Students receive notifications for:
    - Enrollment confirmations.
    - Graded assignments.
    - Course updates.
  - Instructors are notified about:
    - Student enrollments in their courses.
  - Notifications are categorized as **read** or **unread** for easy management.

- **Email Notifications**:
  - Students receive email updates for important events like enrollment confirmations, grades, and course updates.

---

### **6. Role-Based Access Control**
- Implemented with **Spring Security**.
- Restricts access permissions based on the user role:
  - Admin, Instructor, or Student.

---

### **7. Performance Analytics**
- Admins and Instructors can:
  - Generate **Excel reports** for student performance (grades and attendance).
  - Visualize data through **charts** for progress, performance, and course completion.

---

## **Technologies Used**
- **Backend**: Java, Spring Boot, Spring Security
- **Database**: MySQL
- **Authentication**: JSON Web Token (JWT)
- **Build Tool**: Maven
- **Testing**: JUnit
- **Version Control**: Git

---

## **Setup Instructions**
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-username/lms.git
   cd lms
   ```

2. **Configure Database**:
   - Update database settings in `src/main/resources/application.properties`.

3. **Run the Application**:
   ```bash
   mvn spring-boot:run
   ```

4. **Access the Application**:
   - Open your browser and navigate to: `http://localhost:8080`.

---

## **Project Structure**
```plaintext
src/
├── main/
│   ├── java/org/software/lms/
│   │   ├── model/        # Entity Classes
│   │   ├── repository/   # Repository Interfaces
│   │   ├── service/      # Service Layer
│   │   ├── controller/   # REST Controllers
│   │   ├── security/     # JWT & Spring Security Configurations
│   └── resources/
│       ├── application.properties  # Configuration File
├── test/                 # JUnit Tests
├── pom.xml               # Maven Configuration
```
