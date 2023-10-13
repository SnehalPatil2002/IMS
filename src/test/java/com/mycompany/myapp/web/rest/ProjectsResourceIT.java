package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Projects;
import com.mycompany.myapp.repository.ProjectsRepository;
import com.mycompany.myapp.service.criteria.ProjectsCriteria;
import com.mycompany.myapp.service.dto.ProjectsDTO;
import com.mycompany.myapp.service.mapper.ProjectsMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ProjectsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProjectsResourceIT {

    private static final String DEFAULT_PROJECT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_PROJECT_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Long DEFAULT_ORDER_QUANTITY = 1L;
    private static final Long UPDATED_ORDER_QUANTITY = 2L;
    private static final Long SMALLER_ORDER_QUANTITY = 1L - 1L;

    private static final Double DEFAULT_ESIMATED_BUDGET = 1D;
    private static final Double UPDATED_ESIMATED_BUDGET = 2D;
    private static final Double SMALLER_ESIMATED_BUDGET = 1D - 1D;

    private static final Double DEFAULT_FINAL_TOTAL = 1D;
    private static final Double UPDATED_FINAL_TOTAL = 2D;
    private static final Double SMALLER_FINAL_TOTAL = 1D - 1D;

    private static final String ENTITY_API_URL = "/api/projects";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProjectsRepository projectsRepository;

    @Autowired
    private ProjectsMapper projectsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProjectsMockMvc;

    private Projects projects;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Projects createEntity(EntityManager em) {
        Projects projects = new Projects()
            .projectName(DEFAULT_PROJECT_NAME)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .orderQuantity(DEFAULT_ORDER_QUANTITY)
            .esimatedBudget(DEFAULT_ESIMATED_BUDGET)
            .finalTotal(DEFAULT_FINAL_TOTAL);
        return projects;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Projects createUpdatedEntity(EntityManager em) {
        Projects projects = new Projects()
            .projectName(UPDATED_PROJECT_NAME)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .orderQuantity(UPDATED_ORDER_QUANTITY)
            .esimatedBudget(UPDATED_ESIMATED_BUDGET)
            .finalTotal(UPDATED_FINAL_TOTAL);
        return projects;
    }

    @BeforeEach
    public void initTest() {
        projects = createEntity(em);
    }

    @Test
    @Transactional
    void createProjects() throws Exception {
        int databaseSizeBeforeCreate = projectsRepository.findAll().size();
        // Create the Projects
        ProjectsDTO projectsDTO = projectsMapper.toDto(projects);
        restProjectsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(projectsDTO)))
            .andExpect(status().isCreated());

        // Validate the Projects in the database
        List<Projects> projectsList = projectsRepository.findAll();
        assertThat(projectsList).hasSize(databaseSizeBeforeCreate + 1);
        Projects testProjects = projectsList.get(projectsList.size() - 1);
        assertThat(testProjects.getProjectName()).isEqualTo(DEFAULT_PROJECT_NAME);
        assertThat(testProjects.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testProjects.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testProjects.getOrderQuantity()).isEqualTo(DEFAULT_ORDER_QUANTITY);
        assertThat(testProjects.getEsimatedBudget()).isEqualTo(DEFAULT_ESIMATED_BUDGET);
        assertThat(testProjects.getFinalTotal()).isEqualTo(DEFAULT_FINAL_TOTAL);
    }

    @Test
    @Transactional
    void createProjectsWithExistingId() throws Exception {
        // Create the Projects with an existing ID
        projects.setId(1L);
        ProjectsDTO projectsDTO = projectsMapper.toDto(projects);

        int databaseSizeBeforeCreate = projectsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProjectsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(projectsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Projects in the database
        List<Projects> projectsList = projectsRepository.findAll();
        assertThat(projectsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllProjects() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList
        restProjectsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projects.getId().intValue())))
            .andExpect(jsonPath("$.[*].projectName").value(hasItem(DEFAULT_PROJECT_NAME)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].orderQuantity").value(hasItem(DEFAULT_ORDER_QUANTITY.intValue())))
            .andExpect(jsonPath("$.[*].esimatedBudget").value(hasItem(DEFAULT_ESIMATED_BUDGET.doubleValue())))
            .andExpect(jsonPath("$.[*].finalTotal").value(hasItem(DEFAULT_FINAL_TOTAL.doubleValue())));
    }

    @Test
    @Transactional
    void getProjects() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get the projects
        restProjectsMockMvc
            .perform(get(ENTITY_API_URL_ID, projects.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(projects.getId().intValue()))
            .andExpect(jsonPath("$.projectName").value(DEFAULT_PROJECT_NAME))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.orderQuantity").value(DEFAULT_ORDER_QUANTITY.intValue()))
            .andExpect(jsonPath("$.esimatedBudget").value(DEFAULT_ESIMATED_BUDGET.doubleValue()))
            .andExpect(jsonPath("$.finalTotal").value(DEFAULT_FINAL_TOTAL.doubleValue()));
    }

    @Test
    @Transactional
    void getProjectsByIdFiltering() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        Long id = projects.getId();

        defaultProjectsShouldBeFound("id.equals=" + id);
        defaultProjectsShouldNotBeFound("id.notEquals=" + id);

        defaultProjectsShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProjectsShouldNotBeFound("id.greaterThan=" + id);

        defaultProjectsShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProjectsShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProjectsByProjectNameIsEqualToSomething() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where projectName equals to DEFAULT_PROJECT_NAME
        defaultProjectsShouldBeFound("projectName.equals=" + DEFAULT_PROJECT_NAME);

        // Get all the projectsList where projectName equals to UPDATED_PROJECT_NAME
        defaultProjectsShouldNotBeFound("projectName.equals=" + UPDATED_PROJECT_NAME);
    }

    @Test
    @Transactional
    void getAllProjectsByProjectNameIsInShouldWork() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where projectName in DEFAULT_PROJECT_NAME or UPDATED_PROJECT_NAME
        defaultProjectsShouldBeFound("projectName.in=" + DEFAULT_PROJECT_NAME + "," + UPDATED_PROJECT_NAME);

        // Get all the projectsList where projectName equals to UPDATED_PROJECT_NAME
        defaultProjectsShouldNotBeFound("projectName.in=" + UPDATED_PROJECT_NAME);
    }

    @Test
    @Transactional
    void getAllProjectsByProjectNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where projectName is not null
        defaultProjectsShouldBeFound("projectName.specified=true");

        // Get all the projectsList where projectName is null
        defaultProjectsShouldNotBeFound("projectName.specified=false");
    }

    @Test
    @Transactional
    void getAllProjectsByProjectNameContainsSomething() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where projectName contains DEFAULT_PROJECT_NAME
        defaultProjectsShouldBeFound("projectName.contains=" + DEFAULT_PROJECT_NAME);

        // Get all the projectsList where projectName contains UPDATED_PROJECT_NAME
        defaultProjectsShouldNotBeFound("projectName.contains=" + UPDATED_PROJECT_NAME);
    }

    @Test
    @Transactional
    void getAllProjectsByProjectNameNotContainsSomething() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where projectName does not contain DEFAULT_PROJECT_NAME
        defaultProjectsShouldNotBeFound("projectName.doesNotContain=" + DEFAULT_PROJECT_NAME);

        // Get all the projectsList where projectName does not contain UPDATED_PROJECT_NAME
        defaultProjectsShouldBeFound("projectName.doesNotContain=" + UPDATED_PROJECT_NAME);
    }

    @Test
    @Transactional
    void getAllProjectsByStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where startDate equals to DEFAULT_START_DATE
        defaultProjectsShouldBeFound("startDate.equals=" + DEFAULT_START_DATE);

        // Get all the projectsList where startDate equals to UPDATED_START_DATE
        defaultProjectsShouldNotBeFound("startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllProjectsByStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where startDate in DEFAULT_START_DATE or UPDATED_START_DATE
        defaultProjectsShouldBeFound("startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE);

        // Get all the projectsList where startDate equals to UPDATED_START_DATE
        defaultProjectsShouldNotBeFound("startDate.in=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllProjectsByStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where startDate is not null
        defaultProjectsShouldBeFound("startDate.specified=true");

        // Get all the projectsList where startDate is null
        defaultProjectsShouldNotBeFound("startDate.specified=false");
    }

    @Test
    @Transactional
    void getAllProjectsByEndDateIsEqualToSomething() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where endDate equals to DEFAULT_END_DATE
        defaultProjectsShouldBeFound("endDate.equals=" + DEFAULT_END_DATE);

        // Get all the projectsList where endDate equals to UPDATED_END_DATE
        defaultProjectsShouldNotBeFound("endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllProjectsByEndDateIsInShouldWork() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where endDate in DEFAULT_END_DATE or UPDATED_END_DATE
        defaultProjectsShouldBeFound("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE);

        // Get all the projectsList where endDate equals to UPDATED_END_DATE
        defaultProjectsShouldNotBeFound("endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllProjectsByEndDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where endDate is not null
        defaultProjectsShouldBeFound("endDate.specified=true");

        // Get all the projectsList where endDate is null
        defaultProjectsShouldNotBeFound("endDate.specified=false");
    }

    @Test
    @Transactional
    void getAllProjectsByOrderQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where orderQuantity equals to DEFAULT_ORDER_QUANTITY
        defaultProjectsShouldBeFound("orderQuantity.equals=" + DEFAULT_ORDER_QUANTITY);

        // Get all the projectsList where orderQuantity equals to UPDATED_ORDER_QUANTITY
        defaultProjectsShouldNotBeFound("orderQuantity.equals=" + UPDATED_ORDER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProjectsByOrderQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where orderQuantity in DEFAULT_ORDER_QUANTITY or UPDATED_ORDER_QUANTITY
        defaultProjectsShouldBeFound("orderQuantity.in=" + DEFAULT_ORDER_QUANTITY + "," + UPDATED_ORDER_QUANTITY);

        // Get all the projectsList where orderQuantity equals to UPDATED_ORDER_QUANTITY
        defaultProjectsShouldNotBeFound("orderQuantity.in=" + UPDATED_ORDER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProjectsByOrderQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where orderQuantity is not null
        defaultProjectsShouldBeFound("orderQuantity.specified=true");

        // Get all the projectsList where orderQuantity is null
        defaultProjectsShouldNotBeFound("orderQuantity.specified=false");
    }

    @Test
    @Transactional
    void getAllProjectsByOrderQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where orderQuantity is greater than or equal to DEFAULT_ORDER_QUANTITY
        defaultProjectsShouldBeFound("orderQuantity.greaterThanOrEqual=" + DEFAULT_ORDER_QUANTITY);

        // Get all the projectsList where orderQuantity is greater than or equal to UPDATED_ORDER_QUANTITY
        defaultProjectsShouldNotBeFound("orderQuantity.greaterThanOrEqual=" + UPDATED_ORDER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProjectsByOrderQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where orderQuantity is less than or equal to DEFAULT_ORDER_QUANTITY
        defaultProjectsShouldBeFound("orderQuantity.lessThanOrEqual=" + DEFAULT_ORDER_QUANTITY);

        // Get all the projectsList where orderQuantity is less than or equal to SMALLER_ORDER_QUANTITY
        defaultProjectsShouldNotBeFound("orderQuantity.lessThanOrEqual=" + SMALLER_ORDER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProjectsByOrderQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where orderQuantity is less than DEFAULT_ORDER_QUANTITY
        defaultProjectsShouldNotBeFound("orderQuantity.lessThan=" + DEFAULT_ORDER_QUANTITY);

        // Get all the projectsList where orderQuantity is less than UPDATED_ORDER_QUANTITY
        defaultProjectsShouldBeFound("orderQuantity.lessThan=" + UPDATED_ORDER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProjectsByOrderQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where orderQuantity is greater than DEFAULT_ORDER_QUANTITY
        defaultProjectsShouldNotBeFound("orderQuantity.greaterThan=" + DEFAULT_ORDER_QUANTITY);

        // Get all the projectsList where orderQuantity is greater than SMALLER_ORDER_QUANTITY
        defaultProjectsShouldBeFound("orderQuantity.greaterThan=" + SMALLER_ORDER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProjectsByEsimatedBudgetIsEqualToSomething() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where esimatedBudget equals to DEFAULT_ESIMATED_BUDGET
        defaultProjectsShouldBeFound("esimatedBudget.equals=" + DEFAULT_ESIMATED_BUDGET);

        // Get all the projectsList where esimatedBudget equals to UPDATED_ESIMATED_BUDGET
        defaultProjectsShouldNotBeFound("esimatedBudget.equals=" + UPDATED_ESIMATED_BUDGET);
    }

    @Test
    @Transactional
    void getAllProjectsByEsimatedBudgetIsInShouldWork() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where esimatedBudget in DEFAULT_ESIMATED_BUDGET or UPDATED_ESIMATED_BUDGET
        defaultProjectsShouldBeFound("esimatedBudget.in=" + DEFAULT_ESIMATED_BUDGET + "," + UPDATED_ESIMATED_BUDGET);

        // Get all the projectsList where esimatedBudget equals to UPDATED_ESIMATED_BUDGET
        defaultProjectsShouldNotBeFound("esimatedBudget.in=" + UPDATED_ESIMATED_BUDGET);
    }

    @Test
    @Transactional
    void getAllProjectsByEsimatedBudgetIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where esimatedBudget is not null
        defaultProjectsShouldBeFound("esimatedBudget.specified=true");

        // Get all the projectsList where esimatedBudget is null
        defaultProjectsShouldNotBeFound("esimatedBudget.specified=false");
    }

    @Test
    @Transactional
    void getAllProjectsByEsimatedBudgetIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where esimatedBudget is greater than or equal to DEFAULT_ESIMATED_BUDGET
        defaultProjectsShouldBeFound("esimatedBudget.greaterThanOrEqual=" + DEFAULT_ESIMATED_BUDGET);

        // Get all the projectsList where esimatedBudget is greater than or equal to UPDATED_ESIMATED_BUDGET
        defaultProjectsShouldNotBeFound("esimatedBudget.greaterThanOrEqual=" + UPDATED_ESIMATED_BUDGET);
    }

    @Test
    @Transactional
    void getAllProjectsByEsimatedBudgetIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where esimatedBudget is less than or equal to DEFAULT_ESIMATED_BUDGET
        defaultProjectsShouldBeFound("esimatedBudget.lessThanOrEqual=" + DEFAULT_ESIMATED_BUDGET);

        // Get all the projectsList where esimatedBudget is less than or equal to SMALLER_ESIMATED_BUDGET
        defaultProjectsShouldNotBeFound("esimatedBudget.lessThanOrEqual=" + SMALLER_ESIMATED_BUDGET);
    }

    @Test
    @Transactional
    void getAllProjectsByEsimatedBudgetIsLessThanSomething() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where esimatedBudget is less than DEFAULT_ESIMATED_BUDGET
        defaultProjectsShouldNotBeFound("esimatedBudget.lessThan=" + DEFAULT_ESIMATED_BUDGET);

        // Get all the projectsList where esimatedBudget is less than UPDATED_ESIMATED_BUDGET
        defaultProjectsShouldBeFound("esimatedBudget.lessThan=" + UPDATED_ESIMATED_BUDGET);
    }

    @Test
    @Transactional
    void getAllProjectsByEsimatedBudgetIsGreaterThanSomething() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where esimatedBudget is greater than DEFAULT_ESIMATED_BUDGET
        defaultProjectsShouldNotBeFound("esimatedBudget.greaterThan=" + DEFAULT_ESIMATED_BUDGET);

        // Get all the projectsList where esimatedBudget is greater than SMALLER_ESIMATED_BUDGET
        defaultProjectsShouldBeFound("esimatedBudget.greaterThan=" + SMALLER_ESIMATED_BUDGET);
    }

    @Test
    @Transactional
    void getAllProjectsByFinalTotalIsEqualToSomething() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where finalTotal equals to DEFAULT_FINAL_TOTAL
        defaultProjectsShouldBeFound("finalTotal.equals=" + DEFAULT_FINAL_TOTAL);

        // Get all the projectsList where finalTotal equals to UPDATED_FINAL_TOTAL
        defaultProjectsShouldNotBeFound("finalTotal.equals=" + UPDATED_FINAL_TOTAL);
    }

    @Test
    @Transactional
    void getAllProjectsByFinalTotalIsInShouldWork() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where finalTotal in DEFAULT_FINAL_TOTAL or UPDATED_FINAL_TOTAL
        defaultProjectsShouldBeFound("finalTotal.in=" + DEFAULT_FINAL_TOTAL + "," + UPDATED_FINAL_TOTAL);

        // Get all the projectsList where finalTotal equals to UPDATED_FINAL_TOTAL
        defaultProjectsShouldNotBeFound("finalTotal.in=" + UPDATED_FINAL_TOTAL);
    }

    @Test
    @Transactional
    void getAllProjectsByFinalTotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where finalTotal is not null
        defaultProjectsShouldBeFound("finalTotal.specified=true");

        // Get all the projectsList where finalTotal is null
        defaultProjectsShouldNotBeFound("finalTotal.specified=false");
    }

    @Test
    @Transactional
    void getAllProjectsByFinalTotalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where finalTotal is greater than or equal to DEFAULT_FINAL_TOTAL
        defaultProjectsShouldBeFound("finalTotal.greaterThanOrEqual=" + DEFAULT_FINAL_TOTAL);

        // Get all the projectsList where finalTotal is greater than or equal to UPDATED_FINAL_TOTAL
        defaultProjectsShouldNotBeFound("finalTotal.greaterThanOrEqual=" + UPDATED_FINAL_TOTAL);
    }

    @Test
    @Transactional
    void getAllProjectsByFinalTotalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where finalTotal is less than or equal to DEFAULT_FINAL_TOTAL
        defaultProjectsShouldBeFound("finalTotal.lessThanOrEqual=" + DEFAULT_FINAL_TOTAL);

        // Get all the projectsList where finalTotal is less than or equal to SMALLER_FINAL_TOTAL
        defaultProjectsShouldNotBeFound("finalTotal.lessThanOrEqual=" + SMALLER_FINAL_TOTAL);
    }

    @Test
    @Transactional
    void getAllProjectsByFinalTotalIsLessThanSomething() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where finalTotal is less than DEFAULT_FINAL_TOTAL
        defaultProjectsShouldNotBeFound("finalTotal.lessThan=" + DEFAULT_FINAL_TOTAL);

        // Get all the projectsList where finalTotal is less than UPDATED_FINAL_TOTAL
        defaultProjectsShouldBeFound("finalTotal.lessThan=" + UPDATED_FINAL_TOTAL);
    }

    @Test
    @Transactional
    void getAllProjectsByFinalTotalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        // Get all the projectsList where finalTotal is greater than DEFAULT_FINAL_TOTAL
        defaultProjectsShouldNotBeFound("finalTotal.greaterThan=" + DEFAULT_FINAL_TOTAL);

        // Get all the projectsList where finalTotal is greater than SMALLER_FINAL_TOTAL
        defaultProjectsShouldBeFound("finalTotal.greaterThan=" + SMALLER_FINAL_TOTAL);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProjectsShouldBeFound(String filter) throws Exception {
        restProjectsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projects.getId().intValue())))
            .andExpect(jsonPath("$.[*].projectName").value(hasItem(DEFAULT_PROJECT_NAME)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].orderQuantity").value(hasItem(DEFAULT_ORDER_QUANTITY.intValue())))
            .andExpect(jsonPath("$.[*].esimatedBudget").value(hasItem(DEFAULT_ESIMATED_BUDGET.doubleValue())))
            .andExpect(jsonPath("$.[*].finalTotal").value(hasItem(DEFAULT_FINAL_TOTAL.doubleValue())));

        // Check, that the count call also returns 1
        restProjectsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProjectsShouldNotBeFound(String filter) throws Exception {
        restProjectsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProjectsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProjects() throws Exception {
        // Get the projects
        restProjectsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProjects() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        int databaseSizeBeforeUpdate = projectsRepository.findAll().size();

        // Update the projects
        Projects updatedProjects = projectsRepository.findById(projects.getId()).get();
        // Disconnect from session so that the updates on updatedProjects are not directly saved in db
        em.detach(updatedProjects);
        updatedProjects
            .projectName(UPDATED_PROJECT_NAME)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .orderQuantity(UPDATED_ORDER_QUANTITY)
            .esimatedBudget(UPDATED_ESIMATED_BUDGET)
            .finalTotal(UPDATED_FINAL_TOTAL);
        ProjectsDTO projectsDTO = projectsMapper.toDto(updatedProjects);

        restProjectsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, projectsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectsDTO))
            )
            .andExpect(status().isOk());

        // Validate the Projects in the database
        List<Projects> projectsList = projectsRepository.findAll();
        assertThat(projectsList).hasSize(databaseSizeBeforeUpdate);
        Projects testProjects = projectsList.get(projectsList.size() - 1);
        assertThat(testProjects.getProjectName()).isEqualTo(UPDATED_PROJECT_NAME);
        assertThat(testProjects.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testProjects.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testProjects.getOrderQuantity()).isEqualTo(UPDATED_ORDER_QUANTITY);
        assertThat(testProjects.getEsimatedBudget()).isEqualTo(UPDATED_ESIMATED_BUDGET);
        assertThat(testProjects.getFinalTotal()).isEqualTo(UPDATED_FINAL_TOTAL);
    }

    @Test
    @Transactional
    void putNonExistingProjects() throws Exception {
        int databaseSizeBeforeUpdate = projectsRepository.findAll().size();
        projects.setId(count.incrementAndGet());

        // Create the Projects
        ProjectsDTO projectsDTO = projectsMapper.toDto(projects);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, projectsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Projects in the database
        List<Projects> projectsList = projectsRepository.findAll();
        assertThat(projectsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProjects() throws Exception {
        int databaseSizeBeforeUpdate = projectsRepository.findAll().size();
        projects.setId(count.incrementAndGet());

        // Create the Projects
        ProjectsDTO projectsDTO = projectsMapper.toDto(projects);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Projects in the database
        List<Projects> projectsList = projectsRepository.findAll();
        assertThat(projectsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProjects() throws Exception {
        int databaseSizeBeforeUpdate = projectsRepository.findAll().size();
        projects.setId(count.incrementAndGet());

        // Create the Projects
        ProjectsDTO projectsDTO = projectsMapper.toDto(projects);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(projectsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Projects in the database
        List<Projects> projectsList = projectsRepository.findAll();
        assertThat(projectsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProjectsWithPatch() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        int databaseSizeBeforeUpdate = projectsRepository.findAll().size();

        // Update the projects using partial update
        Projects partialUpdatedProjects = new Projects();
        partialUpdatedProjects.setId(projects.getId());

        partialUpdatedProjects.projectName(UPDATED_PROJECT_NAME).orderQuantity(UPDATED_ORDER_QUANTITY);

        restProjectsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProjects.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProjects))
            )
            .andExpect(status().isOk());

        // Validate the Projects in the database
        List<Projects> projectsList = projectsRepository.findAll();
        assertThat(projectsList).hasSize(databaseSizeBeforeUpdate);
        Projects testProjects = projectsList.get(projectsList.size() - 1);
        assertThat(testProjects.getProjectName()).isEqualTo(UPDATED_PROJECT_NAME);
        assertThat(testProjects.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testProjects.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testProjects.getOrderQuantity()).isEqualTo(UPDATED_ORDER_QUANTITY);
        assertThat(testProjects.getEsimatedBudget()).isEqualTo(DEFAULT_ESIMATED_BUDGET);
        assertThat(testProjects.getFinalTotal()).isEqualTo(DEFAULT_FINAL_TOTAL);
    }

    @Test
    @Transactional
    void fullUpdateProjectsWithPatch() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        int databaseSizeBeforeUpdate = projectsRepository.findAll().size();

        // Update the projects using partial update
        Projects partialUpdatedProjects = new Projects();
        partialUpdatedProjects.setId(projects.getId());

        partialUpdatedProjects
            .projectName(UPDATED_PROJECT_NAME)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .orderQuantity(UPDATED_ORDER_QUANTITY)
            .esimatedBudget(UPDATED_ESIMATED_BUDGET)
            .finalTotal(UPDATED_FINAL_TOTAL);

        restProjectsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProjects.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProjects))
            )
            .andExpect(status().isOk());

        // Validate the Projects in the database
        List<Projects> projectsList = projectsRepository.findAll();
        assertThat(projectsList).hasSize(databaseSizeBeforeUpdate);
        Projects testProjects = projectsList.get(projectsList.size() - 1);
        assertThat(testProjects.getProjectName()).isEqualTo(UPDATED_PROJECT_NAME);
        assertThat(testProjects.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testProjects.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testProjects.getOrderQuantity()).isEqualTo(UPDATED_ORDER_QUANTITY);
        assertThat(testProjects.getEsimatedBudget()).isEqualTo(UPDATED_ESIMATED_BUDGET);
        assertThat(testProjects.getFinalTotal()).isEqualTo(UPDATED_FINAL_TOTAL);
    }

    @Test
    @Transactional
    void patchNonExistingProjects() throws Exception {
        int databaseSizeBeforeUpdate = projectsRepository.findAll().size();
        projects.setId(count.incrementAndGet());

        // Create the Projects
        ProjectsDTO projectsDTO = projectsMapper.toDto(projects);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, projectsDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(projectsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Projects in the database
        List<Projects> projectsList = projectsRepository.findAll();
        assertThat(projectsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProjects() throws Exception {
        int databaseSizeBeforeUpdate = projectsRepository.findAll().size();
        projects.setId(count.incrementAndGet());

        // Create the Projects
        ProjectsDTO projectsDTO = projectsMapper.toDto(projects);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(projectsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Projects in the database
        List<Projects> projectsList = projectsRepository.findAll();
        assertThat(projectsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProjects() throws Exception {
        int databaseSizeBeforeUpdate = projectsRepository.findAll().size();
        projects.setId(count.incrementAndGet());

        // Create the Projects
        ProjectsDTO projectsDTO = projectsMapper.toDto(projects);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectsMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(projectsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Projects in the database
        List<Projects> projectsList = projectsRepository.findAll();
        assertThat(projectsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProjects() throws Exception {
        // Initialize the database
        projectsRepository.saveAndFlush(projects);

        int databaseSizeBeforeDelete = projectsRepository.findAll().size();

        // Delete the projects
        restProjectsMockMvc
            .perform(delete(ENTITY_API_URL_ID, projects.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Projects> projectsList = projectsRepository.findAll();
        assertThat(projectsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
