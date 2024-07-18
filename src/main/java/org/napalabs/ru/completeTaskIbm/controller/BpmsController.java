package org.napalabs.ru.completeTaskIbm.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.napalabs.ru.completeTaskIbm.service.BpmsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bpms")
public class BpmsController {

    private final BpmsService bpmsService;

    public BpmsController(BpmsService bpmsService) {
        this.bpmsService = bpmsService;
    }

    @GetMapping("/completeTask")
    public String completeTask(String instanceId) throws JsonProcessingException {
        String taskId = bpmsService.getActiveTaskId(instanceId);
        bpmsService.completeTask(taskId);
        return "Task " + taskId + " completed successfully";
    }
}