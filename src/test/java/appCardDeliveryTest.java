import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.Selenide.*;

public class appCardDeliveryTest {

    public String getDatePlus3Day() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.add(Calendar.DATE, 3);
        String newDate = formatter.format(instance.getTime());
        return newDate;
    }

    public String getWrongDate() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        String newDate = formatter.format(instance.getTime());
        return newDate;
    }

    @BeforeEach
    public void conf() {
        browser = "firefox";
        open("http://localhost:9999/");
        Configuration.timeout = 15000;
    }

    @Test
    void orderingCardDelivery() {
        String datePlus3Day = getDatePlus3Day();
        $("[data-test-id='city'] input").setValue("Санкт-Петербург");
        $("[placeholder='Дата встречи']").setValue(datePlus3Day);
        $("[data-test-id='name'] input").setValue("Пышкин Кот");
        $("[data-test-id='phone'] input").setValue("+79522263366");
        $(".checkbox__box").click();
        $(".button").click();
        $("[data-test-id='notification'].notification_visible div.notification__content")
                .shouldHave(exactText("Встреча успешно забронирована на " + datePlus3Day));
    }

    @Test
    void orderingCardDeliveryWithCompoundSurname() {
        String datePlus3Day = getDatePlus3Day();
        $("[data-test-id='city'] input").setValue("Санкт-Петербург");
        $("[placeholder='Дата встречи']").setValue(datePlus3Day);
        $("[data-test-id='name'] input").setValue("Капабланка-и-Граупера Олег");
        $("[data-test-id='phone'] input").setValue("+79522263366");
        $(".checkbox__box").click();
        $(".button").click();
        $("[data-test-id='notification'].notification_visible div.notification__content")
                .shouldHave(exactText("Встреча успешно забронирована на " + datePlus3Day));
    }

    @Test
    void EmptyFormSendTest() {
        $("[data-test-id='city'] input").setValue("");
        $("[placeholder='Дата встречи']").setValue("");
        $("[data-test-id='name'] input").setValue("");
        $("[data-test-id='phone'] input").setValue("");
        $(".checkbox__box").click();
        $(".button").click();
        $("[data-test-id='city'].input_invalid span.input__sub")
                .shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    void orderingCardDeliveryWithWrongCity() {
        String datePlus3Day = getDatePlus3Day();
        $("[data-test-id='city'] input").setValue("Санктетербург");
        $("[placeholder='Дата встречи']").setValue(datePlus3Day);
        $("[data-test-id='name'] input").setValue("Пышкин Кот");
        $("[data-test-id='phone'] input").setValue("+79522263366");
        $(".checkbox__box").click();
        $(".button").click();
        $("[data-test-id='city'].input_invalid span.input__sub")
                .shouldHave(exactText("Доставка в выбранный город недоступна"));
    }

    @Test
    void orderingCardDeliveryWithWrongDate() {
        String wrongDate = getWrongDate();
        $("[data-test-id='city'] input").setValue("Санкт-Петербург");
        $("[placeholder='Дата встречи']").setValue(wrongDate);
        $("[data-test-id='name'] input").setValue("Пышкин Кот");
        $("[data-test-id='phone'] input").setValue("+79522263366");
        $(".checkbox__box").click();
        $(".button").click();
        $("[data-test-id='date'] span.input__sub")
                .shouldHave(exactText("Заказ на выбранную дату невозможен"));
    }

    @Test
    void orderingCardDeliveryWithWrongNumber() {
        String datePlus3Day = getDatePlus3Day();
        $("[data-test-id='city'] input").setValue("Санкт-Петербург");
        $("[placeholder='Дата встречи']").setValue(datePlus3Day);
        $("[data-test-id='name'] input").setValue("Пышкин Кот");
        $("[data-test-id='phone'] input").setValue("79522263366");
        $(".checkbox__box").click();
        $(".button").click();
        $("[data-test-id='phone'].input_invalid span.input__sub")
                .shouldHave(exactText("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    @Test
    void dropDownTest() {
        String datePlus3Day = getDatePlus3Day();
        $("[data-test-id='city'] input").setValue("Сан");
        $$("[tabindex].menu span.menu-item__control").findBy(text("Санкт-Петербург")).click();
        $("[placeholder='Дата встречи']").setValue(datePlus3Day);
        $("[data-test-id='name'] input").setValue("Пышкин Кот");
        $("[data-test-id='phone'] input").setValue("+79522263366");
        $(".checkbox__box").click();
        $("[data-test-id='city'] input")
                .shouldHave(Condition.attribute("value", "Санкт-Петербург"));
    }

    @Test
    void calendarTest() {
        String datePlus3Day = getDatePlus3Day();
        String date = String.valueOf(Integer.parseInt(datePlus3Day.substring(0, 2)));
        $("[data-test-id='city'] input").setValue("Санкт-Петербург");
        $("[role='button'].icon-button span.icon_name_calendar").click();
        $$("[role='gridcell'].calendar__day").findBy(text(date)).click();
        $("[data-test-id='name'] input").setValue("Пышкин Кот");
        $("[data-test-id='phone'] input").setValue("+79522263366");
        $(".checkbox__box").click();
        $(".button").click();
        $(".notification_visible").shouldBe(visible);
    }

    @Test
    void calendarTestWrongDayClick() {
        String datePlus3Day = getWrongDate();
        String date = String.valueOf(Integer.parseInt(datePlus3Day.substring(0, 2)));
        $("[data-test-id='city'] input").setValue("Санкт-Петербург");
        $("[role='button'].icon-button span.icon_name_calendar").click();
        $$("[role='gridcell'].calendar__day").findBy(text(date)).click();
        $("[data-test-id='name'] input").setValue("Пышкин Кот");
        $("[data-test-id='phone'] input").setValue("+79522263366");
        $(".checkbox__box").click();
        $(".button").click();
        $(".notification_visible").shouldBe(visible);
    }
}

