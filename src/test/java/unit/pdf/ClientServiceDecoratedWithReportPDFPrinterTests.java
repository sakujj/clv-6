package unit.pdf;

import by.sakujj.dto.ClientRequest;
import by.sakujj.dto.ClientResponse;
import by.sakujj.pdf.ClientServiceDecoratedWithReportPDFPrinter;
import by.sakujj.pdf.ReportPDFConfig;
import by.sakujj.pdf.ReportPDFPrinter;
import by.sakujj.services.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import util.ClientTestBuilder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ClientServiceDecoratedWithReportPDFPrinterTests {

    @Mock
    private ClientService clientService;

    @Mock
    private ReportPDFConfig reportPDFConfig;


    private ClientServiceDecoratedWithReportPDFPrinter decorator;


    @BeforeEach
    void setUp() {
        decorator = new ClientServiceDecoratedWithReportPDFPrinter(
                clientService,
                reportPDFConfig,
                "",
                ""
        );
    }


    @Nested
    class findById {
        @Test
        void shouldCallClientServiceAndReportPDFPrinter() {
            // given
            ClientTestBuilder aClient = ClientTestBuilder.aClient();
            ClientResponse expected = aClient.buildResponse();

            Mockito.when(clientService.findById(aClient.getId()))
                    .thenReturn(Optional.of(expected));
            try (MockedStatic<ReportPDFPrinter> printerMockedStatic = Mockito.mockStatic(ReportPDFPrinter.class)) {

                // when
                Optional<ClientResponse> actualOptional = decorator.findById(aClient.getId());

                // then
                printerMockedStatic.verify(
                        () -> ReportPDFPrinter.printToPDF(Mockito.any(),
                                Mockito.any(),
                                Mockito.any()),
                        Mockito.times(1)
                );
                Mockito.verify(clientService).findById(aClient.getId());

                assertThat(actualOptional).isPresent();
                assertThat(actualOptional.get()).isEqualTo(expected);
            }
        }
    }

    @Nested
    class findByEmail {
        @Test
        void shouldCallClientServiceAndReportPDFPrinter() {
            // given
            ClientTestBuilder aClient = ClientTestBuilder.aClient();
            ClientResponse expected = aClient.buildResponse();

            Mockito.when(clientService.findByEmail(aClient.getEmail()))
                    .thenReturn(Optional.of(expected));
            try (MockedStatic<ReportPDFPrinter> printerMockedStatic = Mockito.mockStatic(ReportPDFPrinter.class)) {

                // when
                Optional<ClientResponse> actualOptional = decorator.findByEmail(aClient.getEmail());

                // then
                printerMockedStatic.verify(
                        () -> ReportPDFPrinter.printToPDF(Mockito.any(),
                                Mockito.any(),
                                Mockito.any()),
                        Mockito.times(1)
                );
                Mockito.verify(clientService).findByEmail(aClient.getEmail());

                assertThat(actualOptional).isPresent();
                assertThat(actualOptional.get()).isEqualTo(expected);
            }
        }
    }

    @Nested
    class findAll {
        @Test
        void shouldCallClientServiceAndReportPDFPrinter() {
            // given
            ClientResponse clientResponse1 = ClientTestBuilder.aClient().buildResponse();
            ClientResponse clientResponse2 = ClientTestBuilder.aClient()
                    .withId(UUID.fromString("7e6b649f-ba2a-4a14-8d53-7571c63ee397")).buildResponse();
            ClientResponse clientResponse3 = ClientTestBuilder.aClient()
                    .withId(UUID.fromString("7e6b649f-ba2a-4a14-8d53-7571c63ee397")).buildResponse();
            List<ClientResponse> expected = List.of(clientResponse1, clientResponse2, clientResponse3);

            Mockito.when(clientService.findAll())
                    .thenReturn(expected);
            try (MockedStatic<ReportPDFPrinter> printerMockedStatic = Mockito.mockStatic(ReportPDFPrinter.class)) {

                // when
                List<ClientResponse> actual = decorator.findAll();

                // then
                printerMockedStatic.verify(
                        () -> ReportPDFPrinter.printToPDF(Mockito.any(),
                                Mockito.any(),
                                Mockito.any()),
                        Mockito.times(1)
                );
                Mockito.verify(clientService).findAll();

                assertThat(actual).isNotEmpty();
                assertThat(actual).isEqualTo(expected);
            }
        }
    }

    @Nested
    class deleteById {
        @Test
        void shouldCallClientServiceAndReportPDFPrinter() {
            // given
            ClientTestBuilder aClient = ClientTestBuilder.aClient();
            boolean expected = true;

            Mockito.when(clientService.deleteById(aClient.getId()))
                    .thenReturn(expected);
            try (MockedStatic<ReportPDFPrinter> printerMockedStatic = Mockito.mockStatic(ReportPDFPrinter.class)) {

                // when
                boolean actual = decorator.deleteById(aClient.getId());

                // then
                printerMockedStatic.verify(
                        () -> ReportPDFPrinter.printToPDF(Mockito.any(),
                                Mockito.any(),
                                Mockito.any()),
                        Mockito.times(1)
                );
                Mockito.verify(clientService).deleteById(aClient.getId());

                assertThat(actual).isEqualTo(expected);
            }
        }
    }

    @Nested
    class updateById {
        @Test
        void shouldCallClientServiceAndReportPDFPrinter() {
            // given
            ClientTestBuilder aClient = ClientTestBuilder.aClient();
            ClientRequest clientRequest = aClient.buildRequest();
            boolean expected = true;

            Mockito.when(clientService.updateById(aClient.getId(), clientRequest))
                    .thenReturn(expected);
            try (MockedStatic<ReportPDFPrinter> printerMockedStatic = Mockito.mockStatic(ReportPDFPrinter.class)) {

                // when
                boolean actual = decorator.updateById(aClient.getId(), clientRequest);

                // then
                printerMockedStatic.verify(
                        () -> ReportPDFPrinter.printToPDF(Mockito.any(),
                                Mockito.any(),
                                Mockito.any()),
                        Mockito.times(1)
                );
                Mockito.verify(clientService).updateById(aClient.getId(), clientRequest);

                assertThat(actual).isEqualTo(expected);
            }
        }
    }

    @Nested
    class save {
        @Test
        void shouldCallClientServiceAndReportPDFPrinter() {
            // given
            ClientTestBuilder aClient = ClientTestBuilder.aClient();
            ClientRequest clientRequest = aClient.buildRequest();
            UUID expected = aClient.getId();

            Mockito.when(clientService.save(clientRequest))
                    .thenReturn(expected);
            try (MockedStatic<ReportPDFPrinter> printerMockedStatic = Mockito.mockStatic(ReportPDFPrinter.class)) {

                // when
                UUID actual = decorator.save(clientRequest);

                // then
                printerMockedStatic.verify(
                        () -> ReportPDFPrinter.printToPDF(Mockito.any(),
                                Mockito.any(),
                                Mockito.any()),
                        Mockito.times(1)
                );
                Mockito.verify(clientService).save(clientRequest);

                assertThat(actual).isEqualTo(expected);
            }
        }
    }
}
