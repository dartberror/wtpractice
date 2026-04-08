package training_center.system;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class WebSystemTest {

    private static final String DEFAULT_BASE_URL = "http://localhost:8080/training_center_clean";
    private static final Pattern COURSE_ID_PATTERN = Pattern.compile(".*/courses/(\\d+)$");
    private static final Pattern TEACHER_ID_PATTERN = Pattern.compile(".*/teachers/(\\d+)$");
    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile(".*/students/(\\d+)$");

    private WebDriver driver;
    private String baseUrl;

    @BeforeClass
    public void setUp() {
        baseUrl = System.getProperty("app.baseUrl", DEFAULT_BASE_URL);

        if (!isApplicationAvailable(baseUrl)) {
            throw new SkipException("Web application is not available: " + baseUrl);
        }

        driver = createDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(4));
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void homePage_shouldOpenAndContainMainNavigation() {
        open("/");
        assertTrue(driver.getCurrentUrl().contains("/training_center_clean/"));
        assertTrue(driver.findElement(By.cssSelector("a[href$='/courses']")).isDisplayed());
        assertTrue(driver.findElement(By.cssSelector("a[href$='/teachers']")).isDisplayed());
        assertTrue(driver.findElement(By.cssSelector("a[href$='/students']")).isDisplayed());
        assertTrue(driver.findElement(By.cssSelector("a[href$='/companies']")).isDisplayed());
    }

    @Test
    public void createCompany_shouldSucceed() {
        String companyName = uniqueName("company");
        open("/companies/new");
        waitFor(By.name("name")).sendKeys(companyName);
        waitFor(By.name("address")).sendKeys("Moscow, Test st. 1");
        submitCurrentForm();

        assertTrue(driver.getCurrentUrl().contains("/companies"));
        assertPageContains(companyName);
    }

    @Test
    public void createCompany_shouldShowValidationErrorWhenNameMissing() {
        open("/companies/new");
        WebElement nameInput = waitFor(By.name("name"));
        WebElement addressInput = waitFor(By.name("address"));

        removeClientValidation(nameInput);
        addressInput.sendKeys("Moscow, Empty Name st. 2");
        submitCurrentForm();

        assertDangerAlertPresent();
    }

    @Test
    public void createCompany_shouldShowValidationErrorWhenAddressMissing() {
        open("/companies/new");
        WebElement addressInput = waitFor(By.name("address"));
        waitFor(By.name("name")).sendKeys(uniqueName("company-no-address"));

        removeClientValidation(addressInput);
        submitCurrentForm();

        assertDangerAlertPresent();
    }

    @Test
    public void createStudent_shouldSucceed() {
        EntityRef student = createStudentViaUi();
        assertTrue(student.id != null);
        assertFalse(student.name.isBlank());
    }

    @Test
    public void createStudent_shouldShowValidationErrorWhenFullNameMissing() {
        open("/students/new");
        WebElement fullNameInput = driver.findElement(By.name("fullName"));
        removeClientValidation(fullNameInput);
        submitCurrentForm();

        assertDangerAlertPresent();
    }

    @Test
    public void updateStudent_shouldSucceed() {
        EntityRef student = createStudentViaUi();
        String updatedStudentName = uniqueName("student-updated");

        open("/students/" + student.id + "/edit");
        WebElement fullNameInput = driver.findElement(By.name("fullName"));
        fullNameInput.clear();
        fullNameInput.sendKeys(updatedStudentName);
        submitCurrentForm();

        assertTrue(driver.getCurrentUrl().contains("/students/" + student.id));
        assertPageContains(updatedStudentName);
    }

    @Test
    public void deleteStudent_shouldSucceed() {
        EntityRef student = createStudentViaUi();

        open("/students/" + student.id);
        driver.findElement(By.cssSelector("form[action$='/students/" + student.id + "/delete'] button[type='submit']")).click();

        assertTrue(driver.getCurrentUrl().contains("/students"));
        assertPageNotContains(student.name);
    }

    @Test
    public void createTeacher_shouldSucceed() {
        String companyName = createCompanyViaUi();
        EntityRef teacher = createTeacherViaUi(companyName);
        assertTrue(teacher.id != null);
        assertFalse(teacher.name.isBlank());
    }

    @Test
    public void createTeacher_shouldShowValidationErrorWhenFullNameMissing() {
        String companyName = createCompanyViaUi();

        open("/teachers/new");
        WebElement fullNameInput = driver.findElement(By.name("fullName"));
        removeClientValidation(fullNameInput);
        new Select(driver.findElement(By.name("companyId"))).selectByVisibleText(companyName);
        submitCurrentForm();

        assertDangerAlertPresent();
    }

    @Test
    public void updateTeacher_shouldSucceed() {
        String companyName = createCompanyViaUi();
        EntityRef teacher = createTeacherViaUi(companyName);
        String anotherCompanyName = createCompanyViaUi();
        String updatedTeacherName = uniqueName("teacher-updated");

        open("/teachers/" + teacher.id + "/edit");
        WebElement fullNameInput = driver.findElement(By.name("fullName"));
        fullNameInput.clear();
        fullNameInput.sendKeys(updatedTeacherName);
        new Select(driver.findElement(By.name("companyId"))).selectByVisibleText(anotherCompanyName);
        submitCurrentForm();

        assertTrue(driver.getCurrentUrl().contains("/teachers/" + teacher.id));
        assertPageContains(updatedTeacherName);
        assertPageContains(anotherCompanyName);
    }

    @Test
    public void deleteTeacher_shouldSucceed() {
        String companyName = createCompanyViaUi();
        EntityRef teacher = createTeacherViaUi(companyName);

        open("/teachers/" + teacher.id);
        driver.findElement(By.cssSelector("form[action$='/teachers/" + teacher.id + "/delete'] button[type='submit']")).click();

        assertTrue(driver.getCurrentUrl().contains("/teachers"));
        assertPageNotContains(teacher.name);
    }

    @Test
    public void createCourse_shouldSucceed() {
        String companyName = createCompanyViaUi();
        String courseTitle = uniqueName("course");

        open("/courses/new");
        driver.findElement(By.name("title")).sendKeys(courseTitle);
        driver.findElement(By.name("durationValue")).sendKeys("30");
        driver.findElement(By.name("intensity")).sendKeys("3");
        new Select(driver.findElement(By.name("companyId"))).selectByVisibleText(companyName);
        submitCurrentForm();

        assertTrue(driver.getCurrentUrl().contains("/courses"));
        assertPageContains(courseTitle);
    }

    @Test
    public void createCourse_shouldShowValidationErrorWhenTitleMissing() {
        String companyName = createCompanyViaUi();

        open("/courses/new");
        WebElement titleInput = driver.findElement(By.name("title"));
        removeClientValidation(titleInput);

        driver.findElement(By.name("durationValue")).sendKeys("10");
        driver.findElement(By.name("intensity")).sendKeys("2");
        new Select(driver.findElement(By.name("companyId"))).selectByVisibleText(companyName);
        submitCurrentForm();

        assertDangerAlertPresent();
    }

    @Test
    public void createCourse_shouldShowValidationErrorWhenIntensityInvalid() {
        String companyName = createCompanyViaUi();

        open("/courses/new");
        driver.findElement(By.name("title")).sendKeys(uniqueName("invalid-intensity-course"));
        driver.findElement(By.name("durationValue")).sendKeys("12");
        WebElement intensityInput = driver.findElement(By.name("intensity"));
        removeClientValidation(intensityInput);
        intensityInput.sendKeys("0");
        new Select(driver.findElement(By.name("companyId"))).selectByVisibleText(companyName);
        submitCurrentForm();

        assertDangerAlertPresent();
    }

    @Test
    public void createCourse_shouldShowValidationErrorWhenDurationInvalid() {
        String companyName = createCompanyViaUi();

        open("/courses/new");
        driver.findElement(By.name("title")).sendKeys(uniqueName("invalid-duration-course"));
        WebElement durationInput = driver.findElement(By.name("durationValue"));
        removeClientValidation(durationInput);
        durationInput.sendKeys("0");
        driver.findElement(By.name("intensity")).sendKeys("3");
        new Select(driver.findElement(By.name("companyId"))).selectByVisibleText(companyName);
        submitCurrentForm();

        assertDangerAlertPresent();
    }

    @Test
    public void updateCourse_shouldSucceed() {
        String companyName = createCompanyViaUi();
        Long courseId = createCourseViaUi(companyName, uniqueName("course-update-src"));
        String updatedTitle = uniqueName("course-update-dst");

        open("/courses/" + courseId + "/edit");
        WebElement titleInput = driver.findElement(By.name("title"));
        WebElement durationInput = driver.findElement(By.name("durationValue"));
        WebElement intensityInput = driver.findElement(By.name("intensity"));

        titleInput.clear();
        titleInput.sendKeys(updatedTitle);
        durationInput.clear();
        durationInput.sendKeys("25");
        intensityInput.clear();
        intensityInput.sendKeys("4");
        new Select(driver.findElement(By.name("companyId"))).selectByVisibleText(companyName);
        submitCurrentForm();

        assertTrue(driver.getCurrentUrl().contains("/courses/" + courseId));
        assertPageContains(updatedTitle);
    }

    @Test
    public void deleteCourse_shouldSucceed() {
        String companyName = createCompanyViaUi();
        String courseTitle = uniqueName("course-delete");
        Long courseId = createCourseViaUi(companyName, courseTitle);

        open("/courses/" + courseId);
        driver.findElement(By.cssSelector("form[action$='/delete'] button[type='submit']")).click();

        assertTrue(driver.getCurrentUrl().contains("/courses"));
        assertPageNotContains(courseTitle);
    }

    @Test
    public void addStudentToCourse_shouldSucceed() {
        String companyName = createCompanyViaUi();
        Long courseId = createCourseViaUi(companyName, uniqueName("course-students"));
        EntityRef student = createStudentViaUi();

        open("/courses/" + courseId + "/add-student");
        new Select(driver.findElement(By.name("studentId"))).selectByVisibleText(student.name);
        submitCurrentForm();

        assertTrue(driver.getCurrentUrl().contains("/courses/" + courseId));
        assertPageContains(student.name);
    }

    @Test
    public void addTeacherToCourse_shouldSucceed() {
        String companyName = createCompanyViaUi();
        Long courseId = createCourseViaUi(companyName, uniqueName("course-teachers"));
        EntityRef teacher = createTeacherViaUi(companyName);

        open("/courses/" + courseId + "/add-teacher");
        selectOptionContainingText(By.name("teacherId"), teacher.name);
        submitCurrentForm();

        assertTrue(driver.getCurrentUrl().contains("/courses/" + courseId));
        assertPageContains(teacher.name);
    }

    @Test
    public void removeStudentFromCourse_shouldSucceed() {
        String companyName = createCompanyViaUi();
        Long courseId = createCourseViaUi(companyName, uniqueName("course-remove-student"));
        EntityRef student = createStudentViaUi();

        addStudentToCourse(courseId, student.name);

        open("/courses/" + courseId);
        driver.findElement(By.cssSelector("form[action$='/students/" + student.id + "/delete'] button[type='submit']")).click();

        assertTrue(driver.getCurrentUrl().contains("/courses/" + courseId));
        assertPageNotContains(student.name);
    }

    @Test
    public void removeTeacherFromCourse_shouldSucceed() {
        String companyName = createCompanyViaUi();
        Long courseId = createCourseViaUi(companyName, uniqueName("course-remove-teacher"));
        EntityRef teacher = createTeacherViaUi(companyName);

        assignTeacherToCourse(courseId, teacher.name);

        open("/courses/" + courseId);
        driver.findElement(By.cssSelector("form[action$='/teachers/" + teacher.id + "/delete'] button[type='submit']")).click();

        assertTrue(driver.getCurrentUrl().contains("/courses/" + courseId));
        assertPageNotContains(teacher.name);
    }

    @Test
    public void createLesson_shouldSucceed() {
        String companyName = createCompanyViaUi();
        String courseTitle = uniqueName("course-lesson-ok");
        Long courseId = createCourseViaUi(companyName, courseTitle);
        EntityRef teacher = createTeacherViaUi(companyName);

        assignTeacherToCourse(courseId, teacher.name);
        createLessonViaUi(courseId, teacher.name, LocalDate.now().plusDays(2), "10:00", "11:30");
        assertPageContains(courseTitle);
        assertCourseScheduleHasRows();
    }

    @Test
    public void createLesson_shouldShowValidationErrorWhenEndBeforeStart() {
        String companyName = createCompanyViaUi();
        Long courseId = createCourseViaUi(companyName, uniqueName("course-lesson-bad-time"));
        EntityRef teacher = createTeacherViaUi(companyName);

        assignTeacherToCourse(courseId, teacher.name);

        openLessonForm(courseId);
        selectOptionContainingText(By.name("teacherId"), teacher.name);
        setInputValue(By.name("lessonDate"), LocalDate.now().plusDays(3).toString());
        setInputValue(By.name("startTime"), "15:00");
        setInputValue(By.name("endTime"), "14:00");
        submitLessonForm();

        assertTrue(driver.getCurrentUrl().contains("/courses/" + courseId + "/schedule"));
        assertDangerAlertPresent();
    }

    @Test
    public void teacherSchedule_shouldShowLessonsForInterval() {
        String companyName = createCompanyViaUi();
        String courseTitle = uniqueName("course-teacher-schedule");
        Long courseId = createCourseViaUi(companyName, courseTitle);
        EntityRef teacher = createTeacherViaUi(companyName);
        LocalDate lessonDate = LocalDate.now().plusDays(5);

        assignTeacherToCourse(courseId, teacher.name);
        createLessonViaUi(courseId, teacher.name, lessonDate, "14:00", "15:00");

        LocalDate from = lessonDate.minusDays(7);
        LocalDate to = lessonDate.plusDays(7);
        open("/teachers/" + teacher.id + "/schedule?from=" + from + "&to=" + to);

        assertPageContains(courseTitle);
    }

    @Test
    public void studentSchedule_shouldShowValidationErrorWhenOnlyFromDateProvided() {
        EntityRef student = createStudentViaUi();
        open("/students/" + student.id + "/schedule?from=" + LocalDate.now());
        assertDangerAlertPresent();
    }

    @Test
    public void teacherSchedule_shouldShowValidationErrorWhenOnlyFromDateProvided() {
        String companyName = createCompanyViaUi();
        EntityRef teacher = createTeacherViaUi(companyName);
        open("/teachers/" + teacher.id + "/schedule?from=" + LocalDate.now());
        assertDangerAlertPresent();
    }

    private String createCompanyViaUi() {
        String companyName = uniqueName("company-for-tests");
        open("/companies/new");
        waitFor(By.name("name")).sendKeys(companyName);
        waitFor(By.name("address")).sendKeys("Moscow, Test avenue 10");
        submitCurrentForm();

        assertPageContains(companyName);
        return companyName;
    }

    private EntityRef createStudentViaUi() {
        String studentName = uniqueName("student");
        open("/students/new");
        driver.findElement(By.name("fullName")).sendKeys(studentName);
        submitCurrentForm();

        assertTrue(driver.getCurrentUrl().contains("/students"));
        assertPageContains(studentName);

        WebElement link = driver.findElement(
                By.xpath("//tr[td[normalize-space(text())='" + studentName + "']]//a[contains(@href,'/students/')]")
        );
        Long studentId = extractIdFromHref(link.getAttribute("href"), STUDENT_ID_PATTERN, "student");
        return new EntityRef(studentId, studentName);
    }

    private EntityRef createTeacherViaUi(String companyName) {
        String teacherName = uniqueName("teacher");
        open("/teachers/new");
        driver.findElement(By.name("fullName")).sendKeys(teacherName);
        new Select(driver.findElement(By.name("companyId"))).selectByVisibleText(companyName);
        submitCurrentForm();

        assertTrue(driver.getCurrentUrl().contains("/teachers"));
        assertPageContains(teacherName);

        WebElement link = driver.findElement(
                By.xpath("//tr[td[normalize-space(text())='" + teacherName + "']]//a[contains(@href,'/teachers/')]")
        );
        Long teacherId = extractIdFromHref(link.getAttribute("href"), TEACHER_ID_PATTERN, "teacher");
        return new EntityRef(teacherId, teacherName);
    }

    private Long createCourseViaUi(String companyName, String courseTitle) {
        open("/courses/new");
        driver.findElement(By.name("title")).sendKeys(courseTitle);
        driver.findElement(By.name("durationValue")).sendKeys("20");
        driver.findElement(By.name("intensity")).sendKeys("3");
        new Select(driver.findElement(By.name("companyId"))).selectByVisibleText(companyName);
        submitCurrentForm();

        assertTrue(driver.getCurrentUrl().contains("/courses"));
        assertPageContains(courseTitle);

        WebElement link = driver.findElement(By.xpath("//a[normalize-space(text())='" + courseTitle + "']"));
        return extractIdFromHref(link.getAttribute("href"), COURSE_ID_PATTERN, "course");
    }

    private void assignTeacherToCourse(Long courseId, String teacherName) {
        open("/courses/" + courseId + "/add-teacher");
        selectOptionContainingText(By.name("teacherId"), teacherName);
        submitCurrentForm();

        assertTrue(driver.getCurrentUrl().contains("/courses/" + courseId));
        assertPageContains(teacherName);
    }

    private void addStudentToCourse(Long courseId, String studentName) {
        open("/courses/" + courseId + "/add-student");
        new Select(driver.findElement(By.name("studentId"))).selectByVisibleText(studentName);
        submitCurrentForm();

        assertTrue(driver.getCurrentUrl().contains("/courses/" + courseId));
        assertPageContains(studentName);
    }

    private void createLessonViaUi(Long courseId,
                                   String teacherName,
                                   LocalDate lessonDate,
                                   String startTime,
                                   String endTime) {
        String courseUrlRegex = ".*/courses/" + courseId + "(?:\\?.*)?$";
        String lessonSubmitUrlRegex = ".*/courses/" + courseId + "/schedule(?:\\?.*)?$";
        String newLessonUrlRegex = ".*/courses/" + courseId + "/schedule/new(?:\\?.*)?$";

        openLessonForm(courseId);
        selectOptionContainingText(By.name("teacherId"), teacherName);
        setInputValue(By.name("lessonDate"), lessonDate.toString());
        setInputValue(By.name("startTime"), startTime);
        setInputValue(By.name("endTime"), endTime);
        submitLessonForm();

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(webDriver -> {
                    String currentUrl = webDriver.getCurrentUrl();
                    if (currentUrl.matches(courseUrlRegex)) {
                        return true;
                    }
                    if (currentUrl.matches(newLessonUrlRegex) || currentUrl.matches(lessonSubmitUrlRegex)) {
                        return !webDriver.findElements(By.cssSelector(".alert.alert-danger")).isEmpty();
                    }
                    return false;
                });
        String currentUrl = driver.getCurrentUrl();

        if (!currentUrl.matches(courseUrlRegex)) {
            String alertText = readDangerAlertTextOrEmpty();
            if (alertText.isBlank()) {
                alertText = "Не удалось отправить форму занятия";
            }
            fail("Lesson was not created. Current URL: " + currentUrl + ". Error: " + alertText);
        }

        assertTrue(currentUrl.matches(courseUrlRegex));
    }

    private void openLessonForm(Long courseId) {
        open("/courses/" + courseId + "/schedule/new");

        if (!isElementPresent(By.name("teacherId"))) {
            open("/courses/" + courseId);
            WebElement addLessonLink = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.elementToBeClickable(
                            By.cssSelector("a[href$='/courses/" + courseId + "/schedule/new']")
                    ));
            addLessonLink.click();
        }

        waitFor(By.name("teacherId"));
    }

    private WebElement waitFor(By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(12))
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    private void submitLessonForm() {
        WebElement form = waitFor(By.cssSelector("form[action*='/schedule']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].submit();", form);
    }

    private void submitCurrentForm() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.cssSelector("form button[type='submit']")))
                .click();
    }

    private void open(String path) {
        driver.get(baseUrl + path);
        waitForPageReady();
    }

    private void waitForPageReady() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(webDriver -> {
                    Object readyState = ((JavascriptExecutor) webDriver).executeScript("return document.readyState");
                    return "complete".equals(String.valueOf(readyState));
                });
    }

    private String uniqueName(String prefix) {
        return prefix + "-" + System.currentTimeMillis();
    }

    private Long extractIdFromHref(String href, Pattern pattern, String entityName) {
        Matcher matcher = pattern.matcher(href);
        if (!matcher.matches()) {
            fail("Cannot parse " + entityName + " id from URL: " + href);
        }
        return Long.valueOf(matcher.group(1));
    }

    private void selectOptionContainingText(By selectLocator, String expectedPart) {
        WebElement selectElement = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.presenceOfElementLocated(selectLocator));

        Select select = new Select(selectElement);
        for (WebElement option : select.getOptions()) {
            String optionText = option.getText();
            if (optionText != null && optionText.contains(expectedPart)) {
                select.selectByVisibleText(optionText);
                return;
            }
        }

        fail("Option containing text was not found: " + expectedPart);
    }

    private void removeClientValidation(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].removeAttribute('required');"
                        + "arguments[0].removeAttribute('min');"
                        + "arguments[0].removeAttribute('max');",
                element
        );
    }

    private void setInputValue(By locator, String value) {
        WebElement element = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1];"
                        + "arguments[0].dispatchEvent(new Event('input', {bubbles: true}));"
                        + "arguments[0].dispatchEvent(new Event('change', {bubbles: true}));",
                element,
                value
        );
    }

    private boolean isElementPresent(By locator) {
        try {
            return !driver.findElements(locator).isEmpty();
        } catch (NoSuchElementException ex) {
            return false;
        }
    }

    private String readDangerAlertTextOrEmpty() {
        List<WebElement> alerts = driver.findElements(By.cssSelector(".alert.alert-danger"));
        if (alerts.isEmpty()) {
            return "";
        }
        String text = alerts.get(0).getText();
        return text == null ? "" : text.trim();
    }

    private void assertDangerAlertPresent() {
        long timeoutAt = System.currentTimeMillis() + 5000;
        while (System.currentTimeMillis() < timeoutAt) {
            List<WebElement> alerts = driver.findElements(By.cssSelector(".alert.alert-danger"));
            if (!alerts.isEmpty()) {
                String text = alerts.get(0).getText();
                assertFalse(text == null || text.isBlank());
                return;
            }
            sleepBriefly();
        }
        fail("Expected .alert.alert-danger on page");
    }

    private void assertPageContains(String text) {
        long timeoutAt = System.currentTimeMillis() + 5000;
        while (System.currentTimeMillis() < timeoutAt) {
            if (driver.getPageSource().contains(text)) {
                return;
            }
            sleepBriefly();
        }
        fail("Text not found on page: " + text);
    }

    private void assertPageNotContains(String text) {
        long timeoutAt = System.currentTimeMillis() + 5000;
        while (System.currentTimeMillis() < timeoutAt) {
            if (!driver.getPageSource().contains(text)) {
                return;
            }
            sleepBriefly();
        }
        fail("Text is still present on page: " + text);
    }

    private void assertCourseScheduleHasRows() {
        long timeoutAt = System.currentTimeMillis() + 5000;
        while (System.currentTimeMillis() < timeoutAt) {
            List<WebElement> rows = driver.findElements(By.cssSelector("table.table tbody tr"));
            if (!rows.isEmpty()) {
                return;
            }
            sleepBriefly();
        }
        fail("Expected at least one lesson row in course schedule table");
    }

    private void sleepBriefly() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private WebDriver createDriver() {
        try {
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--headless=new");
            chromeOptions.addArguments("--window-size=1920,1080");
            chromeOptions.addArguments("--disable-gpu");
            return new ChromeDriver(chromeOptions);
        } catch (RuntimeException chromeError) {
            try {
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("--headless=new");
                edgeOptions.addArguments("--window-size=1920,1080");
                return new EdgeDriver(edgeOptions);
            } catch (RuntimeException edgeError) {
                throw new SkipException("Cannot initialize Selenium WebDriver");
            }
        }
    }

    private boolean isApplicationAvailable(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            int statusCode = connection.getResponseCode();
            return statusCode >= 200 && statusCode < 400;
        } catch (Exception ex) {
            return false;
        }
    }

    private static final class EntityRef {
        private final Long id;
        private final String name;

        private EntityRef(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
