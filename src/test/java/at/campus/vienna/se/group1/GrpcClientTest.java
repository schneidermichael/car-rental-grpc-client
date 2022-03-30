package at.campus.vienna.se.group1;

import grpc.currency.converter.*;
import io.grpc.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GrpcClientTest {

    static GrpcClient secureClient;
    static GrpcClient plainClient;
    static LoginResponse login;
    static Token token;

    @BeforeAll
    static void setup() throws IOException {

        ChannelCredentials channelCredentials = TlsChannelCredentials.newBuilder()
                .trustManager(new File("src/main/resources/car-rental-converter.cer"))
                .build();

        secureClient = new GrpcClient("localhost:7241", channelCredentials);

        plainClient = new GrpcClient("localhost",43174);

        LoginRequest request = LoginRequest.newBuilder().setPassword("car").setUsername("group1").build();

        login = plainClient.loginResponse(request);

        token = new Token(login.getToken());

    }

    @Test
    @Disabled
    void LoginSecureTest() {

        //Assert
        assertNotNull(token);
    }

    @Test
    void LoginInSecureTest() {

        //Assert
        assertNotNull(token);
    }

    @Test
    @Disabled
    void listOfCurrenciesSecureTest(){
        //Arrange

        //Act
        ListOfCurrenciesResponse response = secureClient.listOfCurrencies(token);

        //Assert
        assertEquals(32, response.getCurrenciesList().size());
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
    @Disabled
    void currencyPerSymbolSecureTest(){
        //Arrange
        CurrencyPerSymbolRequest request = CurrencyPerSymbolRequest
                .newBuilder()
                .setSymbol("USD")
                .build();

        //Act
        CurrencyPerSymbolResponse response = secureClient.currencyPerSymbolResponse(request,token);

        //Assert
        assertEquals(request.getSymbol(),response.getCurreny().getSymbol());
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
    @Disabled
    void calculatingCrossCurrencySecureTest(){
        //Arrange
        CalculatingCrossCurrencyRequest request = CalculatingCrossCurrencyRequest
                .newBuilder()
                .setSymbolInput("USD")
                .setSymbolOutput("EUR")
                .setAmount(5.0).build();

        //Act
        CalculatingCrossCurrencyResponse response = secureClient.calculatingCrossCurrencyResponse(request,token);

        //Assert
        assertEquals(request.getSymbolOutput(),response.getSymbol());
    }

    @Test
    void calculatingCrossCurrencyInSecureTest(){
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
    }

}
