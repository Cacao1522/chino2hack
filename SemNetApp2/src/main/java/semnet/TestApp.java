package semnet;

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import io.javalin.Javalin;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinThymeleaf;

/**
 * TestAppクラス
 */
public class TestApp {

    public static void main(String[] args) {

        // Thymeleafのテンプレート設定
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode("HTML");
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");

        // TemplateEngineの設定
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        // JavalinにThymeleafを登録
        JavalinRenderer.register(new JavalinThymeleaf(templateEngine), ".html");


        // Javalinアプリの作成
        Javalin app = Javalin.create().start(7000);


        app.get("/test1", ctx -> {
            ctx.html("<html><body><h2>Response Test</h2></body></html>");
        });

        app.get("/test2", ctx -> {
            ctx.render("/index.html");
        });

        app.get("/test3", ctx -> {
            Map<String, Object> model = new HashMap<>();
            model.put("data", new MyData());
            ctx.render("/template.html", model);
        });

    }

}

/**
 * MyDataクラス
 */
class MyData {
    public String var = "ほげ";
    public String getVar() {
        return var;
    }
    public String func1() {
        return "foo";
    }

    public String func2(String str) {
        return "引数: " + str;
    }
}


