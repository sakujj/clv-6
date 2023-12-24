package unit.servlet.util;

import by.sakujj.dto.ClientRequest;
import by.sakujj.servlet.util.ClientsServletUtil;
import com.google.gson.Gson;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import util.ClientTestBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(MockitoExtension.class)
public class ClientsServletUtilTests {
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private Gson gson;
    @Mock
    private Validator validator;

    @ParameterizedTest
    @MethodSource
    void shouldParseUUIDFromRequestURI(String requestURI, String baseURI, UUID expected) {
        // given, when
        Optional<UUID> actual = ClientsServletUtil.parseUUIDFromRequestURI(requestURI, baseURI);

        // then
        assertThat(actual).isPresent();
        assertThat(actual.get()).isEqualTo(expected);
    }

    static Stream<Arguments> shouldParseUUIDFromRequestURI() {
        final String baseURIFirst = "http://request-uri:8080/someInfo/";
        final UUID uuidFirst = UUID.fromString("73b67952-28be-4dc3-9206-688e81515fde");

        final String baseURISecond = "httksd;jfjkslgdfk/df";
        final UUID uuidSecond = UUID.fromString("684cb249-766b-4ea1-8c1b-34be179f92fd");

        final String baseURIThird = "";
        final UUID uuidThird = UUID.fromString("1252eb96-754f-4a5b-9d52-baeef0f2fad4");

        return Stream.of(
                arguments(baseURIFirst + uuidFirst, baseURIFirst, uuidFirst),
                arguments(baseURISecond + uuidSecond, baseURISecond, uuidSecond),
                arguments(baseURIThird + uuidThird, baseURIThird, uuidThird)
        );
    }

    @Test
    void shouldReturnEmptyOptionalWhenParseUUIDFromRequestURI() {
        // given, when
        Optional<UUID> actual = ClientsServletUtil.parseUUIDFromRequestURI("base uri incorrect uuid", "base uri");

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void shouldGetPostRequestBody() throws IOException {
        // given
        String expected = "  ! something, something, test data, something, 1322  ";
        BufferedReader reader = new BufferedReader(new StringReader(expected));

        Mockito.when(httpServletRequest.getContentLength())
                .thenReturn(expected.length());
        Mockito.when(httpServletRequest.getReader())
                .thenReturn(reader);

        // when
        Optional<String> actual = ClientsServletUtil.getPostRequestBodyOrWriteApiErrorToResponse(httpServletRequest, httpServletResponse, gson);

        // then
        assertThat(actual).isPresent();
        assertThat(actual.get()).isEqualTo(expected);
    }

    @Test
    void shouldGetValidatedClientRequest() throws IOException {
        // given
        String expectedJsonData = "  expected json data  ";
        ClientRequest expected = ClientTestBuilder.aClient().buildRequest();

        Mockito.when(gson.fromJson(expectedJsonData, ClientRequest.class))
                .thenReturn(expected);
        Mockito.when(validator.validate(expected))
                .thenReturn(Set.of());

        // when
        Optional<ClientRequest> actual = ClientsServletUtil.getValidatedClientRequestOrWriteApiErrorToResponse(
                expectedJsonData,
                validator,
                httpServletResponse,
                gson);

        // then
        assertThat(actual).isPresent();
        assertThat(actual.get()).isEqualTo(expected);
    }


}
