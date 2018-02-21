package mixit

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.MemoryDataStoreFactory
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.GmailScopes
import com.samskivert.mustache.Mustache
import com.samskivert.mustache.Mustache.TemplateLoader
import mixit.web.StringEscapers
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.io.File




@SpringBootApplication
@EnableConfigurationProperties(MixitProperties::class)
class MixitApplication {

    @Bean
    fun mustacheCompiler(templateLoader: TemplateLoader): Mustache.Compiler =
            Mustache.compiler().withEscaper(StringEscapers().HTML).withLoader(templateLoader)


    @Bean
    fun jacksonFactory() = JacksonFactory.getDefaultInstance()

    @Bean
    fun dataStoreFactory() = MemoryDataStoreFactory.getDefaultInstance()

    @Bean
    fun httpTransport() = GoogleNetHttpTransport.newTrustedTransport()

    @Bean
    fun authorize(): Credential {
//        val apikey = "479194365543-pg0pku7p3qmt8p07r6coka0lglrjdjbk.apps.googleusercontent.com"
//        val apiSecret = "Czm5PM1DHNTCeL2XkWuBPkw3"
//
//        val flow = AuthorizationCodeFlow.Builder(
//                BearerToken.authorizationHeaderAccessMethod(),
//                httpTransport(),
//                jacksonFactory(),
//                GenericUrl(TOKEN_SERVER_URL),
//                ClientParametersAuthentication(apikey, apiSecret),
//                apikey,
//                AUTHORIZATION_SERVER_URL)
//                .setScopes(listOf(GmailScopes.GMAIL_SEND))
//                .setDataStoreFactory(dataStoreFactory()).build()
//
//        val receiver = LocalServerReceiver.Builder().setHost("localhost").setPort(8080).build()
//        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")

        // Load client secrets. https://developers.google.com/identity/protocols/OAuth2ServiceAccount
        val jsonConfig = "{\n" +
                "  \"type\": \"service_account\",\n" +
                "  \"project_id\": \"mixitconf\",\n" +
                "  \"private_key_id\": \"c029bd457bfde81a9f81b5f1023d6c8d9e22cddd\",\n" +
                "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCI/pfmUoZ/KGKY\\nyMlpBWNkAf1tYMl7olw6pDRgc42CJDxOylEwNRDWZ6h9c2eJxCv5DyiSqf8/MFL2\\nMn4rpLHmZ9CeelI6aPcredg9iht1WXh7dIDXmBX3B8c21U04jgpBWoSfNrhNES1p\\nqDhtXMh+gPp3VMd2xmkI2JcBh5wHt9R/TW10TiKKxPgbVicGTWRo1ySsaPXTL/zL\\n+Ve1s71U5KlJREHu2tjm4V6SUvWtNHFJsPwW/1r6a69GmoM04sL6IaVXORb3gU9F\\nOuaFtbcEHtJ8B4v9F+WPZL7DfF/F0eqT80r183DOROkOUtB59cU6Lqwd650AY+8R\\nToEQydiZAgMBAAECggEAPVcF68a62zfZggCxklkryi1L7MxAGyA1PLqmiyNiUsnn\\nyP21AalZGy951YY1b43LhK7hY15D4GbCVuN/9Am/8GM+mZ86r1WtiqV5igCLT+2a\\nUWj2gdJiz2QHsskqVO7gr6eYp3OIbVWKKHt+nJVFXsNgosrurwOjJydX4gL2/csN\\n4KN1FmaJin99mEq8kI4Vb+rdPKSuHvXf3PveKQh6dC3sCMOBVr2yyen1o97Wh6TC\\nCpJGCGdebYdLtkahQMqRpHwhW+/ojizaFZbjaGcJvBLiEAWd8+vPwnafLwPGK+hA\\n/r8ZJdskqQtsWw5XxWdk/C/LcTfyu2v5U5kj7ffM2wKBgQC8j+aKqOVTni/v7/LV\\nlrB+cUny6Xn17j2bZVIjVu8h5n2aoMCZ2zJGJC/SwPLUHOPmilOMwn8Gf5Z5M5Fz\\no3kSJMoADP6/EbSJHAZga80rQBRgcZsoJGjIZbLWf8EaQPTBu2zM77DXCtNN7Cxj\\nMJV9/jqGrUi5jFpFfSD8FUko7wKBgQC5/VcAgnRLPFzHcI2MMq76kHHkNJlDPNYX\\nGa8ey/XK+/DoK8HPOBFMuUGkwJ3zLJ1iouMjI02tBAczpOR8Qqrrmg+MROHUiBr/\\n3bVVP7ftGjBVGHDz72uzzkBj0zgWfs0pIjl4LOnXz5hzQ97tSWIkTGVZ+tiwhQV/\\nuZuz5h5G9wKBgGW0pGAEC3XXq/maDfwAxH/e8UkRf9QTxa0MsIoZOPCrFvKRZHFY\\nVryYhicCJbgdQRu3XqtcuREzPmFvXDPoZ8ROjZoLaFh830jZEeNuIgMDFDqU7GBx\\niwcUO6sQy4IyqRHZ2yyK2HKVBzZPff2eVfPYbY745nIWbxZuEJ2HiaeHAoGAXHCj\\nFljAbkhxIJs7bwwd1eGHCdJuvnF1QwP7+wWKg7nW0b411MVcK9MD4lvt5VguXUDy\\nPZulVrHi1nNFMNF93B21TpVJ3PVM3hEWuGk0BD94+EJumX9M+5Auhq8LeHrLg39E\\nKwu/qgi/D8MTAMLxaXMjCKbZnlpvEj1xOufL8p8CgYBDObdq7cF6QbtiYBhtqGq8\\nQXGw4OscgfLqaCmhmttiIP+ywhH3Hb6oTwaPOb+BfJgW7SfA5spKAYwSqh5i/V6B\\naW0GqkchmTfu3xnupmRSn6LqAq6otxjoqCS5QNfjy4UE32SsNFZYAhTpp5/3Ke9M\\n5kuUkg7ByU452heJ8sTvAw==\\n-----END PRIVATE KEY-----\\n\",\n" +
                "  \"client_email\": \"mixitconf@mixitconf.iam.gserviceaccount.com\",\n" +
                "  \"client_id\": \"102590735977560341858\",\n" +
                "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                "  \"token_uri\": \"https://accounts.google.com/o/oauth2/token\",\n" +
                "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/mixitconf%40mixitconf.iam.gserviceaccount.com\"\n" +
                "}"



        val credential = GoogleCredential
                .fromStream(jsonConfig.byteInputStream())
                .createScoped(listOf(GmailScopes.MAIL_GOOGLE_COM))

        credential.refreshToken()
        return credential

//        val clientSecrets = GoogleClientSecrets.load(jacksonFactory(), StringReader(jsonConfig))
//
//        // Build flow and trigger user authorization request.
//        val flow = GoogleAuthorizationCodeFlow.Builder(GoogleNetHttpTransport.newTrustedTransport(), jacksonFactory(), clientSecrets, listOf(GmailScopes.GMAIL_SEND))
//                .setDataStoreFactory(dataStoreFactory())
//                .setAccessType(null)
//                .build()
//
//        return AuthorizationCodeInstalledApp(flow, LocalServerReceiver()).authorize("user")
    }

    @Bean
    fun gmailService() = Gmail.Builder(httpTransport(), jacksonFactory(), authorize()).setApplicationName("mixitconf").build()

}

fun main(args: Array<String>) {
    runApplication<MixitApplication>(*args)
}
