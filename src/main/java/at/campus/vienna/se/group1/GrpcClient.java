package at.campus.vienna.se.group1;

import com.google.protobuf.Empty;
import grpc.currency.converter.*;
import grpc.currency.converter.CurrencyConverterGrpc;
import io.grpc.*;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A gRPC Client for the proto currency_converter_service.proto
 */
public class GrpcClient {

    private static final Logger LOGGER = Logger.getLogger(GrpcClient.class.getName());

    private final ManagedChannel channel;
    private final CurrencyConverterGrpc.CurrencyConverterBlockingStub blockingStub;

    /**
     * Generate a client without TLS
     * @param host Host name only
     * @param port Port number only
     */
    public GrpcClient(String host, int port) {
        this(

                ManagedChannelBuilder
                        .forAddress(host, port)
                        .usePlaintext()
                        .build());
    }

    /**
     * Generate a client with TLS
     * @param target Combination of Host and Port - host:port
     * @param channelCredentials TlsChannelCredentials with your certificate
     */
    public GrpcClient(String target, ChannelCredentials channelCredentials) {
        this(Grpc.newChannelBuilder(target, channelCredentials).build());
    }

    /**
     * General constructor
     * @param channel
     */
    public GrpcClient(ManagedChannel channel) {
        this.channel = channel;
        this.blockingStub = CurrencyConverterGrpc.newBlockingStub(channel);
    }

    /**
     * Get Bearer Token for ListOfCurrencies, CurrencyPerSymbol and CalculatingCrossCurrency
     * @param request
     * @return containing the Bearer Token
     */
    public LoginResponse loginResponse(LoginRequest request) {

        LoginResponse response = blockingStub.login(LoginRequest.newBuilder().setUsername(request.getUsername()).setPassword(request.getPassword()).build());

        LOGGER.log(Level.INFO, () -> "Bearer Token: " + response.getToken());

        return response;
    }

    /**
     * Get a List of available currencies
     * @param token Bearer Token for Authorization
     * @return containing a list of currencies
     */
    public ListOfCurrenciesResponse listOfCurrencies(Token token) {
        ListOfCurrenciesResponse response =
                blockingStub.withCallCredentials(token).listOfCurrencies(Empty
                        .newBuilder()
                        .build());

        LOGGER.log(Level.INFO, () -> "Response message of ListOfCurrenciesResponse: " + response);

        return response;
    }

    /**
     * Get only one specific currency
     * @param request containing a three-letter symbol of the currency
     * @param token Bearer Token for Authorization
     * @return containing one currency
     */
    public CurrencyPerSymbolResponse currencyPerSymbolResponse(CurrencyPerSymbolRequest request, Token token) {
        CurrencyPerSymbolResponse response =
                blockingStub.withCallCredentials(token).currencyPerSymbol(CurrencyPerSymbolRequest
                        .newBuilder()
                        .setSymbol(request.getSymbol())
                        .build());

        LOGGER.log(Level.INFO, () -> "Response message of CurrencyPerSymbolResponse: " + response);

        return response;
    }

    /**
     * Calculating from currency X to currency Y
     * @param request containing a three-letter symbol as input, a three-letter symbol as output and an amount
     * @param token Bearer Token for Authorization
     * @return containg the result of the exchange
     */
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

    /**
     * Closes the channel after five seconds
     * @throws InterruptedException
     */
    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

}
