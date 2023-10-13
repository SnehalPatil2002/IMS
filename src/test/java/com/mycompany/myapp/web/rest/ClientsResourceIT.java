package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Clients;
import com.mycompany.myapp.domain.SalesOrderd;
import com.mycompany.myapp.domain.enumeration.ClientType;
import com.mycompany.myapp.repository.ClientsRepository;
import com.mycompany.myapp.service.criteria.ClientsCriteria;
import com.mycompany.myapp.service.dto.ClientsDTO;
import com.mycompany.myapp.service.mapper.ClientsMapper;
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
 * Integration tests for the {@link ClientsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ClientsResourceIT {

    private static final String DEFAULT_SNAME = "AAAAAAAAAA";
    private static final String UPDATED_SNAME = "BBBBBBBBBB";

    private static final String DEFAULT_SEMAIL = "AAAAAAAAAA";
    private static final String UPDATED_SEMAIL = "BBBBBBBBBB";

    private static final Long DEFAULT_MOBILE_NO = 1L;
    private static final Long UPDATED_MOBILE_NO = 2L;
    private static final Long SMALLER_MOBILE_NO = 1L - 1L;

    private static final String DEFAULT_COMPANY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_COMPANY_NAME = "BBBBBBBBBB";

    private static final Long DEFAULT_COMPANY_CONTACT_NO = 1L;
    private static final Long UPDATED_COMPANY_CONTACT_NO = 2L;
    private static final Long SMALLER_COMPANY_CONTACT_NO = 1L - 1L;

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_PIN_CODE = "AAAAAAAAAA";
    private static final String UPDATED_PIN_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final ClientType DEFAULT_CLIENT_TYPE = ClientType.SUPPLIER;
    private static final ClientType UPDATED_CLIENT_TYPE = ClientType.CONSUMER;

    private static final String ENTITY_API_URL = "/api/clients";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ClientsRepository clientsRepository;

    @Autowired
    private ClientsMapper clientsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClientsMockMvc;

    private Clients clients;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Clients createEntity(EntityManager em) {
        Clients clients = new Clients()
            .sname(DEFAULT_SNAME)
            .semail(DEFAULT_SEMAIL)
            .mobileNo(DEFAULT_MOBILE_NO)
            .companyName(DEFAULT_COMPANY_NAME)
            .companyContactNo(DEFAULT_COMPANY_CONTACT_NO)
            .address(DEFAULT_ADDRESS)
            .pinCode(DEFAULT_PIN_CODE)
            .city(DEFAULT_CITY)
            .clientType(DEFAULT_CLIENT_TYPE);
        return clients;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Clients createUpdatedEntity(EntityManager em) {
        Clients clients = new Clients()
            .sname(UPDATED_SNAME)
            .semail(UPDATED_SEMAIL)
            .mobileNo(UPDATED_MOBILE_NO)
            .companyName(UPDATED_COMPANY_NAME)
            .companyContactNo(UPDATED_COMPANY_CONTACT_NO)
            .address(UPDATED_ADDRESS)
            .pinCode(UPDATED_PIN_CODE)
            .city(UPDATED_CITY)
            .clientType(UPDATED_CLIENT_TYPE);
        return clients;
    }

    @BeforeEach
    public void initTest() {
        clients = createEntity(em);
    }

    @Test
    @Transactional
    void createClients() throws Exception {
        int databaseSizeBeforeCreate = clientsRepository.findAll().size();
        // Create the Clients
        ClientsDTO clientsDTO = clientsMapper.toDto(clients);
        restClientsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientsDTO)))
            .andExpect(status().isCreated());

        // Validate the Clients in the database
        List<Clients> clientsList = clientsRepository.findAll();
        assertThat(clientsList).hasSize(databaseSizeBeforeCreate + 1);
        Clients testClients = clientsList.get(clientsList.size() - 1);
        assertThat(testClients.getSname()).isEqualTo(DEFAULT_SNAME);
        assertThat(testClients.getSemail()).isEqualTo(DEFAULT_SEMAIL);
        assertThat(testClients.getMobileNo()).isEqualTo(DEFAULT_MOBILE_NO);
        assertThat(testClients.getCompanyName()).isEqualTo(DEFAULT_COMPANY_NAME);
        assertThat(testClients.getCompanyContactNo()).isEqualTo(DEFAULT_COMPANY_CONTACT_NO);
        assertThat(testClients.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testClients.getPinCode()).isEqualTo(DEFAULT_PIN_CODE);
        assertThat(testClients.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testClients.getClientType()).isEqualTo(DEFAULT_CLIENT_TYPE);
    }

    @Test
    @Transactional
    void createClientsWithExistingId() throws Exception {
        // Create the Clients with an existing ID
        clients.setId(1L);
        ClientsDTO clientsDTO = clientsMapper.toDto(clients);

        int databaseSizeBeforeCreate = clientsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClientsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Clients in the database
        List<Clients> clientsList = clientsRepository.findAll();
        assertThat(clientsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllClients() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList
        restClientsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clients.getId().intValue())))
            .andExpect(jsonPath("$.[*].sname").value(hasItem(DEFAULT_SNAME)))
            .andExpect(jsonPath("$.[*].semail").value(hasItem(DEFAULT_SEMAIL)))
            .andExpect(jsonPath("$.[*].mobileNo").value(hasItem(DEFAULT_MOBILE_NO.intValue())))
            .andExpect(jsonPath("$.[*].companyName").value(hasItem(DEFAULT_COMPANY_NAME)))
            .andExpect(jsonPath("$.[*].companyContactNo").value(hasItem(DEFAULT_COMPANY_CONTACT_NO.intValue())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].pinCode").value(hasItem(DEFAULT_PIN_CODE)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].clientType").value(hasItem(DEFAULT_CLIENT_TYPE.toString())));
    }

    @Test
    @Transactional
    void getClients() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get the clients
        restClientsMockMvc
            .perform(get(ENTITY_API_URL_ID, clients.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(clients.getId().intValue()))
            .andExpect(jsonPath("$.sname").value(DEFAULT_SNAME))
            .andExpect(jsonPath("$.semail").value(DEFAULT_SEMAIL))
            .andExpect(jsonPath("$.mobileNo").value(DEFAULT_MOBILE_NO.intValue()))
            .andExpect(jsonPath("$.companyName").value(DEFAULT_COMPANY_NAME))
            .andExpect(jsonPath("$.companyContactNo").value(DEFAULT_COMPANY_CONTACT_NO.intValue()))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.pinCode").value(DEFAULT_PIN_CODE))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY))
            .andExpect(jsonPath("$.clientType").value(DEFAULT_CLIENT_TYPE.toString()));
    }

    @Test
    @Transactional
    void getClientsByIdFiltering() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        Long id = clients.getId();

        defaultClientsShouldBeFound("id.equals=" + id);
        defaultClientsShouldNotBeFound("id.notEquals=" + id);

        defaultClientsShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultClientsShouldNotBeFound("id.greaterThan=" + id);

        defaultClientsShouldBeFound("id.lessThanOrEqual=" + id);
        defaultClientsShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllClientsBySnameIsEqualToSomething() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where sname equals to DEFAULT_SNAME
        defaultClientsShouldBeFound("sname.equals=" + DEFAULT_SNAME);

        // Get all the clientsList where sname equals to UPDATED_SNAME
        defaultClientsShouldNotBeFound("sname.equals=" + UPDATED_SNAME);
    }

    @Test
    @Transactional
    void getAllClientsBySnameIsInShouldWork() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where sname in DEFAULT_SNAME or UPDATED_SNAME
        defaultClientsShouldBeFound("sname.in=" + DEFAULT_SNAME + "," + UPDATED_SNAME);

        // Get all the clientsList where sname equals to UPDATED_SNAME
        defaultClientsShouldNotBeFound("sname.in=" + UPDATED_SNAME);
    }

    @Test
    @Transactional
    void getAllClientsBySnameIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where sname is not null
        defaultClientsShouldBeFound("sname.specified=true");

        // Get all the clientsList where sname is null
        defaultClientsShouldNotBeFound("sname.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsBySnameContainsSomething() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where sname contains DEFAULT_SNAME
        defaultClientsShouldBeFound("sname.contains=" + DEFAULT_SNAME);

        // Get all the clientsList where sname contains UPDATED_SNAME
        defaultClientsShouldNotBeFound("sname.contains=" + UPDATED_SNAME);
    }

    @Test
    @Transactional
    void getAllClientsBySnameNotContainsSomething() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where sname does not contain DEFAULT_SNAME
        defaultClientsShouldNotBeFound("sname.doesNotContain=" + DEFAULT_SNAME);

        // Get all the clientsList where sname does not contain UPDATED_SNAME
        defaultClientsShouldBeFound("sname.doesNotContain=" + UPDATED_SNAME);
    }

    @Test
    @Transactional
    void getAllClientsBySemailIsEqualToSomething() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where semail equals to DEFAULT_SEMAIL
        defaultClientsShouldBeFound("semail.equals=" + DEFAULT_SEMAIL);

        // Get all the clientsList where semail equals to UPDATED_SEMAIL
        defaultClientsShouldNotBeFound("semail.equals=" + UPDATED_SEMAIL);
    }

    @Test
    @Transactional
    void getAllClientsBySemailIsInShouldWork() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where semail in DEFAULT_SEMAIL or UPDATED_SEMAIL
        defaultClientsShouldBeFound("semail.in=" + DEFAULT_SEMAIL + "," + UPDATED_SEMAIL);

        // Get all the clientsList where semail equals to UPDATED_SEMAIL
        defaultClientsShouldNotBeFound("semail.in=" + UPDATED_SEMAIL);
    }

    @Test
    @Transactional
    void getAllClientsBySemailIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where semail is not null
        defaultClientsShouldBeFound("semail.specified=true");

        // Get all the clientsList where semail is null
        defaultClientsShouldNotBeFound("semail.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsBySemailContainsSomething() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where semail contains DEFAULT_SEMAIL
        defaultClientsShouldBeFound("semail.contains=" + DEFAULT_SEMAIL);

        // Get all the clientsList where semail contains UPDATED_SEMAIL
        defaultClientsShouldNotBeFound("semail.contains=" + UPDATED_SEMAIL);
    }

    @Test
    @Transactional
    void getAllClientsBySemailNotContainsSomething() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where semail does not contain DEFAULT_SEMAIL
        defaultClientsShouldNotBeFound("semail.doesNotContain=" + DEFAULT_SEMAIL);

        // Get all the clientsList where semail does not contain UPDATED_SEMAIL
        defaultClientsShouldBeFound("semail.doesNotContain=" + UPDATED_SEMAIL);
    }

    @Test
    @Transactional
    void getAllClientsByMobileNoIsEqualToSomething() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where mobileNo equals to DEFAULT_MOBILE_NO
        defaultClientsShouldBeFound("mobileNo.equals=" + DEFAULT_MOBILE_NO);

        // Get all the clientsList where mobileNo equals to UPDATED_MOBILE_NO
        defaultClientsShouldNotBeFound("mobileNo.equals=" + UPDATED_MOBILE_NO);
    }

    @Test
    @Transactional
    void getAllClientsByMobileNoIsInShouldWork() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where mobileNo in DEFAULT_MOBILE_NO or UPDATED_MOBILE_NO
        defaultClientsShouldBeFound("mobileNo.in=" + DEFAULT_MOBILE_NO + "," + UPDATED_MOBILE_NO);

        // Get all the clientsList where mobileNo equals to UPDATED_MOBILE_NO
        defaultClientsShouldNotBeFound("mobileNo.in=" + UPDATED_MOBILE_NO);
    }

    @Test
    @Transactional
    void getAllClientsByMobileNoIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where mobileNo is not null
        defaultClientsShouldBeFound("mobileNo.specified=true");

        // Get all the clientsList where mobileNo is null
        defaultClientsShouldNotBeFound("mobileNo.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByMobileNoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where mobileNo is greater than or equal to DEFAULT_MOBILE_NO
        defaultClientsShouldBeFound("mobileNo.greaterThanOrEqual=" + DEFAULT_MOBILE_NO);

        // Get all the clientsList where mobileNo is greater than or equal to UPDATED_MOBILE_NO
        defaultClientsShouldNotBeFound("mobileNo.greaterThanOrEqual=" + UPDATED_MOBILE_NO);
    }

    @Test
    @Transactional
    void getAllClientsByMobileNoIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where mobileNo is less than or equal to DEFAULT_MOBILE_NO
        defaultClientsShouldBeFound("mobileNo.lessThanOrEqual=" + DEFAULT_MOBILE_NO);

        // Get all the clientsList where mobileNo is less than or equal to SMALLER_MOBILE_NO
        defaultClientsShouldNotBeFound("mobileNo.lessThanOrEqual=" + SMALLER_MOBILE_NO);
    }

    @Test
    @Transactional
    void getAllClientsByMobileNoIsLessThanSomething() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where mobileNo is less than DEFAULT_MOBILE_NO
        defaultClientsShouldNotBeFound("mobileNo.lessThan=" + DEFAULT_MOBILE_NO);

        // Get all the clientsList where mobileNo is less than UPDATED_MOBILE_NO
        defaultClientsShouldBeFound("mobileNo.lessThan=" + UPDATED_MOBILE_NO);
    }

    @Test
    @Transactional
    void getAllClientsByMobileNoIsGreaterThanSomething() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where mobileNo is greater than DEFAULT_MOBILE_NO
        defaultClientsShouldNotBeFound("mobileNo.greaterThan=" + DEFAULT_MOBILE_NO);

        // Get all the clientsList where mobileNo is greater than SMALLER_MOBILE_NO
        defaultClientsShouldBeFound("mobileNo.greaterThan=" + SMALLER_MOBILE_NO);
    }

    @Test
    @Transactional
    void getAllClientsByCompanyNameIsEqualToSomething() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where companyName equals to DEFAULT_COMPANY_NAME
        defaultClientsShouldBeFound("companyName.equals=" + DEFAULT_COMPANY_NAME);

        // Get all the clientsList where companyName equals to UPDATED_COMPANY_NAME
        defaultClientsShouldNotBeFound("companyName.equals=" + UPDATED_COMPANY_NAME);
    }

    @Test
    @Transactional
    void getAllClientsByCompanyNameIsInShouldWork() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where companyName in DEFAULT_COMPANY_NAME or UPDATED_COMPANY_NAME
        defaultClientsShouldBeFound("companyName.in=" + DEFAULT_COMPANY_NAME + "," + UPDATED_COMPANY_NAME);

        // Get all the clientsList where companyName equals to UPDATED_COMPANY_NAME
        defaultClientsShouldNotBeFound("companyName.in=" + UPDATED_COMPANY_NAME);
    }

    @Test
    @Transactional
    void getAllClientsByCompanyNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where companyName is not null
        defaultClientsShouldBeFound("companyName.specified=true");

        // Get all the clientsList where companyName is null
        defaultClientsShouldNotBeFound("companyName.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByCompanyNameContainsSomething() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where companyName contains DEFAULT_COMPANY_NAME
        defaultClientsShouldBeFound("companyName.contains=" + DEFAULT_COMPANY_NAME);

        // Get all the clientsList where companyName contains UPDATED_COMPANY_NAME
        defaultClientsShouldNotBeFound("companyName.contains=" + UPDATED_COMPANY_NAME);
    }

    @Test
    @Transactional
    void getAllClientsByCompanyNameNotContainsSomething() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where companyName does not contain DEFAULT_COMPANY_NAME
        defaultClientsShouldNotBeFound("companyName.doesNotContain=" + DEFAULT_COMPANY_NAME);

        // Get all the clientsList where companyName does not contain UPDATED_COMPANY_NAME
        defaultClientsShouldBeFound("companyName.doesNotContain=" + UPDATED_COMPANY_NAME);
    }

    @Test
    @Transactional
    void getAllClientsByCompanyContactNoIsEqualToSomething() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where companyContactNo equals to DEFAULT_COMPANY_CONTACT_NO
        defaultClientsShouldBeFound("companyContactNo.equals=" + DEFAULT_COMPANY_CONTACT_NO);

        // Get all the clientsList where companyContactNo equals to UPDATED_COMPANY_CONTACT_NO
        defaultClientsShouldNotBeFound("companyContactNo.equals=" + UPDATED_COMPANY_CONTACT_NO);
    }

    @Test
    @Transactional
    void getAllClientsByCompanyContactNoIsInShouldWork() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where companyContactNo in DEFAULT_COMPANY_CONTACT_NO or UPDATED_COMPANY_CONTACT_NO
        defaultClientsShouldBeFound("companyContactNo.in=" + DEFAULT_COMPANY_CONTACT_NO + "," + UPDATED_COMPANY_CONTACT_NO);

        // Get all the clientsList where companyContactNo equals to UPDATED_COMPANY_CONTACT_NO
        defaultClientsShouldNotBeFound("companyContactNo.in=" + UPDATED_COMPANY_CONTACT_NO);
    }

    @Test
    @Transactional
    void getAllClientsByCompanyContactNoIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where companyContactNo is not null
        defaultClientsShouldBeFound("companyContactNo.specified=true");

        // Get all the clientsList where companyContactNo is null
        defaultClientsShouldNotBeFound("companyContactNo.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByCompanyContactNoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where companyContactNo is greater than or equal to DEFAULT_COMPANY_CONTACT_NO
        defaultClientsShouldBeFound("companyContactNo.greaterThanOrEqual=" + DEFAULT_COMPANY_CONTACT_NO);

        // Get all the clientsList where companyContactNo is greater than or equal to UPDATED_COMPANY_CONTACT_NO
        defaultClientsShouldNotBeFound("companyContactNo.greaterThanOrEqual=" + UPDATED_COMPANY_CONTACT_NO);
    }

    @Test
    @Transactional
    void getAllClientsByCompanyContactNoIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where companyContactNo is less than or equal to DEFAULT_COMPANY_CONTACT_NO
        defaultClientsShouldBeFound("companyContactNo.lessThanOrEqual=" + DEFAULT_COMPANY_CONTACT_NO);

        // Get all the clientsList where companyContactNo is less than or equal to SMALLER_COMPANY_CONTACT_NO
        defaultClientsShouldNotBeFound("companyContactNo.lessThanOrEqual=" + SMALLER_COMPANY_CONTACT_NO);
    }

    @Test
    @Transactional
    void getAllClientsByCompanyContactNoIsLessThanSomething() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where companyContactNo is less than DEFAULT_COMPANY_CONTACT_NO
        defaultClientsShouldNotBeFound("companyContactNo.lessThan=" + DEFAULT_COMPANY_CONTACT_NO);

        // Get all the clientsList where companyContactNo is less than UPDATED_COMPANY_CONTACT_NO
        defaultClientsShouldBeFound("companyContactNo.lessThan=" + UPDATED_COMPANY_CONTACT_NO);
    }

    @Test
    @Transactional
    void getAllClientsByCompanyContactNoIsGreaterThanSomething() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where companyContactNo is greater than DEFAULT_COMPANY_CONTACT_NO
        defaultClientsShouldNotBeFound("companyContactNo.greaterThan=" + DEFAULT_COMPANY_CONTACT_NO);

        // Get all the clientsList where companyContactNo is greater than SMALLER_COMPANY_CONTACT_NO
        defaultClientsShouldBeFound("companyContactNo.greaterThan=" + SMALLER_COMPANY_CONTACT_NO);
    }

    @Test
    @Transactional
    void getAllClientsByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where address equals to DEFAULT_ADDRESS
        defaultClientsShouldBeFound("address.equals=" + DEFAULT_ADDRESS);

        // Get all the clientsList where address equals to UPDATED_ADDRESS
        defaultClientsShouldNotBeFound("address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllClientsByAddressIsInShouldWork() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where address in DEFAULT_ADDRESS or UPDATED_ADDRESS
        defaultClientsShouldBeFound("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS);

        // Get all the clientsList where address equals to UPDATED_ADDRESS
        defaultClientsShouldNotBeFound("address.in=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllClientsByAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where address is not null
        defaultClientsShouldBeFound("address.specified=true");

        // Get all the clientsList where address is null
        defaultClientsShouldNotBeFound("address.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByAddressContainsSomething() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where address contains DEFAULT_ADDRESS
        defaultClientsShouldBeFound("address.contains=" + DEFAULT_ADDRESS);

        // Get all the clientsList where address contains UPDATED_ADDRESS
        defaultClientsShouldNotBeFound("address.contains=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllClientsByAddressNotContainsSomething() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where address does not contain DEFAULT_ADDRESS
        defaultClientsShouldNotBeFound("address.doesNotContain=" + DEFAULT_ADDRESS);

        // Get all the clientsList where address does not contain UPDATED_ADDRESS
        defaultClientsShouldBeFound("address.doesNotContain=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllClientsByPinCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where pinCode equals to DEFAULT_PIN_CODE
        defaultClientsShouldBeFound("pinCode.equals=" + DEFAULT_PIN_CODE);

        // Get all the clientsList where pinCode equals to UPDATED_PIN_CODE
        defaultClientsShouldNotBeFound("pinCode.equals=" + UPDATED_PIN_CODE);
    }

    @Test
    @Transactional
    void getAllClientsByPinCodeIsInShouldWork() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where pinCode in DEFAULT_PIN_CODE or UPDATED_PIN_CODE
        defaultClientsShouldBeFound("pinCode.in=" + DEFAULT_PIN_CODE + "," + UPDATED_PIN_CODE);

        // Get all the clientsList where pinCode equals to UPDATED_PIN_CODE
        defaultClientsShouldNotBeFound("pinCode.in=" + UPDATED_PIN_CODE);
    }

    @Test
    @Transactional
    void getAllClientsByPinCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where pinCode is not null
        defaultClientsShouldBeFound("pinCode.specified=true");

        // Get all the clientsList where pinCode is null
        defaultClientsShouldNotBeFound("pinCode.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByPinCodeContainsSomething() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where pinCode contains DEFAULT_PIN_CODE
        defaultClientsShouldBeFound("pinCode.contains=" + DEFAULT_PIN_CODE);

        // Get all the clientsList where pinCode contains UPDATED_PIN_CODE
        defaultClientsShouldNotBeFound("pinCode.contains=" + UPDATED_PIN_CODE);
    }

    @Test
    @Transactional
    void getAllClientsByPinCodeNotContainsSomething() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where pinCode does not contain DEFAULT_PIN_CODE
        defaultClientsShouldNotBeFound("pinCode.doesNotContain=" + DEFAULT_PIN_CODE);

        // Get all the clientsList where pinCode does not contain UPDATED_PIN_CODE
        defaultClientsShouldBeFound("pinCode.doesNotContain=" + UPDATED_PIN_CODE);
    }

    @Test
    @Transactional
    void getAllClientsByCityIsEqualToSomething() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where city equals to DEFAULT_CITY
        defaultClientsShouldBeFound("city.equals=" + DEFAULT_CITY);

        // Get all the clientsList where city equals to UPDATED_CITY
        defaultClientsShouldNotBeFound("city.equals=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllClientsByCityIsInShouldWork() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where city in DEFAULT_CITY or UPDATED_CITY
        defaultClientsShouldBeFound("city.in=" + DEFAULT_CITY + "," + UPDATED_CITY);

        // Get all the clientsList where city equals to UPDATED_CITY
        defaultClientsShouldNotBeFound("city.in=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllClientsByCityIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where city is not null
        defaultClientsShouldBeFound("city.specified=true");

        // Get all the clientsList where city is null
        defaultClientsShouldNotBeFound("city.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByCityContainsSomething() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where city contains DEFAULT_CITY
        defaultClientsShouldBeFound("city.contains=" + DEFAULT_CITY);

        // Get all the clientsList where city contains UPDATED_CITY
        defaultClientsShouldNotBeFound("city.contains=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllClientsByCityNotContainsSomething() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where city does not contain DEFAULT_CITY
        defaultClientsShouldNotBeFound("city.doesNotContain=" + DEFAULT_CITY);

        // Get all the clientsList where city does not contain UPDATED_CITY
        defaultClientsShouldBeFound("city.doesNotContain=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllClientsByClientTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where clientType equals to DEFAULT_CLIENT_TYPE
        defaultClientsShouldBeFound("clientType.equals=" + DEFAULT_CLIENT_TYPE);

        // Get all the clientsList where clientType equals to UPDATED_CLIENT_TYPE
        defaultClientsShouldNotBeFound("clientType.equals=" + UPDATED_CLIENT_TYPE);
    }

    @Test
    @Transactional
    void getAllClientsByClientTypeIsInShouldWork() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where clientType in DEFAULT_CLIENT_TYPE or UPDATED_CLIENT_TYPE
        defaultClientsShouldBeFound("clientType.in=" + DEFAULT_CLIENT_TYPE + "," + UPDATED_CLIENT_TYPE);

        // Get all the clientsList where clientType equals to UPDATED_CLIENT_TYPE
        defaultClientsShouldNotBeFound("clientType.in=" + UPDATED_CLIENT_TYPE);
    }

    @Test
    @Transactional
    void getAllClientsByClientTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        // Get all the clientsList where clientType is not null
        defaultClientsShouldBeFound("clientType.specified=true");

        // Get all the clientsList where clientType is null
        defaultClientsShouldNotBeFound("clientType.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsBySalesOrderdIsEqualToSomething() throws Exception {
        SalesOrderd salesOrderd;
        if (TestUtil.findAll(em, SalesOrderd.class).isEmpty()) {
            clientsRepository.saveAndFlush(clients);
            salesOrderd = SalesOrderdResourceIT.createEntity(em);
        } else {
            salesOrderd = TestUtil.findAll(em, SalesOrderd.class).get(0);
        }
        em.persist(salesOrderd);
        em.flush();
        clients.addSalesOrderd(salesOrderd);
        clientsRepository.saveAndFlush(clients);
        Long salesOrderdId = salesOrderd.getId();

        // Get all the clientsList where salesOrderd equals to salesOrderdId
        defaultClientsShouldBeFound("salesOrderdId.equals=" + salesOrderdId);

        // Get all the clientsList where salesOrderd equals to (salesOrderdId + 1)
        defaultClientsShouldNotBeFound("salesOrderdId.equals=" + (salesOrderdId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultClientsShouldBeFound(String filter) throws Exception {
        restClientsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clients.getId().intValue())))
            .andExpect(jsonPath("$.[*].sname").value(hasItem(DEFAULT_SNAME)))
            .andExpect(jsonPath("$.[*].semail").value(hasItem(DEFAULT_SEMAIL)))
            .andExpect(jsonPath("$.[*].mobileNo").value(hasItem(DEFAULT_MOBILE_NO.intValue())))
            .andExpect(jsonPath("$.[*].companyName").value(hasItem(DEFAULT_COMPANY_NAME)))
            .andExpect(jsonPath("$.[*].companyContactNo").value(hasItem(DEFAULT_COMPANY_CONTACT_NO.intValue())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].pinCode").value(hasItem(DEFAULT_PIN_CODE)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].clientType").value(hasItem(DEFAULT_CLIENT_TYPE.toString())));

        // Check, that the count call also returns 1
        restClientsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultClientsShouldNotBeFound(String filter) throws Exception {
        restClientsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restClientsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingClients() throws Exception {
        // Get the clients
        restClientsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingClients() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        int databaseSizeBeforeUpdate = clientsRepository.findAll().size();

        // Update the clients
        Clients updatedClients = clientsRepository.findById(clients.getId()).get();
        // Disconnect from session so that the updates on updatedClients are not directly saved in db
        em.detach(updatedClients);
        updatedClients
            .sname(UPDATED_SNAME)
            .semail(UPDATED_SEMAIL)
            .mobileNo(UPDATED_MOBILE_NO)
            .companyName(UPDATED_COMPANY_NAME)
            .companyContactNo(UPDATED_COMPANY_CONTACT_NO)
            .address(UPDATED_ADDRESS)
            .pinCode(UPDATED_PIN_CODE)
            .city(UPDATED_CITY)
            .clientType(UPDATED_CLIENT_TYPE);
        ClientsDTO clientsDTO = clientsMapper.toDto(updatedClients);

        restClientsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clientsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientsDTO))
            )
            .andExpect(status().isOk());

        // Validate the Clients in the database
        List<Clients> clientsList = clientsRepository.findAll();
        assertThat(clientsList).hasSize(databaseSizeBeforeUpdate);
        Clients testClients = clientsList.get(clientsList.size() - 1);
        assertThat(testClients.getSname()).isEqualTo(UPDATED_SNAME);
        assertThat(testClients.getSemail()).isEqualTo(UPDATED_SEMAIL);
        assertThat(testClients.getMobileNo()).isEqualTo(UPDATED_MOBILE_NO);
        assertThat(testClients.getCompanyName()).isEqualTo(UPDATED_COMPANY_NAME);
        assertThat(testClients.getCompanyContactNo()).isEqualTo(UPDATED_COMPANY_CONTACT_NO);
        assertThat(testClients.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testClients.getPinCode()).isEqualTo(UPDATED_PIN_CODE);
        assertThat(testClients.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testClients.getClientType()).isEqualTo(UPDATED_CLIENT_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingClients() throws Exception {
        int databaseSizeBeforeUpdate = clientsRepository.findAll().size();
        clients.setId(count.incrementAndGet());

        // Create the Clients
        ClientsDTO clientsDTO = clientsMapper.toDto(clients);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clientsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Clients in the database
        List<Clients> clientsList = clientsRepository.findAll();
        assertThat(clientsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClients() throws Exception {
        int databaseSizeBeforeUpdate = clientsRepository.findAll().size();
        clients.setId(count.incrementAndGet());

        // Create the Clients
        ClientsDTO clientsDTO = clientsMapper.toDto(clients);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Clients in the database
        List<Clients> clientsList = clientsRepository.findAll();
        assertThat(clientsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClients() throws Exception {
        int databaseSizeBeforeUpdate = clientsRepository.findAll().size();
        clients.setId(count.incrementAndGet());

        // Create the Clients
        ClientsDTO clientsDTO = clientsMapper.toDto(clients);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Clients in the database
        List<Clients> clientsList = clientsRepository.findAll();
        assertThat(clientsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClientsWithPatch() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        int databaseSizeBeforeUpdate = clientsRepository.findAll().size();

        // Update the clients using partial update
        Clients partialUpdatedClients = new Clients();
        partialUpdatedClients.setId(clients.getId());

        partialUpdatedClients
            .sname(UPDATED_SNAME)
            .mobileNo(UPDATED_MOBILE_NO)
            .companyName(UPDATED_COMPANY_NAME)
            .companyContactNo(UPDATED_COMPANY_CONTACT_NO)
            .address(UPDATED_ADDRESS)
            .pinCode(UPDATED_PIN_CODE)
            .clientType(UPDATED_CLIENT_TYPE);

        restClientsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClients.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClients))
            )
            .andExpect(status().isOk());

        // Validate the Clients in the database
        List<Clients> clientsList = clientsRepository.findAll();
        assertThat(clientsList).hasSize(databaseSizeBeforeUpdate);
        Clients testClients = clientsList.get(clientsList.size() - 1);
        assertThat(testClients.getSname()).isEqualTo(UPDATED_SNAME);
        assertThat(testClients.getSemail()).isEqualTo(DEFAULT_SEMAIL);
        assertThat(testClients.getMobileNo()).isEqualTo(UPDATED_MOBILE_NO);
        assertThat(testClients.getCompanyName()).isEqualTo(UPDATED_COMPANY_NAME);
        assertThat(testClients.getCompanyContactNo()).isEqualTo(UPDATED_COMPANY_CONTACT_NO);
        assertThat(testClients.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testClients.getPinCode()).isEqualTo(UPDATED_PIN_CODE);
        assertThat(testClients.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testClients.getClientType()).isEqualTo(UPDATED_CLIENT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateClientsWithPatch() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        int databaseSizeBeforeUpdate = clientsRepository.findAll().size();

        // Update the clients using partial update
        Clients partialUpdatedClients = new Clients();
        partialUpdatedClients.setId(clients.getId());

        partialUpdatedClients
            .sname(UPDATED_SNAME)
            .semail(UPDATED_SEMAIL)
            .mobileNo(UPDATED_MOBILE_NO)
            .companyName(UPDATED_COMPANY_NAME)
            .companyContactNo(UPDATED_COMPANY_CONTACT_NO)
            .address(UPDATED_ADDRESS)
            .pinCode(UPDATED_PIN_CODE)
            .city(UPDATED_CITY)
            .clientType(UPDATED_CLIENT_TYPE);

        restClientsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClients.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClients))
            )
            .andExpect(status().isOk());

        // Validate the Clients in the database
        List<Clients> clientsList = clientsRepository.findAll();
        assertThat(clientsList).hasSize(databaseSizeBeforeUpdate);
        Clients testClients = clientsList.get(clientsList.size() - 1);
        assertThat(testClients.getSname()).isEqualTo(UPDATED_SNAME);
        assertThat(testClients.getSemail()).isEqualTo(UPDATED_SEMAIL);
        assertThat(testClients.getMobileNo()).isEqualTo(UPDATED_MOBILE_NO);
        assertThat(testClients.getCompanyName()).isEqualTo(UPDATED_COMPANY_NAME);
        assertThat(testClients.getCompanyContactNo()).isEqualTo(UPDATED_COMPANY_CONTACT_NO);
        assertThat(testClients.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testClients.getPinCode()).isEqualTo(UPDATED_PIN_CODE);
        assertThat(testClients.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testClients.getClientType()).isEqualTo(UPDATED_CLIENT_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingClients() throws Exception {
        int databaseSizeBeforeUpdate = clientsRepository.findAll().size();
        clients.setId(count.incrementAndGet());

        // Create the Clients
        ClientsDTO clientsDTO = clientsMapper.toDto(clients);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, clientsDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(clientsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Clients in the database
        List<Clients> clientsList = clientsRepository.findAll();
        assertThat(clientsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClients() throws Exception {
        int databaseSizeBeforeUpdate = clientsRepository.findAll().size();
        clients.setId(count.incrementAndGet());

        // Create the Clients
        ClientsDTO clientsDTO = clientsMapper.toDto(clients);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(clientsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Clients in the database
        List<Clients> clientsList = clientsRepository.findAll();
        assertThat(clientsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClients() throws Exception {
        int databaseSizeBeforeUpdate = clientsRepository.findAll().size();
        clients.setId(count.incrementAndGet());

        // Create the Clients
        ClientsDTO clientsDTO = clientsMapper.toDto(clients);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientsMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(clientsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Clients in the database
        List<Clients> clientsList = clientsRepository.findAll();
        assertThat(clientsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClients() throws Exception {
        // Initialize the database
        clientsRepository.saveAndFlush(clients);

        int databaseSizeBeforeDelete = clientsRepository.findAll().size();

        // Delete the clients
        restClientsMockMvc
            .perform(delete(ENTITY_API_URL_ID, clients.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Clients> clientsList = clientsRepository.findAll();
        assertThat(clientsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
