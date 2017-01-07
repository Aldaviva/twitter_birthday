package com.aldaviva.test;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTestNg;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;

import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import java.net.URI;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertNotNull;

/**
 * Mock out a Jersey Client so your service client tests don't hit the network.
 *
 * 1. Subclass this class into your own test class.
 * 2. Add an @InjectMocks field for your service under test. It should have an @Autowired Client field in the service class.
 * 3. Do any test setup (mocking, stubbing, Whiteboxing) in a @BeforeMethod method of your test class.
 * 4. Add a @Path+@GET/@POST+@Produces+@Consumes method in your test class to stub the HTTP response. Assertions are supported.
 * 5. In a @Test method, call a method on your service which makes HTTP requests.
 *
 * @see com.aldaviva.twitter.birthday.twitter.TwitterBirthdayUpdaterTest
 */
@Path("/")
@SuppressWarnings("RestResourceMethodInspection")
public abstract class JerseyClientTest extends JerseyTestNg.ContainerPerMethodTest {

    @Mock protected Client httpClient;

    @BeforeMethod
    public void initHttpClient() {
        MockitoAnnotations.initMocks(this);

        when(httpClient.target(any(URI.class))).thenAnswer(new Answer<WebTarget>() {
            @Override
            public WebTarget answer(final InvocationOnMock invocation) throws Throwable {
                final URI uri = invocation.getArgumentAt(0, URI.class);
                assertNotNull(uri, "client target URI");
                return JerseyClientTest.this.target(uri.getPath());
            }
        });
    }

    @Override
    protected Application configure() {
        return new ResourceConfig(getClass());
    }

}
