package com.theta.userservice.domain.service

import Sl4jLogger.Companion.log
import com.theta.userservice.domain.model.ConfirmationToken
import com.theta.userservice.domain.model.ResetPasswordToken
import com.theta.userservice.controller.dto.EmailDto
import com.theta.userservice.controller.dto.ResponseMessageDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.mail.internet.MimeMessage
import javax.persistence.EntityNotFoundException


@Service
class EmailService(val mailSender: JavaMailSender, val userService: UserService, val confirmationTokenService: ConfirmationTokenService, val resetPasswordTokenService: ResetPasswordTokenService) {
    @Value("\${webclient-baseurl}")
    private val baseUrl: String = ""
    @Async
    fun sendMail(from: String, to: String, subject: String, msg: String) {
        val message: MimeMessage = mailSender.createMimeMessage()
        message.subject = subject
        val helper = MimeMessageHelper(message, true)
        helper.setFrom(from)
        helper.setTo(to)
        helper.setText(msg, true)
        mailSender.send(message)
    }

    fun sendConfirmationEmail(emailDto: EmailDto): ResponseMessageDto {
        val user = userService.findByEmail(emailDto.email)
                ?: throw EntityNotFoundException("user/not-found")
        val confirmationToken = ConfirmationToken(user)
        confirmationTokenService.addConfirmationToken(confirmationToken)
        val msg = "<h1>Hello, ${user.displayName}!</h1><br><p>Confirm your account in the next 24hr using this <a href=\"${baseUrl}${confirmationToken.confirmationToken}/confirm\">link</a></p><p>Token for development testing: ${confirmationToken.confirmationToken}"
        val msg2 = "\n" +
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" style=\"width:100%;font-family:arial, 'helvetica neue', helvetica, sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0\">\n" +
                " <head> \n" +
                "  <meta charset=\"UTF-8\"> \n" +
                "  <meta content=\"width=device-width, initial-scale=1\" name=\"viewport\"> \n" +
                "  <meta name=\"x-apple-disable-message-reformatting\"> \n" +
                "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"> \n" +
                "  <meta content=\"telephone=no\" name=\"format-detection\"> \n" +
                "  <title>New email template 2022-01-09</title> \n" +
                "  <!--[if (mso 16)]>\n" +
                "    <style type=\"text/css\">\n" +
                "    a {text-decoration: none;}\n" +
                "    </style>\n" +
                "    <![endif]--> \n" +
                "  <!--[if gte mso 9]><style>sup { font-size: 100% !important; }</style><![endif]--> \n" +
                "  <!--[if gte mso 9]>\n" +
                "<xml>\n" +
                "    <o:OfficeDocumentSettings>\n" +
                "    <o:AllowPNG></o:AllowPNG>\n" +
                "    <o:PixelsPerInch>96</o:PixelsPerInch>\n" +
                "    </o:OfficeDocumentSettings>\n" +
                "</xml>\n" +
                "<![endif]--> \n" +
                "  <!--[if !mso]><!-- --> \n" +
                "  <link href=\"https://fonts.googleapis.com/css2?family=Righteous&display=swap\" rel=\"stylesheet\"> \n" +
                "  <!--<![endif]--> \n" +
                "  <style type=\"text/css\">\n" +
                ".rollover div {\n" +
                "\tfont-size:0;\n" +
                "}\n" +
                "#outlook a {\n" +
                "\tpadding:0;\n" +
                "}\n" +
                ".ExternalClass {\n" +
                "\twidth:100%;\n" +
                "}\n" +
                ".ExternalClass,\n" +
                ".ExternalClass p,\n" +
                ".ExternalClass span,\n" +
                ".ExternalClass font,\n" +
                ".ExternalClass td,\n" +
                ".ExternalClass div {\n" +
                "\tline-height:100%;\n" +
                "}\n" +
                ".es-button {\n" +
                "\tmso-style-priority:100!important;\n" +
                "\ttext-decoration:none!important;\n" +
                "}\n" +
                "a[x-apple-data-detectors] {\n" +
                "\tcolor:inherit!important;\n" +
                "\ttext-decoration:none!important;\n" +
                "\tfont-size:inherit!important;\n" +
                "\tfont-family:inherit!important;\n" +
                "\tfont-weight:inherit!important;\n" +
                "\tline-height:inherit!important;\n" +
                "}\n" +
                ".es-desk-hidden {\n" +
                "\tdisplay:none;\n" +
                "\tfloat:left;\n" +
                "\toverflow:hidden;\n" +
                "\twidth:0;\n" +
                "\tmax-height:0;\n" +
                "\tline-height:0;\n" +
                "\tmso-hide:all;\n" +
                "}\n" +
                "[data-ogsb] .es-button {\n" +
                "\tborder-width:0!important;\n" +
                "\tpadding:10px 20px 10px 20px!important;\n" +
                "}\n" +
                "[data-ogsb] .es-button.es-button-1 {\n" +
                "\tpadding:0px 5px!important;\n" +
                "}\n" +
                "@media only screen and (max-width:600px) {p, ul li, ol li, a { line-height:150%!important } h1, h2, h3, h1 a, h2 a, h3 a { line-height:120% } h1 { font-size:40px!important; text-align:center } h2 { font-size:26px!important; text-align:center } h3 { font-size:20px!important; text-align:center } .es-header-body h1 a, .es-content-body h1 a, .es-footer-body h1 a { font-size:40px!important } .es-header-body h2 a, .es-content-body h2 a, .es-footer-body h2 a { font-size:26px!important } .es-header-body h3 a, .es-content-body h3 a, .es-footer-body h3 a { font-size:20px!important } .es-menu td a { font-size:16px!important } .es-header-body p, .es-header-body ul li, .es-header-body ol li, .es-header-body a { font-size:16px!important } .es-content-body p, .es-content-body ul li, .es-content-body ol li, .es-content-body a { font-size:16px!important } .es-footer-body p, .es-footer-body ul li, .es-footer-body ol li, .es-footer-body a { font-size:16px!important } .es-infoblock p, .es-infoblock ul li, .es-infoblock ol li, .es-infoblock a { font-size:12px!important } *[class=\"gmail-fix\"] { display:none!important } .es-m-txt-c, .es-m-txt-c h1, .es-m-txt-c h2, .es-m-txt-c h3 { text-align:center!important } .es-m-txt-r, .es-m-txt-r h1, .es-m-txt-r h2, .es-m-txt-r h3 { text-align:right!important } .es-m-txt-l, .es-m-txt-l h1, .es-m-txt-l h2, .es-m-txt-l h3 { text-align:left!important } .es-m-txt-r img, .es-m-txt-c img, .es-m-txt-l img { display:inline!important } .es-button-border { display:inline-block!important } .es-btn-fw { border-width:10px 0px!important; text-align:center!important } .es-adaptive table, .es-btn-fw, .es-btn-fw-brdr, .es-left, .es-right { width:100%!important } .es-content table, .es-header table, .es-footer table, .es-content, .es-footer, .es-header { width:100%!important; max-width:600px!important } .es-adapt-td { display:block!important; width:100%!important } .adapt-img { width:100%!important; height:auto!important } .es-m-p0 { padding:0!important } .es-m-p0r { padding-right:0!important } .es-m-p0l { padding-left:0!important } .es-m-p0t { padding-top:0!important } .es-m-p0b { padding-bottom:0!important } .es-m-p20b { padding-bottom:20px!important } .es-mobile-hidden, .es-hidden { display:none!important } tr.es-desk-hidden, td.es-desk-hidden, table.es-desk-hidden { width:auto!important; overflow:visible!important; float:none!important; max-height:inherit!important; line-height:inherit!important } tr.es-desk-hidden { display:table-row!important } table.es-desk-hidden { display:table!important } td.es-desk-menu-hidden { display:table-cell!important } .es-menu td { width:1%!important } table.es-table-not-adapt, .esd-block-html table { width:auto!important } table.es-social { display:inline-block!important } table.es-social td { display:inline-block!important } a.es-button, button.es-button { font-size:20px!important; display:inline-block!important } .es-m-p5 { padding:5px!important } .es-m-p5t { padding-top:5px!important } .es-m-p5b { padding-bottom:5px!important } .es-m-p5r { padding-right:5px!important } .es-m-p5l { padding-left:5px!important } .es-m-p10 { padding:10px!important } .es-m-p10t { padding-top:10px!important } .es-m-p10b { padding-bottom:10px!important } .es-m-p10r { padding-right:10px!important } .es-m-p10l { padding-left:10px!important } .es-m-p15 { padding:15px!important } .es-m-p15t { padding-top:15px!important } .es-m-p15b { padding-bottom:15px!important } .es-m-p15r { padding-right:15px!important } .es-m-p15l { padding-left:15px!important } .es-m-p20 { padding:20px!important } .es-m-p20t { padding-top:20px!important } .es-m-p20r { padding-right:20px!important } .es-m-p20l { padding-left:20px!important } .es-m-p25 { padding:25px!important } .es-m-p25t { padding-top:25px!important } .es-m-p25b { padding-bottom:25px!important } .es-m-p25r { padding-right:25px!important } .es-m-p25l { padding-left:25px!important } .es-m-p30 { padding:30px!important } .es-m-p30t { padding-top:30px!important } .es-m-p30b { padding-bottom:30px!important } .es-m-p30r { padding-right:30px!important } .es-m-p30l { padding-left:30px!important } .es-m-p35 { padding:35px!important } .es-m-p35t { padding-top:35px!important } .es-m-p35b { padding-bottom:35px!important } .es-m-p35r { padding-right:35px!important } .es-m-p35l { padding-left:35px!important } .es-m-p40 { padding:40px!important } .es-m-p40t { padding-top:40px!important } .es-m-p40b { padding-bottom:40px!important } .es-m-p40r { padding-right:40px!important } .es-m-p40l { padding-left:40px!important } }\n" +
                "</style> \n" +
                " </head> \n" +
                " <body style=\"width:100%;font-family:arial, 'helvetica neue', helvetica, sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0\"> \n" +
                "  <div class=\"es-wrapper-color\" style=\"background-color:#1A1428\"> \n" +
                "   <!--[if gte mso 9]>\n" +
                "\t\t\t<v:background xmlns:v=\"urn:schemas-microsoft-com:vml\" fill=\"t\">\n" +
                "\t\t\t\t<v:fill type=\"tile\" color=\"#1a1428\"></v:fill>\n" +
                "\t\t\t</v:background>\n" +
                "\t\t<![endif]--> \n" +
                "   <table class=\"es-wrapper\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;padding:0;Margin:0;width:100%;height:100%;background-repeat:no-repeat;background-position:center top;background-color:#1A1428\"> \n" +
                "     <tr style=\"border-collapse:collapse\"> \n" +
                "      <td valign=\"top\" style=\"padding:0;Margin:0\"> \n" +
                "       <table class=\"es-header\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;background-color:transparent;background-repeat:repeat;background-position:center top\"> \n" +
                "         <tr style=\"border-collapse:collapse\"> \n" +
                "          <td align=\"center\" style=\"padding:0;Margin:0\"> \n" +
                "           <table class=\"es-header-body\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:transparent;width:600px\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" bgcolor=\"#FFFFFF\"> \n" +
                "             <tr style=\"border-collapse:collapse\"> \n" +
                "              <td align=\"left\" style=\"padding:20px;Margin:0\"> \n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"> \n" +
                "                 <tr style=\"border-collapse:collapse\"> \n" +
                "                  <td class=\"es-m-p0r es-m-p20b\" valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:560px\"> \n" +
                "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"> \n" +
                "                     <tr style=\"border-collapse:collapse\"> \n" +
                "                      <td align=\"center\" style=\"padding:0;Margin:0;font-size:0px\"><img class=\"adapt-img\" src=\"https://tklyee.stripocdn.email/content/guids/c2f91088-8444-473d-bdc2-b1c87009b821/images/risk_logo02.png\" alt style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\" width=\"200\" height=\"200\"></td> \n" +
                "                     </tr> \n" +
                "                   </table></td> \n" +
                "                 </tr> \n" +
                "               </table></td> \n" +
                "             </tr> \n" +
                "           </table></td> \n" +
                "         </tr> \n" +
                "       </table> \n" +
                "       <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-content\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%\"> \n" +
                "         <tr style=\"border-collapse:collapse\"> \n" +
                "          <td align=\"center\" style=\"padding:0;Margin:0\"> \n" +
                "           <table bgcolor=\"#ffffff\" class=\"es-content-body\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:transparent;width:600px\"> \n" +
                "             <tr style=\"border-collapse:collapse\"> \n" +
                "              <td align=\"left\" style=\"Margin:0;padding-bottom:15px;padding-left:20px;padding-right:20px;padding-top:35px\"> \n" +
                "               <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"> \n" +
                "                 <tr style=\"border-collapse:collapse\"> \n" +
                "                  <td class=\"es-m-p0r es-m-p20b\" valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:560px\"> \n" +
                "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" background=\"https://tklyee.stripocdn.email/content/guids/CABINET_621dd5dccf4f6787e98a586b2cebf383/images/3071622474072133.png\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-image:url(https://tklyee.stripocdn.email/content/guids/CABINET_621dd5dccf4f6787e98a586b2cebf383/images/3071622474072133.png);background-repeat:no-repeat;background-position:left bottom\" role=\"presentation\"> \n" +
                "                     <tr style=\"border-collapse:collapse\"> \n" +
                "                      <td align=\"center\" style=\"padding:0;Margin:0\"><h1 style=\"Margin:0;line-height:60px;mso-line-height-rule:exactly;font-family:Righteous, sans-serif;font-size:50px;font-style:normal;font-weight:bold;color:#FFFFFF\">Verify your account!</h1></td> \n" +
                "                     </tr> \n" +
                "                     <tr style=\"border-collapse:collapse\"> \n" +
                "                      <td align=\"center\" style=\"padding:0;Margin:0;padding-top:30px\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;line-height:23px;color:#FFFFFF;font-size:15px\">Thank you for joining the Risk&nbsp; community, but we just need one more thing of you.<br>We just need a single email verification by your account.</p></td> \n" +
                "                     </tr> \n" +
                "                     <tr style=\"border-collapse:collapse\"> \n" +
                "                      <td align=\"center\" style=\"padding:0;Margin:0;font-size:0px\"><a target=\"_blank\" href=\"https://theta-risk.com\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:underline;color:#FFFFFF;font-size:14px\"><img class=\"adapt-img\" alt style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\" width=\"560\"></a></td> \n" +
                "                     </tr> \n" +
                "                     <tr style=\"border-collapse:collapse\"> \n" +
                "                      <td align=\"center\" style=\"padding:0;Margin:0;padding-top:35px;padding-bottom:35px\"> \n" +
                "                       <!--[if mso]><a href=\"https://theta-risk.com\" target=\"_blank\" hidden>\n" +
                "\t<v:roundrect xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:w=\"urn:schemas-microsoft-com:office:word\" esdevVmlButton href=\"${baseUrl}${confirmationToken.confirmationToken}/confirm\" \n" +
                "                style=\"height:36px; v-text-anchor:middle; width:101px\" arcsize=\"0%\" stroke=\"f\"  fillcolor=\"#ffffff\">\n" +
                "\t\t<w:anchorlock></w:anchorlock>\n" +
                "\t\t<center style='color:#333333; font-family:arial, \"helvetica neue\", helvetica, sans-serif; font-size:11px; font-weight:400; line-height:11px;  mso-text-raise:1px'>VERIFY!</center>\n" +
                "\t</v:roundrect></a>\n" +
                "<![endif]--> \n" +
                "                       <!--[if !mso]><!-- --><span class=\"msohide es-button-border\" style=\"border-style:solid;border-color:#DD3AE7;background:#1a1428;border-width:0px;display:inline-block;border-radius:0px;width:auto;mso-hide:all\"><a href=\"${baseUrl}${confirmationToken.confirmationToken}/confirm\" class=\"es-button es-button-1\" target=\"_blank\" style=\"mso-style-priority:100 !important;text-decoration:none;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;color:#333333;font-size:18px;border-style:solid;border-color:#FFFFFF;border-width:0px 5px;display:inline-block;background:#FFFFFF;border-radius:0px;font-family:arial, 'helvetica neue', helvetica, sans-serif;font-weight:normal;font-style:normal;line-height:22px;width:auto;text-align:center\"><img src=\"https://tklyee.stripocdn.email/content/guids/CABINET_621dd5dccf4f6787e98a586b2cebf383/images/56871622473877131.png\" alt=\"icon\" width=\"40\" style=\"display:inline-block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic;vertical-align:middle;margin-right:10px\" align=\"absmiddle\" height=\"36\">VERIFY!</a></span> \n" +
                "                       <!--<![endif]--></td> \n" +
                "                     </tr> \n" +
                "                   </table></td> \n" +
                "                 </tr> \n" +
                "               </table></td> \n" +
                "             </tr> \n" +
                "           </table></td> \n" +
                "         </tr> \n" +
                "       </table> \n" +
                "       <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-footer\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;background-color:transparent;background-repeat:repeat;background-position:center top\"> \n" +
                "         <tr style=\"border-collapse:collapse\"> \n" +
                "          <td align=\"center\" style=\"padding:0;Margin:0\"> \n" +
                "           <table bgcolor=\"#ffffff\" class=\"es-footer-body\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:transparent;width:600px\"> \n" +
                "             <tr style=\"border-collapse:collapse\"> \n" +
                "              <td align=\"left\" style=\"Margin:0;padding-bottom:20px;padding-left:20px;padding-right:20px;padding-top:30px\"> \n" +
                "               <!--[if mso]><table style=\"width:560px\" cellpadding=\"0\" cellspacing=\"0\"><tr><td style=\"width:194px\" valign=\"top\"><![endif]--> \n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-left\" align=\"left\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:left\"> \n" +
                "                 <tr style=\"border-collapse:collapse\"> \n" +
                "                  <td class=\"es-m-p20b\" align=\"left\" style=\"padding:0;Margin:0;width:174px\"> \n" +
                "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"> \n" +
                "                     <tr style=\"border-collapse:collapse\"> \n" +
                "                      <td align=\"center\" style=\"padding:0;Margin:0;font-size:0px\"><a target=\"_blank\" href=\"https://theta-risk.com\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:underline;color:#FFFFFF;font-size:14px\"><img src=\"https://tklyee.stripocdn.email/content/guids/c2f91088-8444-473d-bdc2-b1c87009b821/images/soldierworkpeopledrawing_uau.png\" alt=\"Logo\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\" width=\"80\" title=\"Logo\" height=\"57\"></a></td> \n" +
                "                     </tr> \n" +
                "                     <tr style=\"border-collapse:collapse\"> \n" +
                "                      <td align=\"center\" style=\"padding:0;Margin:0\"><h3 style=\"Margin:0;line-height:31px;mso-line-height-rule:exactly;font-family:Righteous, sans-serif;font-size:26px;font-style:normal;font-weight:normal;color:#ffe28d\">Singplayer</h3></td> \n" +
                "                     </tr> \n" +
                "                     <tr style=\"border-collapse:collapse\"> \n" +
                "                      <td align=\"center\" style=\"padding:10px;Margin:0\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;line-height:17px;color:#FFFFFF;font-size:14px\">Play and dominate the most advanced AI</p></td> \n" +
                "                     </tr> \n" +
                "                   </table></td> \n" +
                "                  <td class=\"es-hidden\" style=\"padding:0;Margin:0;width:20px\"></td> \n" +
                "                 </tr> \n" +
                "               </table> \n" +
                "               <!--[if mso]></td><td style=\"width:173px\" valign=\"top\"><![endif]--> \n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-left\" align=\"left\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:left\"> \n" +
                "                 <tr style=\"border-collapse:collapse\"> \n" +
                "                  <td align=\"left\" class=\"es-m-p20b\" style=\"padding:0;Margin:0;width:173px\"> \n" +
                "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"> \n" +
                "                     <tr style=\"border-collapse:collapse\"> \n" +
                "                      <td align=\"center\" style=\"padding:0;Margin:0;font-size:0px\"><a target=\"_blank\" href=\"https://theta-risk.com\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:underline;color:#FFFFFF;font-size:14px\"><img src=\"https://tklyee.stripocdn.email/content/guids/c2f91088-8444-473d-bdc2-b1c87009b821/images/militarytankroyaltyfreevectorclipartillustration511958_1jj.png\" alt=\"Logo\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\" width=\"90\" title=\"Logo\" height=\"58\"></a></td> \n" +
                "                     </tr> \n" +
                "                     <tr style=\"border-collapse:collapse\"> \n" +
                "                      <td align=\"center\" style=\"padding:0;Margin:0\"><h3 style=\"Margin:0;line-height:31px;mso-line-height-rule:exactly;font-family:Righteous, sans-serif;font-size:26px;font-style:normal;font-weight:normal;color:#ffe28d\">&nbsp;Multiplayer</h3></td> \n" +
                "                     </tr> \n" +
                "                     <tr style=\"border-collapse:collapse\"> \n" +
                "                      <td align=\"center\" style=\"padding:10px;Margin:0\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;line-height:17px;color:#FFFFFF;font-size:14px\">Try to defeat your most worthy opponents from around the globe</p></td> \n" +
                "                     </tr> \n" +
                "                   </table></td> \n" +
                "                 </tr> \n" +
                "               </table> \n" +
                "               <!--[if mso]></td><td style=\"width:20px\"></td><td style=\"width:173px\" valign=\"top\"><![endif]--> \n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-right\" align=\"right\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:right\"> \n" +
                "                 <tr style=\"border-collapse:collapse\"> \n" +
                "                  <td align=\"left\" style=\"padding:0;Margin:0;width:173px\"> \n" +
                "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"> \n" +
                "                     <tr style=\"border-collapse:collapse\"> \n" +
                "                      <td align=\"center\" style=\"padding:0;Margin:0;font-size:0px\"><a target=\"_blank\" href=\"https://theta-risk.com\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:underline;color:#FFFFFF;font-size:14px\"><img src=\"https://tklyee.stripocdn.email/content/guids/c2f91088-8444-473d-bdc2-b1c87009b821/images/imgbinhorseknightlancereversed.png\" alt=\"Logo\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\" width=\"60\" title=\"Logo\" height=\"60\"></a></td> \n" +
                "                     </tr> \n" +
                "                     <tr style=\"border-collapse:collapse\"> \n" +
                "                      <td align=\"center\" style=\"padding:0;Margin:0\"><h3 style=\"Margin:0;line-height:31px;mso-line-height-rule:exactly;font-family:Righteous, sans-serif;font-size:26px;font-style:normal;font-weight:normal;color:#ffe28d\">Friends</h3></td> \n" +
                "                     </tr> \n" +
                "                     <tr style=\"border-collapse:collapse\"> \n" +
                "                      <td align=\"center\" style=\"padding:10px;Margin:0\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;line-height:17px;color:#FFFFFF;font-size:14px\">Now you can finally settle the discussion about who is smarter in your group</p></td> \n" +
                "                     </tr> \n" +
                "                   </table></td> \n" +
                "                 </tr> \n" +
                "               </table> \n" +
                "               <!--[if mso]></td></tr></table><![endif]--></td> \n" +
                "             </tr> \n" +
                "           </table></td> \n" +
                "         </tr> \n" +
                "       </table> \n" +
                "       <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-content\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%\"> \n" +
                "         <tr style=\"border-collapse:collapse\"> \n" +
                "          <td align=\"center\" style=\"padding:0;Margin:0\"> \n" +
                "           <table bgcolor=\"#ffffff\" class=\"es-content-body\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:transparent;width:600px\"> \n" +
                "             <tr style=\"border-collapse:collapse\"> \n" +
                "              <td align=\"left\" style=\"padding:20px;Margin:0\"> \n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"> \n" +
                "                 <tr style=\"border-collapse:collapse\"> \n" +
                "                  <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;width:560px\"> \n" +
                "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"> \n" +
                "                     <tr style=\"border-collapse:collapse\"> \n" +
                "                      <td align=\"center\" class=\"es-infoblock made_with\" style=\"padding:0;Margin:0;line-height:0px;font-size:0px;color:#CCCCCC\"><a target=\"_blank\" href=\"https://theta-risk.com/?utm_source=templates&utm_medium=email&utm_campaign=game_industry_5&utm_content=get_ready\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:underline;color:#CCCCCC;font-size:12px\"><img src=\"https://tklyee.stripocdn.email/content/guids/c2f91088-8444-473d-bdc2-b1c87009b821/images/risk_logo02.png\" alt width=\"200\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\" height=\"200\"></a></td> \n" +
                "                     </tr> \n" +
                "                   </table></td> \n" +
                "                 </tr> \n" +
                "               </table></td> \n" +
                "             </tr> \n" +
                "           </table></td> \n" +
                "         </tr> \n" +
                "       </table></td> \n" +
                "     </tr> \n" +
                "   </table> \n" +
                "  </div>  \n" +
                " </body>\n" +
                "</html>"
        sendMail("no-reply@theta-risk.com", emailDto.email, "Confirm your account!", msg2)
        log.info("confirmation email sent to " + user.email)
        return ResponseMessageDto.Builder().message("email/confirmation-sent").status(200).timeStamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).build()
    }

    fun sendForgotPasswordEmail(emailDto: EmailDto) : ResponseMessageDto {
        val user = userService.findByEmail(emailDto.email)
                ?: throw EntityNotFoundException("user/not-found")
        val resetPasswordToken = ResetPasswordToken(user)
        resetPasswordTokenService.addResetPasswordToken(resetPasswordToken)
        val msg = "<h1>Hello ${user.displayName}</h1><br><p>click this <a href=\"${baseUrl}${resetPasswordToken.resetPasswordToken}/reset\">link</a> ${emailDto.email} to reset your password.</p><p>Token for development ${resetPasswordToken.resetPasswordToken}</p>"
        sendMail("no-reply@theta-risk.com", user.email, "Password reset", msg)
        log.info("forgot password sent to " + user.email)
        return ResponseMessageDto.Builder().message("email/reset-sent").status(200).timeStamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).build()
    }

}