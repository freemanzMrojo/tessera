package com.github.nexus.api;

import com.github.nexus.api.model.ReceiveResponse;
import com.github.nexus.api.model.SendResponse;
import com.github.nexus.service.TransactionService;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class TransactionResourceTest extends JerseyTest {

    @Mock
    private TransactionService transactionService;

    @Override
    public Application configure() {
        MockitoAnnotations.initMocks(this);

        when(transactionService.send(any(), any(), any())).thenReturn("mykey".getBytes());
        when(transactionService.receive(any(), any())).thenReturn("foo".getBytes());

        return new ResourceConfig()
                .register(new TransactionResource(transactionService));
    }

    @After
    public void onTearDown() {
        verifyNoMoreInteractions(transactionService);
    }

    @Test
    public void testSend() {

        JsonObject requestObj = Json.createObjectBuilder()
                .add("payload", "Zm9v")
                .add("from", "bXlwdWJsaWNrZXk=")
                .add("to", "cmVjaXBpZW50MQ==").build();

        Response response = target("/transaction/send")
                .request(MediaType.APPLICATION_JSON)
                .buildPost(Entity.entity(requestObj.toString(), MediaType.APPLICATION_JSON))
                .invoke();

        verify(transactionService, times(1)).send(any(),any(),any());
        assertThat(response).isNotNull();
        assertEquals("bXlrZXk=", response.readEntity(SendResponse.class).getKey());
        assertThat(response.getStatus()).isEqualTo(201);

    }

    @Test
    public void testSendRaw() {

        Response response = target("/transaction/sendraw")
                .request(MediaType.TEXT_PLAIN)
                .header("hFrom", "c2VuZGVy")
                .header("hTo", new String[]{"cmVjaXBpZW50MQ=="})
                .buildPost(Entity.entity("Zm9v", MediaType.TEXT_PLAIN))
                .invoke();

//        verify(transactionService, times(1)).send(any(),any(),any());
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(201);
    }

    @Test
    public void testReceive() {

        JsonObject requestObj = Json.createObjectBuilder()
                .add("key", "ROAZBWtSacxXQrOe3FGAqJDyJjFePR5ce4TSIzmJ0Bc=")
                .add("to", "cmVjaXBpZW50MQ==").build();

        Response response = target("/transaction/receive")
                .request(MediaType.APPLICATION_JSON)
                .buildPost(Entity.entity(requestObj.toString(), MediaType.APPLICATION_JSON))
                .invoke();

        verify(transactionService, times(1)).receive(any(),any());
        assertThat(response).isNotNull();
        assertEquals("Zm9v", response.readEntity(ReceiveResponse.class).getPayload());
        assertThat(response.getStatus()).isEqualTo(201);
    }

    @Test
    public void testReceiveRaw() {

        Response response = target("/transaction/receiveraw")
                .request(MediaType.APPLICATION_JSON)
                .header("hKey", "FOO")
                .header("hTo", "BAR")
                .buildPost(Entity.entity("", MediaType.TEXT_PLAIN))
                .invoke();

//        verify(transactionService, times(1)).receive();
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(201);
    }

    @Test
    public void testDelete() {

        JsonObject requestObj = Json.createObjectBuilder()
                .add("key", "mykey")
                .build();

        Response response = target("/transaction/delete")
                .request(MediaType.APPLICATION_JSON)
                .buildPost(Entity.entity(requestObj.toString(), MediaType.APPLICATION_JSON))
                .invoke();
//
//        verify(transactionService, times(1)).delete(any());
//        assertThat(response).isNotNull();
//        assertThat(response.getStatus()).isEqualTo(201);
    }

    @Test
    public void testResend() {

        JsonObject requestObj = Json.createObjectBuilder()
                .add("type", "test")
                .add("publickey", "mypublickey")
                .add("key", "mykey")
                .build();

        Response response = target("/transaction/resend")
                .request(MediaType.APPLICATION_JSON)
                .buildPost(Entity.entity(requestObj.toString(), MediaType.APPLICATION_JSON))
                .invoke();
//
//        assertThat(response).isNotNull();
//        assertThat(response.getStatus()).isEqualTo(201);
    }

    @Test
    public void testPush() {
        JsonObject requestObj = Json.createObjectBuilder()
                .build();

        Response response = target("/transaction/push")
                .request(MediaType.APPLICATION_JSON)
                .buildPost(Entity.entity(requestObj.toString(), MediaType.APPLICATION_JSON))
                .invoke();
//
//        assertThat(response).isNotNull();
//        assertThat(response.getStatus()).isEqualTo(201);
    }

    @Test
    public void testUpdatePartyInfo() {
        JsonObject requestObj = Json.createObjectBuilder()
                .build();

        Response response = target("/transaction/partyinfo")
                .request(MediaType.APPLICATION_JSON)
                .buildPost(Entity.entity(requestObj.toString(), MediaType.APPLICATION_JSON))
                .invoke();

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(201);
    }

    @Test
    public void testReadInputStreamJustForCoverage() throws IOException {

        final String data = "I LOVE SPARROWS!!";
        
        InputStream inputStream = spy(new ByteArrayInputStream(data.getBytes()));

        String result = TransactionResource.readInputStream(inputStream);
            
        assertThat(result).isEqualTo(data);
        verify(inputStream).close();

    }

    @Test
    public void testReadInputStreamJustForCoverageThrowsIO() throws IOException {

        InputStream inputStream = mock(InputStream.class);

        try {
            TransactionResource.readInputStream(inputStream);
            fail();
        } catch (UncheckedIOException ex) {
            assertThat(ex).isNotNull();
        }
        verify(inputStream).close();

    }
}
