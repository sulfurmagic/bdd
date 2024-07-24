package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import data.DataHelper;
import page.DashboardPage;
import page.LoginPage;


import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static data.DataHelper.generateInvalidAmount;

class MoneyTransferTest {
    DashboardPage dashboardPage;
    DataHelper.CardInfo firstCardInfo;
    DataHelper.CardInfo secondCardInfo;
    int firstCardBalance;
    int secondCardBalance;

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor();
        dashboardPage = verificationPage.validVerify(verificationCode);
        firstCardInfo = DataHelper.getFirstCardInfo();
        secondCardInfo = DataHelper.getSecondCardInfo();
        firstCardBalance = dashboardPage.getCardBalance(0);
        secondCardBalance = dashboardPage.getCardBalance(1);
    }

    @Test
    void shouldTransferFrom1stTo2nd() {
        var amount = generateInvalidAmount(firstCardBalance);
        var expectedBalanceFirstCard = firstCardBalance - amount;
        var expectedBalanceSecondCard = secondCardBalance + amount;
        var transferPage = dashboardPage.selectCardToTransfer(secondCardInfo);
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), firstCardInfo);
        dashboardPage.reloadDashboardPage();
        var actualBalanceFirstCard = dashboardPage.getCardBalance(0);
        var actualBalanceSecondCard = dashboardPage.getCardBalance(1);
        assertAll(() -> assertEquals(expectedBalanceFirstCard, actualBalanceFirstCard),
                () -> assertEquals(expectedBalanceSecondCard, actualBalanceSecondCard));

    }

    @Test
    void shouldErrorIfAmountBiggerBalance() {
        var amount = generateInvalidAmount(secondCardBalance);
        var transferPage = dashboardPage.selectCardToTransfer(firstCardInfo);
        transferPage.makeTransfer(String.valueOf(amount), secondCardInfo);
        transferPage.findErrorMessage("Ошибка!");
        dashboardPage.reloadDashboardPage();
        var actualBalanceFirstCard = dashboardPage.getCardBalance(0);
        var actualBalanceSecondCard = dashboardPage.getCardBalance(1);
        assertAll(() -> assertEquals(firstCardBalance, actualBalanceFirstCard),
                () -> assertEquals(secondCardBalance, actualBalanceSecondCard));
    }
}