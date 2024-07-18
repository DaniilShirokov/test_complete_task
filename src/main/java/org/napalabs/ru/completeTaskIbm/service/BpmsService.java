package org.napalabs.ru.completeTaskIbm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@Service
public class BpmsService {

    @Value("${bpms.url}")
    private String bpmsUrl;

    @Value("${bpms.username}")
    private String username;

    @Value("${bpms.password}")
    private String password;

    private final RestTemplate restTemplate;

    public BpmsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getActiveTaskId(String instanceId) throws JsonProcessingException {
        String url = String.format("%s/rest/bpm/wle/v1/process/%s?/taskSummary/Active", bpmsUrl, instanceId);
        HttpHeaders headers = createHeaders(username, password);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        String taskId = parseTaskIdFromResponse(response.getBody());
        return taskId;
    }

    public void completeTask(String taskId) {
        String url = String.format("%s/rest/bpm/wle/v1/task/%s?action=complete&parts=all", bpmsUrl, taskId);
        HttpHeaders headers = createHeaders(username, password);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
    }

    private HttpHeaders createHeaders(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        return headers;
    }

    private String parseTaskIdFromResponse(String responseBody) throws JsonProcessingException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(responseBody);
            return node.get("data").get("tasks").get(0).get("tkiid").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}