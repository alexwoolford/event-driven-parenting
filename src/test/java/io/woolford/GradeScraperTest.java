package io.woolford;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class GradeScraperTest {

    @Test
    public void gradeScraperTest() throws IOException {

        String jsonGrades = new String(Files.readAllBytes(Paths.get("src/test/resources/example1.json")));

        GradeParser gradeParser = new GradeParser();
        List<GradeRecord> gradeRecordList = gradeParser.parseGrades(jsonGrades);

        assert(gradeRecordList.size() == 8);
    }

}
