package mixit.util

import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.Message
import com.sendgrid.*
import mixit.MixitProperties
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


/**
 * Email
 */
data class EmailMessage(val to: String, val subject: String, val content: String)

/**
 * An email sender is able to send an HTML message via email to a consignee
 */
interface EmailSender {
    fun send(email: EmailMessage)
}

/**
 * We can have an email sender to manage our authentication phase ...
 */
interface PrimaryAuthentEmailSender : EmailSender

/**
 * We can have an email sender to manage our authentication phase ...
 */
interface AuthentEmailSender : EmailSender

/**
 * ... or for send information messages to our users
 */
interface MessageEmailSender : EmailSender

/**
 * Gmail is used in developpement mode (via SMTP) to send email used for authentication
 * or for our different information messages
 */
@Component
class GmailSender(private val properties: MixitProperties, private val javaMailSender: JavaMailSender, private val gmailService: Gmail) : PrimaryAuthentEmailSender {

    override fun send(email: EmailMessage) {
//        val message = javaMailSender.createMimeMessage()
//
//        val helper = MimeMessageHelper(message, true, "UTF-8")
//        helper.setTo(email.to)
//        helper.setSubject(email.subject)
//        message.setContent(email.content, MediaType.TEXT_HTML_VALUE)
//        javaMailSender.send(message)

//        val props = Properties()
//        val session = Session.getDefaultInstance(props, null)
//        val message = MimeMessage(session)

        //message.setFrom(InternetAddress("gui.ehret@gmail.com"))
       // message.addRecipient(javax.mail.Message.RecipientType.TO, InternetAddress(email.to))
      //  message.setSubject(email.subject)
      //  message.setContent(email.content, MediaType.TEXT_HTML_VALUE)

//        var mimeBodyPart = MimeBodyPart()
//
//
//        val multipart = MimeMultipart()
//        multipart.addBodyPart(mimeBodyPart)
//
//        message.setContent(multipart)


        val session = Session.getDefaultInstance(Properties(), null)
        val message = MimeMessage(session)

        message.setFrom(InternetAddress("contact@mix-it.fr"))
        message.addRecipient(javax.mail.Message.RecipientType.TO, InternetAddress(email.to))
        message.subject = email.subject
        message.setContent(email.content, MediaType.TEXT_HTML_VALUE)

        val buffer = ByteArrayOutputStream()
        message.writeTo(buffer)
        //val encodedEmail = Base64.getUrlEncoder().encodeToString(buffer.toByteArray())
        val emailMessage = Message()
        //emailMessage.setRaw(encodedEmail)
        emailMessage.encodeRaw(buffer.toByteArray())
        System.out.println(emailMessage.toPrettyString())
//
//        val msg = WebClient.create("https://www.googleapis.com/gmail")
//                .post()
//                .uri("/v1/users/contact%40mix-it.fr/messages/send?key=AIzaSyAlXGSgUce0xHA4tw7lPURtFTD_0HQLmss")
//                .body(BodyInserters.fromFormData("raw", emailMessage.raw))
//                .accept(MediaType.APPLICATION_JSON)
//                .retrieve()
//                .bodyToMono(Message::class.java)
//                .block()


        // POST https://www.googleapis.com/gmail/v1/users/contact%40mix-it.fr/messages/send?key={YOUR_API_KEY}


        val result = gmailService.users().messages().send("contact@mix-it.fr", emailMessage).execute();

        System.out.println(result.toPrettyString())
    }
}

/**
 * Elastic email is the sender used on our cloud instances for our authentication emails
 */
@Component
@Profile("notusenow")
class ElasticEmailSender(private val properties: MixitProperties) : AuthentEmailSender {

    override fun send(email: EmailMessage) {

        val result = WebClient.create(properties.elasticmail.host)
                .post()
                .uri("/${properties.elasticmail.version}/email/send")
                .body(BodyInserters
                        .fromFormData("apikey", properties.elasticmail.apikey)
                        .with("from", properties.contact)
                        .with("fromName", "MiXiT")
                        .with("to", email.to)
                        .with("subject", email.subject)
                        .with("isTransactional", "true")
                        .with("body", email.content))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ElasticEmailResponse::class.java)
                .block()

        if (result?.success == false) {
            throw RuntimeException(result.error)
        }
    }
}

/**
 * Response returned by elastic email
 */
data class ElasticEmailResponse(val success: Boolean, val error: String? = null, val data: Any? = null)

/**
 * Send grid is the sender used on our cloud instances to send information messages
 */
@Component
class SendGridSender(private val properties: MixitProperties) : AuthentEmailSender, MessageEmailSender{

    override fun send(email: EmailMessage) {

        val mail = Mail(
                Email(properties.contact, "MiXiT"),
                email.subject,
                Email(email.to),
                Content(MediaType.TEXT_HTML_VALUE, email.content))

        val sendGrid = SendGrid(properties.sendgrid.apikey)

        try {
            val request = Request()
            request.method = Method.POST
            request.endpoint = "mail/send"
            request.body = mail.build()
            sendGrid.api(request)
        } catch (ex: IOException) {
            throw RuntimeException(ex)
        }
    }
}

