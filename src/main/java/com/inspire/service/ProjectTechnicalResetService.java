package com.inspire.service;

import org.springframework.stereotype.Service;

@Service
public interface ProjectTechnicalResetService {
    int performTechnicalReset(Integer projectId);
}