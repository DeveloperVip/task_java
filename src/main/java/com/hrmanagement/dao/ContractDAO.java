package com.hrmanagement.dao;

import com.hrmanagement.model.Contract;
import java.util.List;

public interface ContractDAO {
    boolean addContract(Contract contract);

    Contract getContractById(int id);

    List<Contract> getAllContracts();

    List<Contract> getContractsByEmployeeId(int employeeId);

    boolean updateContract(Contract contract);

    boolean deleteContract(int id);
}