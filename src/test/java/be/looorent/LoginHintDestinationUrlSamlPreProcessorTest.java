package be.looorent;

import org.junit.Test;

import java.net.URI;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LoginHintDestinationUrlSamlPreProcessorTest {

    private LoginHintDestinationUrlSamlPreProcessor processor = new LoginHintDestinationUrlSamlPreProcessor();

    private URI defaultDestinationUrl = URI.create("https://whatever.adfs.net/adfs/test");
    private URI destinationWithPlaceholder = URI.create("https://whatever.adfs.net/adfs/test?username=___login_hint___&login_hint=___login_hint___");

    @Test
    public void replaceLoginHintInDefaultDestinationWithoutSubject() {
        URI actual = processor.replaceLoginHint(defaultDestinationUrl, null);
        assertThat(actual, is(equalTo(defaultDestinationUrl)));
    }

    @Test
    public void replaceLoginHintInDefaultDestinationWithEmptySubject() {
        URI actual = processor.replaceLoginHint(defaultDestinationUrl, "");
        assertThat(actual, is(equalTo(defaultDestinationUrl)));
    }

    @Test
    public void replaceLoginHintInDefaultDestinationWithValidSubject() {
        URI actual = processor.replaceLoginHint(defaultDestinationUrl, "hello@world.com");
        assertThat(actual, is(equalTo(defaultDestinationUrl)));
    }

    @Test
    public void replaceLoginHintInPlaceholderWithoutSubject() {
        URI actual = processor.replaceLoginHint(destinationWithPlaceholder, null);
        assertThat(actual, is(equalTo(URI.create("https://whatever.adfs.net/adfs/test?username=&login_hint="))));
    }

    @Test
    public void replaceLoginHintIPlaceholderWithEmptySubject() {
        URI actual = processor.replaceLoginHint(destinationWithPlaceholder, "");
        assertThat(actual, is(equalTo(URI.create("https://whatever.adfs.net/adfs/test?username=&login_hint="))));
    }

    @Test
    public void replaceLoginHintIPlaceholderWithBlankSubject() {
        URI actual = processor.replaceLoginHint(destinationWithPlaceholder, "    ");
        assertThat(actual, is(equalTo(URI.create("https://whatever.adfs.net/adfs/test?username=&login_hint="))));
    }

    @Test
    public void replaceLoginHintInPlaceholderWithValidSubject() {
        URI actual = processor.replaceLoginHint(destinationWithPlaceholder, "hello@world.com");
        assertThat(actual, is(equalTo(URI.create("https://whatever.adfs.net/adfs/test?username=hello%40world.com&login_hint=hello%40world.com"))));
    }

    // TODO verify how to remove Œ
//    @Test
//    public void replaceLoginHintInPlaceholderWithInvalidCharactersSubject() {
//        URI actual = processor.replaceLoginHint(destinationWithPlaceholder, "helloŒ@wŒorld.com");
//        assertThat(actual, is(equalTo(URI.create("https://whatever.adfs.net/adfs/test?username=hello%40world.com&login_hint=hello%40world.com"))));
//    }
}