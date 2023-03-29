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

        /*List<String> result4 = analyzer.getCourses(10, "hours");
        for (String s : result4) {
            System.out.println(s);
        }
        List<String> result5 = analyzer.getCourses(15, "participants");
        for (String s : result5) {
            System.out.println(s);
        }*/

        /*List<String> result6 = analyzer.searchCourses("computer", 20.0, 700);
        for (String s : result6) {
            System.out.println(s);
        }
        List<String> result7 = analyzer.searchCourses("SCIENCE", 25.0, 400);
        for (String s : result7) {
            System.out.println(s);
        }*/

        /*List<String> result8 = analyzer.recommendCourses(25, 1, 1);
        for (String s : result8) {
            System.out.println(s);
        }*/
        /*List<String> result9 = analyzer.recommendCourses(30, 0, 1);
        for (String s : result9) {
            System.out.println(s);
        }*/
        /*List<String> result10 = analyzer.recommendCourses(35, 1, 0);
        for (String s : result10) {
            System.out.println(s);
        }*/

    }
}
