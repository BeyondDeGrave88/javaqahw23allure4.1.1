package ru.netology.delivery.test;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

class DeliveryTest {

    @BeforeAll
    static void setUpAll() {
        // Добавляем листенер в тестовый класс перед выполнением всех тестов
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        // Удаляем листенер после выполнения всех тестов
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");

        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);

        $("[data-test-id='city'] input").setValue(validUser.getCity());
        $("[data-test-id='date'] input")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE)
                .setValue(firstMeetingDate);
        $("[data-test-id='name'] input").setValue(validUser.getName());
        $("[data-test-id='phone'] input").setValue(validUser.getPhone());
        $("[data-test-id='agreement'] .checkbox__box").click();
        $(Selectors.byText("Запланировать")).click();
        $(Selectors.withText("Успешно!")).shouldBe(visible, Duration.ofSeconds(15));
        $("[data-test-id='success-notification'] .notification__content")
                .shouldHave(exactText("Встреча успешно запланирована на " + firstMeetingDate))
                .shouldBe(visible);
        $("[data-test-id='date'] input")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE)
                .setValue(secondMeetingDate);
        $(Selectors.byText("Запланировать")).click();
        $("[data-test-id='replan-notification'] .notification__content")
                .shouldHave(text("У вас уже запланирована встреча на другую дату. Перепланировать?"))
                .shouldBe(visible);
        $(Selectors.byText("Перепланировать")).click();
        $("[data-test-id='success-notification'] .notification__content")
                .shouldHave(exactText("Встреча успешно запланирована на " + secondMeetingDate))
                .shouldBe(visible);
    }

    @Test
    @DisplayName("Should fail with invalid city")
    void shouldFailWithInvalidCity() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForMeeting = 5;
        var meetingDate = DataGenerator.generateDate(daysToAddForMeeting);

        // Вводим несуществующий город
        $("[data-test-id='city'] input").setValue("InvalidCity123");
        $("[data-test-id='date'] input")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE)
                .setValue(meetingDate);
        $("[data-test-id='name'] input").setValue(validUser.getName());
        $("[data-test-id='phone'] input").setValue(validUser.getPhone());
        $("[data-test-id='agreement'] .checkbox__box").click();
        $(Selectors.byText("Запланировать")).click();

        // Проверяем сообщение об ошибке (тест упадет, если сообщение не появится)
        $("[data-test-id='city'] .input__sub")
                .shouldHave(exactText("Доставка в выбранный город недоступна"))
                .shouldBe(visible);
    }

    @Test
    @DisplayName("Should fail with latin name")
    void shouldFailWithLatinName() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForMeeting = 5;
        var meetingDate = DataGenerator.generateDate(daysToAddForMeeting);

        $("[data-test-id='city'] input").setValue(validUser.getCity());
        $("[data-test-id='date'] input")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE)
                .setValue(meetingDate);
        // Вводим имя латинскими буквами
        $("[data-test-id='name'] input").setValue("John Doe");
        $("[data-test-id='phone'] input").setValue(validUser.getPhone());
        $("[data-test-id='agreement'] .checkbox__box").click();
        $(Selectors.byText("Запланировать")).click();

        // Проверяем сообщение об ошибке
        $("[data-test-id='name'] .input__sub")
                .shouldHave(exactText("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."))
                .shouldBe(visible);
    }

    @Test
    @DisplayName("Should fail with past date")
    void shouldFailWithPastDate() {
        var validUser = DataGenerator.Registration.generateUser("ru");

        // Устанавливаем прошедшую дату
        $("[data-test-id='city'] input").setValue(validUser.getCity());
        $("[data-test-id='date'] input")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE)
                .setValue("01.01.2020"); // Прошедшая дата
        $("[data-test-id='name'] input").setValue(validUser.getName());
        $("[data-test-id='phone'] input").setValue(validUser.getPhone());
        $("[data-test-id='agreement'] .checkbox__box").click();
        $(Selectors.byText("Запланировать")).click();

        // Проверяем сообщение об ошибке
        $("[data-test-id='date'] .input__sub")
                .shouldHave(exactText("Заказ на выбранную дату невозможен"))
                .shouldBe(visible);
    }

    @Test
    @DisplayName("Always failing test for demonstration")
    void shouldAlwaysFailForDemonstration() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForMeeting = 5;
        var meetingDate = DataGenerator.generateDate(daysToAddForMeeting);

        $("[data-test-id='city'] input").setValue(validUser.getCity());
        $("[data-test-id='date'] input")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE)
                .setValue(meetingDate);
        $("[data-test-id='name'] input").setValue(validUser.getName());
        $("[data-test-id='phone'] input").setValue(validUser.getPhone());
        $("[data-test-id='agreement'] .checkbox__box").click();
        $(Selectors.byText("Запланировать")).click();

        // Намеренно неправильная проверка - тест упадет
        $("[data-test-id='success-notification'] .notification__content")
                .shouldHave(exactText("Неверное сообщение")) // Это сообщение не появится
                .shouldBe(visible);
    }
}