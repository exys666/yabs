package space.exys.yabs.web

import io.restassured.RestAssured.given
import io.restassured.RestAssured.port
import io.restassured.http.ContentType
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import space.exys.yabs.App
import space.exys.yabs.model.AccountId
import space.exys.yabs.web.model.AccountDto
import space.exys.yabs.web.model.CreateAccountDto
import space.exys.yabs.web.model.OwnerDto


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
                        "balance", comparesEqualTo(0.0f),
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
    fun `should return 404 when get account details of not existsing account`() {
        given()

                .`when`()
                .get("/accounts/{account_id}", AccountId.random().toString())

                .then()
                .statusCode(404)
    }

    private fun createAccount(): AccountId =
            given()
                    .contentType(ContentType.JSON)
                    .body(CreateAccountDto(owner = OwnerDto(firstName = "Joe", lastName = "Doe")))
                    .`when`()
                    .post("/accounts")
                    .then()
                    .extract().body().`as`(AccountDto::class.java).id


}