# Keycloak - SAML adapter for ADFS

This adapter aims at adding ADFS specific features such as `username` and `login_hint` as described in https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-oapx/a8622e66-2285-43c0-bbb9-abfcecdaed86

## Motivation

SAML's Subject element can prefill the username input field during the login process.
`Subject` is an optional standard element of SAML requests: See https://docs.oasis-open.org/security/saml/v2.0/saml-core-2.0-os.pdf, line 2017 and below.
However, `username` and `login_hint` are not.

This adapter reads the subject in the SAML request and forward it to SAML IDP.

Please note this adapter has been designed for ADFS. However, many other IDP can take advantage of these features.

## Keycloak Support

* Support for Keycloak 12+

## Supported features

* Replace expression ``___login_hint___`` inside SAML's destination url during login phase

## Deployment

### Standalone install

* Download `dist/keycloak-saml-adfs-adapter-1.0.0.jar` from this repository
* Add it to `$KEYCLOAK_HOME/standalone/deployments/`

### Docker install

If you are using the official Docker image, here is a `Dockerfile` that automate the install procedure described above:
```
FROM quay.io/keycloak/keycloak:22.0.0

COPY keycloak-saml-adfs-adapter-22.0.0.jar /opt/keycloak/providers/keycloak-saml-adfs-adapter.jar
```

## Use case

TODO 

## Limits

Pay attention that several IDPs do not support the Subject element of SAML. For instance: ADFS simply ignores it: https://docs.microsoft.com/en-us/azure/active-directory/develop/single-sign-on-saml-protocol#subject

## Development

### Build library

```bash
    $ ./gradlew build
```