import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        OnlineCoursesAnalyzer analyzer = new OnlineCoursesAnalyzer("./resource/local.csv");

        /*Map<String, Integer> result1 = analyzer.getPtcpCountByInst();
        System.out.println(result1);*/

        /*Map<String, Integer> result2 = analyzer.getPtcpCountByInstAndSubject();
        System.out.println(result2);*/

        Map<String, List<List<String>>> result3 = analyzer.getCourseListOfInstructor();
        for (Map.Entry<String, List<List<String>>> entry : result3.entrySet()) {
            System.out.println(entry);
        }

    }
}
