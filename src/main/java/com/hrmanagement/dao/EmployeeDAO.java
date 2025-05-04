package com.hrmanagement.dao;

import com.hrmanagement.model.Employee;
import java.util.List;

public interface EmployeeDAO {
    boolean addEmployee(Employee employee);

    Employee getEmployeeById(int id);

    List<Employee> getAllEmployees();

    boolean updateEmployee(Employee employee);

    boolean deleteEmployee(int id);
}