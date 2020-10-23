package master.thesis.ssr;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

@Controller
public class RestController {

    private static final int SIZE = 10000;

    @RequestMapping(path = "/download", method = RequestMethod.GET)
    public ResponseEntity<Resource> download() throws IOException {
        String stringPath = getClass().getClassLoader().getResource("polyfills.js").getPath();
        File file = new File(stringPath);
        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        return ResponseEntity.ok()
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @RequestMapping(path = "/microserviceSeries", method = RequestMethod.GET)
    public FinalResults generateAndCallNextMs() {
        AllResults allResults = new AllResults(generate());
        HttpEntity<AllResults> allResultsHttpEntity = new HttpEntity<>(allResults);
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        FinalResults finalResults = restTemplate.exchange("http://env-microservice-sort.unicloud.pl/sort", HttpMethod.GET, allResultsHttpEntity, FinalResults.class).getBody();
        return finalResults;
    }

    @RequestMapping(path = "/sort", method = RequestMethod.GET)
    public FinalResults justSort(@RequestBody AllResults allResults) {
        int[] sortedResults = sort(allResults);
        return new FinalResults(sortedResults);
    }

    @RequestMapping(path = "/microserviceParallel", method = RequestMethod.GET)
    public FinalResults doAll() {
        AllResults allResults = new AllResults(generate());
        int[] sortedResults = sort(allResults);
        return new FinalResults(sortedResults);
    }

    private int[] generate() {
        int[] allResults = new int[SIZE];
        Random random = new Random();
        for (int i = 0; i < SIZE; i++) {
            allResults[i] = random.nextInt(1000000);
        }
        return allResults;
    }

    private int[] sort(AllResults allResults) {
        int[] results = allResults.getAllResults();
        int[] finalResults = new int[20];
        for (int index = 1; index < SIZE; ++index) {
            int value = results[index];
            int previousIndex = index - 1;
            while (previousIndex >= 0 && results[previousIndex] > value) {
                results[previousIndex + 1] = results[previousIndex];
                previousIndex = previousIndex - 1;
            }
            results[previousIndex + 1] = value;
        }

        for (int i = 0; i < 20; i++) {
            finalResults[i] = results[SIZE - 1 - i];
        }
        return finalResults;
    }

}
