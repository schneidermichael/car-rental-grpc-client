package at.campus.vienna.se.group1;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import grpc.currency.converter.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
class GrpcClientTest {

    static int hostPort = 43174;
    static int containerExposedPort = 80;
    static Consumer<CreateContainerCmd> cmd = e -> e.withPortBindings(new PortBinding(Ports.Binding.bindPort(hostPort), new ExposedPort(containerExposedPort)));

    @Container
    static
    GenericContainer currencyConverter = new GenericContainer(DockerImageName.parse("michaelxschneider/carrentalconverter:0.0.3"))
            .withExposedPorts(containerExposedPort)
            .withCreateContainerCmdModifier(cmd);


    static GrpcClient plainClient;
    static LoginResponse login;
    static Token token;

    @BeforeAll
    static void setup()  {

        currencyConverter.start();

        plainClient = new GrpcClient("localhost",43174);

        LoginRequest request = LoginRequest.newBuilder().setPassword("car").setUsername("group1").build();

        login = plainClient.loginResponse(request);

        token = new Token(login.getToken());

    }

    @Test
    void LoginInSecureTest() {

        //Assert
        assertNotNull(token);
    }

    @Test
    void listOfCurrenciesInSecureTest(){
        //Arrange

        //Act
        ListOfCurrenciesResponse response = plainClient.listOfCurrencies(token);

        //Assert
        assertEquals(32, response.getCurrenciesList().size());
    }

    @Test
    void currencyPerSymbolInSecureTest(){
        //Arrange
        CurrencyPerSymbolRequest request = CurrencyPerSymbolRequest
                .newBuilder()
                .setSymbol("USD")
                .build();

        //Act
        CurrencyPerSymbolResponse response = plainClient.currencyPerSymbolResponse(request,token);

        //Assert
        assertEquals(request.getSymbol(),response.getCurreny().getSymbol());
    }

    @Test
    void calculatingCrossCurrencyInSecureTest() throws ExpiredTokenException {
        //Arrange
        CalculatingCrossCurrencyRequest request = CalculatingCrossCurrencyRequest
                .newBuilder()
                .setSymbolInput("USD")
                .setSymbolOutput("EUR")
                .setAmount(5.0).build();

        //Act
        CalculatingCrossCurrencyResponse response = plainClient.calculatingCrossCurrencyResponse(request,token);

        //Assert
        assertEquals(request.getSymbolOutput(),response.getSymbol());
    }

    @AfterAll
    static void teardown() throws InterruptedException {
        plainClient.shutdown();
        currencyConverter.stop();
    }

}
