import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * </p>
 * This is just a demo for you, please run it on JDK17 (some statements may be not allowed in lower version).
 * This is just a demo, and you can extend and implement functions
 * based on this demo, or implement it in a different way.
 */
public class OnlineCoursesAnalyzer {

    List<Course> courses = new ArrayList<>();

    public OnlineCoursesAnalyzer(String datasetPath) {
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader(datasetPath, StandardCharsets.UTF_8));
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] info = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1);
                Course course = new Course(info[0], info[1], new Date(info[2]), info[3], info[4], info[5],
                        Integer.parseInt(info[6]), Integer.parseInt(info[7]), Integer.parseInt(info[8]),
                        Integer.parseInt(info[9]), Integer.parseInt(info[10]), Double.parseDouble(info[11]),
                        Double.parseDouble(info[12]), Double.parseDouble(info[13]), Double.parseDouble(info[14]),
                        Double.parseDouble(info[15]), Double.parseDouble(info[16]), Double.parseDouble(info[17]),
                        Double.parseDouble(info[18]), Double.parseDouble(info[19]), Double.parseDouble(info[20]),
                        Double.parseDouble(info[21]), Double.parseDouble(info[22]));
                courses.add(course);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //1
    public Map<String, Integer> getPtcpCountByInst() {
        return courses.stream()
                .collect(Collectors.toMap(course -> course.institution, course -> course.participants, Integer::sum));
    }

    //2
    public Map<String, Integer> getPtcpCountByInstAndSubject() {
        Map<String, Integer> result = new LinkedHashMap<>();
        courses.stream()
                .collect(Collectors.toMap(course -> course.institution + "-" + course.subject,
                        course -> course.participants, Integer::sum))
                .entrySet().stream()
                .sorted((e1, e2) -> e1.getValue().equals(e2.getValue())?
                        e1.getKey().compareTo(e2.getKey()) : e2.getValue().compareTo(e1.getValue()))
                .forEach(entry -> result.put(entry.getKey(), entry.getValue()));
        return result;
    }

    //3
    public Map<String, List<List<String>>> getCourseListOfInstructor() {
        Map<String, List<List<String>>> result = new HashMap<>();
        Map<String, List<List<String>>> result2 = new HashMap<>();
        courses.stream()
                .forEach(course -> {
                    String[] instructors = course.instructors.split(", ");
                    if (instructors.length == 1) {
                        if (!result.containsKey(instructors[0])) {
                            List<List<String>> list = new ArrayList<>();
                            list.add(new ArrayList<>());
                            list.add(new ArrayList<>());
                            result.put(instructors[0], list);
                        }
                        List<List<String>> list = result.get(instructors[0]);
                        if (!list.get(0).contains(course.title))
                            list.get(0).add(course.title);  // redundant?
                        //result.put(instructors[0], list);  //
                    }
                    else {
                        for (String instructor : instructors) {
                            if (!result.containsKey(instructor)) {
                                List<List<String>> list = new ArrayList<>();
                                list.add(new ArrayList<>());
                                list.add(new ArrayList<>());
                                result.put(instructor, list);
                            }
                            List<List<String>> list = result.get(instructor);
                            if (!list.get(1).contains(course.title))
                                list.get(1).add(course.title);  // redundant?
                            //result.put(instructors[0], list);  //
                        }
                    }
                });
        result.entrySet().stream().forEach(entry -> {
            Collections.sort(entry.getValue().get(0));
            Collections.sort(entry.getValue().get(1));
        });
        return result;
    }

    //4
    public List<String> getCourses(int topK, String by) {
        if (by.equals("hours")) {
            return courses.stream()
                    .sorted((c1, c2) -> c1.totalHours == c2.totalHours?
                            c1.title.compareTo(c2.title) : Double.compare(c2.totalHours, c1.totalHours))
                    .map(course -> course.title)
                    .distinct()
                    .limit(topK)
                    .toList();
        }
        else {
            return courses.stream()
                    .sorted((c1, c2) -> c1.participants == c2.participants?
                            c1.title.compareTo(c2.title) : Integer.compare(c2.participants, c1.participants))
                    .map(course -> course.title)
                    .distinct()
                    .limit(topK)
                    .toList();
        }
    }

    //5
    public List<String> searchCourses(String courseSubject, double percentAudited, double totalCourseHours) {
        return courses.stream()
                .filter(course -> course.subject.toUpperCase().contains(courseSubject.toUpperCase()) &&
                        course.percentAudited >= percentAudited &&
                        course.totalHours <= totalCourseHours)
                .map(course -> course.title)
                .distinct()
                .sorted()
                .toList();
    }

    //6
    public List<String> recommendCourses(int age, int gender, int isBachelorOrHigher) {
        Map<String, Double> ageMap = courses.stream()
                .collect(Collectors.groupingBy(course -> course.number,
                        Collectors.averagingDouble(course -> course.medianAge)));
        Map<String, Double> genderMap = courses.stream()
                .collect(Collectors.groupingBy(course -> course.number,
                        Collectors.averagingDouble(course -> course.percentMale)));
        Map<String, Double> degreeMap = courses.stream()
                .collect(Collectors.groupingBy(course -> course.number,
                        Collectors.averagingDouble(course -> course.percentDegree)));
        return courses.stream()
                .collect(Collectors.groupingBy(course -> course.number,
                        Collectors.maxBy(Comparator.comparing(c -> c.launchDate))))
                .entrySet().stream()
                .sorted((e1, e2) ->
                        Double.compare(
                            Math.pow(age - ageMap.get(e1.getKey()), 2) +
                            Math.pow(gender * 100 - genderMap.get(e1.getKey()), 2) +
                            Math.pow(isBachelorOrHigher * 100 - degreeMap.get(e1.getKey()), 2),
                            Math.pow(age - ageMap.get(e2.getKey()), 2) +
                            Math.pow(gender * 100 - genderMap.get(e2.getKey()), 2) +
                            Math.pow(isBachelorOrHigher * 100 - degreeMap.get(e2.getKey()), 2)) == 0?
                        e1.getValue().get().title.compareTo(e2.getValue().get().title) :
                        Double.compare(
                            Math.pow(age - ageMap.get(e1.getKey()), 2) +
                            Math.pow(gender * 100 - genderMap.get(e1.getKey()), 2) +
                            Math.pow(isBachelorOrHigher * 100 - degreeMap.get(e1.getKey()), 2),
                            Math.pow(age - ageMap.get(e2.getKey()), 2) +
                            Math.pow(gender * 100 - genderMap.get(e2.getKey()), 2) +
                            Math.pow(isBachelorOrHigher * 100 - degreeMap.get(e2.getKey()), 2))
                )
                .map(e -> e.getValue().get().title)
                .distinct()
                .limit(10)
                .toList();
    }

}

class Course {
    String institution;
    String number;
    Date launchDate;
    String title;
    String instructors;
    String subject;
    int year;
    int honorCode;
    int participants;
    int audited;
    int certified;
    double percentAudited;
    double percentCertified;
    double percentCertified50;
    double percentVideo;
    double percentForum;
    double gradeHigherZero;
    double totalHours;
    double medianHoursCertification;
    double medianAge;
    double percentMale;
    double percentFemale;
    double percentDegree;

    public Course(String institution, String number, Date launchDate,
                  String title, String instructors, String subject,
                  int year, int honorCode, int participants,
                  int audited, int certified, double percentAudited,
                  double percentCertified, double percentCertified50,
                  double percentVideo, double percentForum, double gradeHigherZero,
                  double totalHours, double medianHoursCertification,
                  double medianAge, double percentMale, double percentFemale,
                  double percentDegree) {
        this.institution = institution;
        this.number = number;
        this.launchDate = launchDate;
        if (title.startsWith("\"")) title = title.substring(1);
        if (title.endsWith("\"")) title = title.substring(0, title.length() - 1);
        this.title = title;
        if (instructors.startsWith("\"")) instructors = instructors.substring(1);
        if (instructors.endsWith("\"")) instructors = instructors.substring(0, instructors.length() - 1);
        this.instructors = instructors;
        if (subject.startsWith("\"")) subject = subject.substring(1);
        if (subject.endsWith("\"")) subject = subject.substring(0, subject.length() - 1);
        this.subject = subject;
        this.year = year;
        this.honorCode = honorCode;
        this.participants = participants;
        this.audited = audited;
        this.certified = certified;
        this.percentAudited = percentAudited;
        this.percentCertified = percentCertified;
        this.percentCertified50 = percentCertified50;
        this.percentVideo = percentVideo;
        this.percentForum = percentForum;
        this.gradeHigherZero = gradeHigherZero;
        this.totalHours = totalHours;
        this.medianHoursCertification = medianHoursCertification;
        this.medianAge = medianAge;
        this.percentMale = percentMale;
        this.percentFemale = percentFemale;
        this.percentDegree = percentDegree;
    }
}