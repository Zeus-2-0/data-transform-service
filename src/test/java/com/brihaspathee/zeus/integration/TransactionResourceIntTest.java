package com.brihaspathee.zeus.integration;

import com.brihaspathee.zeus.dto.account.RawTransactionDto;
import com.brihaspathee.zeus.security.model.UserDto;
import com.brihaspathee.zeus.test.BuildTestData;
import com.brihaspathee.zeus.test.TestClass;
import com.brihaspathee.zeus.web.model.DataTransformationDto;
import com.brihaspathee.zeus.web.model.TestRawTransactionDtoRequest;
import com.brihaspathee.zeus.web.response.ZeusApiResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 13, March 2023
 * Time: 9:31 PM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.integration
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransactionResourceIntTest {

    /**
     * Object mapper to read the file and convert it to an object
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Rest template to call the api endpoint
     */
    @Autowired
    private TestRestTemplate testRestTemplate;

    /**
     * The file that contains the test data
     */
    @Value("classpath:com/brihaspathee/zeus/integration/TransactionResourceIntTest.json")
    Resource resourceFile;

    /**
     * The instance of the class that helps to build the input data
     */
    private TestClass<TestRawTransactionDtoRequest> rawTransactionDtoRequestTestClass;

    /**
     * The instance of the class that helps to build the data
     */
    @Autowired
    private BuildTestData<TestRawTransactionDtoRequest> buildTestData;

    /**
     * The list of test requests
     */
    private List<TestRawTransactionDtoRequest> requests = new ArrayList<>();

    /**
     * The setup method is executed before each test method is executed
     * @param testInfo
     * @throws IOException
     */
    @BeforeEach
    void setUp(TestInfo testInfo) throws IOException {

        // Read the file information and convert to test class object
        rawTransactionDtoRequestTestClass = objectMapper.readValue(resourceFile.getFile(), new TypeReference<TestClass<TestRawTransactionDtoRequest>>() {});

        // Build the test data for the test method that is to be executed
        this.requests = buildTestData.buildData(testInfo.getTestMethod().get().getName(),this.rawTransactionDtoRequestTestClass);
    }

    /**
     * This method tests process transaction method
     * @param repetitionInfo
     */
    @RepeatedTest(1)
    @Order(1)
    void testProcessTransaction(RepetitionInfo repetitionInfo){

        log.info("Current Repetition:{}", repetitionInfo.getCurrentRepetition());

        // Retrieve the raw transaction data dto span request for the repetition
        TestRawTransactionDtoRequest rawTransactionDtoRequest = requests.get(repetitionInfo.getCurrentRepetition() - 1);
        RawTransactionDto rawTransactionDto = rawTransactionDtoRequest.getRawTransactionDto();
        DataTransformationDto expectedDataTransformationDto = rawTransactionDtoRequest.getDataTransformationDto();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RawTransactionDto> httpEntity = new HttpEntity<>(rawTransactionDto, headers);
        String uri = "/api/v1/data-transform/false";
        // Call the API Endpoint to process the transaction
        ResponseEntity<ZeusApiResponse> responseEntity = testRestTemplate
                .postForEntity(uri,httpEntity, ZeusApiResponse.class);
        ZeusApiResponse apiResponse = responseEntity.getBody();
        // Get the data transformation dto object
        DataTransformationDto actualDataTransformationDto =
                objectMapper.convertValue(apiResponse.getResponse(), DataTransformationDto.class);
        log.info("Expected Data Transformation Dto:{}", expectedDataTransformationDto);
        log.info("Actual Data Transformation Dto:{}", actualDataTransformationDto);
        LocalDate actual = actualDataTransformationDto.getTransactionDto().getTransactionDetail().getMaintenanceEffectiveDate();
        LocalDate expected = expectedDataTransformationDto.getTransactionDto().getTransactionDetail().getMaintenanceEffectiveDate();

        assertEquals(expected, actual);
    }

}
