package com.mycompany.myapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProjectsMapperTest {

    private ProjectsMapper projectsMapper;

    @BeforeEach
    public void setUp() {
        projectsMapper = new ProjectsMapperImpl();
    }
}
