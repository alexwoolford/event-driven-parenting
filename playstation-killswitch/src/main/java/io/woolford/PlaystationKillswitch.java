package io.woolford;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class PlaystationKillswitch {

    private final Logger logger = LoggerFactory.getLogger(PlaystationKillswitch.class);

    @Value("${fortinet.token}")
    String fortinetToken;

    @Value("${ps4.host}")
    String ps4Host;

    @KafkaListener(topics = "miles-grades-test", groupId = "playstation-killswitch")
    public void consume(String gradeRecordString) throws JsonProcessingException {

        GradeRecord gradeRecord = parseGradeRecordJson(gradeRecordString);
        logger.info(gradeRecord.toString());

        // if bad grade (i.e. not A or B), prevent PS4 from connecting to the internet
        if (!"AB".contains(gradeRecord.getProgressScore())){
            preventPs4FromConnectingToInternet();
        }
    }

    private GradeRecord parseGradeRecordJson(String gradeRecordString) throws JsonProcessingException {

        // parse the message from the `miles-grades` kafka topic into a POJO
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        GradeRecord gradeRecord = null;
        try {
            gradeRecord = mapper.readValue(gradeRecordString, GradeRecord.class);
        } catch (JsonMappingException e) {
            logger.error("Unable to parse: " + gradeRecordString);
            logger.error(e.getMessage());
        }

        return gradeRecord;
    }

    private void preventPs4FromConnectingToInternet() throws JsonProcessingException {

        Unirest.config().verifySsl(false);

        // build a JSON string like this: {"name":"deny-internet","member":[{"name":"deepthought"}]}
        Map<String, String> member = new HashMap<>();
        member.put("name", ps4Host);

        List<Map<String, String>> memberList = new ArrayList<>();
        memberList.add(member);

        Map<String, Object> firewallGroup = new HashMap<>();
        firewallGroup.put("name", "deny-internet");
        firewallGroup.put("member", memberList);

        String firewallGroupJson = new ObjectMapper().writeValueAsString(firewallGroup);

        // add the PS4 address to the deny-internet group
        HttpResponse<JsonNode> response = Unirest.put("https://10.0.1.1:443/api/v2/cmdb/firewall/addrgrp/deny-internet")
                .header("accept", "application/json")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .queryString("access_token", fortinetToken)
                .body(firewallGroupJson)
                .asJson();

        logger.info("added PS4 to deny-internet group");

    }
}
