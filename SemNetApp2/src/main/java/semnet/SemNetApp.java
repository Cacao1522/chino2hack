package semnet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import io.javalin.Javalin;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinThymeleaf;

/**
 * SemNetAppクラス
 */
public class SemNetApp {
	public static void main(String[] args) {
		// H2データベースの初期化
		Database.initializeDatabase();

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
		app.get("/semnet", ctx -> {
			Map<String, Object> model = new HashMap<>();
			String queryStr = ctx.queryParam("queryStr");
			SemanticNet sn = new SemanticNet();
			if (sn.isEmpty(true)) {
				sn.addInitialLinks();

			}

			model.put("sn", sn);
			if (queryStr != null) {
				ArrayList<Link> query = strToQuery(queryStr);
				String result = sn.query(query);
				model.put("result", result);
			} else {
				queryStr = "?x is-a ?y\n?y donot ?z";
			}
			model.put("query", queryStr);
			ctx.render("/semnet.html", model);
		});

		app.post("/semnet", ctx -> {
			Map<String, Object> model = new HashMap<>();
			String queryStr = ctx.formParam("queryStr");
			SemanticNet sn = new SemanticNet();

			if (sn.isEmpty(false)) {
				sn.addInitialLinks();
			}

			model.put("sn", sn);

			if (queryStr != null) {
				ArrayList<Link> query = strToQuery(queryStr);
				String result = sn.query(query);
				model.put("result", result);
			}
			model.put("query", queryStr);
			ctx.render("/semnet.html", model);
		});

		app.post("/addFact", ctx -> {
			String tail = ctx.formParam("tail");
			String link = ctx.formParam("link");
			String head = ctx.formParam("head");
			SemanticNet sn = new SemanticNet();
			if (sn.isEmpty(false)) {
				sn.addInitialLinks();
			}

			// 新しいリンクをセマンティックネットに追加
			sn.addLink(new Link(link, tail, head, sn));

			Map<String, Object> model = new HashMap<>();
			model.put("sn", sn);
			model.put("query", "?x is-a ?y\n?y donot ?z");
			ctx.render("/semnet.html", model);
		});

		app.post("/deleteFact", ctx -> {
			String label = ctx.formParam("label");
			String tail = ctx.formParam("tail");
			String head = ctx.formParam("head");
			SemanticNet sn = new SemanticNet();
			if (sn.isEmpty(false)) {
				sn.addInitialLinks();
			}

			// リンクをセマンティックネットから削除
			sn.removeLink(new Link(label, tail, head, sn));

			Map<String, Object> model = new HashMap<>();
			model.put("sn", sn);
			model.put("query", "?x is-a ?y\n?y donot ?z");
			ctx.render("/semnet.html", model);
		});

		app.post("/search", ctx -> {
			String leave = ctx.formParam("leave");
			String arrive = ctx.formParam("arrive");
			String time = ctx.formParam("time");
			// 制限時間のパラメータを数値に変換
		    double limitedTime;
		    try {
		    	limitedTime = Double.parseDouble(time);
		    } catch (NumberFormatException e) {
		        ctx.status(400).result("制限時間は数値で入力してください。");
		        return;
		    }
		 // 各観光地のキーワードを受け取る
		    List<List<String>> keywordsList = new ArrayList<>();
		    for (int i = 0; ctx.formParam("spots[" + i + "]") != null; i++) {
		        String keywords = ctx.formParam("spots[" + i + "]");
		        // カンマ区切りでキーワードを分割してリストに追加
		        String[] keywordsArray = keywords.split(",");
		        List<String> spotKeywords = new ArrayList<>();
		        for (String keyword : keywordsArray) {
		            spotKeywords.add(keyword.trim()); // 前後の空白を削除して追加
		        }
		        keywordsList.add(spotKeywords); // 観光地ごとのキーワードリストを追加
		    }
		 // キーワードを表示（デバッグ用）
		    for (List<String> keyword : keywordsList) {
		        System.out.println("キーワード: " + keyword);
		    }
//		    Map<String, Object> model = new HashMap<>();
//		    model.put("query", "?x is-a ?y\n?y donot ?z");
//			ctx.render("/semnet.html", model);
		});
	}

	private static ArrayList<Link> strToQuery(String queryStr) {
		ArrayList<Link> query = new ArrayList<>();
		if (queryStr != null) {
			String[] lines = queryStr.split("\n");
			for (String line : lines) {
				line = line.trim();
				String[] tokens = line.split("\\s+");
				if (tokens.length == 3) {
					query.add(new Link(tokens[1], tokens[0], tokens[2], new SemanticNet()));
				}
			}
		}
		return query;
	}
}
