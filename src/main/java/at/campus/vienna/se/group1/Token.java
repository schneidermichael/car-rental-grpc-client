package at.campus.vienna.se.group1;

import io.grpc.CallCredentials;
import io.grpc.Metadata;
import io.grpc.Status;

import java.util.concurrent.Executor;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

public class Token extends CallCredentials {

    public static final Metadata.Key<String> AUTHORIZATION_METADATA_KEY = Metadata.Key.of("Authorization", ASCII_STRING_MARSHALLER);
    public static final String BEARER_TYPE = "Bearer";

    private String bearer;

    public Token(String bearer) {
        this.bearer = bearer;
    }

    @Override
    public void applyRequestMetadata(RequestInfo requestInfo, Executor executor, MetadataApplier metadataApplier) {
        executor.execute(() -> {
            try {
                Metadata headers = new Metadata();
                headers.put(AUTHORIZATION_METADATA_KEY, String.format("%s %s", BEARER_TYPE, bearer));
                metadataApplier.apply(headers);
            } catch (Exception e) {
                metadataApplier.fail(Status.UNAUTHENTICATED.withCause(e));
            }
        });
    }

    @Override
    public void thisUsesUnstableApi() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "Token{" +
                "bearer='" + bearer + '\'' +
                '}';
    }
}
