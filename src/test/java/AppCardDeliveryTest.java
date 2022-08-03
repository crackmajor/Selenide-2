import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.Selenide.*;

public class AppCardDeliveryTest {

    public String getDateWithShift(int shift) {
        return LocalDate.now().plusDays(shift).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public static boolean x(String q, String w) {
        return q.equalsIgnoreCase(w);
    }

    @BeforeEach
    public void conf() {
        browser = "firefox";
        open("http://localhost:9999/");
        Configuration.timeout = 15000;
    }

    @Test
    void orderingCardDelivery() {
        String planningDate = getDateWithShift(3);
        $("[data-test-id='city'] input").setValue("Санкт-Петербург");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[placeholder='Дата встречи']").setValue(planningDate);
        $("[data-test-id='name'] input").setValue("Пышкин Кот");
        $("[data-test-id='phone'] input").setValue("+79522263366");
        $(".checkbox__box").click();
        $(".button").click();
        $("[data-test-id='notification'].notification_visible div.notification__content")
                .shouldHave(exactText("Встреча успешно забронирована на " + planningDate));
    }

    @Test
    void orderingCardDeliveryWithCompoundSurname() {
        String planningDate = getDateWithShift(3);
        $("[data-test-id='city'] input").setValue("Санкт-Петербург");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[placeholder='Дата встречи']").setValue(planningDate);
        $("[data-test-id='name'] input").setValue("Капабланка-и-Граупера Олег");
        $("[data-test-id='phone'] input").setValue("+79522263366");
        $(".checkbox__box").click();
        $(".button").click();
        $("[data-test-id='notification'].notification_visible div.notification__content")
                .shouldHave(exactText("Встреча успешно забронирована на " + planningDate));
    }

    @Test
    void EmptyFormSendTest() {
        $("[data-test-id='city'] input").setValue("");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE, Keys.TAB);
        $("[data-test-id='name'] input").setValue("");
        $("[data-test-id='phone'] input").setValue("");
        $(".checkbox__box").click();
        $(".button").click();
        $("[data-test-id='city'].input_invalid span.input__sub")
                .shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    void orderingCardDeliveryWithWrongCity() {
        $("[data-test-id='city'] input").setValue("Санктетербург");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[placeholder='Дата встречи']").setValue(getDateWithShift(3));
        $("[data-test-id='name'] input").setValue("Пышкин Кот");
        $("[data-test-id='phone'] input").setValue("+79522263366");
        $(".checkbox__box").click();
        $(".button").click();
        $("[data-test-id='city'].input_invalid span.input__sub")
                .shouldHave(exactText("Доставка в выбранный город недоступна"));
    }

    @Test
    void orderingCardDeliveryWithWrongDate() {
        $("[data-test-id='city'] input").setValue("Санкт-Петербург");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[placeholder='Дата встречи']").setValue(getDateWithShift(2));
        $("[data-test-id='name'] input").setValue("Пышкин Кот");
        $("[data-test-id='phone'] input").setValue("+79522263366");
        $(".checkbox__box").click();
        $(".button").click();
        $("[data-test-id='date'] span.input__sub")
                .shouldHave(exactText("Заказ на выбранную дату невозможен"));
    }

    @Test
    void orderingCardDeliveryWithWrongNumber() {
        $("[data-test-id='city'] input").setValue("Санкт-Петербург");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[placeholder='Дата встречи']").setValue(getDateWithShift(3));
        $("[data-test-id='name'] input").setValue("Пышкин Кот");
        $("[data-test-id='phone'] input").setValue("79522263366");
        $(".checkbox__box").click();
        $(".button").click();
        $("[data-test-id='phone'].input_invalid span.input__sub")
                .shouldHave(exactText("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    @Test
    void calendarTestAndDropDownMenuTest() {
        String planningDate = getDateWithShift(80);
        String[] monthNames = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
        int monthIndex = Integer.parseInt(planningDate.substring(3, 5)) - 1;
        String date = String.valueOf(Integer.parseInt(planningDate.substring(0, 2)));
        String planningMonthYear = monthNames[monthIndex] + " " + Integer.parseInt(planningDate.substring(6, 10));

        $("[data-test-id='city'] input").setValue("Са");
        $$("[tabindex].menu span.menu-item__control").findBy(text("Санкт-Петербург")).click();
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[role='button'].icon-button span.icon_name_calendar").click();
        while (!x(($("[class='calendar__name']").getOwnText()), planningMonthYear)) {
            $("[class='calendar__arrow calendar__arrow_direction_right'].calendar__arrow").click();
        }
        $$("[role='gridcell'].calendar__day").findBy(text(date)).click();
        $("[data-test-id='name'] input").setValue("Пышкин Кот");
        $("[data-test-id='phone'] input").setValue("+79522263366");
        $(".checkbox__box").click();
        $("[data-test-id='city'] input")
                .shouldHave(Condition.attribute("value", "Санкт-Петербург"));
        $(".button").click();
        $("[data-test-id='notification'].notification_visible div.notification__content")
                .shouldHave(exactText("Встреча успешно забронирована на " + planningDate));
    }

}