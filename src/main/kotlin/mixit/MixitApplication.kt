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
                "  \"private_key_id\": \"1b1b7cb4a84b965a6232f3b08a4a5186227a87ac\",\n" +
                "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDRtJB+JNTJ0w2o\\nOi1zL9Bou+gR52OohTv0k7KUF/M8b/QjgbPY80pr0AreB4ajtJJ2PAfNwOPHaxTh\\n5MHsHKT/n97rEFwfPXoyNH5lobbI5Tvgk2psrwVxXFGpezSZD788jBlHq0dlNIm4\\n3bNbnXT2DBi1vbnrUes5MMlEodW1hDxcbhsimjF1tRCqWDUQ1oNrazrg8dZA6C0Z\\nLwMI+CKSvkUp4xuIDKhBaLxugQnmjjBnTULthnQJsNjVi11cDDn3US51iOqDYiIP\\nwjf8+E7FInlRBEPlhG5hjZ18jO77wE7ZhejKOUC6yKNKlVabYpOWpsv1CSaGkZ3w\\nCRXmI6HLAgMBAAECggEASjo1KQllbnjvrRvGqa6DgQb+KpQyUNy0L6q1+7iMhtiD\\n3KuU2tanAYl2x0d3r2uDqikug/RiS9yJ3DmKMMak2ryefarhSMeMgMfW86dtmSeW\\n7bOQaJ78+La6SxjKseECim52pkkosNiLZS3IRkXyOANpHa9l5zcsYSZ1H39/M0vS\\nipnWER4XCuYHJexEWRxUSQIDylmxWaTpKNGJUvTyTqxvWSEtFhwtW6XeTPFpxRFD\\nWji8DkvT1YgQYhQPxmcQOVnZfOb08SNZnRgUhHmGXxXw23Cv4wMXnCXO0XyuUqLh\\n2BhNZp3b6LQAwzFbjAdDj/W8Y4TYdgG/aMnYOGvN/QKBgQDzx00qLDubN99v8bLz\\nx8FLVRQ/bTw84+Q6F4ujh5GnzNXuUDFmy/76+eKmozceQk2a6knUyGmiCnDouhed\\nF188O5jIbJYp+MJZAm4kKmXVKYW3qnkCIdqnVEnTkqE/sr1/NDRzAP0f7gVh26cp\\nH7Dcvc9az0ylNkvSBv7ayMGsZwKBgQDcN/YcJqeCQvcdc9bL8zZdql/eQMp8xY8Z\\npppOQbzw76td7z8ex1neFMBtAAPMJsr6SGZeFQvB+Y51KaqtXVNhTbdGU6H9QNHj\\n2lHFDgmUR6TbdXOIOCfuNCfJPMEgs2i+CHjbhPF6HzSnLK6SKEh89PqvolOVbX4o\\nGK2bwbnA/QKBgCShtj64FtzkMHcp4rIvOiSPzFMbnwmnVQxFrX4NSR3l8d+1vTat\\nQMuF8UEJFac+X1sACcgntXfcCctu6013zZ/HdNXpO0djUhD+BH76wAKgEq/etgBU\\nBf9O8ykPx5tCyKbsuVTuuRqrMbPaxeuwD2ucOiRzMhlBZ3NQCcvmjvPRAoGAOBvi\\nDwYiQ+RABjcPkWNkhLT2PDCR5vyHG2gBhNLnCxRlu0dL89GjKhxrctpAHXH84Yw0\\n2N/P4Rpum5eELyvKJM6iVb7VVSuAzuc/Uk/jTO58KJZiW2DDe8O6J2HdazGLDHAo\\nKvZSeGUfC0EtMXwmDAB3JuzbEwWU0S6IfVj8tRkCgYEAxYkYSNIbumOIZcvZMOs9\\nxDFyVYbCVuyg4SYZLr8xF0D0/dYdcZ/1satxebUv3KLqX3ONSFzn0aIzy33WvCKp\\nwq25iOVIg83wyl4smGBWuWlqFTojWRLVv5R42lQxz61UB1TT7FIDmqRxHAibRKLO\\nLlC5Uy8muga/TnqN26l+YeI=\\n-----END PRIVATE KEY-----\\n\",\n" +
                "  \"client_email\": \"mixitconf@mixitconf.iam.gserviceaccount.com\",\n" +
                "  \"client_id\": \"102590735977560341858\",\n" +
                "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                "  \"token_uri\": \"https://accounts.google.com/o/oauth2/token\",\n" +
                "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/mixitconf%40mixitconf.iam.gserviceaccount.com\"\n" +
                "}"

        return GoogleCredential
                .fromStream(jsonConfig.byteInputStream())
                .createScoped(listOf(GmailScopes.GMAIL_SEND))

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
    fun gmailService() = Gmail.Builder(httpTransport(), jacksonFactory(), authorize()).setApplicationName("MiXiT").build()

}

fun main(args: Array<String>) {
    runApplication<MixitApplication>(*args)
}
