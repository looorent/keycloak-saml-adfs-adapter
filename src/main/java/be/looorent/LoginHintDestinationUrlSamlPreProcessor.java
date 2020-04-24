package be.looorent;

import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.dom.saml.v2.assertion.NameIDType;
import org.keycloak.dom.saml.v2.assertion.SubjectType;
import org.keycloak.dom.saml.v2.protocol.AuthnRequestType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.protocol.saml.preprocessor.SamlAuthenticationPreprocessor;
import org.keycloak.sessions.AuthenticationSessionModel;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

import static java.util.Optional.ofNullable;

public class LoginHintDestinationUrlSamlPreProcessor implements SamlAuthenticationPreprocessor {

    private static final Logger LOG = Logger.getLogger(LoginHintDestinationUrlSamlPreProcessor.class);
    private static final String LOGIN_HINT_INVALID_CHARACTERS = "![ -~]"; // based on https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-oapx/a8622e66-2285-43c0-bbb9-abfcecdaed86
    private static final String PLACEHOLDER = "___login_hint___";

    @Override
    public AuthnRequestType beforeSendingLoginRequest(AuthnRequestType authnRequest,
                                                      AuthenticationSessionModel clientSession) {
        authnRequest.setDestination(ofNullable(authnRequest.getDestination())
                .map(url -> replaceLoginHint(url, parseSubject(authnRequest)))
                .orElse(null));
        return authnRequest;
    }

    @Override
    public SamlAuthenticationPreprocessor create(KeycloakSession session) {
        LOG.info("Create preprocessor to replace login_hint placeholders in SAML destination url");
        return new LoginHintDestinationUrlSamlPreProcessor();
    }

    @Override
    public void init(Config.Scope config) {}

    @Override
    public void postInit(KeycloakSessionFactory factory) {}

    @Override
    public void close() {}

    @Override
    public String getId() {
        return "login-hint-destination-url-saml-preprocessor";
    }

    URI replaceLoginHint(URI destination, String subject) {
        LOG.infof("Replacing {{login_hint}} in '%s' with '%s'", destination, subject);
        return URI.create(destination.toString().replace(PLACEHOLDER, sanitizeSubject(subject)));
    }

    private String sanitizeSubject(String subject) {
        return ofNullable(subject)
                .map(String::trim)
                .map(hint -> hint.replaceAll(LOGIN_HINT_INVALID_CHARACTERS, ""))
                .map(hint -> hint.replace("?", ""))
                .filter(hint -> !hint.isEmpty())
                .map(hint -> {
                    try {
                        return URLEncoder.encode(hint, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        LOG.infof("Error when encoding subject %s", hint, e);
                        return null;
                    }
                })
                .orElse("");
    }

    String parseSubject(AuthnRequestType request) {
        return ofNullable(request.getSubject())
                .map(SubjectType::getSubType)
                .map(SubjectType.STSubType::getBaseID)
                .filter(baseId -> baseId instanceof NameIDType)
                .map(NameIDType.class::cast)
                .map(NameIDType::getValue)
                .orElse("keyclaok@not-ready.com"); // TODO wait for subject to be injected in request: https://issues.redhat.com/browse/KEYCLOAK-13950
    }
}
