package io.woolford;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class GradeParser {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public List<GradeRecord> parseGrades(String jsonGrades) throws JsonProcessingException {

        logger.info("parsing grade JSON");
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);;
        JsonNode gradesParentNode = mapper.readTree(jsonGrades);

        List<GradeRecord> gradeRecordList = new ArrayList<GradeRecord>();
        JsonNode termNodes = gradesParentNode.get(0).get("terms");

        for (JsonNode term : termNodes) {
            for (JsonNode course : term.get("courses")){
                for (JsonNode gradingTask : course.get("gradingTasks")){
                    GradeRecord gradeRecord = mapper.readValue(gradingTask.toString(), GradeRecord.class);

                    if (gradeRecord.getProgressPercent() != null){
                        gradeRecordList.add(gradeRecord);
                    }
                }
            }
        }
        return gradeRecordList;

    }
}
