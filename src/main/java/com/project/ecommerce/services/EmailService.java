package com.project.ecommerce.services;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}") private String sender;

    // To send a simple email
    public void sendReturn(String recipient, boolean acceptation) {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(sender,"DB-Shopping");
            mimeMessageHelper.setTo(recipient);
            String common = """
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Welcome Message</title>
                        <style>
                            body {
                                font-family: 'Arial', sans-serif;
                                line-height: 1.6;
                                margin: 0;
                                padding: 0;
                            }

                            .container {
                                max-width: 600px;
                                margin: 20px auto;
                                padding: 20px;
                                background-color: black;
                                color: white;
                                border-radius: 8px;
                                box-shadow: 0 0 10px black;
                                border: 2px solid #f90;
                            }

                            h1 {
                                color: #f90;
                                font-style: italic;
                                font-size: 20px;
                            }

                            p {
                                margin-bottom: 20px;
                            }

                            p span{
                            \tfont-weight: bold;
                            \tfont-style: italic;
                            \tfont-family: monospace;
                            \tfont-size: 15px;
                            }

                            .button {
                                display: inline-block;
                                padding: 10px 20px;
                                font-size: 16px;
                                text-align: center;
                                text-decoration: none;
                                color: #fff;
                                background-color: transparent;
                                border: solid 1px orange;
                                border-radius: 5px;
                            }
                        </style>
                    </head>
                    """;
            if(acceptation){
                String accepted = common +"""
                        <body>
                            <div class="container">
                                <h1>Welcome to DBShopping!</h1>
                                <p>
                                    We are excited to inform you that your registration has been approved by the admin.
                                    Welcome aboard! We are thrilled to have you as part of our community.
                                </p>
                                <p>
                                    If you have any questions or need assistance, feel free to reach out to us.
                                </p>
                                <table width="100%">
                                \t<tr>
                                \t\t<td align="left"><a href="http://localhost:8080/login" class="button">Get Started</a></td>
                                \t\t<td align="right"><p>Best regards,<br/><span>DBShopping</span></p></td>
                                \t</tr>
                                </table>
                            </div>
                        </body>
                        </html>
                        """;
                mimeMessageHelper.setText(accepted, true);
            }
            else{
                String declined = common +"""
                        <body>
                            <div class="container">
                                <h1>DBShopping</h1>
                                <p>
                                    Dear candidate,
                                </p>
                                <p>
                                \tWe regret to inform you that your registration request has been declined by the admin due to some reasons.
                                </p>
                                <p>
                        \t\t\tIf you have any questions or would like further clarification, please reach out to us.
                                </p>
                                <table width="100%">
                                \t<tr>
                                \t\t<td align="right"><p>Best regards,<br/><span>DBShopping</span></p></td>
                                \t</tr>
                                </table>
                            </div>
                        </body>
                        </html>
                        """;
                mimeMessageHelper.setText(declined, true);
            }
            String subject = "Your registration on DBShopping";
            mimeMessageHelper.setSubject(subject);

            javaMailSender.send(mimeMessage);
        }

        catch (jakarta.mail.MessagingException e) {
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
