package com.hrmanagement.dao;

import com.hrmanagement.model.Leave;
import java.util.List;

public interface LeaveDAO {
    boolean addLeave(Leave leave);

    Leave getLeaveById(int id);

    List<Leave> getAllLeaves();

    List<Leave> getLeavesByEmployeeId(int employeeId);

    boolean updateLeave(Leave leave);

    boolean deleteLeave(int id);
}