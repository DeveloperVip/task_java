package com.hrmanagement.dao;

import com.hrmanagement.model.Candidate;
import java.util.List;

public interface CandidateDAO {
    boolean addCandidate(Candidate candidate);

    Candidate getCandidateById(int id);

    List<Candidate> getAllCandidates();

    boolean updateCandidate(Candidate candidate);

    boolean deleteCandidate(int id);
}