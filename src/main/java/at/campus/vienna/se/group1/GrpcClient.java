package at.campus.vienna.se.group1;

import com.google.protobuf.Empty;
import grpc.currency.converter.*;
import grpc.currency.converter.CurrencyConverterGrpc;
import io.grpc.*;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GrpcClient {

    private static final Logger LOGGER = Logger.getLogger(GrpcClient.class.getName());

    private final ManagedChannel channel;
    private final CurrencyConverterGrpc.CurrencyConverterBlockingStub blockingStub;

    GrpcClient(String host, int port) {
        this(

                ManagedChannelBuilder
                        .forAddress(host, port)
                        .usePlaintext()
                        .build());
    }

    GrpcClient(String target, ChannelCredentials channelCredentials) {
        this(Grpc.newChannelBuilder(target, channelCredentials).build());
    }


    GrpcClient(ManagedChannel channel) {
        this.channel = channel;
        this.blockingStub = CurrencyConverterGrpc.newBlockingStub(channel);
    }

    public LoginResponse loginResponse(LoginRequest request) {

        LoginResponse response = blockingStub.login(LoginRequest.newBuilder().setUsername(request.getUsername()).setPassword(request.getPassword()).build());

        LOGGER.log(Level.INFO, () -> "Bearer Token: " + response.getToken());

        return response;
    }

    public ListOfCurrenciesResponse listOfCurrencies(Token token) {
        ListOfCurrenciesResponse response =
                blockingStub.withCallCredentials(token).listOfCurrencies(Empty
                        .newBuilder()
                        .build());

        LOGGER.log(Level.INFO, () -> "Response message of ListOfCurrenciesResponse: " + response);

        return response;
    }

    public CurrencyPerSymbolResponse currencyPerSymbolResponse(CurrencyPerSymbolRequest request, Token token) {
        CurrencyPerSymbolResponse response =
                blockingStub.withCallCredentials(token).currencyPerSymbol(CurrencyPerSymbolRequest
                        .newBuilder()
                        .setSymbol(request.getSymbol())
                        .build());

        LOGGER.log(Level.INFO, () -> "Response message of CurrencyPerSymbolResponse: " + response);

        return response;
    }

    public CalculatingCrossCurrencyResponse calculatingCrossCurrencyResponse(CalculatingCrossCurrencyRequest request, Token token) {
        CalculatingCrossCurrencyResponse response =
                blockingStub.withCallCredentials(token).calculatingCrossCurrency(CalculatingCrossCurrencyRequest
                        .newBuilder()
                        .setSymbolInput(request.getSymbolInput())
                        .setSymbolOutput(request.getSymbolOutput())
                        .setAmount(request.getAmount())
                        .build());

        LOGGER.log(Level.INFO, () -> "Response message of CalculatingCrossCurrencyResponse: " + response);

        return response;
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

}
