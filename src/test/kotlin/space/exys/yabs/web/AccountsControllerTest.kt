package space.exys.yabs.web

import io.restassured.RestAssured.given
import io.restassured.RestAssured.port
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import space.exys.yabs.App
import space.exys.yabs.model.Account
import space.exys.yabs.model.AccountId
import space.exys.yabs.web.model.AccountDto
import space.exys.yabs.web.model.CreateAccountDto
import space.exys.yabs.web.model.CreateTransferDto
import space.exys.yabs.web.model.OwnerDto
import java.math.BigDecimal


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("AccountsController")
class AccountsControllerTest {

    private val app = App()

    @BeforeAll
    fun setUp() {
        app.start()
        port = app.port()
    }

    @AfterAll
    fun tearDown() {
        // Stops the application when the tests have completed
        app.stop()
    }

    @Test
    fun `should return 400 if owners first name is empty when creating account`() {
        given()
                .contentType(ContentType.JSON)
                .body(CreateAccountDto(owner = OwnerDto(firstName = "", lastName = "Doe")))

                .`when`()
                .post("/accounts")

                .then()
                .statusCode(400)
    }

    @Test
    fun `should return 400 if owners last name is empty when creating account`() {
        given()
                .contentType(ContentType.JSON)
                .body(CreateAccountDto(owner = OwnerDto(firstName = "Joe", lastName = "")))

                .`when`()
                .post("/accounts")

                .then()
                .statusCode(400)
    }

    @Test
    fun `should create account`() {
        given()
                .contentType(ContentType.JSON)
                .body(CreateAccountDto(owner = OwnerDto(firstName = "Joe", lastName = "Doe")))

                .`when`()
                .post("/accounts")

                .then()
                .statusCode(201)
                .body(
                        "id", not(emptyString()),
                        "iban", not(emptyString()),
                        "balance", comparesEqualTo(0.0f),
                        "owner.firstName", equalTo("Joe"),
                        "owner.lastName", equalTo("Doe")
                )
    }

    @Test
    fun `should get account details`() {
        val id = createAccount()

        given()

                .`when`()
                .get("/accounts/{account_id}", id.toString())

                .then()
                .statusCode(200)
                .body(
                        "id", equalTo(id.toString()),
                        "iban", not(emptyString()),
                        "balance", comparesEqualTo(100.0f),
                        "owner.firstName", equalTo("Joe"),
                        "owner.lastName", equalTo("Doe")
                )
    }

    @Test
    fun `should return 404 when get account details with incorrect account id`() {
        given()

                .`when`()
                .get("/accounts/{account_id}", "WrongId")

                .then()
                .statusCode(404)
    }

    @Test
    fun `should return 404 when get account details of not existing account`() {
        given()

                .`when`()
                .get("/accounts/{account_id}", AccountId.random().toString())

                .then()
                .statusCode(404)
    }

    @Test
    fun `should make transfer between two accounts`() {
        val id1 = createAccount()
        val id2 = createAccount()

        given()
                .contentType(ContentType.JSON)
                .body(CreateTransferDto(accountId = id2, amount = BigDecimal("10.00")))

                .`when`()
                .post("/accounts/{account_id}/transfers", id1.toString())

                .then()
                .statusCode(201)

        assertThat(getAccount(id1).balance).isEqualByComparingTo("90.00")
        assertThat(getAccount(id2).balance).isEqualByComparingTo("110.00")
    }

    @Test
    fun `should return 412 if account has insufficient funds`() {
        val id1 = createAccount()
        val id2 = createAccount()

        given()
                .contentType(ContentType.JSON)
                .body(CreateTransferDto(accountId = id2, amount = BigDecimal("101.00")))

                .`when`()
                .post("/accounts/{account_id}/transfers", id1.toString())

                .then()
                .statusCode(412)

        assertThat(getAccount(id1).balance).isEqualByComparingTo("100.00")
    }

    @Test
    fun `should return 404 if debtor account does not exists`() {
        val id = createAccount()

        given()
                .contentType(ContentType.JSON)
                .body(CreateTransferDto(accountId = id, amount = BigDecimal("101.00")))

                .`when`()
                .post("/accounts/{account_id}/transfers", AccountId.random().toString())

                .then()
                .statusCode(404)
    }

    @Test
    fun `should return 404 if creditor account does not exists`() {
        val id = createAccount()

        given()
                .contentType(ContentType.JSON)
                .body(CreateTransferDto(accountId = AccountId.random(), amount = BigDecimal("101.00")))

                .`when`()
                .post("/accounts/{account_id}/transfers", id.toString())

                .then()
                .statusCode(404)
    }

    @Test
    fun `should return 400 if invalid amount`() {
        val id1 = createAccount()
        val id2 = createAccount()

        given()
                .contentType(ContentType.JSON)
                .body(CreateTransferDto(accountId = id2, amount = BigDecimal("-0.01")))

                .`when`()
                .post("/accounts/{account_id}/transfers", id1.toString())

                .then()
                .statusCode(400)
    }

    private fun createAccount(): AccountId =
            given()
                    .contentType(ContentType.JSON)
                    .body(CreateAccountDto(owner = OwnerDto(firstName = "Joe", lastName = "Doe")))
                    .`when`()
                    .post("/accounts")
                    .then()
                    .extract().body().`as`(AccountDto::class.java).id

    private fun getAccount(id: AccountId): AccountDto =
            given()
                    .`when`()
                    .get("/accounts/{account_id}", id.toString())
                    .then()
                    .extract().body().`as`(AccountDto::class.java)


}