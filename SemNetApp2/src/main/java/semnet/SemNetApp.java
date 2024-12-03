package semnet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import io.javalin.Javalin;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinThymeleaf;
import planner.DBsubstitute;
import planner.DataBaseDAO;
import planner.Problem;
import planner.TravelPlanningProblem2;
import planner.TravelPlanner;

import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.http.HttpTester.Response;
import org.h2.util.json.JSONObject;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
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

		// APIキーを設定
        String apiKey = "AIzaSyBQjOdvRm0QnKiDotdnkl7lf0_4pQEfRt4";
     // GeoApiContextを作成
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(apiKey) // APIキーを設定
                .build();

		// Javalinアプリの作成
        Javalin app = Javalin.create(config -> {
            // 静的ファイルのルートディレクトリを指定
            config.staticFiles.add("/public"); // src/main/resources/public をマッピング
        }).start(7000);

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
	            //modelをhtmlに持って行くことで変数代入
			DataBaseDAO.initializeDatabase();
			model.put("spotActivity", DataBaseDAO.getSpotActivity());
			model.put("allActivity", DataBaseDAO.allActivity());
			model.put("spotList", DataBaseDAO.getSpotList());
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
			model.put("spotActivity", DataBaseDAO.getSpotActivity());
			model.put("allActivity", DataBaseDAO.allActivity());
			model.put("spotList", DataBaseDAO.getSpotList());
			ctx.render("/semnet.html", model);
		});

		app.post("/travel-want", ctx -> {
			//modelの保存
            Map<String, Object> model = new HashMap<>();
            SemanticNet sn = new SemanticNet();

			if (sn.isEmpty(false)) {
				sn.addInitialLinks();
			}
			model.put("sn", sn);

            String startStr = ctx.formParam("startStr");
            String goalStr = ctx.formParam("goalStr");

            if(startStr != null && goalStr != null) {
	            Object[] spotStr = ctx.formParams("spotStr").toArray();
	            Object[] doStr = ctx.formParams("doStr").toArray();
	            List<String> visitList = new ArrayList<>();
	            List<String> doList = new ArrayList<>();
	            for(var s:spotStr) {
	            	visitList.add((String)s);
	            }
	            for(var s:doStr) {
	            	doList.add((String)s);
	            }
	    		Problem p = new TravelPlanningProblem2(startStr, goalStr, visitList, doList);
	    		DataBaseDAO.initializeDatabase();
	    		var result = new TravelPlanner(p,DataBaseDAO.getSpots()).solve();

	            model.put("result", result);
            } else {
            	model.put("result", List.of("出発地点と終着地点は必ず入力してください。"));
            }
            model.put("spotActivity", DataBaseDAO.getSpotActivity());
			model.put("allActivity", DataBaseDAO.allActivity());
			model.put("spotList", DataBaseDAO.getSpotList());
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
			model.put("spotActivity", DataBaseDAO.getSpotActivity());
			model.put("allActivity", DataBaseDAO.allActivity());
			model.put("spotList", DataBaseDAO.getSpotList());
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
			model.put("spotActivity", DataBaseDAO.getSpotActivity());
			model.put("allActivity", DataBaseDAO.allActivity());
			model.put("spotList", DataBaseDAO.getSpotList());
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
		    Search s = new Search(leave, arrive, limitedTime, keywordsList);
		    s.search();
			Map<String, Object> model = new HashMap<>();
			SemanticNet sn = new SemanticNet();

			if (sn.isEmpty(false)) {
				sn.addInitialLinks();
			}
			System.out.println(s.result);
			model.put("sn", sn);
			model.put("origin", leave);
			model.put("destination", arrive);
			model.put("result", s.result);
			model.put("distance", s.maxdist);
			model.put("totaltime", s.maxtotal);
			model.put("satisscore", s.maxss);
			model.put("movetime", s.optimalTravelTimes);
			model.put("spotActivity", DataBaseDAO.getSpotActivity());
			model.put("allActivity", DataBaseDAO.allActivity());
			model.put("spotList", DataBaseDAO.getSpotList());
			ctx.render("/semnet.html", model);
//		    Map<String, Object> model = new HashMap<>();
//		    model.put("query", "?x is-a ?y\n?y donot ?z");
//			ctx.render("/semnet.html", model);
		});
		app.get("/spot/{name}", ctx -> {
		    String name = ctx.pathParam("name");
		    TouristSpot spot = getTouristSpotByName(name); // 観光地名で観光地を検索
		    if (spot != null) {
		        ctx.render("/spot-detail.html", Map.of("spot", spot));
		    } else {
		        ctx.status(404).result("観光地が見つかりません");
		    }
		});


		/*app.get("/route", ctx -> {
		    String origin = "Tokyo Station";
		    String destination = "Kyoto Station";
		    String[] waypoints = {"Nagoya Station", "Osaka Station"}; // 経由地の例

		    try {
		        // Directions API の呼び出し
		        DirectionsResult result = DirectionsApi.newRequest(context)
		                .origin(origin)
		                .destination(destination)
		                .waypoints(waypoints)
		                .optimizeWaypoints(true)
		                .await();

		        // エンコードされたポリラインをクライアントに送信
		        if (result.routes != null && result.routes.length > 0) {
		            String encodedPath = result.routes[0].overviewPolyline.getEncodedPath();
		            System.out.println("パス：" + encodedPath);
		            ctx.json(Map.of("encodedPath", encodedPath));
		        } else {
		            ctx.status(404).result("No routes found");
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		        ctx.status(500).result("Error fetching route: " + e.getMessage());
		    }
		});*/

	}
	// 名前で観光地を取得するヘルパーメソッド
	private static TouristSpot getTouristSpotByName(String name) {
		Search search = new Search();
	    // データソースから検索（ここでは例としてメモリ内データ）
	    return search.touristSpots.stream()
	        .filter(spot -> spot.getName().equals(name))
	        .findFirst()
	        .orElse(null);
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
