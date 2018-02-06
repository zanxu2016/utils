package me.xuzan.utils.mail;

import com.sun.mail.util.MailSSLSocketFactory;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class MailUtil {

    //发送126邮件
    public static Boolean sendEmail126() {
        final Properties props = w126EmailProps();
        return sendEmail(props);
    }

    //发送qq邮件
    public static Boolean sendEmailQQ() {
        final Properties props = qqParentEmailProps();
        return sendEmail(props);
    }

    //发送邮件
    public static Boolean sendEmail(final Properties props) {
        addCommProps(props);
        //构建授权信息，用于进行SMPT进行身份验证
        Authenticator authenticator = getAuthenticator(props);

        //使用环境属性和授权信息，创建邮件会话
        Session mailSession = getSession(props, authenticator);

        try {
            //创建邮件消息
            MimeMessage message = getMessage(mailSession, props);

            //发送邮件
            sendMail(mailSession, props, message);

        } catch (Exception e) {
            System.out.println("======:" + e.getMessage());
            return false;
        }
        return true;
    }

    //添加公共参数，可根据具体需求修改
    private static void addCommProps(Properties props) {

        //登入邮箱服务器需要验证
        props.put("mail.stmp.auth", "true");
        props.put("mail.stmp.port", 25);
        //设置协议
        props.put("mail.transport.protocol", "smtp");
        //设置邮件主题
        props.put("mail.title", "中奖了！");
        //设置发件人
        try {
            String from = "Eric <" + props.getProperty("mail.user") + ">";
//            String from = "波多野结衣 <" + props.getProperty("mail.user") + ">";
            props.put("mail.from", MimeUtility.encodeText(from));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //设置邮件内容
        String content = "带静态资源的邮件内容 图片:<img src='cid:picture' />";
//        String content = "<p>恭喜夜天晰！您获得火影忍者提供的波多野结衣（はたの ゆい）演出门票两张，02月06日至14日10点-16点凭手机号码及身份证到上海市普陀区曹杨路电信营业厅领取，逾时视为放弃。</p>";
        props.put("mail.content", content);
        //设置邮件内容的类型
        props.put("mail.type", "text/html;charset=utf-8");
    }

    //获取授权
    private static Authenticator getAuthenticator(final Properties props) {
        //构建授权信息，用于进行SMPT进行身份验证
        return new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                //用户名、密码
                String userName = props.getProperty("mail.user");
                String password = props.getProperty("mail.password");
                return new PasswordAuthentication(userName, password);
            }
        };
    }

    //获取会话
    private static Session getSession(final Properties props, Authenticator authenticator) {
        //使用环境属性和授权信息，创建邮件会话
        Session mailSession = Session.getDefaultInstance(props, authenticator);
//        mailSession.setDebug(true);//开启debug模式
        return mailSession;
    }

    //组装发送消息，可根据具体需要设置
    private static MimeMessage getMessage(Session mailSession, Properties props) throws Exception {
        //创建邮件消息
        MimeMessage message = new MimeMessage(mailSession);

        try {
            //设置发件人
            InternetAddress from = new InternetAddress(props.getProperty("mail.from"));
            message.setFrom(from);
            Address[] addresses = new Address[1];
            //接收方回复的邮件地址
            addresses[0] = new InternetAddress(props.getProperty("mail.reply"));
            message.setReplyTo(addresses);
            //设置收件人
            InternetAddress to = new InternetAddress(props.getProperty("mail.to"));
            message.setRecipient(MimeMessage.RecipientType.TO, to);
            //设置邮件标题
            message.setSubject(props.getProperty("mail.title"));

            //添加附加部分
            //邮件内容部分1--->文本内容
            MimeBodyPart contentPart = new MimeBodyPart();//邮件中的文字部分
            contentPart.setContent(props.getProperty("mail.content"), props.getProperty("mail.type"));

            //邮件内容部分2--->附件1
            MimeBodyPart dataPart = new MimeBodyPart();//附件1
            dataPart.setDataHandler(new DataHandler(new FileDataSource(getFilePath())));
            dataPart.setFileName(MimeUtility.encodeText(getFileName()));//中文附件名，解决乱码

            //把上面的3部分组装在一起，设置到msg中
            MimeMultipart mm = new MimeMultipart();
            mm.addBodyPart(contentPart);
            mm.addBodyPart(dataPart);
            message.setContent(mm);

        } catch (Exception e) {
            System.out.println("======:" + e.getMessage());
            throw e;
        }
        return message;
    }

    //发送邮件方法
    private static void sendMail(Session mailSession, Properties props, MimeMessage message) throws Exception {
        try {
            /*
             * 下面发送方法是一个很好的方法，尤其是在我们在同一个邮件服务器上发送多个邮件时。
             * 因为这时我们将在连接邮件服务器后连续发送邮件，然后再关闭掉连接。
             * Transport.send(message)这个基本的方法是在每次调用时进行与邮件服务器的连接的，
             * 对于在同一个邮件服务器上发送多个邮件来讲可谓低效的方式，
             * 所以采用下面的发送方式会好点。
             */
            Transport transport = mailSession.getTransport("smtp");
            transport.connect(props.getProperty("mail.stmp.host"), props.getProperty("mail.user"), props.getProperty("mail.password"));
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (Exception e) {
            System.out.println("======:" + e.getMessage());
            throw e;
        }
    }

    //网易发给QQ大号，回复给QQ小号
    public static Properties w126EmailProps() {
        Properties props = new Properties();
        props.put("mail.stmp.host", "smtp.126.com");//邮件服务器地址
        props.put("mail.user", "zanxu88@126.com");//发件人邮箱地址
        props.put("mail.password", "xz198979820511");//发件人邮箱授权密码
        props.put("mail.reply", "2628641193@qq.com");//回复邮箱地址
        props.put("mail.to", "250476498@qq.com");//收件人邮箱地址
        return props;
    }

    //大号发给小号
    public static Properties qqParentEmailProps() {
        //开启SSL加密
        Properties props = qqSSL();
        props.put("mail.stmp.host", "smtp.qq.com");//邮件服务器地址
        props.put("mail.user", "250476498@qq.com");//发件人邮箱地址
        props.put("mail.password", "bczbltxpzyxnbjcc");//发件人邮箱授权密码
        props.put("mail.reply", "2628641193@qq.com");//回复邮箱地址
//        props.put("mail.to", "550175930@qq.com");//收件人邮箱地址（小葛）
        props.put("mail.to", "2397618606@qq.com");//收件人邮箱地址（小兰）

        return props;
    }

    //小号发给大号
    public static Properties qqChileEmailProps() {
        //开启SSL加密
        Properties props = qqSSL();
        props.put("mail.stmp.host", "smtp.qq.com");
        props.put("mail.user", "2628641193@qq.com");
        props.put("mail.password", "eqflrnbjqyzxdiaf");
        props.put("mail.reply", "2628641193@qq.com");
        props.put("mail.to", "250476498@qq.com");

        return props;
    }

    //QQ邮箱发送前需要SSL加密
    private static Properties qqSSL() {
        Properties props = new Properties();
        //开启SSL加密
        //开启安全协议
        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.socketFactory", sf);
        return props;
    }

    //项目文件根路径
    public static String rootPath() {
        return System.getProperty("user.dir");
    }

    //测试文件路径
    public static String getFilePath() {
        return rootPath() + "\\src\\main\\resources\\images\\天道酬勤.jpg";
    }

    //测试文件名称
    public static String getFileName() {
        return "天道酬勤.jpg";
    }

    public static void main(String[] args) throws Exception {
        System.out.println(sendEmailQQ());
    }
}
