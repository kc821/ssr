package master.thesis.ssr;

import lombok.Getter;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import java.util.Random;

@Getter
@ManagedBean("resultSmallScope")
@ViewScoped
public class ResultSmallScope {

    private Integer[] results;

    @PostConstruct
    public void init() {
        int size = 50;
        results = new Integer[20];
        int[] allResults = new int[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            allResults[i] = random.nextInt(1000000);
        }

        for (int index = 1; index < size; ++index) {
            int value = allResults[index];
            int previousIndex = index - 1;
            while (previousIndex >= 0 && allResults[previousIndex] > value) {
                allResults[previousIndex + 1] = allResults[previousIndex];
                previousIndex = previousIndex - 1;
            }
            allResults[previousIndex + 1] = value;
        }

        for (int i = 0; i < 20; i++) {
            results[i] = allResults[size - 1 - i];
        }

    }
}
